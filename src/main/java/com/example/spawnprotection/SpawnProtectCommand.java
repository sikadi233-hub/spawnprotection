package com.example.spawnprotection;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnProtectCommand implements CommandExecutor {

    private final SpawnProtection plugin;
    private final SpawnConfig config;

    public SpawnProtectCommand(SpawnProtection plugin, SpawnConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "center":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + "该命令只能由玩家执行！");
                    return true;
                }
                config.setCenter(player.getLocation());
                player.sendMessage(ChatColor.GREEN + "主城保护中心已设置为你的当前位置！");
                player.sendMessage(ChatColor.YELLOW + "世界: " + player.getWorld().getName()
                        + " | X: " + player.getLocation().getBlockX()
                        + " | Y: " + player.getLocation().getBlockY()
                        + " | Z: " + player.getLocation().getBlockZ());
                plugin.getLogger().info(player.getName() + " 将主城保护中心设为 "
                        + player.getLocation().getBlockX() + ", "
                        + player.getLocation().getBlockY() + ", "
                        + player.getLocation().getBlockZ());
                break;

            case "radius":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "用法: /spawnprotect radius <数字>");
                    return true;
                }
                try {
                    int newRadius = Integer.parseInt(args[1]);
                    if (newRadius <= 0) {
                        sender.sendMessage(ChatColor.RED + "半径必须大于0！");
                        return true;
                    }
                    config.setRadius(newRadius);
                    sender.sendMessage(ChatColor.GREEN + "主城保护半径已设为 " + newRadius + " 格！");
                    plugin.getLogger().info(sender.getName() + " 将主城保护半径设为 " + newRadius);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "请输入有效的数字！");
                }
                break;

            case "world":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + "该命令只能由玩家执行！");
                    return true;
                }
                config.setWorld(player.getWorld());
                player.sendMessage(ChatColor.GREEN + "主城保护世界已切换为: " + player.getWorld().getName());
                plugin.getLogger().info(sender.getName() + " 将主城保护世界设为 " + player.getWorld().getName());
                break;

            case "reload":
                config.reload();
                sender.sendMessage(ChatColor.GREEN + "配置文件已重载！");
                sender.sendMessage(ChatColor.YELLOW + "中心: " + config.getCenter().getWorld().getName()
                        + " | X: " + config.getCenter().getBlockX()
                        + " | Y: " + config.getCenter().getBlockY()
                        + " | Z: " + config.getCenter().getBlockZ()
                        + " | 半径: " + config.getRadius());
                plugin.getLogger().info(sender.getName() + " 重载了主城保护配置");
                break;

            case "info":
                sender.sendMessage(ChatColor.GOLD + "===== 主城保护信息 =====");
                sender.sendMessage(ChatColor.YELLOW + "世界: " + ChatColor.WHITE + config.getCenter().getWorld().getName());
                sender.sendMessage(ChatColor.YELLOW + "中心: " + ChatColor.WHITE
                        + config.getCenter().getBlockX() + ", "
                        + config.getCenter().getBlockY() + ", "
                        + config.getCenter().getBlockZ());
                sender.sendMessage(ChatColor.YELLOW + "半径: " + ChatColor.WHITE + config.getRadius() + " 格");
                break;

            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "===== 主城保护命令 =====");
        sender.sendMessage(ChatColor.YELLOW + "/spawnprotect center  " + ChatColor.WHITE + "- 把当前位置设为保护中心");
        sender.sendMessage(ChatColor.YELLOW + "/spawnprotect radius <数字>  " + ChatColor.WHITE + "- 设置保护半径");
        sender.sendMessage(ChatColor.YELLOW + "/spawnprotect world  " + ChatColor.WHITE + "- 切换到当前世界");
        sender.sendMessage(ChatColor.YELLOW + "/spawnprotect reload  " + ChatColor.WHITE + "- 重载配置文件");
        sender.sendMessage(ChatColor.YELLOW + "/spawnprotect info  " + ChatColor.WHITE + "- 查看当前保护信息");
        sender.sendMessage(ChatColor.GRAY + "别名: /sp");
    }
}
