package live.innocraft.smbridge;

import com.google.common.io.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.config.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SMBridge extends Plugin implements Listener {

    private HashSet<ProxiedPlayer> authPlayers;

    private Map<String, PlayerSession> sessions;
    private ConfigFile sessionConfig;
    private ConfigFile mainConfig;

    private String cfg_message_not_auth;
    private String cfg_message_session_resumed;
    private Long cfg_session_active_period;
    private String cfg_auth_server;
    private List<String> cfg_allowed_cmds;

    public void reloadConfigs() {
        mainConfig = new ConfigFile(this, "config.yml");
        sessionConfig = new ConfigFile(this, "session.yml");

        if (!mainConfig.GetLoadedState() || !sessionConfig.GetLoadedState()) {
            getLogger().log(Level.SEVERE, "Couldn't load config file, Disabling...");
            onDisable();
            return;
        }

        loadConfigs();
    }

    public void loadConfigs() {
        Configuration sessionCfg = sessionConfig.GetConfiguration();
        Configuration mainCfg = mainConfig.GetConfiguration();

        if (sessionCfg.contains("Sessions"))
            sessions = PlayerSession.List2Sessions(sessionCfg.getStringList("Sessions"));
        else
            sessions = new HashMap<String, PlayerSession>();

        //ToDo: remove entries with old date

        cfg_message_not_auth = mainCfg.getString("Messages.message-not-auth");
        cfg_message_session_resumed = mainCfg.getString("Messages.message-session-resumed");
        cfg_session_active_period = mainCfg.getLong("Session.active-session-period");
        cfg_auth_server = mainCfg.contains("Bungee.auth-server") ? mainCfg.getString("Bungee.auth-server") : "";
        cfg_allowed_cmds = mainCfg.contains("Bungee.allowed-cmds") ? mainCfg.getStringList("Bungee.allowed-cmds") : null;
    }

    private void saveSessionConfig() {
        Configuration sessionCfg = sessionConfig.GetConfiguration();

        sessionCfg.set("Sessions", PlayerSession.Sessions2List(sessions));

        sessionConfig.Save();
    }

    private void AuthtorizePlayer(ProxiedPlayer p, Server s) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("cmds");
        out.writeUTF("session");
        s.getInfo().sendData( "smbridge:main", out.toByteArray() );
        p.sendMessage(new TextComponent(ChatColor.RED + cfg_message_session_resumed));
    }

    @Override
    public void onEnable() {
        reloadConfigs();

        authPlayers = new HashSet<ProxiedPlayer>();

        authPlayers.addAll(getProxy().getPlayers());

        getProxy().registerChannel( "smbridge:main" );
        getLogger().info( "SM Bridge Enabled!" );

        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this, new PluginCommands(this));
    }

    @Override
    public void onDisable() {
        saveSessionConfig();
        getLogger().info( "SM Bridge Disabled!" );
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event)
    {
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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMessage(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer) || e.isCancelled())
            return;

        ProxiedPlayer player = (ProxiedPlayer) e.getSender();

        if (!authPlayers.contains(player))
            return;

        if (e.isCommand()) {
            if (cfg_allowed_cmds != null && cfg_allowed_cmds.contains(e.getMessage().toLowerCase().split(" ")[0]))
                return;
            e.setCancelled(true);
            player.sendMessage(new TextComponent(ChatColor.RED + cfg_message_not_auth));
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectedEvent event) {
        if (cfg_auth_server.equals("") || !event.getServer().getInfo().getName().equalsIgnoreCase(cfg_auth_server))
            return; // This is not the auth server - skip
        if (!event.getPlayer().hasPermission("securitymaster.use"))
            return;

        //A new player joined, add him/her to the list and check session
        ProxiedPlayer p = event.getPlayer();
        String name = p.getName().toLowerCase();

        authPlayers.add(p);

        if (sessions.containsKey(name)) {
            PlayerSession key = sessions.get(name);
            if (key.ip.equals(p.getAddress().getAddress().getHostAddress()) &&
                    System.currentTimeMillis() - key.date < cfg_session_active_period * 1000) {
                AuthtorizePlayer(p, event.getServer());
                return;
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event)   {
        //A player disconnected, remove him/her from the list
        ProxiedPlayer p = event.getPlayer();
        authPlayers.remove(p);
    }
}
