package live.innocraft.smbridge;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class SMBridge extends Plugin implements Listener {

    private HashSet<ProxiedPlayer> authPlayers;

    private Map<String, PlayerSession> sessions;
    private Configuration config;

    private boolean loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "connections.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //sessions = new HashMap<String, Pair<String, Long>>();

        /*
        Collection<String> keys = config.getKeys();

        for (String k : keys) {
            String ip = config.getString(k + ".ip");
            Long date = config.getLong(k + ".date");
            sessions.put(k, new Pair(ip, date));
        }
         */

        //sessions = (HashMap<String, PlayerSession>)config.get("Sessions");
        if (config.contains("Sessions"))
            sessions = PlayerSession.List2Sessions(config.getStringList("Sessions"));
        else
            sessions = new HashMap<String, PlayerSession>();

        return true;
    }

    private void saveConfig() {
        config.set("Sessions", PlayerSession.Sessions2List(sessions));
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "connections.yml"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Couldn't save to config!");
            e.printStackTrace();
        }

    }

    private void AuthtorizePlayer(ProxiedPlayer p) {
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("cmds");
                out.writeUTF("session");
                p.getServer().getInfo().sendData( "smbridge:main", out.toByteArray() );
                p.sendMessage(new TextComponent("Session check is successful."));
            }
        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onEnable() {
        if (!loadConfig()) {
            getLogger().log(Level.SEVERE, "Couldn't load config file, Disabling...");
            onDisable();
            return;
        }

        authPlayers = new HashSet<ProxiedPlayer>();

        authPlayers.addAll(getProxy().getPlayers());

        getProxy().registerChannel( "smbridge:main" );
        getLogger().info( "SM Bridge Enabled!" );

        getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable() {
        saveConfig();
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
            getLogger().log(Level.WARNING, "SM - RECEIVED some PLUGIN MESSAGE");

            // the receiver is a ProxiedPlayer when a server talks to the proxy
            if ( event.getReceiver() instanceof ProxiedPlayer)
            {
                getLogger().log(Level.WARNING, "SM - RECEIVED PLUGIN MESSAGE");
                ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();
                String cmd = in.readUTF();

                //Login command - login player
                if (cmd.equalsIgnoreCase("login")) {
                    authPlayers.remove(receiver);

                    PlayerSession psession = new PlayerSession(receiver.getAddress().getAddress().getHostAddress());
                    sessions.put(receiver.getName().toLowerCase(), psession);
                    saveConfig();
                }
            }
            // the receiver is a server when the proxy talks to a server
            /*
            if ( event.getReceiver() instanceof Server)
            {
                Server receiver = (Server) event.getReceiver();
                // do things
            }
             */
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
            player.sendMessage(new TextComponent("You're not authorized!"));
        }
    }

    @EventHandler
    public void onConnect(PostLoginEvent event)   {
        //A new player joined, add him/her to the list
        ProxiedPlayer p = event.getPlayer();
        String name = p.getName().toLowerCase();

        if (sessions.containsKey(name)) {
            getLogger().log(Level.WARNING, "Found name!");
            PlayerSession key = sessions.get(name);
            if (key.ip.equals(p.getAddress().toString()))
                getLogger().log(Level.WARNING, "Found IP!");
            if (System.currentTimeMillis() - key.date < 10000)
                getLogger().log(Level.WARNING, "Found Date!");
            if (key.ip.equals(p.getAddress().getAddress().getHostAddress()) &&
                    System.currentTimeMillis() - key.date < 10000) {
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
