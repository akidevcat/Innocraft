package live.innocraft.essentials;

import live.innocraft.essentials.classrooms.Classrooms;
import live.innocraft.essentials.timetable.Timetable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public final class Essentials extends JavaPlugin {

    private HashMap<Class<?>, EssentialsModule> modules;

    private EssentialsConfiguration essentialsCfg;

    // Reloads configuration files
    public void ReloadConfigurations() {
        essentialsCfg.ReloadAll();
    }

    public EssentialsConfiguration GetConfiguration() {
        return essentialsCfg;
    }

    public <T extends EssentialsModule> T getModule(Class<T> moduleType) {
        return moduleType.cast(modules.get(moduleType));
    }

    private void addModule(EssentialsModule module) {
        modules.put(module.getClass(), module);
    }

    @Override
    public void onEnable() {
        //Load configuration files
        essentialsCfg = new EssentialsConfiguration(this);

        //Enable Core module
        new EssentialsPlaceholderExpansion(this);

        LoadInternalModules();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void ReloadAll() {
        ReloadConfigurations();
        for (EssentialsModule module : modules.values())
            module.Reload();
    }

    /**
     * Loads internal modules. Basically, creates instances of all classes extending EssentialsModule
     */
    private void LoadInternalModules() {
        modules = new HashMap<>();
        Reflections reflections = new Reflections("live.innocraft.essentials");
        Set<Class<? extends EssentialsModule>> classes = reflections.getSubTypesOf(EssentialsModule.class);
        for (Class<? extends EssentialsModule> aClass : classes) {
            try {
                EssentialsModule module = aClass.getDeclaredConstructor(Essentials.class).newInstance(this);
                addModule(module);
            } catch (InstantiationException e) {
                e.printStackTrace();
                CriticalError("InstantiationException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect EssentialsModule constructors.");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                CriticalError("IllegalAccessException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect EssentialsModule constructors.");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                CriticalError("InvocationTargetException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect EssentialsModule constructors.");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                CriticalError("NoSuchMethodException - A problem has occurred while loading internal modules. " +
                        "This can be caused by incorrect EssentialsModule constructors.");
            }
        }
    }

    protected void CriticalError(String errorText) {
        getLogger().log(Level.SEVERE, "A critical error was encountered... Stopping the plugin");
        getLogger().log(Level.SEVERE, errorText);
        Bukkit.getPluginManager().disablePlugin(this);
    }
}
