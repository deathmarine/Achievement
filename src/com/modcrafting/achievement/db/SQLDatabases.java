package com.modcrafting.achievement.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.modcrafting.achievement.Achievement;

public class SQLDatabases {
	static Achievement plugin;
	
	public Connection getSQLConnection() {
		String dataHandler = plugin.getConfig().getString("Database");
		String mysqlDatabase = plugin.getConfig().getString("MYSQL.Database","jdbc:mysql://localhost:3306/minecraft");
		String mysqlUser = plugin.getConfig().getString("MYSQL.User","root");
		String mysqlPassword = plugin.getConfig().getString("MYSQL.Password","root");
		if(dataHandler.equalsIgnoreCase("mysql")){
			try {
				return DriverManager.getConnection(mysqlDatabase + "?autoReconnect=true&user=" + mysqlUser + "&password=" + mysqlPassword);
			} catch (SQLException ex) {
				plugin.getLogger().severe("Unable to retreive connection" + ex);
			}
			return null;
		}
		if(dataHandler.equalsIgnoreCase("sqlite")){
			String dbname = plugin.getConfig().getString("sqlite-dbname", "achievements");
			File dbfile = new File(plugin.getDataFolder(), dbname + ".db");
			if (!dbfile.exists()){
				try {
					dbfile.createNewFile();
					Class.forName("org.sqlite.JDBC");
		            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbfile);
		            Statement st = conn.createStatement();
		            st.execute("CREATE TABLE IF NOT EXISTS `breaks` (" +
		    			"playername varchar(32)," +
		    			"blockid SMALLINT UNSIGNED," +
		    			"breaks INT UNSIGNED," +
		    			"PRIMARY KEY(`playername`, `blockid`)" +
		    			")");
		    		st.execute("CREATE TABLE IF NOT EXISTS `places` (" +
		    			"playername varchar(32)," +
		    			"blockid SMALLINT UNSIGNED," +
		    			"places INT UNSIGNED," +
		    			"PRIMARY KEY(`playername`, `blockid`)" +
		    			")");
		    		st.execute("CREATE TABLE IF NOT EXISTS `kills` (" +
		    			"playername varchar(32)," +
		    			"mobname varchar(32)," + 
		    			"kills INT UNSIGNED," +
		    			"PRIMARY KEY (`playername`, `mobname`)" +
		    			")");
		    		st.execute("CREATE TABLE IF NOT EXISTS `crafts` (" +
		   				"playername varchar(32)," +
		   				"item SMALLINT UNSIGNED," + 
		   				"times INT UNSIGNED," +
		   				"PRIMARY KEY (`playername`, `item`)" +
		   				")");
	        		return conn;
				} catch (IOException ex) {
					plugin.getLogger().severe( "File write error: " + dbname);
				} catch (SQLException ex) {
					plugin.getLogger().severe("SQLite exception on initialize"+ ex);
			    } catch (ClassNotFoundException ex) {
			    	plugin.getLogger().severe("You need the SQLite JBDC library. Google it. Put it in /lib folder.");
			    }
			}
			try {
	            Class.forName("org.sqlite.JDBC");
	            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbfile);
	            return conn;
        		
	        } catch (SQLException ex) {
	            plugin.getLogger().severe("SQLite exception on initialize"+ ex);
	        } catch (ClassNotFoundException ex) {
	        	plugin.getLogger().severe("You need the SQLite library." + ex);
	        }
	    }
		return null;
	}	
	public void initialize(Achievement plugin){
		SQLDatabases.plugin = plugin;
		Connection conn = getSQLConnection();
		if (conn == null) {
			plugin.getLogger().severe( "[Achievement] Could not establish SQL connection. Disabling Achievement");
			plugin.getLogger().severe( "[Achievement] Adjust Settings in Config or set MySql: False");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		} else {
			try {
				DatabaseMetaData dbm = conn.getMetaData();
				ResultSet rs = dbm.getTables(null, null, "breaks", null);
            	if (!rs.next()){
            		conn.setAutoCommit(false);
            		Statement st = conn.createStatement();
            		st.execute("CREATE TABLE IF NOT EXISTS `breaks` (" +
    	    			"playername varchar(32)," +
    	    			"blockid SMALLINT UNSIGNED," +
    		    		"breaks INT UNSIGNED," +
    		    		"PRIMARY KEY(`playername`, `blockid`)" +
    		    		")");
    		    	st.execute("CREATE TABLE IF NOT EXISTS `places` (" +
    		    		"playername varchar(32)," +
    		    		"blockid SMALLINT UNSIGNED," +
    		    		"places INT UNSIGNED," +
    		    		"PRIMARY KEY(`playername`, `blockid`)" +
    		    		")");
    		    	st.execute("CREATE TABLE IF NOT EXISTS `kills` (" +
    		    		"playername varchar(32)," +
    		    		"mobname varchar(32)," + 
    		    		"kills INT UNSIGNED," +
    		    		"PRIMARY KEY (`playername`, `mobname`)" +
    		    		")");
    		    	st.execute("CREATE TABLE IF NOT EXISTS `crafts` (" +
    		   			"playername varchar(32)," +
    		   			"item SMALLINT UNSIGNED," + 
    		   			"times INT UNSIGNED," +
    		   			"PRIMARY KEY (`playername`, `item`)" +
    		   			")");
            		conn.commit();
					if (rs != null)
						rs.close();
					if (conn != null)
						conn.close();
            		plugin.getLogger().severe("Tables created.");
            	}
			} catch (SQLException ex) {
				plugin.getLogger().severe("Couldn't execute MySQL statement: "+ ex);
			}
		}
		
	}
	public Integer getCrafts(Player player, ItemStack item) {
		try {
			Connection conn = getSQLConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT times from `crafts` WHERE playername = '" + player.getName() + "' AND item = " + item.getTypeId() + "");
			Integer itemCrafts = 0;
			while(rs.next()) {
				itemCrafts = rs.getInt("times");
			}
			return itemCrafts;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public void registerCraft(Player player, ItemStack item) {
		try {
			Connection conn = getSQLConnection();
			Integer itemCrafts = 0;
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT times FROM `crafts` WHERE playername = '" + player.getName() + "' AND item = " + item.getTypeId());
		while(rs.next()) {
			itemCrafts = rs.getInt("times");
		}
		Integer newCrafts = itemCrafts + 1;
		st.execute("replace into `crafts` (playername, item, times) VALUES ('" + player.getName() + "'," + item.getTypeId() + ", " + newCrafts + ")");
		} catch(SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	public Integer getKills(Player player, String mobname) {
		try {
			Connection conn = getSQLConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT kills from `kills` WHERE playername = '" + player.getName() + "' AND mobname = '" + mobname + "'");
			Integer kills = 0;
			while(rs.next()) {
				kills = rs.getInt("kills");
			}
			return kills;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	public void registerKill(Player player, String mobname) {
		try {
			Connection conn = getSQLConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT kills from `kills` WHERE playername = '" + player.getName() + "' AND mobname = '" + mobname + "'");
			Integer prev = 0;
			while(rs.next()) {
				prev = rs.getInt("kills");
			}
			Integer newkills = prev + 1;
			st.execute("replace into `kills` (playername, mobname, kills) VALUES ('" + player.getName() + "', '" + mobname + "', " + newkills + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void registerPlace(Player player, Block block) {
		try {
			Connection conn = getSQLConnection();
			Integer blockBreaks = 0;
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT places from `places` WHERE playername = '" + player.getName() + "' AND blockid = " + block.getTypeId() + "");
			while(rs.next()) {
				blockBreaks = rs.getInt("places");
			}
			Integer newBreaks = blockBreaks + 1;
			st.execute("replace into `places` (playername, blockid, places) VALUES ('" + player.getName() + "'," + block.getTypeId() + ", " + newBreaks + ")");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public Integer getBlockPlaces(Player player, Block block) {
		try {
			Connection conn = getSQLConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT places from `places` WHERE playername = '" + player.getName() + "' AND blockid = " + block.getTypeId() + "");
			Integer blockPlaces = 0;
			while(rs.next()) {
				blockPlaces = rs.getInt("places");
			}
			return blockPlaces;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public Integer getBlockBreaks(Player player, Block block) {
		try {
			Connection conn = getSQLConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT breaks from `breaks` WHERE playername = '" + player.getName() + "' AND blockid = " + block.getTypeId() + "");
			Integer blockBreaks = 0;
			while(rs.next()) {
				blockBreaks = rs.getInt("breaks");
			}
			return blockBreaks;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public void registerBreak(Player player, Block block) {
		try {
		Connection conn = getSQLConnection();
		Integer blockBreaks = 0;
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT breaks FROM `breaks` WHERE playername = '" + player.getName() + "' AND blockid = " + block.getTypeId());
		while(rs.next()) {
			blockBreaks = rs.getInt("breaks");
		}
		Integer newBreaks = blockBreaks + 1;
		st.execute("replace into `breaks` (playername, blockid, breaks) VALUES ('" + player.getName() + "'," + block.getTypeId() + ", " + newBreaks + ")");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
