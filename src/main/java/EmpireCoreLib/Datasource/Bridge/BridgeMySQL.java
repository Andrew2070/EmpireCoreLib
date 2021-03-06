package EmpireCoreLib.Datasource.Bridge;


import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.mysql.jdbc.Driver;

import EmpireCoreLib.EmpireCoreLib;
import EmpireCoreLib.Configuration.Config;
import EmpireCoreLib.Configuration.ConfigProperty;
import EmpireCoreLib.Configuration.ConfigTemplate;

public class BridgeMySQL extends BridgeSQL {

	public ConfigProperty<String> username = Config.instance.DBUsername;

	public ConfigProperty<String> password = Config.instance.DBPassword;

	public ConfigProperty<String> host = Config.instance.DBHost;

	public ConfigProperty<String> database = Config.instance.Database;

	public BridgeMySQL(ConfigTemplate config) {
		config.addBinding(username);
		config.addBinding(password);
		config.addBinding(host);
		config.addBinding(database, true);

		initProperties();
		initConnection();
	}

	@Override
	protected void initProperties() {
		autoIncrement = "AUTO_INCREMENT";

		properties.put("autoReconnect", "true");
		properties.put("user", username.get());
		properties.put("password", password.get());
		properties.put("relaxAutoCommit", "true");
	}

	@Override
	protected void initConnection() {
		this.dsn = "jdbc:mysql://" + host.get() + "/" + database.get();

		try {
			DriverManager.registerDriver(new Driver());
		} catch (SQLException ex) {
			EmpireCoreLib.logger.info("Failed to register driver for MySQL database." + " Exception: " + ex);
			EmpireCoreLib.logger.info(ExceptionUtils.getStackTrace(ex));
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
			EmpireCoreLib.logger.info("Failed to get SQL connection! {} " + " DSN: " + dsn);
			EmpireCoreLib.logger.info(ExceptionUtils.getStackTrace(ex));
		}
	}
}