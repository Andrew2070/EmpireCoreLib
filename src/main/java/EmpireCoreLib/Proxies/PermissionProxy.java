package EmpireCoreLib.Proxies;

import EmpireCoreLib.EmpireCoreLib;
import EmpireCoreLib.Configuration.Config;
import EmpireCoreLib.Permissions.EmpireCoreBridge;
import EmpireCoreLib.Permissions.IPermissionBridge;

public class PermissionProxy {
	public static final String PERM_SYSTEM_EMPIRES = "$Empires";

	private static IPermissionBridge permissionManager;

	public static IPermissionBridge getPermissionManager() {
		if (permissionManager == null) {
			init();
		}
		return permissionManager;
	}

	public static void init() {

		if (Config.instance.permissionSystem.get().equals(PERM_SYSTEM_EMPIRES)) {
			permissionManager = new EmpireCoreBridge();
			((EmpireCoreBridge) permissionManager).loadConfigs();
			EmpireCoreLib.logger.info("Currently using built-in permission system.");
			EmpireCoreLib.logger.info("This is not fully functional and only works for mods that use this API.");
			EmpireCoreLib.logger.info("If you have Bukkit or ForgeEssentials installed please use that instead.");
		}
		}
	}