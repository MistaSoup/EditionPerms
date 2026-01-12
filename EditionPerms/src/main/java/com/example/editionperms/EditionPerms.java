package com.example.editionperms;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class EditionPerms extends JavaPlugin implements Listener {
    
    private final Map<UUID, PermissionAttachment> attachments = new HashMap<>();
    private final Map<String, PermissionGroup> groups = new LinkedHashMap<>();
    private String bedrockPrefix;
    private boolean useUuidCheck;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("EditionPerms has been enabled! Folia-compatible mode active.");
        
        // Apply permissions to already online players (in case of reload)
        for (Player player : getServer().getOnlinePlayers()) {
            applyPermissions(player);
        }
    }
    
    @Override
    public void onDisable() {
        // Remove all permission attachments
        for (PermissionAttachment attachment : attachments.values()) {
            attachment.remove();
        }
        attachments.clear();
        getLogger().info("EditionPerms has been disabled!");
    }
    
    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        groups.clear();
        
        // Load detection settings
        bedrockPrefix = config.getString("detection.bedrock-prefix", ".");
        useUuidCheck = config.getBoolean("detection.bedrock-uuid-check", true);
        
        // Load permission groups
        ConfigurationSection groupsSection = config.getConfigurationSection("groups");
        if (groupsSection != null) {
            for (String groupName : groupsSection.getKeys(false)) {
                ConfigurationSection groupConfig = groupsSection.getConfigurationSection(groupName);
                if (groupConfig != null) {
                    PermissionGroup group = new PermissionGroup(
                        groupName,
                        groupConfig.getString("prefix", ""),
                        groupConfig.getString("type", "java"),
                        groupConfig.getStringList("permissions")
                    );
                    groups.put(groupName, group);
                    getLogger().info("Loaded group '" + groupName + "' with " + group.permissions.size() + " permissions");
                }
            }
        }
        
        getLogger().info("Loaded " + groups.size() + " permission groups");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("editionpermsreload")) {
            if (!sender.hasPermission("editionperms.reload") && !sender.isOp()) {
                sender.sendMessage("§cYou don't have permission to use this command!");
                return true;
            }
            
            // Remove all current attachments
            for (PermissionAttachment attachment : attachments.values()) {
                attachment.remove();
            }
            attachments.clear();
            
            // Reload config
            reloadConfig();
            loadConfigValues();
            
            // Reapply permissions to all online players
            for (Player player : getServer().getOnlinePlayers()) {
                applyPermissions(player);
            }
            
            sender.sendMessage("§aEditionPerms config reloaded and permissions reapplied!");
            getLogger().info("Config reloaded by " + sender.getName());
            return true;
        }
        return false;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Use Folia's entity scheduler to ensure thread-safety
        player.getScheduler().run(this, (ScheduledTask task) -> {
            applyPermissions(player);
        }, null);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // Clean up attachment when player leaves
        if (attachments.containsKey(playerId)) {
            attachments.get(playerId).remove();
            attachments.remove(playerId);
        }
    }
    
    private void applyPermissions(Player player) {
        String playerName = player.getName();
        UUID playerId = player.getUniqueId();
        
        // Remove old attachment if exists
        if (attachments.containsKey(playerId)) {
            attachments.get(playerId).remove();
        }
        
        // Create new permission attachment
        PermissionAttachment attachment = player.addAttachment(this);
        
        // Determine player type
        boolean isBedrock = isBedrockPlayer(player);
        String playerType = isBedrock ? "bedrock" : "java";
        
        // Find matching groups and apply permissions
        List<String> appliedGroups = new ArrayList<>();
        int totalPermissions = 0;
        
        for (PermissionGroup group : groups.values()) {
            if (shouldApplyGroup(player, group, playerType)) {
                for (String permission : group.permissions) {
                    attachment.setPermission(permission, true);
                    totalPermissions++;
                }
                appliedGroups.add(group.name);
            }
        }
        
        attachments.put(playerId, attachment);
        
        if (totalPermissions > 0) {
            getLogger().info(playerType.toUpperCase() + " player " + playerName + 
                " granted " + totalPermissions + " permissions from groups: " + 
                String.join(", ", appliedGroups));
        }
    }
    
    private boolean isBedrockPlayer(Player player) {
        String playerName = player.getName();
        
        // Check prefix
        if (playerName.startsWith(bedrockPrefix)) {
            return true;
        }
        
        // Check UUID format if enabled
        if (useUuidCheck) {
            String uuidStr = player.getUniqueId().toString();
            // Bedrock UUIDs start with 00000000-0000-0000
            if (uuidStr.startsWith("00000000-0000-0000")) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean shouldApplyGroup(Player player, PermissionGroup group, String playerType) {
        // Check if group type matches player type
        if (!group.type.equalsIgnoreCase(playerType) && !group.type.equalsIgnoreCase("all")) {
            return false;
        }
        
        // Check if player name matches prefix (if prefix is specified)
        if (!group.prefix.isEmpty()) {
            return player.getName().startsWith(group.prefix);
        }
        
        // If no prefix specified, apply to all players of this type
        return true;
    }
    
    private static class PermissionGroup {
        final String name;
        final String prefix;
        final String type;
        final List<String> permissions;
        
        PermissionGroup(String name, String prefix, String type, List<String> permissions) {
            this.name = name;
            this.prefix = prefix;
            this.type = type;
            this.permissions = permissions;
        }
    }
}