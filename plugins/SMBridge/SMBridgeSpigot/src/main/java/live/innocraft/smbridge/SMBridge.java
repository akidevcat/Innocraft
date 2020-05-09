package live.innocraft.smbridge;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.stefan911.securitymaster.lite.api.SecurityMasterAPI;
import me.stefan911.securitymaster.lite.api.events.player.PlayerLoginEvent;
import me.stefan911.securitymaster.lite.api.events.player.PlayerRegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class SMBridge extends JavaPlugin implements Listener, PluginMessageListener {

    private SecurityMasterAPI smapi;

    private void AuthtorizePlayer(Player p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("cmds");
        out.writeUTF("login");
        p.sendPluginMessage(this, "smbridge:main", out.toByteArray());
    }

    @Override
    public void onEnable() {
        smapi = SecurityMasterAPI.getInstance(this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "smbridge:main");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "smbridge:main", this);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("smbridge:main"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("cmds")) {
            if (in.readUTF().equals("session")) {
                smapi.login(player);
            }
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //Check session
        //If true - send login request to bungee
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        //Send login request to bungee
        AuthtorizePlayer(event.getPlayer());
    }

    @EventHandler
    public void onRegister(PlayerRegisterEvent event) {
        //Send login request to bungee
        AuthtorizePlayer(event.getPlayer());
    }
}
