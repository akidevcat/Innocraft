package live.innocraft.essentialsbungee;

public abstract class EssentialsModule {

    private final EssentialsBungee plugin;

    public EssentialsModule(EssentialsBungee plugin) {
        this.plugin = plugin;
    }

    public void Reload() {}
    public void Sync() {}
    public void LateInitialization() {}
    public void OnDisable() {}

    public EssentialsBungee getPlugin() {
        return plugin;
    }

}
