package live.innocraft.essentials.common;

public abstract class EssentialsModule {

    private final Essentials plugin;
    private boolean enabled = true;

    public EssentialsModule(Essentials plugin) {
        this.plugin = plugin;
    }

    public void activate() {
        enabled = true;
        onActivation();
    }
    public void deactivate() {
        enabled = false;
        onDeactivation();
    }

    public void onReload() {}
    public void onSync() {}
    public void onLateInitialization() {}
    public void onDisable() {}
    private void onDeactivation() {}
    private void onActivation() {}

    public Essentials getPlugin() {
        return plugin;
    }

}
