import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESClient {
    public static String decryptAES(String encryptedText, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String args[]) throws Exception {

        Socket socket = new Socket("127.0.01", 4000);
        // try (Socket socket = new Socket("127.0.01", 4000);)

        // DataInputStream dis = new DataInputStream(socket.getInputStream());

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String reckey = br.readLine();
            System.out.println("Secret Key: " + reckey);
            bw.write("key-received");
            bw.newLine();
            bw.flush();

            String recmes = br.readLine();
            System.out.println(recmes);
            String decmes = decryptAES(recmes, reckey);
            System.out.println("OTP Received: " + decmes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }

    }
}