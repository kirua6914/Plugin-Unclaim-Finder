package com.example.plugin.listeners;

import com.example.plugin.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BoussoleListener implements Listener {
    private final Main plugin;
    private final Map<UUID, BukkitTask> playerTasks = new HashMap<>();

    public BoussoleListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.COMPASS) {
            return;
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || 
            !item.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Unclaim Finder")) {
            return;
        }

        if (!player.hasPermission(plugin.getConfig().getString("permissions.use-boussole"))) {
            player.sendMessage(plugin.getMessage("no-permission-use"));
            event.setCancelled(true);
            return;
        }

        // Vérifie le cooldown
        if (playerTasks.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("cooldown"));
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        checkBlocksInChunks(player);
    }

    private void checkBlocksInChunks(Player player) {
        // Annule la tâche précédente si elle existe
        if (playerTasks.containsKey(player.getUniqueId())) {
            playerTasks.get(player.getUniqueId()).cancel();
        }

        // Crée une nouvelle tâche avec cooldown
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                // Exécute la recherche de blocs de manière asynchrone
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int blockCount = countBlocksInChunks(player);
                        
                        // Affiche le résultat dans le thread principal
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.sendTitle(
                                    ChatColor.YELLOW + String.valueOf(blockCount) + "%",
                                    "",
                                    plugin.getFadeIn(),
                                    plugin.getStay(),
                                    plugin.getFadeOut()
                                );
                            }
                        }.runTask(plugin);
                    }
                }.runTaskAsynchronously(plugin);
            }
        }.runTaskLater(plugin, 20L); // 1 seconde de délai

        // Enregistre la tâche pour le cooldown
        playerTasks.put(player.getUniqueId(), task);

        // Supprime la tâche après le cooldown
        new BukkitRunnable() {
            @Override
            public void run() {
                playerTasks.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, plugin.getCooldown());
    }

    private int countBlocksInChunks(Player player) {
        int blockCount = 0;
        Block centerBlock = player.getLocation().getBlock();
        int centerChunkX = centerBlock.getChunk().getX();
        int centerChunkZ = centerBlock.getChunk().getZ();
        int radius = plugin.getChunkRadius();
        List<Material> blocksToDetect = plugin.getBlocksToDetect();

        // Optimisation : ne scanne que les chunks chargés
        for (int chunkX = centerChunkX - radius; chunkX <= centerChunkX + radius; chunkX++) {
            for (int chunkZ = centerChunkZ - radius; chunkZ <= centerChunkZ + radius; chunkZ++) {
                if (!centerBlock.getWorld().isChunkLoaded(chunkX, chunkZ)) {
                    continue;
                }

                for (int x = 0; x < 16; x++) {
                    for (int y = -64; y < 320; y++) {
                        for (int z = 0; z < 16; z++) {
                            Block currentBlock = centerBlock.getWorld().getBlockAt(
                                chunkX * 16 + x,
                                y,
                                chunkZ * 16 + z
                            );
                            
                            if (blocksToDetect.contains(currentBlock.getType())) {
                                blockCount++;
                            }
                        }
                    }
                }
            }
        }

        return blockCount;
    }
} 