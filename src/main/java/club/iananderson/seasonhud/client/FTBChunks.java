package club.iananderson.seasonhud.client;

import club.iananderson.seasonhud.SeasonHUD;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.ftb.mods.ftbchunks.FTBChunksWorldConfig;
import dev.ftb.mods.ftbchunks.client.FTBChunksClientConfig;
import dev.ftb.mods.ftbchunks.client.MinimapPosition;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.client.map.MapManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;

import static club.iananderson.seasonhud.CurrentSeason.getSeasonLower;
import static club.iananderson.seasonhud.CurrentSeason.getSeasonName;

/* Todo
    * Switch names over to translatable ones
 */

public class FTBChunks {
    public static boolean ftbChunksLoaded() {
        return ModList.get().isLoaded("ftbchunks");
    }

    public static void renderFtbHUD(Minecraft mc, MatrixStack seasonStack){
        //Season Name
        String seasonName = getSeasonName();
        TranslationTextComponent MINIMAP_TEXT_SEASON = new TranslationTextComponent(seasonName);

        if (ftbChunksLoaded()) {
            boolean biome = FTBChunksClientConfig.MINIMAP_BIOME.get();
            boolean xyz = FTBChunksClientConfig.MINIMAP_XYZ.get();

            int i = 0;

            if(biome){
                i++;
            }
            if(xyz){
                i++;
            }

            //Icon chooser
            ResourceLocation SEASON = new ResourceLocation(SeasonHUD.MODID, "textures/season/" + getSeasonLower() + ".png");

            if (mc.player != null && mc.level != null && MapManager.inst != null) {
                double guiScale = mc.getWindow().getGuiScale();
                int ww = mc.getWindow().getGuiScaledWidth();
                int wh = mc.getWindow().getGuiScaledHeight();
                MapDimension dim = MapDimension.getCurrent();
                if (dim != null) {
                    if (dim.dimension != mc.level.dimension()) {
                        MapDimension.updateCurrent();
                    }

                    if (!mc.options.renderDebug && (Boolean) FTBChunksClientConfig.MINIMAP_ENABLED.get() && (Integer) FTBChunksClientConfig.MINIMAP_VISIBILITY.get() != 0 && !(Boolean) FTBChunksWorldConfig.FORCE_DISABLE_MINIMAP.get()) {
                        float scale = (float) ((Double) FTBChunksClientConfig.MINIMAP_SCALE.get() * 4.0 / guiScale);
                        int s = (int) (64.0 * (double) scale);
                        double s2d = (double) s / 2.0;
                        MinimapPosition minimapPosition = (MinimapPosition) FTBChunksClientConfig.MINIMAP_POSITION.get();
                        int x = minimapPosition.getX(ww, s);
                        int y = minimapPosition.getY(wh, s);
                        //float offsetX = (Integer) FTBChunksClientConfig.MINIMAP_POSITION.get();
                        //int offsetY = (Integer) FTBChunksClientConfig.MINIMAP_OFFSET_Y.get();

                        //MinimapPosition.MinimapOffsetConditional offsetConditional = (MinimapPosition.MinimapOffsetConditional) FTBChunksClientConfig.MINIMAP_POSITION_OFFSET_CONDITION.get();

                        //if (offsetConditional.isNone() || offsetConditional.getPosition() == minimapPosition) {
                            //x += minimapPosition.posX == 0 ? offsetX : -offsetX;
                            //y -= minimapPosition.posY > 1 ? offsetY : -offsetY;
                        //}


                        seasonStack.pushPose();
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        seasonStack.translate((double)x + s2d, (double)(y + s) + 3.0, 0.0);
                        seasonStack.scale((float)(0.5 * (double)scale), (float)(0.5 * (double)scale), 1.0F);


                        int bsw = mc.font.width(MINIMAP_TEXT_SEASON);
                        int iconDim = mc.font.lineHeight;

                        //Font
                        mc.font.drawShadow(seasonStack, MINIMAP_TEXT_SEASON, (float)((-bsw) + iconDim/2)/ 2.0F, (float)(i * 11), -1);

                        //Icon
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                        mc.getTextureManager().bind(SEASON);
                        AbstractGui.blit(seasonStack,(int)((-bsw) / 2.0F)-iconDim, (int)(i * 11), 0, 0, iconDim, iconDim, iconDim, iconDim);
                        mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
                        RenderSystem.disableBlend();
                        seasonStack.popPose();
                    }
                }
            }
        }
    };
}

