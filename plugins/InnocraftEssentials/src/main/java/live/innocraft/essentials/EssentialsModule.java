package live.innocraft.essentials;

public abstract class EssentialsModule {

    private final Essentials plugin;

    public EssentialsModule(Essentials plugin) {
        this.plugin = plugin;
    }

    public void Reload() {}
    public void Sync() {}
    public void LateInitialization() {}
    public void OnDisable() {}

    public Essentials getPlugin() {
        return plugin;
    }

}
