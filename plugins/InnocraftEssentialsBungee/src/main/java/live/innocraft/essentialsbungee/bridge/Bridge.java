package live.innocraft.essentialsbungee.bridge;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import live.innocraft.essentialsbungee.EssentialsBungee;
import live.innocraft.essentialsbungee.EssentialsModule;
import live.innocraft.essentialsbungee.ustudy.UStudy;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Bridge extends EssentialsModule implements Listener {

    public Bridge(EssentialsBungee plugin) {
        super(plugin);

        plugin.getProxy().registerChannel( "innocraft:main" );

        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event)
    {
        if ( !event.getTag().equalsIgnoreCase("innocraft:main")) return;
        if (!(event.getReceiver() instanceof ProxiedPlayer)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput( event.getData() );
        String subChannel = in.readUTF();
        ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();

        switch (subChannel) {
            case "ChangePlayerServer":

                String server = in.readUTF();
                boolean bypass = in.readBoolean();
                if (!bypass && getPlugin().getModule(UStudy.class).getServerRestrictionState(server))
                    return;
                try {
                    receiver.connect(getPlugin().getProxy().getServerInfo(server));

                    String discordID = in.readUTF();

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("LinkMinecord");
                    out.writeUTF(discordID);
                    getPlugin().getProxy().getServerInfo(server).sendData( "innocraft:main", out.toByteArray() );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case "InvalidateServerRestriction":
                getPlugin().getModule(UStudy.class).SendRestrictionInvalidationRequestAll();
                break;
        }
        /*
        if ( !event.getTag().equalsIgnoreCase("smbridge:main")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput( event.getData() );
        String subChannel = in.readUTF();
        if ( subChannel.equalsIgnoreCase( "cmds" ) )
        {
            // the receiver is a ProxiedPlayer when a server talks to the proxy
            if ( event.getReceiver() instanceof ProxiedPlayer)
            {
                ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();
                String cmd = in.readUTF();

                //Login command - login player
                if (cmd.equalsIgnoreCase("login")) {
                    authPlayers.remove(receiver);

                    PlayerSession psession = new PlayerSession(receiver.getAddress().getAddress().getHostAddress());
                    sessions.put(receiver.getName().toLowerCase(), psession);
                    saveSessionConfig();
                    return;
                }
                if (cmd.equalsIgnoreCase("login-no-session")) {
                    authPlayers.remove(receiver);
                    return;
                }
            }
        }
         */
    }


}
