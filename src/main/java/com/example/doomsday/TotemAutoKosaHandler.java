package com.example.doomsday;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TotemAutoKosaHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("AutoKosa-TotemHandler");
    
    // Track players who need scythe damage after totem use
    private static final Set<UUID> pendingScytheDamage = new HashSet<>();
    
    public static void register() {
        // Process pending scythe hits on server tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<UUID> iterator = pendingScytheDamage.iterator();
            while (iterator.hasNext()) {
                UUID playerId = iterator.next();
                ServerPlayer player = server.getPlayerList().getPlayer(playerId);
                
                if (player != null && player.isAlive()) {
                    // Apply scythe damage
                    applyScytheDamage(player);
                    iterator.remove();
                    LOGGER.info("Applied scythe damage to player: {}", player.getScoreboardName());
                } else {
                    // Remove if player not found or dead
                    iterator.remove();
                }
            }
        });
        
        LOGGER.info("TotemAutoKosaHandler registered successfully");
    }
    
    public static void scheduleScytheDamage(ServerPlayer player) {
        if (player != null) {
            pendingScytheDamage.add(player.getUUID());
            LOGGER.info("Scheduled scythe damage for player: {}", player.getScoreboardName());
        }
    }
    
    private static void applyScytheDamage(ServerPlayer player) {
        if (player == null || !player.isAlive()) {
            return;
        }
        
        try {
            // Get the damage type for player attack
            ServerLevel level = player.serverLevel();
            DamageSource damageSource = player.damageSources().playerAttack(player);
            
            // Apply 4 HP damage (2 hearts)
            float damageAmount = 4.0f;
            
            // Apply damage to the player
            player.hurt(damageSource, damageAmount);
            
            LOGGER.info("Applied {} HP scythe damage to player: {}", damageAmount, player.getScoreboardName());
            
        } catch (Exception e) {
            LOGGER.error("Failed to apply scythe damage: {}", e.getMessage());
        }
    }
    
    // Public method to manually trigger scythe damage
    public static void triggerScytheDamage(ServerPlayer player) {
        scheduleScytheDamage(player);
    }
}