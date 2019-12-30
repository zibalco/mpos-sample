package ir.zibal.zibalsdk.Encryption; /**
 * Created by Mohammad on 3/1/2018.
 */

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static byte[] encrypt(String strToEncrypt)
    {
        try
        {
            byte[] key = {(byte) 0xfa, (byte) 0xa7, (byte) 0x8b, (byte) 0xce, (byte) 0xbd, (byte) 0x3a, (byte) 0xa4, (byte) 0xbf, (byte) 0xc7, 0x6a, 0x3c, 0x45, 0x05, 0x2d, 0x39, 0x61};
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
//            return cipher.doFinal(hexStringToByteArray(strToEncrypt));
            return cipher.doFinal(strToEncrypt.getBytes());
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static byte[] decrypt(byte[] strToDecrypt)
    {
        try
        {
            byte[] key = {(byte) 0xfa, (byte) 0xa7, (byte) 0x8b, (byte) 0xce, (byte) 0xbd, (byte) 0x3a, (byte) 0xa4, (byte) 0xbf, (byte) 0xc7, 0x6a, 0x3c, 0x45, 0x05, 0x2d, 0x39, 0x61};
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(strToDecrypt);
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
