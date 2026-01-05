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
        // Используем фазу END и проверяем наличие игрока максимально просто
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.player != null && levitateKey != null) {
                if (levitateKey.isDown()) {
                    applyLevitation(mc);
                }
            }
        }
    }

    private void applyLevitation(Minecraft mc) {
        if (mc.level == null || mc.player == null) return;

        // Радиус поиска — 10 блоков вокруг игрока
        double radius = 10.0;
        AABB area = mc.player.getBoundingBox().inflate(radius);
        List<Entity> entities = mc.level.getEntities(mc.player, area);

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living && entity != mc.player) {
                // Получаем текущее движение
                Vec3 movement = living.getDeltaMovement();
                // Устанавливаем вертикальную скорость 0.3 (заметный подъем)
                living.setDeltaMovement(movement.x, 0.3, movement.z);
                // Помечаем, что сущность получила импульс (важно для некоторых мобов)
                living.hasImpulse = true;
            }
        }
    }
}
