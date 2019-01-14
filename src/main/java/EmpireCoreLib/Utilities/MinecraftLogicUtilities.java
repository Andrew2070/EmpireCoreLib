package EmpireCoreLib.Utilities;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MinecraftLogicUtilities {

	public static MinecraftServer getMinecraftServer() {
	return FMLCommonHandler.instance().getMinecraftServerInstance();
	}
}
