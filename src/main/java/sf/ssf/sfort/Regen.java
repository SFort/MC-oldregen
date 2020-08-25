package sf.ssf.sfort;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class Regen implements ModInitializer{
    public static int delay = 80;
    public static float cost = 1.5F;
    public static float heal = 1.0F;
    public static boolean ignoreGamerules = false;
    
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
                    "^-Duration in ticks between regeneration [80]",
                    "^-How much exhaustion should regeneration cause  [1.5]",
                    "^-How much should regeneration heal  [1.0]",
                    "^-Ignore natural regeneration gamerule [false] true | false"
            );
            String[] init =new String[Math.max(la.size(), defaultDesc.size() * 2)];
            Arrays.fill(init,"");
            String[] ls = la.toArray(init);
            for (int i = 0; i<defaultDesc.size();++i)
                ls[i*2+1]= defaultDesc.get(i);

            try{ delay =Math.max(Integer.parseInt(ls[0]),0);}catch (NumberFormatException ignored){}
            ls[0] = String.valueOf(delay);

            try{ cost = Math.max(Float.parseFloat(ls[2]),0.0F);}catch (NumberFormatException ignored){}
            ls[2] = String.valueOf(cost);

            try{ heal = Math.max(Float.parseFloat(ls[4]),0.0F);}catch (NumberFormatException ignored){}
            ls[4] = String.valueOf(heal);

            ignoreGamerules = ls[6].contains("true");
            ls[6] = String.valueOf(ignoreGamerules);

            if(ls.length>defaultDesc.size()*2){
                for (int i = (defaultDesc.size()*2)+1; i<=ls.length;i+=2){
                    ls[i] = "!#Unknown value / config from the future";
                }
            }
            Files.write(confFile.toPath(), Arrays.asList(ls));
            System.out.println("tf.ssf.sfort.eternaleats successfully loaded config file");
        } catch(Exception e) {
            System.out.println("tf.ssf.sfort.eternaleats failed to load config file, using defaults\n"+e);
        }
    }
}