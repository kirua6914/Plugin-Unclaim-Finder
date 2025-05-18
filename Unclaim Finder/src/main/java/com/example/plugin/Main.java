package com.example.plugin;

import com.example.plugin.commands.BoussoleCommand;
import com.example.plugin.listeners.BoussoleListener;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {
    private List<Material> blocksToDetect;
    private int chunkRadius;
    private FileConfiguration langConfig;
    private File langFile;

    @Override
    public void onEnable() {
        // Sauvegarde la configuration par défaut
        saveDefaultConfig();
        
        // Charge la configuration
        loadConfig();
        
        // Charge le fichier de langue
        loadLang();
        
        // Enregistrement de la commande
        getCommand("bsl").setExecutor(new BoussoleCommand(this));
        
        // Enregistrement du listener
        getServer().getPluginManager().registerEvents(new BoussoleListener(this), this);
        
        getLogger().info("Plugin activé avec succès !");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin désactivé !");
    }

    public void loadConfig() {
        FileConfiguration config = getConfig();
        
        // Charge la liste des blocs à détecter
        blocksToDetect = new ArrayList<>();
        for (String blockName : config.getStringList("blocks-to-detect")) {
            try {
                Material material = Material.valueOf(blockName);
                blocksToDetect.add(material);
            } catch (IllegalArgumentException e) {
                getLogger().warning("Bloc invalide dans la configuration: " + blockName);
            }
        }
        
        // Charge le rayon de chunks
        chunkRadius = config.getInt("chunk-radius", 1);
    }

    private void loadLang() {
        langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            saveResource("lang.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMessage(String path) {
        String message = langConfig.getString("messages." + path);
        if (message == null) {
            return "Message manquant: " + path;
        }
        return message.replace("&", "§");
    }

    public List<Material> getBlocksToDetect() {
        return blocksToDetect;
    }

    public int getChunkRadius() {
        return chunkRadius;
    }

    public int getFadeIn() {
        return getConfig().getInt("display.fade-in", 10);
    }

    public int getStay() {
        return getConfig().getInt("display.stay", 100);
    }

    public int getFadeOut() {
        return getConfig().getInt("display.fade-out", 10);
    }

    public int getCooldown() {
        return getConfig().getInt("display.cooldown", 100);
    }
} 