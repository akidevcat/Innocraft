package live.innocraft.essentials;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class EssentialsPlaceholderExpansion extends PlaceholderExpansion {

    private Essentials plugin;

    public EssentialsPlaceholderExpansion(Essentials plugin) {
        this.plugin = plugin;

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            register();
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "InnocraftEssentials";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(identifier.equals("event")){
            return plugin.GetConfiguration().GetCfgCommon().getString("iu-events.current-event", "");
        }

        if(identifier.equals("event_begin")){
            return plugin.GetConfiguration().GetCfgCommon().getString("iu-events.current-event-date-begin", "");
        }

        if(identifier.equals("event_end")){
            return plugin.GetConfiguration().GetCfgCommon().getString("iu-events.current-event-date-end", "");
        }

        return null;
    }

}
