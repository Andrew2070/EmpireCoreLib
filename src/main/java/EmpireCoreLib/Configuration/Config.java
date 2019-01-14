package EmpireCoreLib.Configuration;

/**
 * @author Andrew2070
 *
 *   Configuration Template:
 *   
 *   [EX] Configuration Example:
 *
 *	 public ConfigProperty<String/Int/Double/Long/Boolean> Name = new ConfigProperty<String/Int/Double/Long/Boolean>(
 *				"Configuration Display Name", "Main Category",
 *				"Description of Configuration Property.",
 *				"Default Value");
 *
 *	Steps:
 *	1. Specify Object Type
 *	2. Specify Name
 *	3. Initialize ConfigProperty, Specify Object Type Again
 *	4. Specify Name t appears in configuration
 *	5. Specify Main Category
 *  6. Specify Description
 *  7. Specify Default Value (if Boolean then true/false, if String then "String", if Double then "0.0")
 *
 *
 *
 */
public class Config extends ConfigTemplate {

    public static final Config instance = new Config();
      
    public ConfigProperty<String> localization = new ConfigProperty<String>(
            "localization", "localization",
            "The localization file used, currently we only support English.",
            "en_US");
    
    public ConfigProperty<String> Database = new ConfigProperty<String>(
            "dbName", "database",
            "The Name Of The Database",
            "EmpiresCoreDB");
    
    public ConfigProperty<String> DBType = new ConfigProperty<String>(
            "dbType", "database",
            "Database Type, Supported Options: MySQL, SQLite, SQL",
            "SQLite");
    
    public ConfigProperty<String> DBHost = new ConfigProperty<String>(
            "dbHost", "database",
            "Database Host",
            "Host");
    
    public ConfigProperty<String> DBPath = new ConfigProperty<String>(
            "dbPath", "database",
            "Alternative Database Path",
            "dbPath");
    
    public ConfigProperty<String> DBUsername = new ConfigProperty<String>(
            "dbUsername", "database",
            "Database Username",
            "username");
    
    public ConfigProperty<String> DBPassword = new ConfigProperty<String>(
            "dbPassword", "database",
            "Database Password",
            "password");
    
    public ConfigProperty<String> permissionSystem = new ConfigProperty<String>(
    		"permissionSystem", "Permissions",
    		"Specify Permission System To Be Used, Currently no other modular support, don't touch unless you know what you're doing.",
    		"$Empires");

	public ConfigProperty<Boolean> fullAccessForOPS = new ConfigProperty<Boolean>(
			"fullAccessForOps", "Permissions",
			"Specify Whether Ops get Full Access",
			true);
	
	public ConfigProperty<String> defaultGroup = new ConfigProperty<String>(
			"defaultGroupName", "Permissions",
			"Specify Default Group Name",
			"default");
	
	
			
			
    
    
}