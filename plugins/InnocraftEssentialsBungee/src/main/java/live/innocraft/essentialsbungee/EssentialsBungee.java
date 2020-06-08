package live.innocraft.essentialsbungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public final class EssentialsBungee extends Plugin {

    private HashMap<Class<?>, EssentialsModule> modules;

    public <T extends EssentialsModule> T getModule(Class<T> moduleType) {
        if (!modules.containsKey(moduleType))
            CriticalError("Module wasn't found: " + moduleType.toString());
        return moduleType.cast(modules.get(moduleType));
    }

    private void addModule(EssentialsModule module) {
        modules.put(module.getClass(), module);
    }

    @Override
    public void onEnable() {
        LoadInternalModules();
    }

    @Override
    public void onDisable() {
        for (EssentialsModule module : modules.values())
            module.OnDisable();
    }

    public void ReloadAll() {
        for (EssentialsModule module : modules.values())
            module.Reload();
    }

    private void LoadInternalModules() {
        modules = new HashMap<>();
        Reflections reflections = new Reflections("live.innocraft.essentials");
        Set<Class<? extends EssentialsModule>> classes = reflections.getSubTypesOf(EssentialsModule.class);
        for (Class<? extends EssentialsModule> aClass : classes) {
            try {
                EssentialsModule module = aClass.getDeclaredConstructor(EssentialsBungee.class).newInstance(this);
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

        // Call Late Initialization
        for (EssentialsModule m : modules.values())
            m.LateInitialization();
    }

    public void CriticalError(String errorText) {
        getLogger().log(Level.SEVERE, "A critical error was encountered... Stopping the plugin");
        getLogger().log(Level.SEVERE, errorText);
        getProxy().getPluginManager().unregisterListeners(this);
        getProxy().getPluginManager().unregisterCommands(this);
        onDisable();
    }

    public void SyncAll() {
        for (EssentialsModule m : modules.values())
            m.Sync();
    }
}
