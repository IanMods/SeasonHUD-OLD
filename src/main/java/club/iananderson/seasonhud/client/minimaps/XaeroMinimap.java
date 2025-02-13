package club.iananderson.seasonhud.client.minimaps;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import xaero.common.core.XaeroMinimapCore;
import xaero.common.gui.IScreenBase;

import java.util.ArrayList;
import java.util.Objects;

import static club.iananderson.seasonhud.impl.minimaps.CurrentMinimap.loadedMinimap;
import static club.iananderson.seasonhud.impl.opac.OpenPartiesAndClaims.inClaim;
import static club.iananderson.seasonhud.impl.sereneseasons.CurrentSeason.getSeasonName;
import static club.iananderson.seasonhud.impl.sereneseasons.CurrentSeason.getSeasonResource;
import static xaero.common.minimap.info.BuiltInInfoDisplays.*;
import static xaero.common.settings.ModOptions.modMain;

public class XaeroMinimap {
    public static final IGuiOverlay XAERO_SEASON = (ForgeGui, seasonStack, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation dim = Objects.requireNonNull(mc.level).dimension().location();
        int chunkX = mc.player.chunkPosition().x;
        int chunkZ = mc.player.chunkPosition().z;

        ArrayList<Component> underText = getSeasonName();

        if (loadedMinimap("xaerominimap") || loadedMinimap("xaerominimapfair")) {
            //Data
            float mapSize = XaeroMinimapCore.currentSession.getModMain().getSettings().getMinimapSize();//Minimap Size

            double scale = mc.getWindow().getGuiScale();

            float minimapScale = XaeroMinimapCore.currentSession.getModMain().getSettings().getMinimapScale();
            float mapScale = ((float) (scale / (double) minimapScale));
            float fontScale = 1 / mapScale;

            int padding = 9;

            float x = XaeroMinimapCore.currentSession.getModMain().getInterfaces().getMinimapInterface().getX();
            float y = XaeroMinimapCore.currentSession.getModMain().getInterfaces().getMinimapInterface().getY();
            float halfSize = mapSize / 2;

            float scaledX = (x * mapScale);
            float scaledY = (y * mapScale);

            boolean xBiome = BIOME.getState();
            boolean xDim = DIMENSION.getState();
            boolean xCoords = COORDINATES.getState();
            boolean xAngles = ANGLES.getState();
            boolean xWeather = WEATHER.getState();
            boolean xClaim = HIGHLIGHTS.getState();
            boolean XInClaim = inClaim(dim,chunkX,chunkZ);
            int xLight = LIGHT_LEVEL.getState();
            int xTime = TIME.getState();


            int trueCount = 0;
            if (xBiome) {trueCount++;}
            if (xDim) {trueCount++;}
            if (xCoords) {trueCount++;}
            if (xAngles) {trueCount++;}
            if (xWeather) {trueCount++;}
            if (xClaim && XInClaim) {trueCount++;}
            if (xLight > 0) {trueCount++;}
            if (xTime > 0) {trueCount++;}

            //Icon
            float stringWidth = mc.font.width(underText.get(0));
            float stringHeight = (mc.font.lineHeight) + 1;

            int iconDim = (int) stringHeight - 1;
            int offsetDim = 1;

            float totalWidth = (stringWidth + iconDim + offsetDim);

            int align = XaeroMinimapCore.currentSession.getModMain().getSettings().minimapTextAlign;
            float scaledHeight = (int) ((float) height * mapScale);
            boolean under = scaledY + mapSize / 2 < scaledHeight / 2;

            float center = (float) (padding - 0.5 + halfSize + iconDim + offsetDim - totalWidth / 2);
            float left = 6 + iconDim;
            float right = (int) (mapSize + 2 + padding - stringWidth);

            int stringX = (int)(scaledX + (align == 0 ? center : (align == 1 ? left : right)));
            int stringY = (int)(scaledY + (under ? mapSize + (2 * padding) : -9) + (trueCount * stringHeight * (under ? 1 : -1)));

            if ((!modMain.getSettings().hideMinimapUnderScreen || mc.screen == null || mc.screen instanceof IScreenBase || mc.screen instanceof ChatScreen || mc.screen instanceof DeathScreen)
                    && (!modMain.getSettings().hideMinimapUnderF3 || !mc.options.renderDebug) && modMain.getSettings().getMinimap()) {
                seasonStack.pose().pushPose();
                seasonStack.pose().scale(fontScale, fontScale, 1.0F);

                //Font
                for (Component s : underText) {
                    seasonStack.drawString(mc.font, s.getString(), stringX, stringY, -1);
                }

                underText.clear();

                //Icon
                ResourceLocation SEASON = getSeasonResource();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, SEASON);
                seasonStack.blit(SEASON, (int) (stringX - iconDim - offsetDim), (int) stringY, 0, 0, iconDim, iconDim, iconDim, iconDim);
                seasonStack.pose().popPose();
            }
        }

    };
}


