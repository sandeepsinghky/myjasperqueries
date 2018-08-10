

package org.dhhs.dirm.acts.query;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public abstract interface QueryInterface
{
	public abstract void invoke(DataSource paramDataSource, QueryDateBean paramQueryDateBean) throws QueryException;

	public abstract Connection getConnection() throws SQLException;

	public abstract void closeConnection(Connection paramConnection) throws SQLException;

	public abstract String getQueryString();

	public abstract void formatReport(DataOutputStream paramDataOutputStream, String paramString) throws IOException;

	public abstract void sendEmail() throws QueryException;

	public abstract void loadProperties() throws QueryException;

	public abstract void handleConditions() throws QueryException;

	public abstract File makeDirs();

	public abstract String makeFile();
}
