package tf.ssf.sfort.oldregen;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tf.ssf.sfort.ini.SFIni;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

public class OlRegenConfig implements ModInitializer{
	public static Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "tf.ssf.sfort.oldregen";

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
		SFIni defIni = new SFIni();
        defIni.load(String.join("\n", new String[]{
                "; Duration in ticks between saturation based regeneration [80]",
                "saturation.delay=80",
                "; How much exhaustion should saturation regeneration cause [6.0]",
                "saturation.exhaustion=6",
                "; How much should saturation regeneration heal  [1.0]",
                "saturation.heal=1",
                "; Should saturation regen ignore natural regeneration gamerule [false] true | false",
                "saturation.ignoreGamerule=false",
                "; Amount of food required for saturation based regen [20]",
                "saturation.reqFood=20",
                "; Duration in ticks between food based regeneration [80]",
                "food.delay=80",
                "; How much exhaustion should food regeneration cause  [6.0]",
                "food.exhaustion=6",
                "; How much should food regeneration heal  [1.0]",
                "food.heal=1",
                "; Amount of food required for food based regen [18]",
                "food.reqFood=18",
                "; Duration in ticks between player taking damage from starvation [80]",
                "starvation.delay=80",
                "; Amount of damage taken for starving [1.0]",
                "starvation.damage=1",
                "; Should starvation instantly kill [false] true | false",
                "starvation.instantKill=false"
        }));
        legacyLoad(defIni);
        File confFile = new File(
                FabricLoader.getInstance().getConfigDir().toString(),
                "GoodOlRegen.sf.ini"
        );
        if (!confFile.exists()) {
            try {
                Files.write(confFile.toPath(), defIni.toString().getBytes());
                LOGGER.log(Level.INFO,MOD_ID+" successfully created config file");
                loadIni(defIni);
            } catch (IOException e) {
                LOGGER.log(Level.ERROR,MOD_ID+" failed to create config file, using defaults", e);
            }
            return;
        }
        try {
            SFIni ini = new SFIni();
            String text = Files.readString(confFile.toPath());
            int hash = text.hashCode();
            ini.load(text);
            for (Map.Entry<String, List<SFIni.Data>> entry : defIni.data.entrySet()) {
                List<SFIni.Data> list = ini.data.get(entry.getKey());
                if (list == null || list.isEmpty()) {
                    ini.data.put(entry.getKey(), entry.getValue());
                } else {
                    list.get(0).comments = entry.getValue().get(0).comments;
                }
            }
            loadIni(ini);
            String iniStr = ini.toString();
            if (hash != iniStr.hashCode()) {
                Files.write(confFile.toPath(), iniStr.getBytes());
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR,MOD_ID+" failed to load config file, using defaults", e);
        }
	}

    private void loadIni(SFIni ini) {
        setOrResetInt(ini, "saturation.delay", d->sat_delay=d, sat_delay);
        setOrResetDouble(ini, "saturation.exhaustion", d->sat_cost=d.floatValue(), sat_cost);
        setOrResetDouble(ini, "saturation.heal", d->sat_heal=d.floatValue(), sat_heal);
        setOrResetBool(ini, "saturation.ignoreGamerule", d->ignoreGamerules=d, ignoreGamerules);
        setOrResetInt(ini, "saturation.reqFood", d->sat_food_req=d, sat_food_req);
        setOrResetInt(ini, "food.delay", d->food_delay=d, food_delay);
        setOrResetDouble(ini, "food.exhaustion", d->food_cost=d.floatValue(), food_cost);
        setOrResetDouble(ini, "food.heal", d->food_heal=d.floatValue(), food_heal);
        setOrResetInt(ini, "food.reqFood", d->food_req=d, food_req);
        setOrResetInt(ini, "starvation.delay", d->starve_delay=d, starve_delay);
        setOrResetDouble(ini, "starvation.damage", d->starve_cost=d.floatValue(), starve_cost);
        setOrResetBool(ini, "starvation.instantKill", d->starve_kill=d, starve_kill);
		LOGGER.log(Level.INFO,MOD_ID+" finished loaded config file");
    }

    public static void setOrResetBool(SFIni ini, String key, Consumer<Boolean> set, boolean bool) {
        try {
            set.accept(ini.getBoolean(key));
        } catch (Exception e) {
            SFIni.Data data = ini.getLastData(key);
            if (data != null) data.val = Boolean.toString(bool);
            LOGGER.log(Level.ERROR,MOD_ID+" failed to load "+key+", setting to default value", e);
        }
    }
    public static void setOrResetDouble(SFIni ini, String key, Consumer<Double> set, double bool) {
        try {
            set.accept(ini.getDouble(key));
        } catch (Exception e) {
            SFIni.Data data = ini.getLastData(key);
            if (data != null) data.val = Double.toString(bool);
            LOGGER.log(Level.ERROR,MOD_ID+" failed to load "+key+", setting to default value", e);
        }
    }
    public static void setOrResetInt(SFIni ini, String key, Consumer<Integer> set, int bool) {
        try {
            set.accept(ini.getInt(key));
        } catch (Exception e) {
            SFIni.Data data = ini.getLastData(key);
            if (data != null) data.val = Integer.toString(bool);
            LOGGER.log(Level.ERROR,MOD_ID+" failed to load "+key+", setting to default value", e);
        }
    }

    public static void legacyLoad(SFIni inIni){
        Map<String, String> oldConf = new HashMap<>();
		File confFile = new File(
				FabricLoader.getInstance().getConfigDir().toString(),
				"GoodOlRegen.conf"
		);
        if (!confFile.exists()) return;
		try {
			List<String> la = Files.readAllLines(confFile.toPath());
			String[] ls = la.toArray(new String[Math.max(la.size(), 24)|1]);
			try{ oldConf.put("saturation.delay", Integer.toString(Math.max(Integer.parseInt(ls[0]),0)));}catch (Exception ignored){}
			try{ oldConf.put("saturation.exhaustion", Float.toString(Math.max(Float.parseFloat(ls[2]),0.0F)));}catch (Exception ignored){}
			try{ oldConf.put("saturation.heal", Float.toString(Math.max(Float.parseFloat(ls[4]),0.0F)));}catch (Exception ignored){}
			try{ oldConf.put("saturation.ignoreGamerule", Boolean.toString(ls[6].contains("true")));}catch (Exception ignore){}
			try{ oldConf.put("saturation.reqFood", Integer.toString(Math.max(Integer.parseInt(ls[8]),0)));}catch (Exception ignored){}
			try{ oldConf.put("food.delay", Integer.toString(Math.max(Integer.parseInt(ls[10]),0)));}catch (Exception ignored){}
			try{ oldConf.put("food.exhaustion", Float.toString(Math.max(Float.parseFloat(ls[12]),0.0F)));}catch (Exception ignored){}
			try{ oldConf.put("food.heal", Float.toString(Math.max(Float.parseFloat(ls[14]),0.0F)));}catch (Exception ignored){}
			try{ oldConf.put("food.reqFood", Integer.toString(Math.max(Integer.parseInt(ls[16]),0)));}catch (Exception ignored){}
			try{ oldConf.put("starvation.delay", Integer.toString(Math.max(Integer.parseInt(ls[18]),0)));}catch (Exception ignored){}
			try{ oldConf.put("starvation.damage", Float.toString(Math.max(Float.parseFloat(ls[20]),0.0F)));}catch (Exception ignored){}
			try{ oldConf.put("starvation.instantKill", Boolean.toString(ls[22].contains("true"))); }catch (Exception ignore){}

            for (Map.Entry<String, String> entry : oldConf.entrySet()) {
                SFIni.Data data = inIni.getLastData(entry.getKey());
                if (data != null) {
                    data.val = entry.getValue();
                }
            }

            Files.delete(confFile.toPath());
			LOGGER.log(Level.INFO,"tf.ssf.sfort.oldregen successfully loaded legacy config file");
		} catch(Exception e) {
			LOGGER.log(Level.ERROR,"tf.ssf.sfort.oldregen failed to load legacy config file\n"+e);
		}
	}
}