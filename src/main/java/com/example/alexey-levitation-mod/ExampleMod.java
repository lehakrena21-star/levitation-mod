package com.alexey.levitationmod;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Mod(alexey-levitation-mod.MOD_ID)
public class alexey-levitation-mod {
    public static final String MOD_ID = "alexey-levitation-mod";
    private static KeyMapping launchKey;

    public alexey-levitation-mod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setupClient(final FMLClientSetupEvent event) {
        // Регистрируем клавишу X
        launchKey = new KeyMapping("key.levitation.launch", GLFW.GLFW_KEY_X, "key.categories.misc");
        // В новых версиях Forge/NeoForge регистрация может отличаться, 
        // но для 1.21.1 через ClientRegistry или аналогичный механизм:
        net.minecraftforge.client.event.RegisterKeyMappingsEvent.register(launchKey);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && launchKey.isDown()) {
            var player = Minecraft.getInstance().player;
            if (player != null && player.level() != null) {
                // Ищем существ в радиусе 30 блоков
                List<Entity> entities = player.level().getEntities(player, player.getBoundingBox().inflate(30));
                
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity) {
                        // Подбрасываем вверх (изменение вектора движения)
                        // В Minecraft 1.21.1 используем setDeltaMovement
                        entity.setDeltaMovement(0, 1.5, 0); // 1.5 дает резкий толчок вверх примерно на 20 блоков
                        entity.hasImpulse = true;
                    }
                }
            }
        }
    }
}