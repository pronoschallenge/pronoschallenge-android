package fr.pronoschallenge.util;

import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: Thomas Delhoménie
 * Date: 19/12/10
 * Time: 19:38
 */
public class SecurityUtil {

    /**
     * Encode a string to MD5
     * @param s
     * @return
     */
    static public String encodeMD5(String s) {
        String encodedString = null;
        try {
            java.security.MessageDigest msgDigest = java.security.MessageDigest.getInstance("MD5");
            msgDigest.reset();
            msgDigest.update(s.getBytes());
            byte[] digest = msgDigest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < digest.length; i++)
            {
                String hex = Integer.toHexString(0xFF & digest[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            encodedString = hexString.toString();
        } catch (NoSuchAlgorithmException nsae) {
             nsae.printStackTrace();
        }

        return encodedString;
    }


}
