package live.innocraft.essentials;

public abstract class EssentialsModule {

    private final Essentials plugin;

    public EssentialsModule(Essentials plugin) {
        this.plugin = plugin;
    }

    public abstract void Reload();

    public Essentials getPlugin() {
        return plugin;
    }

}
