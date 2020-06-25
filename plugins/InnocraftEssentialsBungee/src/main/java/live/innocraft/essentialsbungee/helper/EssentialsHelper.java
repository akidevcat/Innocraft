package live.innocraft.essentialsbungee.helper;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class EssentialsHelper {

    public static String HashSHA256(String originalString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
            final StringBuilder builder = new StringBuilder();
            for(byte b : hash)
                builder.append(String.format("%02x", b));
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String ConvertMinutesToTimeString(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        String result = "";
        if (h < 10)
            result += "0";
        result += String.valueOf(h) + ":";
        if (m < 10)
            result += "0";
        result += String.valueOf(m);
        return result;
    }

    public static String GetTimeStamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
    }

    public static String GetDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
    }

    public static String ReadURLContent (String url) throws IOException {
        Scanner scanner = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A");
        String result = scanner.next();
        scanner.close();
        return result;
    }

    public static boolean isLinkValid(String url)
    {
        try {
            new URL(url).toURI();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
