package com.alexey.levitationmod;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;
import java.util.List;

@Mod("levitationmod")
public class LevitationMod {
    public static final String MOD_ID = "levitationmod";
    private static KeyMapping launchKey;

    public LevitationMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setupClient(final FMLClientSetupEvent event) {
        launchKey = new KeyMapping("key.levitation.launch", GLFW.GLFW_KEY_X, "key.categories.misc");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && launchKey != null && launchKey.isDown()) {
            var player = Minecraft.getInstance().player;
            if (player != null && player.level() != null) {
                List<Entity> entities = player.level().getEntities(player, player.getBoundingBox().inflate(30));
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity) {
                        entity.setDeltaMovement(0, 1.5, 0);
                        entity.hasImpulse = true;
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(launchKey);
    }
}
