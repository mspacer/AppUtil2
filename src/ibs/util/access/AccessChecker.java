package ibs.util.access;

import ibs.util.db.DataBaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccessChecker {
	private final DataBaseHelper dataBaseHelper;
	private final String system;

	public AccessChecker(DataBaseHelper dataBaseHelper, String system) {
		this.dataBaseHelper = dataBaseHelper;
		this.system = system;
	}

	protected final String getSystem() {
		return system;
	}

	public static boolean checkUnlessAccessible(DataBaseHelper dataBaseHelper, String system, String objectType, String objectId, StringBuffer message) throws Exception {
		Connection con = dataBaseHelper.getConnection();
		boolean result = false;
		try {
			PreparedStatement stmt = con.prepareStatement("select name from table(pib.security.get_missing_switches(?, ?, ?))");
			try {
				stmt.setString(1, system);
				stmt.setString(2, objectType);
				stmt.setString(3, objectId);
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					if(result) {
						message.append(", ");
					} else {
						result = true;
					}
					message.append(rs.getString(1));
				}
			} finally {
				stmt.close();
			}
		} finally {
			con.close();
		}
		return result;
	}

	public boolean checkUnlessAccessible(String objectType, String objectId, StringBuffer message) throws Exception {
		return checkUnlessAccessible(dataBaseHelper, system, objectType, objectId, message);
	}
}
