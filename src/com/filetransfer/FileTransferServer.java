package com.filetransfer;

import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Random;

/**
 * Incoming Messages
 * Message Type 1 : Client requesting for authentication
 * Message Type 2 : Client sending Secure Random Bytes for AES Session Key Generation
 * Message Type 3 : Client wants to download file
 * Message Type 4 : Client wants to upload file. Sends File features.
 * Message Type 5 : Client sends part of file to upload contents.
 * Message Type 6 : Client sends the final part/all of file to upload contents.
 */

public class FileTransferServer {
	private static ServerSocket serverSocket = null;
	private static int port = 4444;
	private static SecretKey sessionKey = null;
	private static PrivateKey privateKey = null;
	private static String publicKeyAlgorithm = null;
	private static int currentSequenceNumber = -1;
	private static int maxSequenceNumber = -1;
	private static Random random = new Random();
	private static String serverRoot = "ServerRoot/";

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		String ServerCertificateFilePath = "server-certificate.crt";
		String privateKeyServerFilePath = "server-private.key";
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
			if (args.length > 1) {
				ServerCertificateFilePath = args[1];
				if (args.length > 2) {
					privateKeyServerFilePath = args[2];
				}
			}
		}

		// Open socket at port.
		serverSocket = new ServerSocket(port);
		// Read public key algorithm from the certificate file.
		readPublicKeyAlgorithmFromCertificate(ServerCertificateFilePath);
		// Read the private key from the file.
		readPrivateKeyFromFile(privateKeyServerFilePath);

		// Create the server root director if not present.
		File directory = new File("ServerRoot");
		if (!directory.exists())
			directory.mkdirs();
		// Accept an incoming connection.
		Socket clientSocket = serverSocket.accept();
		OutputStream out = clientSocket.getOutputStream();
		InputStream input = clientSocket.getInputStream();

		while (true) {
			int type = input.read();
			if (type == 1) {
				// Client sends auth request.
				handleAuthenticationRequest(out, ServerCertificateFilePath);
			} else if (type == 2) {
				// Client sends shared key.
				handleKeyGeneration(input, out);
			} else if (type == 3 && sessionKey != null) {
				// Client wants to download.
				handleClientDownload(input, out);
			} else if (type == 4 && sessionKey != null) {
				// Client wants to upload.
				handleClientUpload(input, out, ServerCertificateFilePath);
			} else if (type == -1) {
				// Connection lost with client.
				clientSocket = serverSocket.accept();
				out = clientSocket.getOutputStream();
				input = clientSocket.getInputStream();
			} else {
				// Invalid message received.
				out.close();
				input.close();
				throw new RuntimeException("Invalid type " + type);
			}
		}

	}

	/**
	 * Handle the key generation from the message containing the random.
	 *
	 * @param input The input stream of the server socket.
	 * @param out   The output stream of the server socket.
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	private static void handleKeyGeneration(InputStream input, OutputStream out) throws IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		// Client sending us session key.
		byte[] encryptedRandomBytesLength = new byte[4];
		input.read(encryptedRandomBytesLength);
		byte[] encryptedRandomBytes = new byte[FileTransferHelper.getInteger(encryptedRandomBytesLength)];
		input.read(encryptedRandomBytes);
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(publicKeyAlgorithm);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			System.exit(1);
		}
		// Decrypt the random from the the client message using your private key.
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] sessionKeyBytes = cipher.doFinal(encryptedRandomBytes);
		// Build the AES shared sessionKey from the random.
		sessionKey = new SecretKeySpec(sessionKeyBytes, "AES");
		// Generate a new sequence number.
		currentSequenceNumber = random.nextInt(FileTransferHelper.SEQUENCE_NUMBER_RANGE);
		maxSequenceNumber = currentSequenceNumber + FileTransferHelper.SEQUENCE_NUMBER_RANGE;
		// Send the sequence number to the client.
		byte[] messageBytes = FileTransferHelper.buildMessage(FileTransferHelper.getBytes(currentSequenceNumber), sessionKey);
		out.write(2);
		out.write(FileTransferHelper.getBytes(messageBytes.length));
		out.write(messageBytes);
		// Increment the sequence number. If sequence number > max sequence number, the session key is expired.
		currentSequenceNumber++;
		if (currentSequenceNumber > maxSequenceNumber)
			sessionKey = null;
	}

	/**
	 * Handle an authentication request sent by the client.
	 *
	 * @param out The output stream of the server socket.
	 * @throws IOException
	 */
	private static void handleAuthenticationRequest(OutputStream out, String ServerCertificateFilePath) throws IOException {
		// Authenticate type, so send certificate
		List<String> lines = Files.readAllLines(Paths.get(ServerCertificateFilePath));
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (String line : lines) {
			sb.append(prefix);
			sb.append(line);
			prefix = "\n";
		}
		byte[] bytesToWrite = sb.toString().getBytes();
		out.write(1);
		out.write(FileTransferHelper.getBytes(bytesToWrite.length));
		out.write(bytesToWrite);
	}

	/**
	 * Handle the client download request.
	 *
	 * @param input The input stream of the server socket.
	 * @param out   The output stream of the server socket.
	 * @throws IOException
	 */
	private static void handleClientDownload(InputStream input, OutputStream out) throws IOException {
		// Client wants to download.
		if (sessionKey == null) {
			throw new RuntimeException("Session not established.");
		}
		byte[] messageLength = new byte[4];
		input.read(messageLength);
		byte[] message = new byte[FileTransferHelper.getInteger(messageLength)];
		input.read(message);
		// Verify the hash of the message.
		if (FileTransferHelper.verifyHash(message, sessionKey)) {
			byte[] locationBytes = new byte[message.length - 20];
			System.arraycopy(message, 0, locationBytes, 0, locationBytes.length);
			// Extract the file location from the message.
			String location = new String(locationBytes);
			File file = new File(serverRoot + location);
			if (!file.exists()) {
				// File does not exists, send file not found message (type 3).
				out.write(3);
				byte[] fileMessage = FileTransferHelper.buildMessage(FileTransferHelper.getBytes(currentSequenceNumber), sessionKey);
				out.write(fileMessage);
			} else {
				// File exists.
				// Send the file features to client.
				byte[] fileNameBytes = file.getName().getBytes();
				byte[] fileFeatures = new byte[fileNameBytes.length + 8 + FileTransferHelper.FILE_FEATURES_SIZE];
				System.arraycopy(FileTransferHelper.getBytes(currentSequenceNumber), 0, fileFeatures, 0, 4);
				FileTransferHelper.getFileFeatures(fileFeatures, 4, file);
				System.arraycopy(FileTransferHelper.getBytes(fileNameBytes.length), 0, fileFeatures, FileTransferHelper.FILE_FEATURES_SIZE + 4, 4);
				System.arraycopy(fileNameBytes, 0, fileFeatures, FileTransferHelper.FILE_FEATURES_SIZE + 8, fileNameBytes.length);
				byte[] fileFeaturesWithHash = FileTransferHelper.buildMessage(fileFeatures, sessionKey);

				out.write(4);
				out.write(FileTransferHelper.getBytes(fileFeaturesWithHash.length));
				out.write(fileFeaturesWithHash);

				// Write the file contents
				int remainingBytes = (int) file.length();
				byte[] buffer = new byte[FileTransferHelper.MAX_FILE_SIZE_READ];
				FileInputStream fis = new FileInputStream(file);
				int downloadSN = 0;
				while (remainingBytes > buffer.length) {
					// Send MAX_FILE_SIZE_READ bytes part of the file.
					fis.read(buffer);
					remainingBytes -= buffer.length;
					byte[] fileMessage = new byte[buffer.length + 8];
					System.arraycopy(FileTransferHelper.getBytes(currentSequenceNumber), 0, fileMessage, 0, 4);
					System.arraycopy(FileTransferHelper.getBytes(downloadSN), 0, fileMessage, 4, 4);
					System.arraycopy(buffer, 0, fileMessage, 8, buffer.length);
					byte[] fileMessageWithHash = FileTransferHelper.buildMessage(fileMessage, sessionKey);
					downloadSN++;
					out.write(5);
					out.write(FileTransferHelper.getBytes(fileMessageWithHash.length));
					out.write(fileMessageWithHash);
				}
				// Send the remaining part of the file.
				buffer = new byte[remainingBytes];
				fis.read(buffer);
				byte[] fileMessage = new byte[buffer.length + 8];
				System.arraycopy(FileTransferHelper.getBytes(currentSequenceNumber), 0, fileMessage, 0, 4);
				System.arraycopy(FileTransferHelper.getBytes(downloadSN), 0, fileMessage, 4, 4);
				System.arraycopy(buffer, 0, fileMessage, 8, buffer.length);
				byte[] fileMessageWithHash = FileTransferHelper.buildMessage(fileMessage, sessionKey);
				out.write(6);
				out.write(FileTransferHelper.getBytes(fileMessageWithHash.length));
				out.write(fileMessageWithHash);
			}
			// Increment the sequence number.
			currentSequenceNumber++;
			if (currentSequenceNumber > maxSequenceNumber)
				sessionKey = null;
		} else {
			throw new RuntimeException("Hash not verified.");
		}
	}

	/**
	 * Handle the client request for upload.
	 *
	 * @param input                     The input stream of the server socket.
	 * @param out                       The output stream of the server socket.
	 * @param ServerCertificateFilePath The file path of the server certificate for authentication.
	 * @throws IOException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 */
	private static void handleClientUpload(InputStream input, OutputStream out, String ServerCertificateFilePath) throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
		// Read the file features first
		byte[] fileFeaturesLength = new byte[4];
		input.read(fileFeaturesLength);
		byte[] fileFeatures = new byte[FileTransferHelper.getInteger(fileFeaturesLength)];
		input.read(fileFeatures);
		if (!FileTransferHelper.verifyHash(fileFeatures, sessionKey))
			throw new RuntimeException("Hash not verified.");

		byte[] fileNameLength = new byte[4];
		System.arraycopy(fileFeatures, FileTransferHelper.FILE_FEATURES_SIZE, fileNameLength, 0, 4);
		byte[] fileNameBytes = new byte[FileTransferHelper.getInteger(fileNameLength)];
		System.arraycopy(fileFeatures, FileTransferHelper.FILE_FEATURES_SIZE + 4, fileNameBytes, 0, fileNameBytes.length);
		String fileName = new String(fileNameBytes);

		File file = new File(serverRoot + fileName);
		if (!file.exists() || file.canWrite()) {
			// File does not exist or can overwrite the file.
			byte[] successMessage = FileTransferHelper.getBytes(currentSequenceNumber);
			byte[] successMessageWithHash = FileTransferHelper.buildMessage(successMessage, sessionKey);
			out.write(7);
			out.write(FileTransferHelper.getBytes(successMessageWithHash.length));
			out.write(successMessageWithHash);
			// Increment the sequence number.
			currentSequenceNumber++;
			if (currentSequenceNumber > maxSequenceNumber)
				sessionKey = null;

			// Check if the increase in sequence number triggered a expiry of session key. The sessionKey will be null in this case.
			int messageType = input.read();
			if (messageType == 1) {
				handleAuthenticationRequest(out, ServerCertificateFilePath);
				messageType = input.read();
				if (messageType == 2) {
					handleKeyGeneration(input, out);
				} else {
					throw new RuntimeException("Something went wrong in authentication.");
				}
			}

			// Get the file contents and put in the file.
			FileTransferHelper.setFileFeatures(fileFeatures, 0, file);
			FileOutputStream fos = new FileOutputStream(file);
			int uploadSN = 0;
			while (messageType == 5) {
				// Read part of the contents of the file.
				byte[] contentLength = new byte[4];
				input.read(contentLength);
				byte[] fileContentWithHash = new byte[FileTransferHelper.getInteger(contentLength)];
				input.read(fileContentWithHash);
				if (!FileTransferHelper.verifyHash(fileContentWithHash, sessionKey))
					throw new RuntimeException("Hash not verified.");
				byte[] uploadSNMessage = new byte[4];
				System.arraycopy(fileContentWithHash, 0, uploadSNMessage, 0, 4);
				if (uploadSN != FileTransferHelper.getInteger(uploadSNMessage))
					throw new RuntimeException("Upload sn does not match");
				byte[] fileContent = new byte[fileContentWithHash.length - 24];
				System.arraycopy(fileContentWithHash, 4, fileContent, 0, fileContent.length);
				fos.write(fileContent);
				uploadSN++;
				messageType = input.read();
			}

			// Read the remaining contents of the file.
			if (messageType == 6) {
				byte[] contentLength = new byte[4];
				input.read(contentLength);
				byte[] fileContentWithHash = new byte[FileTransferHelper.getInteger(contentLength)];
				input.read(fileContentWithHash);
				if (!FileTransferHelper.verifyHash(fileContentWithHash, sessionKey))
					throw new RuntimeException("Hash not verified.");
				byte[] uploadSNMessage = new byte[4];
				System.arraycopy(fileContentWithHash, 0, uploadSNMessage, 0, 4);
				if (uploadSN != FileTransferHelper.getInteger(uploadSNMessage))
					throw new RuntimeException("Upload sn does not match: " + uploadSN + " " + FileTransferHelper.getInteger(uploadSNMessage));
				byte[] fileContent = new byte[fileContentWithHash.length - 24];
				System.arraycopy(fileContentWithHash, 4, fileContent, 0, fileContent.length);
				fos.write(fileContent);
				byte[] uploadSuccessMessage = FileTransferHelper.buildMessage(FileTransferHelper.getBytes(currentSequenceNumber), sessionKey);
				out.write(9);
				out.write(uploadSuccessMessage);
				// Increment the current sequence number.
				currentSequenceNumber++;
				if (currentSequenceNumber > maxSequenceNumber)
					sessionKey = null;
			} else {
				throw new RuntimeException("Message type " + messageType + " found instead of Message type 5/6");
			}
		} else {
			// File cannot be overwritten.
			byte[] failureMessage = FileTransferHelper.getBytes(currentSequenceNumber);
			byte[] failureMessageWithHash = FileTransferHelper.buildMessage(failureMessage, sessionKey);
			out.write(8);
			out.write(FileTransferHelper.getBytes(failureMessageWithHash.length));
			out.write(failureMessageWithHash);
			currentSequenceNumber++;
			if (currentSequenceNumber > maxSequenceNumber)
				sessionKey = null;
		}
	}

	/**
	 * Read the server private key from the file.
	 *
	 * @param privateKeyServerFilePath The file containing the private key of the server.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private static void readPrivateKeyFromFile(String privateKeyServerFilePath) throws IOException, GeneralSecurityException {
		final String PEM_RSA_PRIVATE_START = "-----BEGIN RSA PRIVATE KEY-----";
		final String PEM_RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";
		File file = new File(privateKeyServerFilePath);
		Path path = Paths.get(file.getAbsolutePath());
		String privateKeyPem = new String(Files.readAllBytes(path));
		// Remove the headers.
		privateKeyPem = privateKeyPem.replace(PEM_RSA_PRIVATE_START, "").replace(PEM_RSA_PRIVATE_END, "");
		// Remove blank spaces or new lines.
		privateKeyPem = privateKeyPem.replaceAll("\\s", "");
		DerInputStream derReader = new DerInputStream(Base64.getDecoder().decode(privateKeyPem));
		DerValue[] seq = derReader.getSequence(0);
		if (seq.length < 9) {
			throw new GeneralSecurityException("Could not parse a PKCS1 private key.");
		}
		BigInteger modulus = seq[1].getBigInteger();
		BigInteger privateExp = seq[3].getBigInteger();
		RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus, privateExp);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		privateKey = factory.generatePrivate(keySpec);
	}

	/**
	 * Read the public key algorithm from the server certificate.
	 *
	 * @param certificateSeverFilePath The server certificate file path.
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 */
	private static void readPublicKeyAlgorithmFromCertificate(String certificateSeverFilePath) 
			throws CertificateException, FileNotFoundException {
		CertificateFactory x509Factory = CertificateFactory.getInstance("X.509");
		X509Certificate certificateServer = (X509Certificate) x509Factory.generateCertificate(new FileInputStream(certificateSeverFilePath));
		publicKeyAlgorithm = certificateServer.getPublicKey().getAlgorithm();
	}
}