package me.yanvsky.unityegg;

import me.yanvsky.unityegg.other.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PluginCommand implements CommandExecutor, TabCompleter {
    private final Main plugin;
    private final FileConfiguration config;

    public PluginCommand(final Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("unityegg")) {
            return false;
        }

        if (args.length < 3 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(getMessage("usage"));
            return true;
        }

        Player target = this.plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(getMessage("no_player"));
            return true;
        }

        ConfigurationSection eggConfig = this.config.getConfigurationSection("items." + args[2]);
        if (eggConfig == null) {
            sender.sendMessage(getMessage("itemNotFound"));
            return true;
        }

        Material eggMaterial = Material.getMaterial(eggConfig.getString("material"));
        if (eggMaterial == null) {
            sender.sendMessage(getMessage("material_not_found"));
            return true;
        }

        ItemStack egg = createCustomEgg(eggMaterial, eggConfig);
        target.getInventory().addItem(egg);

        String displayName = Utils.formatColor(eggConfig.getString("displayName"));
        sender.sendMessage(getMessage("successful_received").replace("{item}", displayName));
        return true;
    }

    private ItemStack createCustomEgg(final Material eggType, final ConfigurationSection eggConfig) {
        ItemStack egg = new ItemStack(eggType);
        ItemMeta meta = egg.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(Utils.formatColor(eggConfig.getString("displayName")));
            meta.setLore(eggConfig.getStringList("lore").stream()
                    .map(Utils::formatColor)
                    .collect(Collectors.toList()));
            egg.setItemMeta(meta);
        }

        return egg;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String alias, final String[] args) {
        List<String> completions = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("unityegg")) {
            if (args.length == 1) {
                completions.add("give");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
                completions.addAll(plugin.getServer().getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toSet()));
            } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
                completions.addAll(config.getConfigurationSection("items").getKeys(false));
            }
        }

        return completions;
    }

    private String getMessage(String path) {
        return Utils.formatColor(config.getString("messages." + path, "Сообщение не найдено: " + path));
    }
}
