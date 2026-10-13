package com.example.doomsday.mixin;

import com.example.doomsday.TotemAutoKosaHandler;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class PlayerTotemMixin {
    
    @Inject(
        method = "checkTotemDeathProtection",
        at = @At("RETURN")
    )
    private void onTotemUsed(CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player = (ServerPlayer)(Object)this;
        
        // If totem was successfully used (return value is true)
        if (cir.getReturnValue()) {
            // Schedule scythe damage for next tick
            TotemAutoKosaHandler.scheduleScytheDamage(player);
        }
    }
}