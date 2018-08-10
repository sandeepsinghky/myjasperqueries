

package org.dhhs.dirm.acts.query;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class StoredProcedureTest
{
	@SuppressWarnings("unused")
	public StoredProcedureTest()
	{
		Connection connection = null;
		try
		{
			Class.forName("hit.db2.Db2Driver");

			DriverManager.setLogWriter(new PrintWriter(new FileOutputStream("C:/HitConPool.log")));

			connection = DriverManager.getConnection("jdbc:db2://scca.sips.state.nc.us:5019;rdbname=NETSNDB01;package_collection_id=fko_acts_eproject", "TS64S54", "idly0503");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		Date start = Date.valueOf("2003-05-01");
		Date end = Date.valueOf("2003-05-31");
		try
		{
			System.out.println("Started");

			CallableStatement statement = connection.prepareCall("{call FKOS0001.FKAAS006(?,?,?,?,?,?,?,?,?,?,?)}");

			statement.setString(1, "45");
			statement.setString(2, "2003-01-01");
			statement.registerOutParameter(3, 1);
			statement.registerOutParameter(4, 1);
			statement.registerOutParameter(5, 1);
			statement.registerOutParameter(6, 1);
			statement.registerOutParameter(7, 1);
			statement.registerOutParameter(8, 1);
			statement.registerOutParameter(9, 1);
			statement.registerOutParameter(10, 4);
			statement.registerOutParameter(11, 1);

			ResultSet rs = statement.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			System.out.println("First Column Name: " + rsmd.getColumnName(1));
			System.out.println("Second Column Name: " + rsmd.getColumnName(2));
			System.out.println("First Column Label: " + rsmd.getColumnLabel(1));
			System.out.println("Second Column Label: " + rsmd.getColumnLabel(2));
			while (rs.next())
			{
				System.out.println("Return Data:" + rs.getString("Date") + " " + rs.getString("Check") + " " + rs.getString("Payee") + " " + rs.getString("Status") + " " + rs.getString("Amount"));
			}
			rs.close();
			statement.close();
		} catch (SQLException e)
		{
			System.out.println("Database SQL Exception Occurred");
			System.out.println(e.getErrorCode());
			System.out.println(e.getSQLState());
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		try
		{
			if (!connection.isClosed())
			{
				connection.close();
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		new StoredProcedureTest();
		System.exit(0);
	}
}
