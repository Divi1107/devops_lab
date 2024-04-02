import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class AESServer {

    public static String generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // 128-bit key size
        SecretKey secretKey = keyGen.generateKey();
        byte[] encodedKey = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(encodedKey);
    }

    public static String encryptAES(String plainText, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static void main(String args[]) throws Exception {
        // RSAServer rsa = new RSAServer();

        ServerSocket serverSocket = new ServerSocket(4000);
        Socket clientSocket = serverSocket.accept();
        Scanner sc = new Scanner(System.in);

        try {
            // DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            Random random = new Random();
            int minRange = 10000;
            int maxRange = 99999;

            System.out.println("Automatic OTP Generated: ");

            String originalText = String.valueOf(random.nextInt((maxRange - minRange) + 1) + minRange);
            System.out.println(originalText);
            // String originalText=sc.nextLine();
            // int mess=45;
            String secretKey = generateAESKey();
            System.out.println("Secret Key:" + secretKey);
            bw.write(secretKey);
            bw.newLine();
            bw.flush();

            String mfc = br.readLine();
            String encryptedText;
            if (mfc.equals("key-received"))
                ;
            // Encrypt the original text
            {
                encryptedText = encryptAES(originalText, secretKey);
                System.out.println(encryptedText);
            }

            bw.write(encryptedText);
            bw.newLine();
            bw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            serverSocket.close();
            clientSocket.close();
            sc.close();
        }
    }
}