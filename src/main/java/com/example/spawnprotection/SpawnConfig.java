package com.example.spawnprotection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class SpawnConfig {

    private final SpawnProtection plugin;
    private Location center;
    private int radius;
    private int radiusSquared;

    public SpawnConfig(SpawnProtection plugin) {
        this.plugin = plugin;
        reload();
    }

    /** 从配置文件重新读取 */
    public void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        String worldName = config.getString("spawn.world", "");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
            plugin.getLogger().warning("配置的世界不存在，使用默认世界: " + world.getName());
        }

        double x = config.getDouble("spawn.x", world.getSpawnLocation().getX());
        double y = config.getDouble("spawn.y", world.getSpawnLocation().getY());
        double z = config.getDouble("spawn.z", world.getSpawnLocation().getZ());
        center = new Location(world, x, y, z);

        radius = config.getInt("spawn.radius", 100);
        radiusSquared = radius * radius;
    }

    /** 设置保护中心并保存 */
    public void setCenter(Location loc) {
        this.center = loc.clone();
        plugin.getConfig().set("spawn.world", loc.getWorld().getName());
        plugin.getConfig().set("spawn.x", loc.getX());
        plugin.getConfig().set("spawn.y", loc.getY());
        plugin.getConfig().set("spawn.z", loc.getZ());
        plugin.saveConfig();
    }

    /** 设置保护半径并保存 */
    public void setRadius(int radius) {
        this.radius = radius;
        this.radiusSquared = radius * radius;
        plugin.getConfig().set("spawn.radius", radius);
        plugin.saveConfig();
    }

    /** 设置保护世界并保存 */
    public void setWorld(World world) {
        this.center = new Location(world, center.getX(), center.getY(), center.getZ());
        plugin.getConfig().set("spawn.world", world.getName());
        plugin.saveConfig();
    }

    public boolean isInSpawn(Location loc) {
        if (!loc.getWorld().equals(center.getWorld())) return false;
        return center.distanceSquared(loc) <= radiusSquared;
    }

    public Location getCenter() { return center; }
    public int getRadius() { return radius; }
}
