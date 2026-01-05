package com.alexey.levitationmod;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onKeyRegister);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onKeyRegister(RegisterKeyMappingsEvent event) {
        levitateKey = new KeyMapping("key.levitation.lift", GLFW.GLFW_KEY_X, "key.categories.misc");
        event.register(levitateKey);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && levitateKey != null && levitateKey.isDown()) {
                executeLevitation(mc);
            }
        }
    }

    private void executeLevitation(Minecraft mc) {
        if (mc.level == null || mc.player == null) return;

        // Радиус 15 блоков во все стороны
        double r = 15.0;
        AABB area = mc.player.getBoundingBox().inflate(r, r, r);
        List<Entity> entities = mc.level.getEntities(mc.player, area);

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                // Устанавливаем четкую скорость вверх (0.5 — это заметный прыжок)
                Vec3 currentMove = living.getDeltaMovement();
                living.setDeltaMovement(currentMove.x, 0.5, currentMove.z);
                
                // Чтобы моб не "зависал" из-за гравитации сервера, 
                // мы просто подталкиваем его каждый тик, пока нажата X
                living.hasImpulse = true;
            }
        }
    }
}
