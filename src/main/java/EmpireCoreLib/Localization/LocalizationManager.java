package EmpireCoreLib.Localization;


import java.util.HashMap;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;

/**
 * Centralized localization access
 */
public class LocalizationManager {

	private static Map<String, Localization> localizations = new HashMap<String, Localization>();

	/**
	 * Registers a localization file to be used globally The key string should
	 * be the first part of any localization key that is found in the file
	 */
	public static void register(Localization local, String key) {
		localizations.put(key, local);
	}

	/**
	 * Finds the localized version that the key is pointing at and sends it to
	 * the ICommandSender
	 */
	public static void send(ICommandSender sender, String localizationKey, Object... args) {
		sender.sendMessage(get(localizationKey, args));
	}

	public static ITextComponent get(String localizationKey, Object... args) {
		Localization local = localizations.get(localizationKey.split("\\.")[0]);
		return local.getLocalization(localizationKey, args);
	}
}