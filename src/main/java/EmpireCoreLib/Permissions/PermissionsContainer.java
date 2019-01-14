package EmpireCoreLib.Permissions;


import java.util.ArrayList;
import java.util.Iterator;

import EmpireCoreLib.Localization.LocalizationManager;
import net.minecraft.util.text.ITextComponent;



public class PermissionsContainer extends ArrayList<Node> {
	

	public SimplePermissionLevels hasPermission(String permission) {
		SimplePermissionLevels permLevel = SimplePermissionLevels.NONE;
		if (contains(permission)) {
			permLevel = SimplePermissionLevels.ALLOWED;
		}

		for (Node p : this) {
			
			if (p.fullNodeName().endsWith("*")) {
				if (permission.startsWith(p.fullNodeName().substring(0, p.fullNodeName().length() - 1))) {
					permLevel = SimplePermissionLevels.ALLOWED;
				} else if (p.getLocalizedName().startsWith("-") && permission.startsWith(p.fullNodeName().substring(1, p.fullNodeName().length() - 1))) {
					permLevel = SimplePermissionLevels.DENIED;
				}
			} else {
				if (permission.equals(p)) {
					permLevel = SimplePermissionLevels.ALLOWED;
				} else if (p.getLocalizedName().startsWith("-") && permission.equals(p.fullNodeName().substring(1))) {
					permLevel = SimplePermissionLevels.DENIED;
				}
			}
		}

		return permLevel;
	}

	public boolean remove(String perm) {
		for (Iterator<Node> it = iterator(); it.hasNext();) {
			Node p = it.next();
			if (p.equals(perm)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
}
