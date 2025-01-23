package me.yanvsky.unityegg.other;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String formatColor(final String msg) {
        final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        final Matcher matcher = hexPattern.matcher(msg);
        final StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            final String hexColor = matcher.group(1);
            final StringBuilder hexBuilder = new StringBuilder("&x");
            for (final char c : hexColor.toCharArray()) {
                hexBuilder.append("&").append(c);
            }
            matcher.appendReplacement(buffer, hexBuilder.toString());
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
