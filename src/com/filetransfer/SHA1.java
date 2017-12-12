package com.filetransfer;

import java.nio.ByteBuffer;

public class SHA1 {

	/**
	 * Generate a keyed hash of the message, i.e. SHA1(key | message | key).
	 *
	 * @param message The message which needs to be hashed.
	 * @param key     The secret key for hashing.
	 * @return 160-bit Keyed hash of the message.
	 */
	public byte[] keyedHash(byte[] message, byte[] key) {
		byte[] messageKey = new byte[message.length + 2 * key.length];
		System.arraycopy(key, 0, messageKey, 0, key.length);
		System.arraycopy(message, 0, messageKey, key.length, message.length);
		System.arraycopy(key, 0, messageKey, key.length + message.length, key.length);
		return digest(messageKey);
	}

	/**
	 * Generate hash for the message, i.e. SHA1(Message).
	 *
	 * @param message The String message whose hash has to be generated.
	 * @return 160-bit hash of the message.
	 */
	public byte[] digest(String message) {
		return digest(message.getBytes());
	}

	/**
	 * Generate hash for the message, i.e. SHA1(Message).
	 *
	 * @param message The bytes of the message whose hash has to be generated.
	 * @return 160-bit hash of the message.
	 */
	public byte[] digest(byte[] message) {
		byte[] paddedMessage = addPadding(message);
		int[] hash = {0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476, 0xc3d2e1f0};
		int numOfIterations = paddedMessage.length / 64;
		for (int i = 0; i < numOfIterations; i++) {
			performBlockOperation(paddedMessage, hash, i);
		}
		ByteBuffer b = ByteBuffer.allocate(20);
		for (int word : hash) {
			b.putInt(word);
		}
		return b.array();
	}

	/**
	 * Add padding to make the message have multiple of 512 bytes.
	 *
	 * @param message The message whose hash is to be generated.
	 * @return bytes of the message with padding added.
	 */
	private byte[] addPadding(byte[] message) {
		long l = (long) message.length * 8, k;
		k = 512 - ((l + 65) % 512);
		ByteBuffer b = ByteBuffer.allocate(message.length + (65 + (int) k) / 8);
		b.put(message);
		//Since the value of k will be at least 7
		b.put((byte) 0x80);
		for (int i = 0; i < ((k + 1) / 8) - 1; i++) {
			b.put((byte) 0);
		}
		b.putLong(l);
		return b.array();
	}

	/**
	 * Perform a block operation of SHA 1 Algorithm.
	 *
	 * @param message The message whose hash has to be generated.
	 * @param hash    The hash calculated till now.
	 * @param block   The block number.
	 */
	private void performBlockOperation(byte[] message, int[] hash, int block) {
		int[] words = new int[80];
		int offset = block * 64;
		ByteBuffer b = ByteBuffer.allocate(4);
		for (int i = 0; i < 16; i++) {
			b.put(message[offset]);
			b.put(message[offset + 1]);
			b.put(message[offset + 2]);
			b.put(message[offset + 3]);
			b.position(0);
			words[i] = b.getInt();
			b.clear();
			offset += 4;
		}
		for (int i = 16; i < 80; i++) {
			int x = words[i - 3] ^ words[i - 8] ^ words[i - 14] ^ words[i - 16];
			words[i] = (x << 1) | (x >>> 31);
		}
		int[] oldHash = new int[5];
		System.arraycopy(hash, 0, oldHash, 0, hash.length);
		for (int i = 0; i < 20; i++) {
			int t = hash[4] + ((hash[0] << 5) | (hash[0] >>> 27)) + words[i] + 0x5a827999 + ((hash[1] & hash[2]) | (~hash[1] & hash[3]));
			hash[4] = hash[3];
			hash[3] = hash[2];
			hash[2] = (hash[1] << 30) | (hash[1] >>> 2);
			hash[1] = hash[0];
			hash[0] = t;
		}
		for (int i = 20; i < 40; i++) {
			int t = hash[4] + ((hash[0] << 5) | (hash[0] >>> 27)) + words[i] + 0x6ed9eba1 + (hash[1] ^ hash[2] ^ hash[3]);
			hash[4] = hash[3];
			hash[3] = hash[2];
			hash[2] = (hash[1] << 30) | (hash[1] >>> 2);
			hash[1] = hash[0];
			hash[0] = t;
		}
		for (int i = 40; i < 60; i++) {
			int t = hash[4] + ((hash[0] << 5) | (hash[0] >>> 27)) + words[i] + 0x8f1bbcdc + ((hash[1] & hash[2]) | (hash[1] & hash[3]) | (hash[2] & hash[3]));
			hash[4] = hash[3];
			hash[3] = hash[2];
			hash[2] = (hash[1] << 30) | (hash[1] >>> 2);
			hash[1] = hash[0];
			hash[0] = t;
		}
		for (int i = 60; i < 80; i++) {
			int t = hash[4] + ((hash[0] << 5) | (hash[0] >>> 27)) + words[i] + 0xca62c1d6 + (hash[1] ^ hash[2] ^ hash[3]);
			hash[4] = hash[3];
			hash[3] = hash[2];
			hash[2] = (hash[1] << 30) | (hash[1] >>> 2);
			hash[1] = hash[0];
			hash[0] = t;
		}
		for (int i = 0; i < hash.length; i++) {
			hash[i] += oldHash[i];
		}
	}
}
