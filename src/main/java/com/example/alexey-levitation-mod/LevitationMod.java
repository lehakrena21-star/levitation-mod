package com.alexey.levitationmod;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Mod(LevitationMod.MOD_ID)
public class LevitationMod {
    public static final String MOD_ID = "levitationmod";
    private static KeyMapping levitateKey;
    private boolean errorLogged = false;

    public LevitationMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onKeyRegister);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onKeyRegister(RegisterKeyMappingsEvent event) {
        levitateKey = new KeyMapping("key.levitation.lift", GLFW.GLFW_KEY_X, "key.categories.misc");
        event.register(levitateKey);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // Безопасная проверка: не вызываем методы напрямую без проверки фазы
        if (event.phase != TickEvent.Phase.END) return;

        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.player != null && levitateKey != null) {
                if (levitateKey.isDown()) {
                    executeLevitation(mc);
                }
            }
        } catch (Throwable t) {
            if (!errorLogged) {
                System.err.println("LevitationMod Critical Error: " + t.getMessage());
                errorLogged = true;
            }
        }
    }

    private void executeLevitation(Minecraft mc) {
        try {
            if (mc.player == null || mc.level == null) return;

            double radius = 10.0;
            AABB area = mc.player.getBoundingBox().inflate(radius);
            List<Entity> entities = mc.level.getEntities(mc.player, area);

            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity && entity != mc.player) {
                    livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0, 0.2, 0));
                }
            }
        } catch (Throwable ignored) {}
    }
}
