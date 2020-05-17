package live.innocraft.essentials;

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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

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
            return new String[]{};
        Player player = WorldGuardPlugin.inst().wrapPlayer(bukkitPlayer);
        Location loc = player.getLocation();
        BlockVector3 pos = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(player.getWorld());
        if (regions == null)
            return new String[]{};
        ApplicableRegionSet set = regions.getApplicableRegions(pos);
        ArrayList<String> result = new ArrayList<String>();
        for (ProtectedRegion region : set)
            result.add(region.getId());

        return (String[])result.toArray();
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
        return (ProtectedRegion[])result.toArray();
    }
}
