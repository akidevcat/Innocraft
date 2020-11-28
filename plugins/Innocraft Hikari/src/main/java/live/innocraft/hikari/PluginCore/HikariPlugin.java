package live.innocraft.hikari.PluginCore;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
//import org.reflections.Reflections;
//import org.reflections.util.ClasspathHelper;
//import org.reflections.util.ConfigurationBuilder;

//import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public abstract class HikariPlugin extends JavaPlugin {

    private boolean isReady = false;
    private HashMap<Class<?>, HikariPluginModule> modules;
    private HashMap<Class<?>, Object> dependencies;
    private HashMap<Class<?>, HikariPluginConfiguration> configurations;

    public void kickPlayerSync(Player p, String msg) {
        Bukkit.getScheduler().runTask(this, () -> p.kickPlayer(msg));
    }

    // Reloads configuration files
    public void reloadConfigurations() {
        for (HikariPluginConfiguration cfg : configurations.values())
            cfg.loadFile();
    }

    public <T extends HikariPluginModule> T getModule(Class<T> moduleType) {
        if (!modules.containsKey(moduleType))
            return null;
        return moduleType.cast(modules.get(moduleType));
    }

    private void addModule(HikariPluginModule module) {
        modules.put(module.getClass(), module);
    }

    public <T> T getDependency(Class<T> dependencyType) {
        if (dependencies.containsKey(dependencyType))
            return dependencyType.cast(dependencies.get(dependencyType));
        return null;
    }

    public boolean hasDependency(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    public <T extends HikariPluginConfiguration> T getConfiguration(Class<T> cfgType) {
        if (!configurations.containsKey(cfgType))
            return null;
        return cfgType.cast(configurations.get(cfgType));
    }

    private void addConfiguration(HikariPluginConfiguration cfg) { configurations.put(cfg.getClass(), cfg); }

    public void invokeProxyMethod(String moduleName, String methodName, String... args) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(moduleName);
            out.writeUTF(methodName);
            out.writeShort(args.length);
            for (String arg : args) out.writeUTF(arg);
            p.sendPluginMessage(this, "innocraft:methods", out.toByteArray());
            return;
        }
    }

    public void reloadAllModules() {
        reloadConfigurations();
        for (HikariPluginModule module : modules.values())
            module.onReload();
    }

    /**
     * Loads internal modules. Basically, creates instances of all classes extending HikariPluginModule
     */
    public void loadInternalModules() {
        modules = new HashMap<>();
        System.out.println("[Hikari] Loading " + this.getName() + "'s plugin modules");
        ArrayList<Class<? extends HikariPluginModule>> classes = getPluginModulesList();
        for (Class<? extends HikariPluginModule> aClass : classes) {
            try {
                HikariPluginModule module = aClass.getDeclaredConstructor(HikariPlugin.class).newInstance(this);
                addModule(module);
                System.out.println("[Hikari] " + aClass.getName() + " loaded!");
            } catch (InstantiationException e) {
                e.printStackTrace();
                logError("InstantiationException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect HikariPluginModule constructors.", true);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                logError("IllegalAccessException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect HikariPluginModule constructors.", true);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                logError("InvocationTargetException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect HikariPluginModule constructors.", true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                logError("NoSuchMethodException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect HikariPluginModule constructors.", true);
            }
        }

        // Call Late Initialization
        for (HikariPluginModule m : modules.values())
            m.onLateInitialization();
    }

    public void loadDependencies() {
        dependencies = new HashMap<>();
    }

    private void addDependency(Object dependency) {
        dependencies.put(dependency.getClass(), dependency);
    }

//    public abstract Reflections getClassReflections();

    public abstract ArrayList<Class<? extends HikariPluginModule>> getPluginModulesList();
    public abstract ArrayList<Class<? extends HikariPluginConfiguration>> getPluginConfigurationsList();

    public void loadConfigurations() {
        configurations = new HashMap<>();
        ArrayList<Class<? extends HikariPluginConfiguration>> classes = getPluginConfigurationsList();
        for (Class<? extends HikariPluginConfiguration> aClass : classes) {
            try {
                HikariPluginConfiguration cfg = aClass.getDeclaredConstructor(HikariPlugin.class).newInstance(this);
                cfg.loadFile();
                addConfiguration(cfg);
            } catch (InstantiationException e) {
                e.printStackTrace();
                logError("InstantiationException - A problem has occurred while loading configurations. " +
                        "This can be caused by incorrect HikariPluginConfiguration constructors.", true);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                logError("IllegalAccessException - A problem has occurred while loading configurations. " +
                        "This can be caused by incorrect HikariPluginConfiguration constructors.", true);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                logError("InvocationTargetException - A problem has occurred while loading configurations. " +
                        "This can be caused by incorrect HikariPluginConfiguration constructors.", true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                logError("NoSuchMethodException - A problem has occurred while loading configurations. " +
                        "This can be caused by incorrect HikariPluginConfiguration constructors.", true);
            }
        }

        // Call Late Initialization
        for (HikariPluginConfiguration cfg : configurations.values())
            cfg.onLateInitialization();
    }

    public void log(String message) {
        getLogger().log(Level.INFO, message);
    }

    public void logError(String message, boolean stopPlugin) {
        getLogger().log(Level.SEVERE, message);
        if (stopPlugin)
            getLogger().log(Level.SEVERE, "Stopping Hikari plugin... ＞﹏＜"); //ToDo: stop the plugin
    }

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel( this, "innocraft:methods" );

        loadConfigurations();
        loadInternalModules();
        loadDependencies();

        onPluginEnabled();

        isReady = true;
    }

    @Override
    public void onDisable() {
        for (HikariPluginModule module : modules.values())
            module.onDisable();

        onPluginDisabled();
    }

    public void onPluginEnabled() {}
    public void onPluginDisabled() {}

    public boolean isReady() {
        return isReady;
    }
}
