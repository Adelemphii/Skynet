package tech.adelemphii.skynet;

import org.bukkit.plugin.java.JavaPlugin;
import tech.adelemphii.skynet.discord.yuh4j.Yuh4j;

public final class Skynet extends JavaPlugin {

    private static Skynet INSTANCE;

    // discord bots
    private Yuh4j yuh4j;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.yuh4j = new Yuh4j(this);
    }

    @Override
    public void onDisable() {
        yuh4j.stop(false);
    }

    public static Skynet getInstance() {
        return INSTANCE;
    }

    public Yuh4j getYuh4j() {
        return yuh4j;
    }
}
