package live.innocraft.smbridge;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.stefan911.securitymaster.lite.api.SecurityMasterAPI;
import me.stefan911.securitymaster.lite.api.events.player.PlayerLoginEvent;
import me.stefan911.securitymaster.lite.api.events.player.PlayerRegisterEvent;
import me.stefan911.securitymaster.lite.api.events.player.PlayerUnregisterEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class SMBridge extends JavaPlugin implements Listener, PluginMessageListener {

    private SecurityMasterAPI smapi = null;

    private void AuthtorizePlayer(Player p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("cmds");
        out.writeUTF("login");
        p.sendPluginMessage(this, "smbridge:main", out.toByteArray());
    }

    private void AuthtorizePlayerNoSession(Player p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("cmds");
        out.writeUTF("login-no-session");
        p.sendPluginMessage(this, "smbridge:main", out.toByteArray());
    }

    private void DeleteSession(Player p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("cmds");
        out.writeUTF("desession");
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
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("smbridge:main"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("cmds")) {
            if (in.readUTF().equals("session")) {
                if (smapi.isRegistered(player)) {
                    smapi.login(player);
                    AuthtorizePlayerNoSession(player);
                }
            }
        }
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
