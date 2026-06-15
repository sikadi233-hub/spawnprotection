package com.example.spawnprotection;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {
    private final RegionManager regionManager;

    public CommandHandler(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("spawnprotect.admin")) {
            sender.sendMessage("§c权限不足。");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> addRegion(sender, args);
            case "delete" -> deleteRegion(sender, args);
            case "list" -> listRegions(sender);
            case "reload" -> reloadConfig(sender);
            default -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§e===== 主城保护插件 =====");
        sender.sendMessage("§e/spawnprotect add <名称> <世界> <x1> <y1> <z1> <x2> <y2> <z2> §7- 添加区域");
        sender.sendMessage("§e/spawnprotect delete <名称> §7- 删除区域");
        sender.sendMessage("§e/spawnprotect list §7- 列出所有区域");
        sender.sendMessage("§e/spawnprotect reload §7- 重载配置文件");
    }

    private void addRegion(CommandSender sender, String[] args) {
        if (args.length < 9) {
            sender.sendMessage("§c用法: /spawnprotect add <名称> <世界> <x1> <y1> <z1> <x2> <y2> <z2>");
            return;
        }
        String name = args[1];
        World world = Bukkit.getWorld(args[2]);
        if (world == null) {
            sender.sendMessage("§c世界 '" + args[2] + "' 不存在。");
            return;
        }
        try {
            int x1 = Integer.parseInt(args[3]);
            int y1 = Integer.parseInt(args[4]);
            int z1 = Integer.parseInt(args[5]);
            int x2 = Integer.parseInt(args[6]);
            int y2 = Integer.parseInt(args[7]);
            int z2 = Integer.parseInt(args[8]);

            int minX = Math.min(x1, x2);
            int minY = Math.min(y1, y2);
            int minZ = Math.min(z1, z2);
            int maxX = Math.max(x1, x2);
            int maxY = Math.max(y1, y2);
            int maxZ = Math.max(z1, z2);

            Region region = new Region(name, world, minX, minY, minZ, maxX, maxY, maxZ);
            regionManager.addRegion(region);
            sender.sendMessage("§a区域 '" + name + "' 已添加/更新并保存。");
        } catch (NumberFormatException e) {
            sender.sendMessage("§c坐标必须为整数。");
        }
    }

    private void deleteRegion(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c用法: /spawnprotect delete <名称>");
            return;
        }
        if (regionManager.deleteRegion(args[1])) {
            sender.sendMessage("§a区域 '" + args[1] + "' 已删除。");
        } else {
            sender.sendMessage("§c未找到名为 '" + args[1] + "' 的区域。");
        }
    }

    private void listRegions(CommandSender sender) {
        sender.sendMessage("§e当前保护区域 (" + regionManager.getRegions().size() + "个):");
        for (Region r : regionManager.getRegions()) {
            sender.sendMessage(String.format(" §7- §f%s §7[世界:%s, 坐标:%d,%d,%d → %d,%d,%d]",
                    r.getName(),
                    r.getWorld().getName(),
                    r.getMinX(), r.getMinY(), r.getMinZ(),
                    r.getMaxX(), r.getMaxY(), r.getMaxZ()));
        }
    }

    private void reloadConfig(CommandSender sender) {
        SpawnProtection.getInstance().reloadConfig();
        regionManager.loadRegions();
        sender.sendMessage("§a配置文件已重载，当前加载 " + regionManager.getRegions().size() + " 个区域。");
    }
}