package EmpireCoreLib.Localization;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import EmpireCoreLib.EmpireCoreLib;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Loads and handles Localization files
 */
public class Localization {
	public static final String defaultLocalization = "en_US";

	private Map<String, String> localizations = new HashMap<String, String>();
	private String filePath;
	private String lang;
	private String classPath;
	private Class clazz;

	public Localization(String filePath, String lang, String classPath, Class clazz) {
		this.filePath = filePath;
		this.lang = lang;
		this.classPath = classPath;
		this.clazz = clazz;
		EmpireCoreLib.logger.info(filePath);
		EmpireCoreLib.logger.info(lang);
		EmpireCoreLib.logger.info(classPath);
		EmpireCoreLib.logger.info(clazz.toString());
		
		load();
	}

	private Reader getReader() throws FileNotFoundException {
		InputStream is = null;

		if (filePath != null) {
			File file = new File(filePath + lang + ".lang");
			if (file.exists() && !file.isDirectory()) {
				is = new FileInputStream(file);
				EmpireCoreLib.logger.info("IF Statement triggered on line 49");
			}
		}
		try {
		if (is == null) {
			is = clazz.getResourceAsStream(classPath + lang + ".lang");
			EmpireCoreLib.logger.info("Reverting to en_US.lang because " + lang + ".lang does not exist");

		}
		} catch (NullPointerException e) {
			is = clazz.getResourceAsStream(classPath + defaultLocalization + ".lang");
			
		}

		return new InputStreamReader(is);
	}

	/**
	 * Do the actual loading of the Localization file
	 */
	public void load() {
		localizations.clear();

		try {
			BufferedReader br = new BufferedReader(getReader());
			String line;

			while ((line = br.readLine()) != null) {
				line = line.trim(); // Trim it in-case there is spaces before
									// the actual key-value pairs
				String[] entry = line.split("=");
				if (line.startsWith("#") || line.isEmpty() || entry.length < 2) {
					// Ignore entries that are not formatted correctly (maybe
					// log later)
					// Ignore comments and empty lines
					continue;
				}

				localizations.put(entry[0].trim(), entry[1].trim());
			}
			br.close();
		} catch (IOException ex) {
			EmpireCoreLib.logger.info("Failed to load localization file!");
			EmpireCoreLib.logger.info(ExceptionUtils.getStackTrace(ex));
		}
	}

	public ITextComponent getLocalization(String key, Object... args) {
		return new TextComponentTranslation(key, args);
	}

	public boolean hasLocalization(String key) {
		return localizations.containsKey(key);
	}

	public Map<String, String> getLocalizationMap() {
		return localizations;
	}
}