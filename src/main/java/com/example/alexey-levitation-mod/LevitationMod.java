package com.alexey.levitationmod;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
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

    public LevitationMod() {
        // Регистрация событий
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onKeyRegister);
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Регистрация клавиши X
    public void onKeyRegister(RegisterKeyMappingsEvent event) {
        levitateKey = new KeyMapping("key.levitation.lift", GLFW.GLFW_KEY_X, "key.categories.misc");
        event.register(levitateKey);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // Проверяем только в конце тика и только если игрок в мире
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null) {
            try {
                // Пытаемся проверить нажатие клавиши
                if (levitateKey != null && levitateKey.isDown()) {
                    executeLevitation();
                }
            } catch (NoSuchMethodError e) {
                // Если метод не найден, выводим ошибку в консоль ОДИН раз, чтобы не спамить
                static boolean errorLogged = false;
                if (!errorLogged) {
                    System.err.println("LevitationMod ERROR: Метод проверки клавиши не найден! " + e.getMessage());
                    errorLogged = true;
                }
            }
        }
    }

    private void executeLevitation() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        double radius = 10.0;
        AABB area = mc.player.getBoundingBox().inflate(radius);
        List<Entity> entities = mc.level.getEntities(mc.player, area);

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                // Подбрасываем моба вверх
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0, 0.2, 0));
            }
        }
    }
}
