package live.innocraft.aozora;

import live.innocraft.hikari.HikariCore;
import live.innocraft.hikari.PluginCore.HikariPlugin;
import live.innocraft.hikari.PluginCore.HikariPluginConfiguration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;


public final class AozoraMain extends HikariPlugin {

    private HikariCore hikariCore;

//    @Override
//    public void onEnable() {
//        //Reflections reflections = new Reflections("live.innocraft.aozora");
//        Reflections reflections = new Reflections(new ConfigurationBuilder()
//                .setUrls(ClasspathHelper.forPackage("live.innocraft.aozora")));
//
//        System.out.println(reflections);
//    }

    @Override
    public void onPluginEnabled() {

        hikariCore = HikariCore.getInstance();

    }

    @Override
    public Reflections getClassReflections() {
        System.out.println(getClass().getPackage().getName());
        return new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("live.innocraft.aozora")));
    }

    /*
    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel( this, "innocraft:methods" );

        Reflections reflections = new Reflections(this.getClass().getPackage().getName());
        Set<Class<? extends HikariPluginConfiguration>> classes = reflections.getSubTypesOf(HikariPluginConfiguration.class);
        System.out.println(this.getClass().getPackage().getName());

        doSomeTests();

        onPluginEnabled();
    }

     */
}
