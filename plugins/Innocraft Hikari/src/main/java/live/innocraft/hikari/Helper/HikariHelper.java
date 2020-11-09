package live.innocraft.hikari.Helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

public class HikariHelper {

    public static String parseDBString(String value) {
        if (value == null || value.equalsIgnoreCase("null"))
            return null;
        return value;
    }

    public static UUID parseDBUniqueID(String raw) {
        if (raw == null || raw.equalsIgnoreCase("null"))
            return null;
        return UUID.fromString(raw);
    }

    public static Date parseDBDate(String raw) {
        if (raw == null || raw.equalsIgnoreCase("null"))
            return new Date(2082758400000L);
        try {
            return new SimpleDateFormat("ddMMyyyy").parse(raw);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String parseDBLanguage(String raw) {
        if (raw == null || raw.equalsIgnoreCase("null"))
            return "en_EN";
        return raw;
    }

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

    public static String GetTimeStamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
    }

    public static String GetDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
    }

    public static String readURLContent(String url) throws IOException, MalformedURLException {
        Scanner scanner = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A");
        String result = scanner.next();
        scanner.close();
        return result;
    }

    public static boolean downloadURLContent(String url, String path) {
        try {
            InputStream in = new URL(url).openStream();
            Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean validateCsv(String path, int rows) {
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().split(",").length != rows)
                    return false;
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    public static boolean isNicknameValid(String name) {
        if (name.length() > 16)
            return false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            // Check for valid characters: letters, numbers and underscores
            if (c < '0' || c > 'z')
                return false;
            if (c > '9' && c < 'A')
                return false;
            if (c > 'Z' && c < '_')
                return false;
            if (c > '_' && c < 'a')
                return false;
        }
        return true;
    }

}
