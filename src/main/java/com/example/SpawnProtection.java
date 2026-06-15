package com.example.spawnprotection;

import org.bukkit.plugin.java.JavaPlugin;

public class SpawnProtection extends JavaPlugin {
    private static SpawnProtection instance;
    private RegionManager regionManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        regionManager = new RegionManager(this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(regionManager), this);
        getCommand("spawnprotect").setExecutor(new CommandHandler(regionManager));
        getLogger().info("SpawnProtection 已启用！");
    }

    @Override
    public void onDisable() {
        regionManager.saveRegions();
        getLogger().info("SpawnProtection 已卸载！");
    }

    public static SpawnProtection getInstance() {
        return instance;
    }
}