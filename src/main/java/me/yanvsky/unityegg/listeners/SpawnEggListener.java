package me.yanvsky.unityegg.listeners;

import me.yanvsky.unityegg.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class SpawnEggListener implements Listener {
    private final Main plugin;
    private final FileConfiguration config;

    public SpawnEggListener(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onEggUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item != null && item.hasItemMeta()) {
            ConfigurationSection itemsSection = config.getConfigurationSection("items");
            if (itemsSection == null) return;

            for (String eggName : itemsSection.getKeys(false)) {
                ConfigurationSection eggConfig = itemsSection.getConfigurationSection(eggName);
                String materialName = eggConfig.getString("material");

                if (item.getType().name().equals(materialName) && event.getAction().toString().contains("RIGHT") && event.hasBlock()) {
                    event.setCancelled(true);
                    Block clickedBlock = event.getClickedBlock();

                    if (clickedBlock.getType() == Material.SPAWNER) {
                        spawnSpawnerFromEgg(player, eggConfig, clickedBlock);
                    } else {
                        spawnMobsFromEgg(player, eggConfig, clickedBlock.getLocation());
                    }
                }
            }
        }
    }

    private void spawnMobsFromEgg(Player player, ConfigurationSection eggConfig, Location spawnLocation) {
        ConfigurationSection mobsConfig = eggConfig.getConfigurationSection("mobs");
        double totalChance = calculateTotalChance(mobsConfig);
        String mobName = selectRandomMob(mobsConfig, totalChance);

        if (mobName != null) {
            EntityType entityType = EntityType.valueOf(mobName);
            Sound sound = Sound.valueOf(mobsConfig.getString(mobName + ".sound"));
            Location adjustedLocation = findSafeSpawnLocation(spawnLocation);

            player.getWorld().playSound(adjustedLocation, sound, 1, 1);
            player.getWorld().spawnEntity(adjustedLocation, entityType);
        }
    }

    private void spawnSpawnerFromEgg(Player player, ConfigurationSection eggConfig, Block spawnerLocation) {
        CreatureSpawner spawner = (CreatureSpawner) spawnerLocation.getState();
        ConfigurationSection mobsConfig = eggConfig.getConfigurationSection("mobs");
        double totalChance = calculateTotalChance(mobsConfig);
        String mobName = selectRandomMob(mobsConfig, totalChance);

        if (mobName != null) {
            EntityType entityType = EntityType.valueOf(mobName);
            Sound sound = Sound.valueOf(mobsConfig.getString(mobName + ".sound"));

            player.getWorld().playSound(player.getLocation(), sound, 1, 1);
            spawner.setSpawnedType(entityType);
            spawner.update();
        }
    }

    private double calculateTotalChance(ConfigurationSection mobsConfig) {
        double totalChance = 0.0;
        for (String mobName : mobsConfig.getKeys(false)) {
            totalChance += mobsConfig.getDouble(mobName + ".chance");
        }
        return totalChance;
    }

    private String selectRandomMob(ConfigurationSection mobsConfig, double totalChance) {
        double random = new Random().nextDouble() * totalChance;
        for (String mobName : mobsConfig.getKeys(false)) {
            double chance = mobsConfig.getDouble(mobName + ".chance");
            if (random <= chance) {
                return mobName;
            }
            random -= chance;
        }
        return null;
    }

    private Location findSafeSpawnLocation(Location location) {
        for (int y = 0; y <= 3; y++) {
            Location adjustedLocation = location.clone().add(0, y, 0);
            if (isSafeLocation(adjustedLocation)) {
                return adjustedLocation;
            }
        }
        return location;
    }

    private boolean isSafeLocation(Location location) {
        return location.getBlock().isEmpty() && location.clone().add(0, 1, 0).getBlock().isEmpty();
    }
}