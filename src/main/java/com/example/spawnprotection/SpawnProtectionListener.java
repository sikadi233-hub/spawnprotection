package com.example.spawnprotection;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.event.block.Action;

public class SpawnProtectionListener implements Listener {

    private final SpawnProtection plugin;
    private final SpawnConfig config;
    private final NamespacedKey menuKey;

    public SpawnProtectionListener(SpawnProtection plugin, SpawnConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.menuKey = new NamespacedKey(plugin, "spawn_menu");
    }

    // ==================== 登录 ====================

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (canBypass(player)) return;
        if (config.isInSpawn(player.getLocation())) {
            applySpawnSetup(player);
        }
    }

    // ==================== 移动 ====================

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;
        if (canBypass(event.getPlayer())) return;
        if (config.isInSpawn(event.getPlayer().getLocation()) && isMenuItemEnabled()) {
            applySpawnSetup(event.getPlayer());
        }
    }

    // ==================== 传送 ====================

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (canBypass(player)) return;
        if (config.isInSpawn(event.getTo())) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (config.isInSpawn(player.getLocation())) {
                    applySpawnSetup(player);
                }
            });
        }
    }

    // ==================== 右键菜单物品 ====================

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta.getPersistentDataContainer().has(menuKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);
            String command = plugin.getConfig().getString("menu-item.command", "cd");
            event.getPlayer().performCommand(command);
        }
    }


    // ==================== 防止丢弃菜单物品 ====================

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (canBypass(event.getPlayer())) return;
        if (!config.isInSpawn(event.getPlayer().getLocation())) return;
        if (!isMenuItemEnabled()) return;
        if (isMenuItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    // ==================== 方块破坏 ====================

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (canBypass(event.getPlayer())) return;
        if (config.isInSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getMsg("no-break"));
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (canBypass(event.getPlayer())) return;
        if (config.isInSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getMsg("no-place"));
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (canBypass(event.getPlayer())) return;
        if (config.isInSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getMsg("no-bucket"));
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (canBypass(event.getPlayer())) return;
        if (config.isInSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getMsg("no-bucket"));
        }
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null && canBypass(event.getPlayer())) return;
        if (config.isInSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
            if (event.getPlayer() != null) {
                event.getPlayer().sendMessage(getMsg("no-ignite"));
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (config.isInSpawn(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (config.isInSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (config.isInSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (config.isInSpawn(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (canBypass(attacker)) return;
        if (config.isInSpawn(victim.getLocation())) {
            event.setCancelled(true);
            attacker.sendMessage(getMsg("no-pvp"));
        }
    }

    // ==================== 辅助方法 ====================

    private void applySpawnSetup(Player player) {
        applyGameMode(player);
        if (isMenuItemEnabled()) {
            giveMenuItem(player);
        }
    }

    private void applyGameMode(Player player) {
        if (!isGamemodeChangeEnabled()) return;
        GameMode target = getTargetGameMode();
        if (target == null || player.getGameMode() == target) return;
        player.setGameMode(target);
        player.sendMessage(getMsg("gamemode-changed"));
    }

    private void giveMenuItem(Player player) {
        PlayerInventory inv = player.getInventory();
        if (hasMenuItem(inv)) return;

        if (plugin.getConfig().getBoolean("menu-item.clear-inventory", true)) {
            inv.clear();
            player.sendMessage(getMsg("inventory-cleared"));
        }

        ItemStack menuItem = createMenuItem();
        int slot = plugin.getConfig().getInt("menu-item.slot", 8);
        inv.setItem(slot, menuItem);
    }

    private ItemStack createMenuItem() {
        String matName = plugin.getConfig().getString("menu-item.material", "NETHER_STAR");
        Material material = Material.matchMaterial(matName);
        if (material == null) material = Material.NETHER_STAR;

        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        String name = plugin.getConfig().getString("menu-item.name", "&6&l菜单");
        meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(name));
        meta.getPersistentDataContainer().set(menuKey, PersistentDataType.BYTE, (byte) 1);
        meta.setEnchantmentGlintOverride(true);

        item.setItemMeta(meta);
        return item;
    }

    private boolean hasMenuItem(PlayerInventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (isMenuItem(item)) return true;
        }
        return false;
    }

    private boolean isMenuItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(menuKey, PersistentDataType.BYTE);
    }

    private boolean isMenuItemEnabled() {
        return plugin.getConfig().getBoolean("menu-item.enabled", true);
    }

    private boolean isGamemodeChangeEnabled() {
        return plugin.getConfig().getBoolean("gamemode-change.enabled", true);
    }

    private boolean canBypass(Player player) {
        return player.hasPermission("spawnprotection.bypass");
    }

    private String getMsg(String path) {
        String raw = plugin.getConfig().getString("messages." + path, "&c你不能在主城这么做！");
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    private GameMode getTargetGameMode() {
        String modeName = plugin.getConfig().getString("gamemode-change.mode", "survival");
        if (modeName == null || modeName.isEmpty()) return null;
        try {
            return GameMode.valueOf(modeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
