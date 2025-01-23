package me.yanvsky.unityegg;

import me.yanvsky.unityegg.listeners.SpawnEggListener;
import me.yanvsky.unityegg.other.LicenseKey;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!isLicenseValid()) {
            getLogger().warning("§e---------------------------------------");
            getLogger().warning("§e- §fПлагин §cвыключился§f. Неверный ключ.");
            getLogger().warning("§e---------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("unityegg").setExecutor(new PluginCommand(this));
        getServer().getPluginManager().registerEvents(new SpawnEggListener(this), this);

        getLogger().info("§7---------------------------------------");
        getLogger().info("§7- §fПлагин §fвключился.");
        getLogger().info("§7---------------------------------------");
    }

    private boolean isLicenseValid() {
        String licenseKey = getConfig().getString("license-key");
        return LicenseKey.isLicenseValid(licenseKey);
    }
}
