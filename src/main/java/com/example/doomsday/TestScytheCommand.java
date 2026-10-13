package com.example.doomsday;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class TestScytheCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger("AutoKosa-TestCommand");
    
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }
    
    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("autokosa")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("test")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayer();
                    if (player != null) {
                        TotemAutoKosaHandler.triggerScytheDamage(player);
                        context.getSource().sendSuccess(() -> 
                            Component.literal("Applied scythe damage to yourself!"), true);
                    }
                    return 1;
                })
                .then(Commands.argument("target", EntityArgument.players())
                    .executes(context -> {
                        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "target");
                        for (ServerPlayer target : targets) {
                            TotemAutoKosaHandler.triggerScytheDamage(target);
                        }
                        context.getSource().sendSuccess(() -> 
                            Component.literal("Applied scythe damage to " + targets.size() + " players!"), true);
                        return targets.size();
                    })
                )
            )
            .then(Commands.literal("info")
                .executes(context -> {
                    context.getSource().sendSuccess(() -> 
                        Component.literal("AutoKosa Mod v1.0 - Automatically hits players with scythe after totem use"), false);
                    return 1;
                })
            )
        );
        
        LOGGER.info("AutoKosa test commands registered");
    }
}