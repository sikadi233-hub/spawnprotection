package com.example.spawnprotection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegionManager {
    private final SpawnProtection plugin;
    private final List<Region> regions = new ArrayList<>();

    public RegionManager(SpawnProtection plugin) {
        this.plugin = plugin;
        loadRegions();
    }

    public void loadRegions() {
        regions.clear();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("regions");
        if (sec == null) return;

        for (String name : sec.getKeys(false)) {
            String worldName = sec.getString(name + ".world");
            if (worldName == null) continue;
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("区域 '" + name + "' 的世界 '" + worldName + "' 不存在，已跳过");
                continue;
            }
            int minX = sec.getInt(name + ".min-x");
            int minY = sec.getInt(name + ".min-y");
            int minZ = sec.getInt(name + ".min-z");
            int maxX = sec.getInt(name + ".max-x");
            int maxY = sec.getInt(name + ".max-y");
            int maxZ = sec.getInt(name + ".max-z");
            regions.add(new Region(name, world, minX, minY, minZ, maxX, maxY, maxZ));
        }
        plugin.getLogger().info("已加载 " + regions.size() + " 个保护区域");
    }

    public void saveRegions() {
        // 备份当前配置中的其他键
        plugin.getConfig().set("regions", null);
        for (Region region : regions) {
            String path = "regions." + region.getName();
            plugin.getConfig().set(path + ".world", region.getWorld().getName());
            plugin.getConfig().set(path + ".min-x", region.getMinX());
            plugin.getConfig().set(path + ".min-y", region.getMinY());
            plugin.getConfig().set(path + ".min-z", region.getMinZ());
            plugin.getConfig().set(path + ".max-x", region.getMaxX());
            plugin.getConfig().set(path + ".max-y", region.getMaxY());
            plugin.getConfig().set(path + ".max-z", region.getMaxZ());
        }
        plugin.saveConfig();
    }

    public void addRegion(Region region) {
        regions.removeIf(r -> r.getName().equalsIgnoreCase(region.getName()));
        regions.add(region);
        saveRegions();
    }

    public boolean deleteRegion(String name) {
        boolean removed = regions.removeIf(r -> r.getName().equalsIgnoreCase(name));
        if (removed) saveRegions();
        return removed;
    }

    public Optional<Region> getRegion(String name) {
        return regions.stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
    }

    public List<Region> getRegions() {
        return new ArrayList<>(regions);
    }

    public boolean isProtected(Location loc) {
        for (Region region : regions) {
            if (region.contains(loc)) return true;
        }
        return false;
    }
}