package com.example.spawnprotection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerInteractEvent.Action;

import java.util.Iterator;

public class ProtectionListener implements Listener {
    private final RegionManager regionManager;

    public ProtectionListener(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (regionManager.isProtected(e.getBlock().getLocation()) && !e.getPlayer().hasPermission("spawnprotect.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c此区域受保护，无法破坏方块。");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (regionManager.isProtected(e.getBlock().getLocation()) && !e.getPlayer().hasPermission("spawnprotect.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c此区域受保护，无法放置方块。");
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (regionManager.isProtected(e.getBlock().getLocation()) && !e.getPlayer().hasPermission("spawnprotect.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c此区域受保护，无法倾倒液体。");
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent e) {
        if (regionManager.isProtected(e.getBlock().getLocation()) && !e.getPlayer().hasPermission("spawnprotect.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c此区域受保护，无法取走液体。");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        // 禁止踩踏农田
        if (e.getAction() == Action.PHYSICAL && e.getClickedBlock() != null
                && e.getClickedBlock().getType() == org.bukkit.Material.FARMLAND
                && regionManager.isProtected(e.getClickedBlock().getLocation())
                && !e.getPlayer().hasPermission("spawnprotect.bypass")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player damager && e.getEntity() instanceof Player victim) {
            if (regionManager.isProtected(victim.getLocation()) && !damager.hasPermission("spawnprotect.bypass")) {
                e.setCancelled(true);
                damager.sendMessage("§c此区域禁止PVP！");
            }
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent e) {
        if (e.getRemover() instanceof Player player
                && regionManager.isProtected(e.getEntity().getLocation())
                && !player.hasPermission("spawnprotect.bypass")) {
            e.setCancelled(true);
            player.sendMessage("§c此区域受保护，无法破坏悬挂物。");
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (regionManager.isProtected(e.getLocation())) {
            e.getBlockList().clear();
            return;
        }
        Iterator<org.bukkit.block.Block> it = e.getBlockList().iterator();
        while (it.hasNext()) {
            if (regionManager.isProtected(it.next().getLocation())) {
                it.remove();
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if (regionManager.isProtected(e.getBlock().getLocation())) {
            e.getBlockList().clear();
            return;
        }
        Iterator<org.bukkit.block.Block> it = e.getBlockList().iterator();
        while (it.hasNext()) {
            if (regionManager.isProtected(it.next().getLocation())) {
                it.remove();
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if (regionManager.isProtected(e.getBlock().getLocation())) {
            if (!(e.getEntity() instanceof Player)) {
                e.setCancelled(true);
            }
        }
    }
}