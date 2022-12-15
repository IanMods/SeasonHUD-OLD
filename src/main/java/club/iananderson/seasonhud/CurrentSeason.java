package club.iananderson.seasonhud;

import club.iananderson.seasonhud.config.SeasonHUDClientConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.extensions.IForgeMinecraft;
import net.minecraftforge.common.MinecraftForge;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CurrentSeason {
    //Currently implemented languages
    public static Locale getCurrentLocale(){
        Minecraft mc = Minecraft.getInstance();
        return mc.getLanguageManager().getSelected().getJavaLocale();
    }

    //Improve later, will work for now
    public static List<String> supportedLanguages(){
        List<String> language = new ArrayList<>();
        language.add("en_us");
        language.add("zh_cn");
        return language;
    }

    //Get the current season in Season type
    public static Season getCurrentSeason(){
        Minecraft mc = Minecraft.getInstance();
        return SeasonHelper.getSeasonState(Objects.requireNonNull(mc.level)).getSeason();
    }

    //Get the current season in SubSeason type
    public static Season.SubSeason getCurrentSubSeason(){
        Minecraft mc = Minecraft.getInstance();
        return SeasonHelper.getSeasonState(Objects.requireNonNull(mc.level)).getSubSeason();
    }

    public static Season.TropicalSeason getCurrentTropicalSeason(){
        Minecraft mc = Minecraft.getInstance();
        return SeasonHelper.getSeasonState(Objects.requireNonNull(mc.level)).getTropicalSeason();
    }

    //Get the current date of the season
    public static int getDate(){
        Minecraft mc = Minecraft.getInstance();
        return (SeasonHelper.getSeasonState(Objects.requireNonNull(mc.level)).getDay() % 8) + 1;
    }

    //Convert Season to lower case (for file names)
    public static String getSeasonLower(){
        return getCurrentSeason().name().toLowerCase();
    }

    //Convert to lower case (for file names)
    public static String getSubSeasonLower(){
        return getCurrentSubSeason().name().toLowerCase();
    }

    public static String getTropicalSeasonLowered(){
        return getCurrentTropicalSeason().name().toLowerCase();
    }

    public static boolean isTropicalSeason(){
        Minecraft mc = Minecraft.getInstance();
        return SeasonHelper.usesTropicalSeasons(Objects.requireNonNull(mc.level).getBiome(Objects.requireNonNull(mc.player).getOnPos()));
    }

   //Localized name for the hud
    public static ArrayList<Component> getSeasonName() {
        //System.out.println(getCurrentLocale());
        ArrayList<Component> text = new ArrayList<>();
        if (supportedLanguages().contains(getCurrentLocale().toString().toLowerCase())) {
            if (SeasonHUDClientConfigs.showSubSeason.get() && SeasonHUDClientConfigs.showDay.get()) {
                if (isTropicalSeason()) {
                    text.add(Component.translatable("desc.seasonhud.detailed", Component.translatable("desc.seasonhud." + getTropicalSeasonLowered()), getDate()));
                } else {
                    text.add(Component.translatable("desc.seasonhud.detailed", Component.translatable("desc.seasonhud." + getSubSeasonLower()), getDate()));
                }
            } else if (SeasonHUDClientConfigs.showSubSeason.get() && !SeasonHUDClientConfigs.showDay.get()) {
                if (isTropicalSeason()) {
                    text.add(Component.translatable("desc.seasonhud.summary", Component.translatable("desc.seasonhud." + getTropicalSeasonLowered())));
                } else {
                    text.add(Component.translatable("desc.seasonhud.summary", Component.translatable("desc.seasonhud." + getSubSeasonLower())));
                }
            } else {
                text.add(Component.translatable("desc.sereneseasons." + getSeasonLower()));
            }
        } else {
            text.add(Component.translatable("desc.sereneseasons." + getSeasonLower()));
        }
        return text;
    }
}

