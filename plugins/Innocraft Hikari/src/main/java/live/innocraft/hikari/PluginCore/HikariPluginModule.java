package live.innocraft.hikari.PluginCore;

public class HikariPluginModule {

    private final HikariPlugin plugin;
    private boolean enabled = true;

    public HikariPluginModule(HikariPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        enabled = true;
        onActivation();
    }
    public void disable() {
        enabled = false;
        onDeactivation();
    }

    public void onReload() {}
    public void onLateInitialization() {}
    public void onDisable() {}
    private void onDeactivation() {}
    private void onActivation() {}

    public HikariPlugin getPlugin() {
        return plugin;
    }

    public <T extends HikariPluginModule> T getModule(Class<T> moduleType) {
        return getPlugin().getModule(moduleType);
    }
    public <T extends HikariPluginConfiguration> T getConfiguration(Class<T> cfgType) { return getPlugin().getConfiguration(cfgType); }

}
