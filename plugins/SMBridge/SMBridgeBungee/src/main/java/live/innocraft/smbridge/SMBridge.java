package live.innocraft.smbridge;

import com.google.common.io.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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

    public void loadConfigs() {
        Configuration sessionCfg = sessionConfig.GetConfiguration();
        Configuration mainCfg = mainConfig.GetConfiguration();

        if (sessionCfg.contains("Sessions"))
            sessions = PlayerSession.List2Sessions(sessionCfg.getStringList("Sessions"));
        else
            sessions = new HashMap<String, PlayerSession>();

        cfg_message_not_auth = mainCfg.getString("Messages.message-not-auth");
        cfg_message_session_resumed = mainCfg.getString("Messages.message-session-resumed");
        cfg_session_active_period = mainCfg.getLong("Session.active-session-period");
    }

    private void saveSessionConfig() {
        Configuration sessionCfg = sessionConfig.GetConfiguration();

        sessionCfg.set("Sessions", PlayerSession.Sessions2List(sessions));

        sessionConfig.Save();
    }

    private void AuthtorizePlayer(ProxiedPlayer p) {
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("cmds");
                out.writeUTF("session");
                p.getServer().getInfo().sendData( "smbridge:main", out.toByteArray() );
                p.sendMessage(new TextComponent(ChatColor.RED + cfg_message_session_resumed));
            }
        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onEnable() {
        mainConfig = new ConfigFile(this, "config.yml");
        sessionConfig = new ConfigFile(this, "session.yml");

        if (!mainConfig.GetLoadedState() || !sessionConfig.GetLoadedState()) {
            getLogger().log(Level.SEVERE, "Couldn't load config file, Disabling...");
            onDisable();
            return;
        }

        loadConfigs();

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
            e.setCancelled(true);
            player.sendMessage(new TextComponent(ChatColor.RED + cfg_message_not_auth));
        }
    }

    @EventHandler
    public void onConnect(PostLoginEvent event)   {
        //A new player joined, add him/her to the list
        ProxiedPlayer p = event.getPlayer();
        String name = p.getName().toLowerCase();

        if (sessions.containsKey(name)) {
            PlayerSession key = sessions.get(name);
            if (key.ip.equals(p.getAddress().getAddress().getHostAddress()) &&
                    System.currentTimeMillis() - key.date < cfg_session_active_period * 1000) {
                AuthtorizePlayer(p);
                return;
            }

        }

        authPlayers.add(p);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event)   {
        //A player disconnected, remove him/her from the list
        ProxiedPlayer p = event.getPlayer();
        authPlayers.remove(p);
    }
}
