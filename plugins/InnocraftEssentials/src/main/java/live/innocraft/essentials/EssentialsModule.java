package live.innocraft.essentials;

public abstract class EssentialsModule {

    private final Essentials plugin;

    public EssentialsModule(Essentials plugin) {
        this.plugin = plugin;
    }

    public abstract void Reload();
    public void Sync() {}
    public abstract void LateInitialization();
    public void OnDisable() {}

    public Essentials getPlugin() {
        return plugin;
    }

}
