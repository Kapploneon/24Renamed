package com.filetransfer;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;

public class FileTransferHelper {

	final static String VERSION = "1.0";
	final static int FILE_FEATURES_SIZE = 5;
	final static int SEQUENCE_NUMBER_RANGE = 10;
	final static int MAX_FILE_SIZE_READ = 1024;
	private static SHA1 sha1 = new SHA1();

	/**
	 * Get bytes from Integer.
	 *
	 * @param integer The integer to get the bytes from.
	 * @return 4 length array of type byte.
	 */
	public static byte[] getBytes(int integer) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(integer);
		return bb.array();
	}

	/**
	 * Get integer from a array of bytes.
	 *
	 * @param bytes Array of bytes containing the integer.
	 * @return The integer represented by the first 4 bytes.
	 */
	public static int getInteger(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}

	/**
	 * Calculate the keyed hash of the message and append the hash to the message.
	 *
	 * @param message    The message whose hash is to be calculated.
	 * @param sessionKey The session key.
	 * @return Message along appended with the hash.
	 */
	public static byte[] buildMessage(byte[] message, SecretKey sessionKey) {
		if (sessionKey != null) {
			byte[] hashBytes = sha1.keyedHash(message, sessionKey.getEncoded());
			byte[] messageWithHash = new byte[message.length + hashBytes.length];
			System.arraycopy(message, 0, messageWithHash, 0, message.length);
			System.arraycopy(hashBytes, 0, messageWithHash, message.length, hashBytes.length);
			return messageWithHash;
		} else
			throw new RuntimeException("Not authenticated");
	}

	/**
	 * Verifies if the hash part of the message is same as the hash we get from hashing the message part.
	 *
	 * @param message    The message with hash.
	 * @param sessionKey The session key.
	 * @return True if the hash of the message part matches with the hash part of the message, otherwise false.
	 */
	public static boolean verifyHash(byte[] message, SecretKey sessionKey) {
		byte[] messagePart = new byte[message.length - 20];
		System.arraycopy(message, 0, messagePart, 0, messagePart.length);
		byte[] hashMessagePart = sha1.keyedHash(messagePart, sessionKey.getEncoded());
		for (int i = 0; i < hashMessagePart.length; i++) {
			if (hashMessagePart[i] != message[messagePart.length + i])
				return false;
		}
		return true;
	}

	/**
	 * Get the file features into message with an offset of start.
	 *
	 * @param message The message byte array to write the file features.
	 * @param start   The offset of of the message start.
	 * @param file    The file whose features are to be copied to teh message array.
	 * @throws IOException
	 */
	public static void getFileFeatures(byte[] message, int start, File file) throws IOException {
		if (message.length < start + FILE_FEATURES_SIZE)
			throw new RuntimeException("Message size is not enough to load features.");
		DosFileAttributes fileFeatures = Files.readAttributes(Paths.get(file.getAbsolutePath()), DosFileAttributes.class);
		int index = start;
		message[index++] = (byte) (file.canExecute() ? 0 : 1);
		message[index++] = (byte) (fileFeatures.isArchive() ? 0 : 1);
		message[index++] = (byte) (fileFeatures.isHidden() ? 0 : 1);
		message[index++] = (byte) (fileFeatures.isReadOnly() ? 0 : 1);
		message[index++] = (byte) (fileFeatures.isSystem() ? 0 : 1);
	}

	/**
	 * Creates the file if not present, and sets the file features.
	 *
	 * @param message The message array containing the file features.
	 * @param start   The offset of the message array from which the data is file features.
	 * @param file    The file whose features are to be set.
	 * @throws IOException
	 */
	public static void setFileFeatures(byte[] message, int start, File file) throws IOException {
		if (message.length < start + FILE_FEATURES_SIZE)
			throw new RuntimeException("Message size is not enough to load features.");
		Path path = Paths.get(file.getAbsolutePath());
		int index = start;
		//If file does not exist create a new one.
		if (!file.exists())
			file.createNewFile();
		file.setExecutable(message[index++] == 0);
		Files.setAttribute(path, "dos:archive", message[index++] == 0);
		Files.setAttribute(path, "dos:hidden", message[index++] == 0);
		Files.setAttribute(path, "dos:readonly", message[index++] == 0);
		Files.setAttribute(path, "dos:system", message[index++] == 0);
	}

}
