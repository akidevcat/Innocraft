package live.innocraft.essentialsbungee.core;

import live.innocraft.essentialsbungee.core.EssentialsBungee;

public abstract class EssentialsModule {

    private final EssentialsBungee plugin;

    public EssentialsModule(EssentialsBungee plugin) {
        this.plugin = plugin;
    }

    public void onReload() {}
    public void Sync() {}
    public void onLateInitialization() {}
    public void onDisable() {}

    public EssentialsBungee getPlugin() {
        return plugin;
    }

}
