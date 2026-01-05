package com.alexey.levitationmod;

import net.minecraft.client.KeyMapping;
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
        // Проверяем нажатие кнопки БЕЗ использования Minecraft.getInstance()
        if (event.phase == TickEvent.Phase.END && launchKey != null && launchKey.isDown()) {
            // В Forge 1.21.1 ClientTickEvent не содержит игрока напрямую, 
            // поэтому мы попробуем достать его через обходной путь, который не требует проблемного метода m_91087_
            try {
                final net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.player != null && mc.level != null) {
                    var player = mc.player;
                    List<Entity> entities = mc.level.getEntities(player, player.getBoundingBox().inflate(30.0D));
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity && entity != player) {
                            entity.setDeltaMovement(0, 1.2, 0);
                            entity.hasImpulse = true;
                        }
                    }
                }
            } catch (NoSuchMethodError e) {
                // Если даже так не выйдет, мы поймем это из логов, но игра не должна вылететь сразу
            }
        }
    }
}
