package com.example.plugin.commands;

import com.example.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BoussoleCommand implements CommandExecutor {
    private final Main plugin;

    public BoussoleCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("plugin.admin")) {
            sender.sendMessage(plugin.getMessage("no-permission-command"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("must-be-player"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack boussole = createBoussole();
        player.getInventory().addItem(boussole);
        player.sendMessage(plugin.getMessage("received-boussole"));

        return true;
    }

    private ItemStack createBoussole() {
        ItemStack boussole = new ItemStack(Material.COMPASS);
        ItemMeta meta = boussole.getItemMeta();
        
        meta.setDisplayName(ChatColor.YELLOW + "Unclaim Finder");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click droit pour voir les coffres");
        meta.setLore(lore);
        
        boussole.setItemMeta(meta);
        return boussole;
    }
} 