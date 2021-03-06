package EmpireCoreLib.Datasource.Bridge;


import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.sqlite.JDBC;

import EmpireCoreLib.EmpireCoreLib;
import EmpireCoreLib.Configuration.Config;
import EmpireCoreLib.Configuration.ConfigProperty;
import EmpireCoreLib.Configuration.ConfigTemplate;

public class BridgeSQLite extends BridgeSQL {

	public ConfigProperty<String> dbPath = Config.instance.DBPath;

	public BridgeSQLite(ConfigTemplate config) {
		dbPath.set(EmpireCoreLib.DATABASE_FOLDER + config.getModID() + "/data.db");
		config.addBinding(dbPath, true);
		initProperties();
		initConnection();
	}

	@Override
	protected void initConnection() {
		File file = new File(dbPath.get());
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		this.dsn = "jdbc:sqlite:" + dbPath.get();

		try {
			DriverManager.registerDriver(new JDBC());
		} catch (SQLException ex) {
			EmpireCoreLib.logger.info("Failed to register driver for SQLite database." + " exception: " + ex);
		}

		try {
			if (conn != null && !conn.isClosed()) {
				try {
					conn.close();
				} catch (SQLException ex) {
				} // Ignore since we are just closing an old connection
				conn = null;
			}

			conn = DriverManager.getConnection(dsn, properties);
		} catch (SQLException ex) {
			EmpireCoreLib.logger.info("Failed to get SQL connection! {}" + " dsn: " + dsn);
			EmpireCoreLib.logger.info(ExceptionUtils.getStackTrace(ex));
		}
	}

	@Override
	protected void initProperties() {
		properties.put("foreign_keys", "ON");
	}
}