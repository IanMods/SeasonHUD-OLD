package club.iananderson.seasonhud.client;

import club.iananderson.seasonhud.SeasonHUD;
import com.mojang.blaze3d.systems.RenderSystem;
import journeymap.client.JourneymapClient;
import journeymap.client.io.ThemeLoader;
import journeymap.client.render.draw.DrawUtil;
import journeymap.client.ui.UIManager;
import journeymap.client.ui.minimap.MiniMap;
import journeymap.client.ui.theme.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;

import static club.iananderson.seasonhud.data.CurrentSeason.*;

public class JourneyMap {

    public static boolean journeymapLoaded() {return ModList.get().isLoaded("journeymap");}

    public static final IIngameOverlay JOURNEYMAP_SEASON = (ForgeGui, seasonStack, partialTick, scaledWidth, scaledHeight) -> {
        Minecraft mc = Minecraft.getInstance();

        if (journeymapLoaded()) {
            Theme.LabelSpec label = new Theme.LabelSpec();


            JourneymapClient jm = JourneymapClient.getInstance();
            Font fontRenderer = mc.font;
            MiniMap minimap = UIManager.INSTANCE.getMiniMap();

            String emptyLabel = "jm.theme.labelsource.blank";
            String info3Label = jm.getActiveMiniMapProperties().info3Label.get();
            String info4Label = jm.getActiveMiniMapProperties().info4Label.get();
            ArrayList<Component> MINIMAP_TEXT_SEASON= getSeasonName();

            float fontScale = jm.getActiveMiniMapProperties().fontScale.get();
            float guiSize = (float) mc.getWindow().getGuiScale();


            boolean fontShadow = label.shadow;

            double labelHeight = ((DrawUtil.getLabelHeight(fontRenderer, fontShadow)) * (fontScale));
            double labelWidth = fontRenderer.width(MINIMAP_TEXT_SEASON.get(0))*fontScale;

            int minimapHeight = minimap.getDisplayVars().minimapHeight;


            int halfHeight = minimapHeight / 2;

            Theme.LabelSpec currentTheme = ThemeLoader.getCurrentTheme().minimap.square.labelBottom;
            int labelColor = currentTheme.background.getColor();
            int textColor = currentTheme.foreground.getColor();
            float labelAlpha = currentTheme.background.alpha;
            float textAlpha = currentTheme.foreground.alpha;
            int frameWidth = ThemeLoader.getCurrentTheme().minimap.square.right.width/2;

            int infoLabelCount = 0;
            if (!info3Label.equals(emptyLabel)) {
                infoLabelCount++;
            }
            if (!info4Label.equals(emptyLabel)) {
                infoLabelCount++;
            }

            int vPad = (int) (((labelHeight/fontScale) - 9) / 2.0);
            double bgHeight = (labelHeight * infoLabelCount) + (vPad) + frameWidth;


            //Icon chooser
            int iconDim = (int) (mc.font.lineHeight*fontScale);
            double labelPad = 1*fontScale;
            double totalIconSize = (iconDim+(labelPad));


            ResourceLocation SEASON = new ResourceLocation(SeasonHUD.MODID,
                        "textures/season/" + getSeasonFileName() + ".png");

            //Values
            if (!mc.isPaused()) {
                seasonStack.pushPose();
                seasonStack.scale(1 / guiSize, 1 / guiSize, 1.0F);

                double textureX = minimap.getDisplayVars().centerPoint.getX();
                double textureY = minimap.getDisplayVars().centerPoint.getY();
                double translateX = totalIconSize/2;
                double translateY = halfHeight + bgHeight+(fontScale < 1.0 ? 0.5 : 0.0);


                double labelX = (textureX + translateX);
                double labelY = (textureY + translateY);

                double totalRectWidth = labelWidth+totalIconSize;
                double iconRectX = (float)(textureX-Math.max(1.0,totalRectWidth)/2-(fontScale > 1.0 ? 0.0 : 0.5));
                //basically half the label width from the center

                //double labelIconX = textureX;
                double labelIconX = (float)(textureX - totalRectWidth / 2.0 - (fontScale > 1.0 ? 0.0 : 0.5));
                    //half the label width
                double labelIconY = labelY+(labelHeight/2)-(iconDim/2.0);
                    //moves the icon to  the vertical center of the label

                for (Component s : MINIMAP_TEXT_SEASON) {
                    DrawUtil.drawLabel(seasonStack, s.getString(), labelX, labelY, DrawUtil.HAlign.Center, DrawUtil.VAlign.Below, labelColor, labelAlpha, textColor, textAlpha, fontScale, fontShadow);
                    //No touchy. Season label offset by icon+padding
                }

                DrawUtil.drawRectangle(seasonStack,iconRectX-(2*labelPad),labelY,totalRectWidth-labelWidth,labelHeight,labelColor,labelAlpha);
                    //Rectangle for the icon

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, SEASON);
                GuiComponent.blit(seasonStack,(int)(labelIconX),(int)(labelIconY),0,0,iconDim,iconDim,iconDim,iconDim);
                //DrawUtil.drawImage(seasonStack, 0,labelX,labelY,false,fontScale,0);
                seasonStack.popPose();
            }
        }
    };
}