package com.alexey.levitationmod;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import java.util.List;

@Mod("levitationmod")
public class LevitationMod {
    private static KeyMapping launchKey;

    public LevitationMod() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onKeyRegister);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onKeyRegister(RegisterKeyMappingsEvent event) {
        launchKey = new KeyMapping("key.levitation.launch", GLFW.GLFW_KEY_X, "key.categories.misc");
        event.register(launchKey);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            var mc = Minecraft.getInstance();
            // Используем метод .isDown() вместо сломанного m_90857_
            if (launchKey != null && launchKey.isDown() && mc.player != null) {
                var player = mc.player;
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
}
