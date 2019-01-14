package EmpireCoreLib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Maps;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import EmpireCoreLib.Commands.Client.CoreCommands;
import EmpireCoreLib.Commands.Engine.CommandManager;
import EmpireCoreLib.Configuration.Config;
import EmpireCoreLib.JSON.JsonConfig;
import EmpireCoreLib.Localization.Localization;
import EmpireCoreLib.Localization.LocalizationManager;
import EmpireCoreLib.Permissions.EmpireCoreBridge;
import EmpireCoreLib.Permissions.PermissionManager;
import EmpireCoreLib.Proxies.PermissionProxy;
import EmpireCoreLib.Utilities.LogFormatter;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = EmpireCoreLib.MODID, name = "EmpiresCore", version = EmpireCoreLib.VERSION, dependencies = "after:worldedit", updateJSON = EmpireCoreLib.UPDATEURL, acceptableRemoteVersions = "*", acceptedMinecraftVersions = EmpireCoreLib.MCVERSIONS)
public class EmpireCoreLib
{
	@Mod.Instance(EmpireCoreLib.MODID)
	public static 						   EmpireCoreLib 		   instance;
    public static final 				   String              MODID              = "empirecore";
    public static final 				   String              VERSION            = "1.12.2";
    public static final 				   String              UPDATEURL          = "";
    public final static 				   String              MCVERSIONS         = "[1.9.4, 1.13]";
    public 								   Localization		   LOCAL;
    public static boolean                  allCommandUse      					  = false;
    public static 						   File                configFile         = null;
    public static 						   File                jsonFile           = null;
    public static 						   String 			   CONFIG_FOLDER 	  = "";
	public static						   String 			   DATABASE_FOLDER 	  = "";
    public static 					       Logger              logger             = Logger.getLogger(MODID);
    public static boolean                  debug             					  = false;
    public static Map<String, String>      customCommandPerms 					  = Maps.newHashMap();
    public static boolean                  wrapOpCommands     					  = true;
    private final List<JsonConfig> 		   jsonConfigs 							  = new ArrayList<JsonConfig>();

    static ExclusionStrategy               exclusion          = new ExclusionStrategy()
                                                              {
                                                                  @Override
                                                                  public boolean shouldSkipField(FieldAttributes f)
                                                                  {
                                                                      String name = f.getName();
                                                                      return name.startsWith("_");
                                                                  }

                                                                  @Override
                                                                  public boolean shouldSkipClass(Class<?> clazz)
                                                                  {
                                                                      return false;
                                                                  }
                                                              };

    public EmpireCoreLib() {
        initLogger();
    }

    private void initLogger() {
        FileHandler logHandler = null;
        logger.setLevel(Level.ALL);
        try
        {
            File logs = new File("." + File.separator + "logs");
            logs.mkdirs();
            File logfile = new File(logs, MODID + ".log");
            if ((logfile.exists() || logfile.createNewFile()) && logfile.canWrite() && logHandler == null)
            {
                logHandler = new FileHandler(logfile.getPath());
                logHandler.setFormatter(new LogFormatter());
                logger.addHandler(logHandler);
            }
        }
        catch (SecurityException | IOException e)
        {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
    	//CONFIGURATION:
    	CONFIG_FOLDER = e.getModConfigurationDirectory().getPath() + "/EmpireCoreLib/";
		DATABASE_FOLDER = e.getModConfigurationDirectory().getParent() + "/databases/";
    	Config.instance.init(CONFIG_FOLDER + "/EmpireCoreLib.cfg", "EmpireCoreLib");
    	LOCAL = new Localization(CONFIG_FOLDER + "/Localization/", Config.instance.localization.get(),
				"/EmpireCoreLib/Localization/", EmpireCoreLib.class);
    	LocalizationManager.register(LOCAL, "EmpireCoreLib");
    	
    	
    	
    }
    
    public void loadConfig() {
		Config.instance.reload();
		PermissionProxy.init();
		LOCAL.load();


		for (JsonConfig jsonConfig : jsonConfigs) {
			jsonConfig.init();
		}
	}

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
    	loadConfig();
    	logger.info("EmpireCore Started");
    	if (PermissionProxy.getPermissionManager() instanceof EmpireCoreBridge) {
			CommandManager.registerCommands(PermissionManager.class, "Empires.cmd", EmpireCoreLib.instance.LOCAL);

		}
    	
    	for (JsonConfig jsonConfig : jsonConfigs) {
			jsonConfig.init();
		}
    }
    
    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
    	
    }
    
    private void registerCommands() {
		CommandManager.registerCommands(CoreCommands.class, "EmpireCore", LOCAL);
	}


    public static void loadPerms() {
    
    }

    public static void savePerms() {
       
    }

}
