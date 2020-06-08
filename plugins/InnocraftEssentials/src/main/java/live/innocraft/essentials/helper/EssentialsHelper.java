package live.innocraft.essentials.helper;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import java.io.IOException;
import java.net.MalformedURLException;
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

import static com.sk89q.jnbt.NBTUtils.toVector;

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

    public static String[] GetRegionsNames(org.bukkit.entity.Player bukkitPlayer) {
        if (bukkitPlayer == null)
            return new String[0];
        Player player = WorldGuardPlugin.inst().wrapPlayer(bukkitPlayer);
        Location loc = player.getLocation();
        BlockVector3 pos = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(player.getWorld());
        if (regions == null)
            return new String[0];
        ApplicableRegionSet set = regions.getApplicableRegions(pos);
        ArrayList<String> result = new ArrayList<String>();
        for (ProtectedRegion region : set)
            result.add(region.getId());

        return result.toArray(new String[0]);
    }

    public static ProtectedRegion[] GetRegions(org.bukkit.entity.Player bukkitPlayer) {
        if (bukkitPlayer == null)
            return new ProtectedRegion[]{};
        Player player = WorldGuardPlugin.inst().wrapPlayer(bukkitPlayer);
        Location loc = player.getLocation();
        BlockVector3 pos = BlockVector3.at(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(player.getWorld());
        if (regions == null)
            return new ProtectedRegion[]{};
        ApplicableRegionSet set = regions.getApplicableRegions(pos);
        ArrayList<ProtectedRegion> result = new ArrayList<ProtectedRegion>();
        for (ProtectedRegion region : set)
            result.add(region);
        return result.toArray(new ProtectedRegion[0]);
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
