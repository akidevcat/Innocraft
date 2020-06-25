package live.innocraft.essentialsbungee.core;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import live.innocraft.essentialsbungee.ustudy.UStudy;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public final class EssentialsBungee extends Plugin implements Listener {

    private HashMap<String, EssentialsModule> modules;

    public <T extends EssentialsModule> T getModule(Class<T> moduleType) {
        if (!modules.containsKey(moduleType.getSimpleName()))
            CriticalError("Module wasn't found: " + moduleType.getSimpleName());
        return moduleType.cast(modules.get(moduleType.getSimpleName()));
    }

    private void addModule(EssentialsModule module) {
        modules.put(module.getClass().getSimpleName(), module);
    }

    @Override
    public void onEnable() {

        LoadInternalModules();

        getProxy().registerChannel( "innocraft:methods" );
        getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable() {
        for (EssentialsModule module : modules.values())
            module.onDisable();
    }

    public void ReloadAll() {
        for (EssentialsModule module : modules.values())
            module.onReload();
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
            m.onLateInitialization();
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

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("innocraft:methods")) return;
        if (!(event.getReceiver() instanceof ProxiedPlayer)) return;

        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());

            String moduleName = in.readUTF();
            EssentialsModule module = modules.get(moduleName);
            if (module == null) {
                getLogger().log(Level.SEVERE, "Received incorrect module name: " + moduleName);
                return;
            }
            String methodName = in.readUTF();
            short argCount = in.readShort();

            Object[] arguments = new Object[argCount];
            Class<?>[] argsTypes = new Class<?>[argCount];
            for (int i = 0; i < argCount; i++) {
                arguments[i] = in.readUTF();
                argsTypes[i] = String.class;
            }

            Method method = module.getClass().getMethod(methodName, argsTypes);
            method.invoke(module, (Object[]) arguments);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void invokeServerMethod(String serverName, String moduleName, String methodName, String... args) {
        if (ProxyServer.getInstance().getPlayers().isEmpty())
            return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(moduleName);
        out.writeUTF(methodName);
        out.writeShort(args.length);
        for (String arg : args) out.writeUTF(arg);
        getProxy().getServerInfo(serverName).sendData( "innocraft:methods", out.toByteArray() );
    }
}
