package de.paulwolf.stegano.encrypt;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class EncryptionWizard {

    public static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] iv)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        assert cipher != null;
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        byte[] ciphertext = cipher.doFinal(plaintext);
        System.out.println("CT length: " + ciphertext.length);
        return ciphertext;
    }

    public static byte[] decrypt(byte[] ciphertext, SecretKey key, byte[] iv)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("AES/GCM/NoPadding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		assert cipher != null;
		cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
		return cipher.doFinal(ciphertext);
	}
}
