package sf.ssf.sfort;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class Regen implements ModInitializer{
    public static Logger LOGGER = LogManager.getLogger();

    public static int food_delay = 80;
    public static float food_cost = 6.0F;
    public static float food_heal = 1.0F;
    public static int food_req = 18;
    public static int sat_food_req = 20;
    public static int sat_delay = 80;
    public static float sat_cost = 6.0F;
    public static float sat_heal = 1.0F;
    public static boolean ignoreGamerules = false;
    public static int starve_delay = 80;
    public static float starve_cost = 1.0F;
    public static boolean starve_kill = false;
    
    @Override
    public void onInitialize() {
        // Configs
        File confFile = new File(
                FabricLoader.getInstance().getConfigDir().toString(),
                "GoodOlRegen.conf"
        );
        try {
            confFile.createNewFile();
            List<String> la = Files.readAllLines(confFile.toPath());
            List<String> defaultDesc = Arrays.asList(
                    "^-Duration in ticks between saturation based regeneration [80]",
                    "^-How much exhaustion should saturation regeneration cause  [6.0]",
                    "^-How much should saturation regeneration heal  [1.0]",
                    "^-Should saturation regen ignore natural regeneration gamerule [false] true | false",
                    "^-Amount of food required for saturation based regen [20]",
                    "^-Duration in ticks between food based regeneration [80]",
                    "^-How much exhaustion should food regeneration cause  [6.0]",
                    "^-How much should food regeneration heal  [1.0]",
                    "^-Amount of food required for food based regen [18]",
                    "^-Duration in ticks between player taking damage from starvation [80]",
                    "^-Amount of damage taken for starving [1.0]",
                    "^-Should starvation instantly kill [false] true | false"
            );
            String[] init =new String[Math.max(la.size(), defaultDesc.size() * 2)|1];
            String[] ls = la.toArray(init);
            for (int i = 0; i<defaultDesc.size();++i)
                ls[i*2+1]= defaultDesc.get(i);

            try{ sat_delay =Math.max(Integer.parseInt(ls[0]),0);}catch (Exception ignored){}
            ls[0] = String.valueOf(sat_delay);

            try{ sat_cost = Math.max(Float.parseFloat(ls[2]),0.0F);}catch (Exception ignored){}
            ls[2] = String.valueOf(sat_cost);

            try{ sat_heal = Math.max(Float.parseFloat(ls[4]),0.0F);}catch (Exception ignored){}
            ls[4] = String.valueOf(sat_heal);

            try{ignoreGamerules = ls[6].contains("true");}catch (Exception ignore){}
            ls[6] = String.valueOf(ignoreGamerules);

            try{ sat_food_req = Math.max(Integer.parseInt(ls[8]),0);}catch (Exception ignored){}
            ls[8] = String.valueOf(sat_food_req);

            try{ food_delay =Math.max(Integer.parseInt(ls[10]),0);}catch (Exception ignored){}
            ls[10] = String.valueOf(food_delay);

            try{ food_cost = Math.max(Float.parseFloat(ls[12]),0.0F);}catch (Exception ignored){}
            ls[12] = String.valueOf(food_cost);

            try{ food_heal = Math.max(Float.parseFloat(ls[14]),0.0F);}catch (Exception ignored){}
            ls[14] = String.valueOf(food_heal);

            try{ food_req = Math.max(Integer.parseInt(ls[16]),0);}catch (Exception ignored){}
            ls[16] = String.valueOf(food_req);

            try{ starve_delay = Math.max(Integer.parseInt(ls[18]),0);}catch (Exception ignored){}
            ls[18] = String.valueOf(starve_delay);

            try{ starve_cost = Math.max(Float.parseFloat(ls[20]),0.0F);}catch (Exception ignored){}
            ls[20] = String.valueOf(starve_cost);

            try { starve_kill = ls[22].contains("true"); }catch (Exception ignore){}
            ls[22] = String.valueOf(starve_kill);

            Files.write(confFile.toPath(), Arrays.asList(ls));
            LOGGER.log(Level.INFO,"tf.ssf.sfort.oldregen successfully loaded config file");
        } catch(Exception e) {
            LOGGER.log(Level.ERROR,"tf.ssf.sfort.oldregen failed to load config file, using defaults\n"+e);
        }
    }
}