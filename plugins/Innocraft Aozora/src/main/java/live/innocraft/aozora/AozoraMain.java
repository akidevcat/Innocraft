package live.innocraft.aozora;

import live.innocraft.hikari.Common.HikariPlayerManager;
import live.innocraft.hikari.Discord.HikariDiscord;
import live.innocraft.hikari.Discord.HikariDiscordConfiguration;
import live.innocraft.hikari.HikariCore;
import live.innocraft.hikari.HikariCoreConfiguration;
import live.innocraft.hikari.PluginCore.HikariPlugin;
import live.innocraft.hikari.PluginCore.HikariPluginConfiguration;
import live.innocraft.hikari.PluginCore.HikariPluginModule;
import live.innocraft.hikari.SQL.HikariSQL;

import java.util.ArrayList;
import java.util.Set;


public final class AozoraMain extends HikariPlugin {

    private HikariCore hikariCore;

    @Override
    public ArrayList<Class<? extends HikariPluginModule>> getPluginModulesList() {
        ArrayList<Class<? extends HikariPluginModule>> modules = new ArrayList<>();
        modules.add(AozoraDiscord.class);
        modules.add(AozoraManager.class);
        modules.add(AozoraSQL.class);
        return modules;
    }

    @Override
    public ArrayList<Class<? extends HikariPluginConfiguration>> getPluginConfigurationsList() {
        ArrayList<Class<? extends HikariPluginConfiguration>> cfgs = new ArrayList<>();
        //cfgs.add(AozoraCon.class);
        //cfgs.add(HikariCoreConfiguration.class);
        return cfgs;
    }

    @Override
    public void onPluginEnabled() {

        hikariCore = HikariCore.getInstance();

    }

//    @Override
//    public Reflections getClassReflections() {
//        System.out.println(getClass().getPackage().getName());
//        return new Reflections("live.innocraft.aozora");
//    }


//    @Override
//    public void onEnable() {
//        getServer().getMessenger().registerOutgoingPluginChannel( this, "innocraft:methods" );
//
//        Reflections reflections = new Reflections(this.getClass().getPackage().getName());
//        Set<Class<? extends HikariPluginConfiguration>> classes = reflections.getSubTypesOf(HikariPluginConfiguration.class);
//        //System.out.println(this.getClass().getPackage().getName());
//
//        //doSomeTests();
//
//        //onPluginEnabled();
//    }


}
