package live.innocraft.essentials.bridge;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import live.innocraft.essentials.common.Essentials;
import live.innocraft.essentials.common.EssentialsModule;
import live.innocraft.essentials.discord.Discord;
import me.spomg.minecord.api.MAPI;
import me.stefan911.securitymaster.lite.utils.account.AccountManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class Bridge extends EssentialsModule implements Listener, PluginMessageListener {

    public Bridge(Essentials plugin) {
        super(plugin);

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "innocraft:main");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "innocraft:main", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equalsIgnoreCase("innocraft:main"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        switch (subchannel) {
            case "LinkMinecord":

                String discordID = in.readUTF();
                if (Bukkit.getPluginManager().isPluginEnabled("Minecord") && !discordID.equalsIgnoreCase("none")) {
                    MAPI mapi = getPlugin().getServer().getServicesManager().load(MAPI.class);
                    assert mapi != null;
                    if (!mapi.isPlayerLinked(player)) {
                        mapi.link(player, getPlugin().getModule(Discord.class).getMemberByID(discordID));
                        ChangePlayerServerForce(player, "auth");
                    }
                }

                break;
        }
        /*
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
         */
    }

    public void ChangePlayerServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ChangePlayerServer");
        out.writeUTF(server);
        out.writeBoolean(player.hasPermission("innocraft.serverbypass")); //Bypass server closed check
        if (Bukkit.getPluginManager().isPluginEnabled("SecurityMaster"))
            out.writeUTF(new AccountManager(player.getUniqueId()).getDiscordID()); //Send discord ID
        else
            out.writeUTF("none");
        player.sendPluginMessage(getPlugin(), "innocraft:main", out.toByteArray());
    }

    public void ChangePlayerServerForce(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ChangePlayerServer");
        out.writeUTF(server);
        out.writeBoolean(true); //Bypass server closed check
        if (Bukkit.getPluginManager().isPluginEnabled("SecurityMaster"))
            out.writeUTF(new AccountManager(player.getUniqueId()).getDiscordID()); //Send discord ID
        else
            out.writeUTF("none");
        player.sendPluginMessage(getPlugin(), "innocraft:main", out.toByteArray());
    }

    public void SendRestrictionInvalidationRequest(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("InvalidateServerRestriction");
        player.sendPluginMessage(getPlugin(), "innocraft:main", out.toByteArray());
    }

}
