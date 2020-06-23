package live.innocraft.essentials.core;

import live.innocraft.essentials.common.*;
import me.spomg.minecord.api.MAPI;
import me.stefan911.securitymaster.lite.api.SecurityMasterAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public final class Essentials extends JavaPlugin {

    private HashMap<Class<?>, EssentialsModule> modules;
    private HashMap<Class<?>, Object> dependencies;
    private HashMap<Class<?>, EssentialsConfiguration> configurations;

    public void setPlayerPermissionGroup(Player player, String group) {
        if (!player.isOnline())
            return;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " parent set " + group);
    }

    public ServerType getServerType() {
        return getConfiguration(CommonConfiguration.class).getServerType();
    }

    public void sendChatMessage(String msgLabel, CommandSender s) {
        if (s instanceof Player)
            sendChatMessage(msgLabel, (Player)s, ((Player)s).getLocale());
        else
            sendChatMessage(msgLabel, s, "en-EN");
    }

    public void sendChatMessage(String msgLabel, CommandSender s, String lang) {
        s.sendMessage(getChatMessageColor(msgLabel, lang));
    }

    public void sendChatMessageFormat(String msgLabel, CommandSender s, String... args) {
        if (s instanceof Player)
            sendChatMessageFormat(msgLabel, (Player)s, ((Player)s).getLocale(), args);
        else
            sendChatMessageFormat(msgLabel, s, "en-EN", args);
    }

    public void sendChatMessageFormat(String msgLabel, CommandSender s, String lang, String... args) {
        s.sendMessage(getChatMessageFormatColor(msgLabel, lang, args));
    }

    public String getChatMessageColor(String msgLabel, String lang) {
        return getConfiguration(MessagesConfiguration.class).getChatMessageColor(msgLabel, lang);
    }

    public String getChatMessageFormatColor(String msgLabel, String lang, String... args) {
        return getConfiguration(MessagesConfiguration.class).getChatMessageFormatColor(msgLabel, lang, args);
    }

    public String getMessageColor(String msgLabel, String subcategory, String lang) {
        return getConfiguration(MessagesConfiguration.class).getMessageColor(msgLabel, subcategory, lang);
    }

    public String getMessageColorFormat(String msgLabel, String subcategory, String lang, String... args) {
        return getConfiguration(MessagesConfiguration.class).getMessageColorFormat(msgLabel, subcategory, lang, args);
    }

    // Reloads configuration files
    public void reloadConfigurations() {
        for (EssentialsConfiguration cfg : configurations.values())
            cfg.loadFile();
    }

    public <T extends EssentialsModule> T getModule(Class<T> moduleType) {
        if (!modules.containsKey(moduleType))
            criticalError("Module wasn't found: " + moduleType.toString());
        return moduleType.cast(modules.get(moduleType));
    }

    private void addModule(EssentialsModule module) {
        modules.put(module.getClass(), module);
    }

    public <T> T getDependency(Class<T> dependencyType) {
        if (dependencies.containsKey(dependencyType))
            return dependencyType.cast(dependencies.get(dependencyType));
        return null;
    }

    public <T extends EssentialsConfiguration> T getConfiguration(Class<T> cfgType) {
        if (!configurations.containsKey(cfgType))
            criticalError("Configuration wasn't found: " + cfgType.toString());
        return cfgType.cast(configurations.get(cfgType));
    }

    private void addConfiguration(EssentialsConfiguration cfg) { configurations.put(cfg.getClass(), cfg); }

    @Override
    public void onEnable() {
        //Enable Core module
        new EssentialsPlaceholderExpansion(this);

        loadConfigurations();
        loadInternalModules();
        loadDependencies();
    }

    @Override
    public void onDisable() {
        for (EssentialsModule module : modules.values())
            module.onDisable();
    }

    public void reloadAll() {
        reloadConfigurations();
        for (EssentialsModule module : modules.values())
            module.onReload();
    }

    /**
     * Loads internal modules. Basically, creates instances of all classes extending EssentialsModule
     */
    private void loadInternalModules() {
        modules = new HashMap<>();
        Reflections reflections = new Reflections("live.innocraft.essentials");
        Set<Class<? extends EssentialsModule>> classes = reflections.getSubTypesOf(EssentialsModule.class);
        for (Class<? extends EssentialsModule> aClass : classes) {
            try {
                if (getConfiguration(CommonConfiguration.class).getModuleState(aClass.getName())) {
                    EssentialsModule module = aClass.getDeclaredConstructor(Essentials.class).newInstance(this);
                    addModule(module);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
                criticalError("InstantiationException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect EssentialsModule constructors.");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                criticalError("IllegalAccessException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect EssentialsModule constructors.");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                criticalError("InvocationTargetException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect EssentialsModule constructors.");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                criticalError("NoSuchMethodException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect EssentialsModule constructors.");
            }
        }

        // Call Late Initialization
        for (EssentialsModule m : modules.values())
            m.onLateInitialization();
    }

    private void loadDependencies() {
        dependencies = new HashMap<>();
        if (Bukkit.getPluginManager().isPluginEnabled("SecurityMaster")) {
            dependencies.put(SecurityMasterAPI.class, SecurityMasterAPI.getInstance(this));
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Minecord")) {
            dependencies.put(MAPI.class, getServer().getServicesManager().load(MAPI.class));
        }
    }

    private void loadConfigurations() {
        configurations = new HashMap<>();
        Reflections reflections = new Reflections("live.innocraft.essentials");
        Set<Class<? extends EssentialsConfiguration>> classes = reflections.getSubTypesOf(EssentialsConfiguration.class);
        for (Class<? extends EssentialsConfiguration> aClass : classes) {
            try {
                EssentialsConfiguration cfg = aClass.getDeclaredConstructor(Essentials.class).newInstance(this);
                cfg.loadFile();
                addConfiguration(cfg);
            } catch (InstantiationException e) {
                e.printStackTrace();
                criticalError("InstantiationException - A problem has occurred while loading configurations. " +
                        "This can be caused by incorrect EssentialsConfiguration constructors.");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                criticalError("IllegalAccessException - A problem has occurred while loading configurations. " +
                        "This can be caused by incorrect EssentialsConfiguration constructors.");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                criticalError("InvocationTargetException - A problem has occurred while loading configurations. " +
                        "This can be caused by incorrect EssentialsConfiguration constructors.");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                criticalError("NoSuchMethodException - A problem has occurred while loading configurations. " +
                        "This can be caused by incorrect EssentialsConfiguration constructors.");
            }
        }

        // Call Late Initialization
        for (EssentialsConfiguration cfg : configurations.values())
            cfg.onLateInitialization();
    }

    public void criticalError(String errorText) {
        getLogger().log(Level.SEVERE, "A critical error was encountered... Stopping the plugin");
        getLogger().log(Level.SEVERE, errorText);
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void syncAll() {
        for (EssentialsModule m : modules.values())
            m.onSync();
    }
}
