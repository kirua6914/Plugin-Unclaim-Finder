package com.example.monplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class MonPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Code exécuté au démarrage du plugin
        getLogger().info("MonPlugin a été activé !");
    }
    
    @Override
    public void onDisable() {
        // Code exécuté à l'arrêt du plugin
        getLogger().info("MonPlugin a été désactivé !");
    }
} 