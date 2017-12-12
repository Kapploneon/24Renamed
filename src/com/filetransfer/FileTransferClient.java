package com.filetransfer;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Incoming Message Types
 * Message Type 1: Server sending server certificate
 * Message Type 2: Server sending the sequence number for the first time with keyed hash with session key.
 * Message Type 3: Server sends only sequence number. File is not in server.
 * Message Type 4: Server sends Sequence Number, file features.
 * Message Type 5: Server sends Sequence Number, part of the file contents.
 * Message Type 6: Server sends Sequence Number, the final part/all of the file contents.
 * Message Type 7: Server can upload the file. Send the file.
 * Message Type 8: Server cannot upload the file as a file which does not have write permission already exists with that name.
 * Message Type 9: Server uploaded the file successfully.
 */

public class FileTransferClient {

	private static final Pattern downloadPattern = Pattern.compile("^download (\\S.*)");
	private static final Pattern uploadPattern = Pattern.compile("^upload (\\S.*)");
	private static Socket socket = null;
	private static String host = "127.0.0.1";
	private static int port = 4444;
	private static SecretKey sessionKey = null;
	private static X509Certificate certificateCA;
	private static CertificateFactory x509Factory;
	private static SecureRandom secureRandom = new SecureRandom();
	private static int currentSequenceNumber = -1;
	private static int maxSequenceNumber = -1;
	private static String clientRoot = "ClientRoot/";

	public static void main(String[] args) throws IOException, CertificateException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException {
		String certificateCAFilePath = "ashkan-certificate.crt";
		if (args.length > 0) {
			host = args[0];
			if (args.length > 1) {
				port = Integer.parseInt(args[1]);
				if (args.length > 2) {
					certificateCAFilePath = args[2];
				}
			}
		}
		boolean isExit = false;
		String command;
		Matcher matcher;
		Scanner in = new Scanner(System.in);
		// Load the CA certificate.
		x509Factory = CertificateFactory.getInstance("X.509");
		FileInputStream fins = new FileInputStream(certificateCAFilePath);
		certificateCA = (X509Certificate) x509Factory.generateCertificate(fins);
		// Establish connection with server.
		try {
			socket = new Socket(host, port);
		} catch (ConnectException e) {
			System.out.println("Cannot connect to server. Check if it is online and then try again later.");
			System.exit(1);
		}

		// Create the Client root directory if not present already.
		File directory = new File("ClientRoot");
		if (!directory.exists())
			directory.mkdirs();
		splashScreen();
		authenticateServer();
		while (!isExit) {
			System.out.print("fta> ");
			command = in.nextLine();
			if (command.equals("quit")) {
				// User typed quit.
				isExit = true;
			} else if (command.equals("help")) {
				// User typed help.
				help();
			} else {
				// Authenticate if session key is not present
				if (sessionKey == null) {
					int tries = 10;
					while (tries > 0 && sessionKey == null) {
						authenticateServer();
						tries--;
					}
					if (sessionKey == null)
						throw new RuntimeException("Authentication of server failed 10 times.");
				}
				if ((matcher = downloadPattern.matcher(command)).find()) {
					// User wants to download a file.
					downloadFile(matcher.group(1));
				} else if ((matcher = uploadPattern.matcher(command)).find()) {
					// User wants to upload a file.
					uploadFile(matcher.group(1));
				} else {
					//Invalid input selected by user.
					System.out.println("Invalid command. Type 'help' for list of commands");
				}
			}
		}
		// Close the input and output streams of the socket before exiting the application.
		closeInputAndOutputStreams();
		System.exit(0);
	}

	/**
	 * Help Method displays command information.
	 */
	private static void help() {
		System.out.println(line("*", 80));
		System.out.println("Supported commands");
		System.out.println("All commands below are case sensitive\n");
		System.out.println("\tdownload <file_name>		Download the file whose name is file_name from server. The file is saved in ClientRoot directory.");
		System.out.println("\tupload <file_name>		Upload the file whose name is file_name to server. The working directory is ClientRoot.");
		System.out.println("\thelp						Show this help information");
		System.out.println("\tquit						Exit the program");
		System.out.println();
		System.out.println();
		System.out.println(line("*", 80));
	}

	/**
	 * Displays the Splash screen.
	 */
	private static void splashScreen() {
		System.out.println(line("-", 80));
		System.out.println("Welcome to File Transfer Application");
		System.out.println("Application Version " + getVersion());
		System.out.println("\nType \"help\" to display supported commands.");
		System.out.println(line("-", 80));
	}

	/**
	 * Gets the current version
	 *
	 * @return Version string
	 */
	private static String getVersion() {
		return FileTransferHelper.VERSION;
	}

	private static String line(String s, int num) {
		StringBuilder a = new StringBuilder();
		for (int i = 0; i < num; i++) {
			a.append(s);
		}
		return a.toString();
	}

	/**
	 * Close the input and output streams of the socket.
	 *
	 * @throws IOException
	 */
	private static void closeInputAndOutputStreams() throws IOException {
		OutputStream out = socket.getOutputStream();
		InputStream input = socket.getInputStream();
		out.close();
		input.close();
	}

	/**
	 * Download file from the server at a given location.
	 *
	 * @param location The location in server.
	 * @throws IOException
	 */
	private static void downloadFile(String location) throws IOException {
		OutputStream out = socket.getOutputStream();
		InputStream input = socket.getInputStream();
		// Request download
		byte[] messageBytes = location.getBytes();
		byte[] messageWithHash = FileTransferHelper.buildMessage(messageBytes, sessionKey);
		out.write(3);
		out.write(FileTransferHelper.getBytes(messageWithHash.length));
		out.write(messageWithHash);
		int messageType = input.read();
		if (messageType == 3) {
			// No file was found with that name.
			byte[] sqNumBytesWithHash = new byte[24];
			input.read(sqNumBytesWithHash);
			verifySequenceNumAndHash(sqNumBytesWithHash, true);
			System.out.println("File not found");
		} else if (messageType == 4) {
			// File found, read the file features first
			byte[] fileFeaturesLength = new byte[4];
			input.read(fileFeaturesLength);
			byte[] fileFeatures = new byte[FileTransferHelper.getInteger(fileFeaturesLength)];
			input.read(fileFeatures);
			// Verify sequence number and hash.
			verifySequenceNumAndHash(fileFeatures, false);
			byte[] fileNameLength = new byte[4];
			System.arraycopy(fileFeatures, FileTransferHelper.FILE_FEATURES_SIZE + 4, fileNameLength, 0, 4);
			byte[] fileNameBytes = new byte[FileTransferHelper.getInteger(fileNameLength)];
			System.arraycopy(fileFeatures, FileTransferHelper.FILE_FEATURES_SIZE + 8, fileNameBytes, 0, fileNameBytes.length);
			String fileName = new String(fileNameBytes);
			File file = new File(clientRoot + fileName);
			// Set the file features
			FileTransferHelper.setFileFeatures(fileFeatures, 4, file);

			// Read the contents of the file.
			messageType = input.read();
			FileOutputStream fos = new FileOutputStream(file);
			int downloadSN = 0;
			while (messageType == 5) {
				// Read part of the contents of the file. Download SN will be used to verify the integrity.
				// All messages of type 5 and 6 for one download has same sequence number but different download sequence number.
				byte[] contentLength = new byte[4];
				input.read(contentLength);
				byte[] fileContentWithHash = new byte[FileTransferHelper.getInteger(contentLength)];
				input.read(fileContentWithHash);
				verifySequenceNumAndHash(fileContentWithHash, false);
				byte[] downloadSNMessage = new byte[4];
				System.arraycopy(fileContentWithHash, 4, downloadSNMessage, 0, 4);
				// Verify download Sequence number.
				if (downloadSN != FileTransferHelper.getInteger(downloadSNMessage))
					throw new RuntimeException("Download sn does not match");
				byte[] fileContent = new byte[fileContentWithHash.length - 28];
				System.arraycopy(fileContentWithHash, 8, fileContent, 0, fileContent.length);
				fos.write(fileContent);
				downloadSN++;
				messageType = input.read();
			}
			if (messageType == 6) {
				//Read the last part/all of the contents of the file.
				byte[] contentLength = new byte[4];
				input.read(contentLength);
				byte[] fileContentWithHash = new byte[FileTransferHelper.getInteger(contentLength)];
				input.read(fileContentWithHash);
				verifySequenceNumAndHash(fileContentWithHash, true);
				byte[] downloadSNMessage = new byte[4];
				System.arraycopy(fileContentWithHash, 4, downloadSNMessage, 0, 4);
				// Verify download sequence number.
				if (downloadSN != FileTransferHelper.getInteger(downloadSNMessage))
					throw new RuntimeException("Download sn does not match: " + downloadSN + " " + FileTransferHelper.getInteger(downloadSNMessage));
				byte[] fileContent = new byte[fileContentWithHash.length - 28];
				System.arraycopy(fileContentWithHash, 8, fileContent, 0, fileContent.length);
				fos.write(fileContent);
				System.out.println("Downloaded the file successfully.");
			} else {
				throw new RuntimeException("Message type " + messageType + " found instead of Message type 5/6");
			}
		}
	}

	/**
	 * Uploads the file at location to the server.
	 *
	 * @param location The location of the file in the client.
	 * @throws IOException
	 * @throws IllegalBlockSizeException
	 * @throws CertificateException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	private static void uploadFile(String location) throws IOException, IllegalBlockSizeException, CertificateException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		OutputStream out = socket.getOutputStream();
		InputStream input = socket.getInputStream();
		File file = new File(clientRoot + location);
		if (!file.exists()) {
			// File does not exist.
			System.out.println("Not found location: " + clientRoot + location);
		} else {
			// File exists.
			// Write the file Features.
			byte[] fileNameBytes = file.getName().getBytes();
			byte[] fileFeatures = new byte[fileNameBytes.length + 4 + FileTransferHelper.FILE_FEATURES_SIZE];
			// Copy the file features.
			FileTransferHelper.getFileFeatures(fileFeatures, 0, file);
			System.arraycopy(FileTransferHelper.getBytes(fileNameBytes.length), 0, fileFeatures, FileTransferHelper.FILE_FEATURES_SIZE, 4);
			System.arraycopy(fileNameBytes, 0, fileFeatures, FileTransferHelper.FILE_FEATURES_SIZE + 4, fileNameBytes.length);
			// Build the hash and append to the message.
			byte[] fileFeaturesWithHash = FileTransferHelper.buildMessage(fileFeatures, sessionKey);
			out.write(4);
			out.write(FileTransferHelper.getBytes(fileFeaturesWithHash.length));
			out.write(fileFeaturesWithHash);

			int messageType = input.read();
			if (messageType == 7) {
				// File can be uploaded.
				byte[] successMessageLength = new byte[4];
				input.read(successMessageLength);
				byte[] successMessage = new byte[FileTransferHelper.getInteger(successMessageLength)];
				input.read(successMessage);
				// Verify the hash and sequence number.
				verifySequenceNumAndHash(successMessage, true);
				// If the sequence number increase have triggered the expiration of sequence numbers, establish a new authentication with server.
				if (sessionKey == null) {
					authenticateServer();
				}
				// Write the file contents, send MAX_FILE_SIZE_READ bytes part of the file. If file size < 1024 bytes, send the whole file.
				int remainingBytes = (int) file.length();
				byte[] buffer = new byte[FileTransferHelper.MAX_FILE_SIZE_READ];
				FileInputStream fis = new FileInputStream(file);
				int uploadSN = 0;
				while (remainingBytes > buffer.length) {
					// Upload a part of the file.
					fis.read(buffer);
					remainingBytes -= buffer.length;
					byte[] fileMessage = new byte[buffer.length + 4];
					System.arraycopy(FileTransferHelper.getBytes(uploadSN), 0, fileMessage, 0, 4);
					System.arraycopy(buffer, 0, fileMessage, 4, buffer.length);
					byte[] fileMessageWithHash = FileTransferHelper.buildMessage(fileMessage, sessionKey);
					uploadSN++;
					out.write(5);
					out.write(FileTransferHelper.getBytes(fileMessageWithHash.length));
					out.write(fileMessageWithHash);
				}
				// Upload the remaining file/the whole file.
				buffer = new byte[remainingBytes];
				fis.read(buffer);
				byte[] fileMessage = new byte[buffer.length + 4];
				System.arraycopy(FileTransferHelper.getBytes(uploadSN), 0, fileMessage, 0, 4);
				System.arraycopy(buffer, 0, fileMessage, 4, buffer.length);
				// Build the hash and append to message.
				byte[] fileMessageWithHash = FileTransferHelper.buildMessage(fileMessage, sessionKey);
				out.write(6);
				out.write(FileTransferHelper.getBytes(fileMessageWithHash.length));
				out.write(fileMessageWithHash);

				messageType = input.read();
				if (messageType == 9) {
					//The file was uploaded successfully.
					byte[] message = new byte[24];
					input.read(message);
					verifySequenceNumAndHash(message, true);
					System.out.println("Uploaded successfully.");
				} else {
					throw new RuntimeException("Something went wrong.");
				}
			} else if (messageType == 8) {
				// File already exists and it cannot be rewritten in the server.
				byte[] failureMessageLength = new byte[4];
				input.read(failureMessageLength);
				byte[] failureMessage = new byte[FileTransferHelper.getInteger(failureMessageLength)];
				input.read(failureMessage);
				verifySequenceNumAndHash(failureMessage, true);
			}
		}
	}

	/**
	 * Verify the sequence number (and increment it if needed) and hash for this message. Sequence number is the first 4 bytes of the message.
	 *
	 * @param message     The message sent by the server.
	 * @param incrementSN Should we increment the sequence number after verifying it.
	 */
	private static void verifySequenceNumAndHash(byte[] message, boolean incrementSN) {
		if (FileTransferHelper.verifyHash(message, sessionKey)) {
			// The hash of the message is verified.
			byte[] sqNumBytes = new byte[4];
			System.arraycopy(message, 0, sqNumBytes, 0, 4);
			if (FileTransferHelper.getInteger(sqNumBytes) != currentSequenceNumber) {
				// The sequence number is incorrect.
				throw new RuntimeException("Incorrect sequence number.");
			} else if (incrementSN) {
				// The sequence number is correct and incremented.
				currentSequenceNumber++;
				if (currentSequenceNumber > maxSequenceNumber) {
					sessionKey = null;
					currentSequenceNumber = -1;
					maxSequenceNumber = -1;
				}
			}
		} else {
			throw new RuntimeException("Hash not verified.");
		}
	}

	/**
	 * Starts the authentication of server.
	 *
	 * @throws IOException
	 * @throws CertificateException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchAlgorithmException
	 */
	private static void authenticateServer() throws IOException, CertificateException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException {
		// Send the authentication request.
		System.out.println("Sending Authenticating request...");
		OutputStream out = socket.getOutputStream();
		InputStream input = socket.getInputStream();
		out.write(1);

		int messageType = input.read();
		if (messageType == 1) {
			// Server sends certificate.
			byte[] certificateLengthBytes = new byte[4];
			input.read(certificateLengthBytes);
			byte[] certificateServerBytes = new byte[FileTransferHelper.getInteger(certificateLengthBytes)];
			input.read(certificateServerBytes);
			X509Certificate certificateServer = (X509Certificate) x509Factory.generateCertificate(
					new ByteArrayInputStream(certificateServerBytes)
			);
			// Verify certificate of server by using the CA's public key.
			try {
				certificateServer.verify(certificateCA.getPublicKey());
			} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
				System.out.println("Certificate not verified!");
				e.printStackTrace();
				return;
			}

			// Get the server public key.
			PublicKey pubServer = certificateServer.getPublicKey();
			// Generate a Random 128-bit newAESKey.
			byte[] newAESKey = new byte[16];
			secureRandom.nextBytes(newAESKey);
			// Use the Random to create a session Key using AES.
			sessionKey = new SecretKeySpec(newAESKey, "AES");
			Cipher cipher = null;
			try {
				cipher = Cipher.getInstance(pubServer.getAlgorithm());
			} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
				e.printStackTrace();
				System.exit(1);
			}
			// Encrypt the random generated using the public key of the server.
			cipher.init(Cipher.ENCRYPT_MODE, pubServer);
			byte[] encryptedRandom = cipher.doFinal(newAESKey);
			// Send message type 2, i.e. send the encrypted Random generated to the server.
			out.write(2);
			out.write(FileTransferHelper.getBytes(encryptedRandom.length));
			out.write(encryptedRandom);
			messageType = input.read();
			if (messageType != 2) {
				throw new RuntimeException("Invalid message type received.");
			}
			byte[] seqNumLength = new byte[4];
			input.read(seqNumLength);
			byte[] seqNumBytes = new byte[FileTransferHelper.getInteger(seqNumLength)];
			input.read(seqNumBytes);
			// Verify the hash
			if (FileTransferHelper.verifyHash(seqNumBytes, sessionKey)) {
				// Extract the sequence number from the server and store it along with max sequence number for which the session key expires.
				byte[] seqNumPartBytes = new byte[4];
				System.arraycopy(seqNumBytes, 0, seqNumPartBytes, 0, 4);
				currentSequenceNumber = FileTransferHelper.getInteger(seqNumPartBytes);
				maxSequenceNumber = currentSequenceNumber + FileTransferHelper.SEQUENCE_NUMBER_RANGE;
				currentSequenceNumber++;
			} else {
				throw new RuntimeException("Hash not verified.");
			}
		} else {
			throw new RuntimeException("Invalid message type received.");
		}
		System.out.println("Server authenticated.");
	}
}
