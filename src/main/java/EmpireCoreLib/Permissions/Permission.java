package EmpireCoreLib.Permissions;
import java.util.Collection;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.context.IContext;

public class Permission implements IPermissionHandler {
	
	public Permission() {
		
	}

	@Override
	public void registerNode(String node, DefaultPermissionLevel level, String desc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<String> getRegisteredNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPermission(GameProfile profile, String node, IContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getNodeDescription(String node) {
		// TODO Auto-generated method stub
		return null;
	}

}
