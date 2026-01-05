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
    // Проверяем только конец тика и наличие игрока
    if (event.phase != TickEvent.Phase.END) return;
    
    Minecraft mc = Minecraft.getInstance();
    if (mc.player == null || mc.level == null) return;

    // Полностью убираем проверку на активное окно (mc.isWindowActive)
    // Оставляем только проверку клавиши. 
    // Если окно не в фокусе, клавиша обычно и так не нажмется.
    if (levitateKey != null && levitateKey.isDown()) {
        double r = 10.0;
        AABB area = mc.player.getBoundingBox().inflate(r);
        List<Entity> entities = mc.level.getEntities(mc.player, area);

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living && entity != mc.player) {
                Vec3 v = living.getDeltaMovement();
                living.setDeltaMovement(v.x, 0.5, v.z);
                living.hasImpulse = true;
            }
        }
    }
}
