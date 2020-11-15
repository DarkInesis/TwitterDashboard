package TseInfo6.TwitterDashboard.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import TseInfo6.TwitterDashboard.Utilities;

public class DatabaseManager {
	private static DatabaseManager instance = null;
	private Connection conn;
	private HashMap<String, Integer> usernameIdMap;
	private HashMap<String, HashMap<String, Integer>> categoryIdMap;
	
	private DatabaseManager() {
		
		usernameIdMap = new HashMap<String, Integer>();	
		categoryIdMap = new HashMap<String, HashMap<String, Integer>>();
		
		//Initit database tables
		Statement stmt;
		 try {
			 Class.forName("org.hsqldb.jdbc.JDBCDriver");
			 String url = "jdbc:hsqldb:file:db/database";
			 this.conn = DriverManager.getConnection(url, "SA", "");
			 
			 stmt = conn.createStatement();
			 stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CREDENTIAL"
			 		+ "(id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, username VARCHAR(100) );");
			 
			 stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CATEGORY" + 
					 			"(id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
					 			"name VARCHAR(100), user_id VARCHAR(100) );");
			 
			 stmt.executeUpdate("CREATE TABLE IF NOT EXISTS USER"
			 		+ "(screen_name VARCHAR(100), picture_url VARCHAR(100), user_id INTEGER,"
			 		+ "PRIMARY KEY(screen_name, user_id) );");

 			 stmt.executeUpdate("CREATE TABLE IF NOT EXISTS HASHTAG"
	 		+ "(id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name VARCHAR(100)," + 
 					 " cat_id INTEGER, user_id INTEGER )"); 
 			 
 			 stmt.executeUpdate("SET DATABASE SQL UNIQUE NULLS FALSE");
 			 
 			 try {
  				stmt.executeUpdate("ALTER TABLE CREDENTIAL ADD CONSTRAINT CREDENTIAL_UNIQUE_CONSTRAINT UNIQUE(username) ");
  				stmt.executeUpdate("ALTER TABLE CATEGORY ADD CONSTRAINT CATEGORY_UNIQUE_CONSTRAINT UNIQUE(name, user_id) ");
  				stmt.executeUpdate("ALTER TABLE HASHTAG ADD CONSTRAINT HASHTAG_UNIQUE_CATEGORY_CONSTRAINT UNIQUE(name, user_id, cat_id) ");
  				//stmt.executeUpdate("ALTER TABLE HASHTAG ADD CONSTRAINT HASHTAG_UNIQUE_USER_CONSTRAINT UNIQUE(name, user_id) ");
 			 }
 			 catch(SQLSyntaxErrorException ex) { 
 				 // Constraint was already created
 			 }
 			 finally {
 				stmt.close();
 			 }
		 }
		 catch(SQLException | ClassNotFoundException  ex) {
			 ex.printStackTrace();	 
			 this.close();
		 }
		 
	}
	
	/**
	 * Utility function used for debugging.
	 * Prints the result set onto the standard output
	 * @param resultSet
	 * @throws SQLException
	 */
	public static void printResultSet(ResultSet resultSet) throws SQLException {
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (resultSet.next()) {
		    for (int i = 1; i <= columnsNumber; i++) {
		        if (i > 1) System.out.print(",  ");
		        String columnValue = resultSet.getString(i);
		        System.out.print(columnValue + " " + rsmd.getColumnName(i));
		    }
		    System.out.println("");
		}
	}
	
	/**
	 * Used for debug only.
	 * Prints all users inside database to screen
	 * @throws SQLException
	 */
	public void printUsers() throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute("SELECT * FROM CREDENTIAL");
		 ResultSet sr = stmt.getResultSet();
		 printResultSet(sr);
		 stmt.close();
	}
	
	/**
	 * Get only instance of singleton (lazy)
	 * @return
	 */
	synchronized public static DatabaseManager getManager() {
		if(instance == null) {
			instance = new DatabaseManager();
		}
		
		return instance;
	}
	
	/**
	 * Get user id for a user stored inside the database.
	 * This methods first checks if the cahce contains the desired id.
	 * If the desired id is not there, a query is executed and the cache update
	 * @param username 
	 * @return the ID
	 * @throws SQLException if user not found
	 * @see getCategoryId
	 */
	private Integer getUsernameId(final String username) throws SQLException {
		if (usernameIdMap.containsKey(username)) {
			return usernameIdMap.get(username);
		} else {
			try (Statement stmt = conn.createStatement()) {
				stmt.execute("SELECT id from CREDENTIAL WHERE USERNAME = '" + username + "'");
				ResultSet set = stmt.getResultSet();

				if (set.next()) {
					Integer id = set.getInt(1);
					usernameIdMap.put(username, id);
					return id;
				} else {
					throw new SQLException("ResultSet is empty !");
				}
			}
		}
	}
	
	/**
	 * Get the id of a category for THE SPECIFIED user.
	 * (Indeed several users can have the same category name).
	 * A cache is used and maintained
	 * @param username
	 * @param categoryName
	 * @return the id
	 * @throws SQLException if categoryID not found
	 */
	private Integer getCategoryId(final String username, final String categoryName) throws SQLException {
		Integer user_id = getUsernameId(username);
		
		if (categoryIdMap.containsKey(categoryName) && categoryIdMap.get(categoryName).containsKey(username)) {
			return categoryIdMap.get(categoryName).get(username);
		} else {
			try (Statement stmt = conn.createStatement()) {
				stmt.execute("SELECT ID from CATEGORY WHERE NAME = '" + categoryName + "' AND USER_ID = " + user_id);
				ResultSet set = stmt.getResultSet();

				if (set.next()) {
					Integer id = set.getInt(1);
					
					if(!categoryIdMap.containsKey(categoryName)) {
						categoryIdMap.put(categoryName, new HashMap<String, Integer>());
					}
					categoryIdMap.get(categoryName).put(username, id);
					return id;
				} else {
					throw new SQLException("ResultSet is empty !");
				}
			}

		}

	}
	
	/**
	 * Adds a user to the database
	 * @param username the username to add
	 * @return true if added, false otherwise
	 */
	public boolean registerUser(final String username){ 
		
		try(Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("INSERT INTO CREDENTIAL(USERNAME) VALUES('" + username + "');");
			return true;
		}
		catch(SQLIntegrityConstraintViolationException ex) {
			//This means that user was already added
			return false;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Removes a user from the favorite
	 * @param username the user which will have his favorites updated
	 * @param screen_name the user to add
	 * @return true if removed, false otherwise
	 */
	public boolean removeUserFromFavorite(final String username, final String screen_name) {
		
		try(Statement stmt = conn.createStatement()) {
			Integer id = getUsernameId(username);
			String query = "DELETE FROM USER us WHERE us.USER_ID = " + id + " AND us.SCREEN_NAME = '" + screen_name + "'";
			stmt.executeUpdate(query);
			return true;
		}
		catch(SQLIntegrityConstraintViolationException ex) {
			//This means that user was already deleted
			return false;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Adds a user to the favorites
	 * @param username the user which will have his favorites updated
	 * @param screen_name the user to add
	 * @param picture_url his picture
	 * @return true if added, false otherwise
	 */
	public boolean addUserToFavorite(final String username, final String screen_name, final String picture_url) {
		try(Statement stmt = conn.createStatement()) {
			Integer id = getUsernameId(username);
			stmt.executeUpdate("INSERT INTO USER(SCREEN_NAME, PICTURE_URL, USER_ID ) VALUES('" + screen_name + "','" + picture_url +"'," + id + ")");
			return true;
		}
		catch(SQLIntegrityConstraintViolationException ex) {
			//This mean that hashtag was already inserted (PK constraint violation)	
			return false;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * Removes hashtag from a user's favorites
	 * @param username the user owning the favorites
	 * @param hashtag the hastag name Capitalized (first ltter uppercase and all others lowercase)
	 * @return true if removed, false otherwise
	 */
	public boolean removeHashtagFromFavorite(final String username, final String hashtag) {
		String capitalizedHashtagName = Utilities.asLowercaseWithFirstLetterCapitalized(hashtag);
		
		try(Statement stmt = conn.createStatement()) {
			Integer id = getUsernameId(username);
			String query = "DELETE FROM HASHTAG hash WHERE hash.USER_ID = " + id + " AND hash.NAME = '" + capitalizedHashtagName + "'";
			stmt.executeUpdate(query);
			return true;
		}
		catch(SQLIntegrityConstraintViolationException ex) {
			//This mean that user was already deleted
			return false;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * Adds a hashtag to a user's favorites
	 * @param username the user whose favorites will be modified
	 * @param hashtag the hashtag name Capitalized (first ltter uppercase and all others lowercase)
	 * @return true if added, false otherwise
	 */
	public boolean addHastagToFavorite(final String username, final String hashtag) {
		String capitalizedHashtagName = Utilities.asLowercaseWithFirstLetterCapitalized(hashtag);
		
		try(Statement stmt = conn.createStatement()) {
			Integer id = getUsernameId(username);
			stmt.executeUpdate("INSERT INTO HASHTAG(NAME, CAT_ID, USER_ID) VALUES('" + capitalizedHashtagName + "', NULL, " + id + ")");
			return true;
		}
		catch(SQLIntegrityConstraintViolationException ex) {
			//This mean that hashtag was already added
			return false;
		}
		catch(SQLException ex) {
			return false;
			//This means that user was already inserted (PK constraint violation)
		}
		
	}
	
	/**
	 * Adds a category inside the user centers of interests
	 * @param username the user
	 * @param categoryName the new category
	 * @return true if added, false otherwise
	 */
	public boolean addCategory(final String username, final String categoryName) {		
		try(Statement stmt = conn.createStatement()) {
			Integer id = getUsernameId(username);
			stmt.executeUpdate("INSERT INTO CATEGORY(NAME, USER_ID) VALUES('" + categoryName + "'," + id + ")");
			return true;
		}
		catch(SQLIntegrityConstraintViolationException ex) {
			//This means that category was already added
			return false;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Removed category from a user centers of interest
	 * @param username the user
	 * @param categoryName the category to remove
	 * @return true if removed, false otherwise
	 */
	public boolean removeCategory(final String username, final String categoryName) {
		try(Statement stmt = conn.createStatement()) {
			Integer user_id = getUsernameId(username);
			Integer cat_id = getCategoryId(username, categoryName);
			
			//Delete Category from cache
			if(categoryIdMap.containsKey(categoryName)) {
				categoryIdMap.get(categoryName).remove(username);
			}
			
			//Delete all hashtag inside this category
			String query = "DELETE FROM HASHTAG hash WHERE hash.CAT_ID = " + cat_id;
			stmt.executeUpdate(query);
			
			//Delete category
			String query2 = "DELETE FROM CATEGORY cat WHERE cat.USER_ID = " + user_id + " AND cat.NAME = '" +
							categoryName + "'";
			stmt.executeUpdate(query2);
			
			return true;
		}
		catch(SQLIntegrityConstraintViolationException ex) {
			//This means that category was already added
			return false;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Adds a hashtag inside one of the category inside the user's centers of interest
	 * @param username the user
	 * @param categoryName the category to insert into
	 * @param hashtagName the new hashtag name
	 * @return true if inserted, false otherwise
	 */
	public boolean insertHashtagIntoCategory(final String username, final String categoryName, final String hashtagName) {
		//capitalize
		String capitalizedHashtagName = Utilities.asLowercaseWithFirstLetterCapitalized(hashtagName);
		
		try(Statement stmt = conn.createStatement()) {
			Integer cat_id = getCategoryId(username, categoryName);
			String query = "INSERT INTO HASHTAG(NAME, CAT_ID, USER_ID) VALUES('" + capitalizedHashtagName + "'," + cat_id + ", NULL)";
			stmt.executeUpdate(query);
			return true;
		}
		catch(SQLIntegrityConstraintViolationException ex) {
			return false;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Removed a hashtag from a category inside a user's centers of interest
	 * @param username the user
	 * @param categoryName the category the remove from
	 * @param hashtagName the hashtag name to remove
	 * @return true if removed, false otherwise
	 */
	public boolean removeHashtagFromCategory(final String username, final String categoryName, final String hashtagName) {
		//Capitalize
		String capitalizedHashtagName = Utilities.asLowercaseWithFirstLetterCapitalized(hashtagName);
		
		try(Statement stmt = conn.createStatement()) {
			Integer cat_id = getCategoryId(username, categoryName);
			String query = "DELETE FROM HASHTAG hash WHERE hash.NAME = '" + capitalizedHashtagName + "'" +
							" AND hash.CAT_ID = " + cat_id;
			stmt.executeUpdate(query);
			return true;
		}
		catch(SQLIntegrityConstraintViolationException ex) {
			return false;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	//Using try with resources without catch to ensure proper release of resources
	/**
	 * Get all categories of a user's centers of interest
	 * @param username the user
	 * @return all the categories as a {@link List}<{@link String}>
	 * @throws SQLException
	 */
	public List<String> getCategories(final String username) throws SQLException  {
		try(Statement stmt = conn.createStatement()) {
			stmt.execute("SELECT cat.NAME FROM CATEGORY cat JOIN CREDENTIAL cred ON cat.USER_ID = cred.ID WHERE cred.USERNAME = '" + username + "'");
			List<String> categories = new ArrayList<String>();
			ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
					categories.add(rs.getString(1));			
			}
			return categories;
		}
	}
	
	/**
	 * Gets all the hashtag inside a user's favorites section
	 * @param username the user
	 * @return a list of hashtags
	 * @throws SQLException
	 */
	public List<String> getFavoriteHashtags(final String username) throws SQLException {
		try(Statement stmt = conn.createStatement()) {
			stmt.execute("SELECT hash.NAME FROM HASHTAG hash JOIN CREDENTIAL cred ON hash.USER_ID = cred.ID WHERE (cred.USERNAME = '" + username + "' AND hash.CAT_ID IS NULL)");
			List<String> hashtag = new ArrayList<String>();
			ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
					hashtag.add(rs.getString(1));			
			}
			return hashtag;
		}
	}
	
	/**
	 * Gets all the users (screen_names) inside a user's favorites section
	 * @param username the user owning the section
	 * @return teh list of fav users
	 * @throws SQLException
	 */
	public List<ArrayList<String>> getFavoriteUsers(final String username) throws SQLException {
		try(Statement stmt = conn.createStatement()) {
			stmt.execute("SELECT u.SCREEN_NAME, u.PICTURE_URL FROM USER u JOIN CREDENTIAL cred ON u.USER_ID = cred.ID WHERE cred.USERNAME = '" + username + "'");
			List<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
			ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
					ArrayList<String> row = new ArrayList<String>();
					row.add(rs.getString(2));
					row.add(rs.getString(1));	
					rows.add(row);
			}
			return rows;
		}
	}
	
	/**
	 * Gets all hashtags inside a category 
	 * @param username the user
	 * @param categoryName the target category 
	 * @return all the hashtags
	 * @throws SQLException
	 */
	public List<String> getHashtagsInCategory(final String username, final String categoryName) throws SQLException{
		Integer user_id = getUsernameId(username);
		Integer cat_id = getCategoryId(username, categoryName);
		
		try(Statement stmt = conn.createStatement()) {
			String query = "SELECT hash.NAME FROM HASHTAG hash INNER JOIN CATEGORY cat ON hash.cat_id = cat.id INNER JOIN CREDENTIAL cred ON cat.USER_ID = cred.id";
			query += " WHERE cred.ID = " + user_id +" AND cat.ID = " + cat_id + "";
			stmt.execute(query);
			
			List<String> hashtags = new ArrayList<String>();
			ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
					hashtags.add(rs.getString(1));			
			}
			return hashtags;
		}
	}
	
	/**
	 * Gets number of favorites (hashtags or users according to table param)
	 * @param username the user
	 * @param table can be "hash" or "user"
	 * @return the number of elements (hash or user) inside favorites section
	 * @throws SQLException
	 */
	private Integer getFavoriteCount(final String username, final String table) throws SQLException {
		Integer user_id = getUsernameId(username);

		try (Statement stmt = conn.createStatement()) {
			String query;
			if(table.equals("hash")) {
				query = "SELECT COUNT(hash.USER_ID) FROM HASHTAG hash WHERE hash.USER_ID = "
						+ user_id;
			}
			else if(table.equals("user")) {
				query = "SELECT COUNT(us.USER_ID) FROM USER us WHERE us.USER_ID = "
						+ user_id;
			}
			else {
				throw new SQLException("table must be 'hash' or 'user' only !");
			}
			
			stmt.execute(query);

			ResultSet rs = stmt.getResultSet();
			Integer result;
			if (rs.next()) {
				result = rs.getInt(1);
			} else {
				throw new SQLException("SELECT did not return any count !");
			}
			return result;
		}
	}
	/**
	 * Gets the number of hashtag inside favorites section
	 * @param username the user
	 * @return number of hashtag
	 * @throws SQLException
	 * @see getFavoriteCount
	 */
	public Integer getFavoriteHashtagCount(final String username) throws SQLException {
		return getFavoriteCount(username, "hash");
	}
	
	/**
	 * Gets the number of user inside the favorites section of a user
	 * @param username the user
	 * @return number of users
	 * @throws SQLException
	 * @see getFavoriteCount
	 */
	
	public Integer getFavoriteUserCount(final String username) throws SQLException {
		return getFavoriteCount(username, "user");
	}
	
	/**
	 * Release resources held by database and closes connection
	 * Needed to flush data to disk !
	 */
	public void close() {
		if (this.conn != null) {
			try {
				this.conn.close();
				this.conn = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
