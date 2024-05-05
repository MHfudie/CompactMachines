package dev.compactmods.machines.client;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class PlayerFaceRenderer {
    public static final GameProfile EMPTY_PROFILE = new GameProfile(Util.NIL_UUID, "Empty Player");

    public static void render(GameProfile profile, GuiGraphics graphics, PoseStack poseStack, int x, int y) {
        final var skins = Minecraft.getInstance().getSkinManager();
        final var playerSkin = skins.getInsecureSkin(profile);

        // pose, x, y, ???, hatLayer, upsideDown
        net.minecraft.client.gui.components.PlayerFaceRenderer.draw(graphics, playerSkin.texture(), x, y, 12, false, false);
    }
}
