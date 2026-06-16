package com.example.spawnprotection;

import org.bukkit.plugin.java.JavaPlugin;

public class SpawnProtection extends JavaPlugin {

    private SpawnConfig spawnConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        spawnConfig = new SpawnConfig(this);
        getServer().getPluginManager().registerEvents(
                new SpawnProtectionListener(this, spawnConfig), this);
        getCommand("spawnprotect").setExecutor(new SpawnProtectCommand(this, spawnConfig));
        getLogger().info("SpawnProtection 已启用。中心: "
                + spawnConfig.getCenter().getBlockX() + ", "
                + spawnConfig.getCenter().getBlockY() + ", "
                + spawnConfig.getCenter().getBlockZ()
                + " 半径: " + spawnConfig.getRadius());
    }

    public SpawnConfig getSpawnConfig() {
        return spawnConfig;
    }
}
