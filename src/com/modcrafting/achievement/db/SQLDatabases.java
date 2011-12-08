package com.modcrafting.achievement.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.modcrafting.achievement.Achievement;

public class SQLDatabases {
	public final static Logger log = Logger.getLogger("Minecraft");
	static Achievement plugin;
	
	public Connection getSQLConnection() {
		YamlConfiguration Config = (YamlConfiguration) plugin.getConfig();
		String dataHandler = Config.getString("Database");
		String mysqlDatabase = Config.getString("mysql-database","jdbc:mysql://localhost:3306/minecraft");
		String mysqlUser = Config.getString("mysql-user","root");
		String mysqlPassword = Config.getString("mysql-password","root");
		if(dataHandler.equalsIgnoreCase("mysql")){
			try {

				return DriverManager.getConnection(mysqlDatabase + "?autoReconnect=true&user=" + mysqlUser + "&password=" + mysqlPassword);
			} catch (SQLException ex) {
			
				log.log(Level.SEVERE, "Unable to retreive connection", ex);
			}
			return null;
		}
		if(dataHandler.equalsIgnoreCase("sqlite")){

			String dbname = Config.getString("sqlite-dbname", "banlist");
			String maindir = "plugins/Achievement/";
			File dataFolder = new File(maindir, dbname + ".db");
			if (!dataFolder.exists()){
				try {
					dataFolder.createNewFile();
					Class.forName("org.sqlite.JDBC");
		            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
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
					log.log(Level.SEVERE, "File write error: " + dbname);
				} catch (SQLException ex) {
			            log.log(Level.SEVERE,"SQLite exception on initialize", ex);
			    } catch (ClassNotFoundException ex) {
			        	log.log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
			    }
			}
			try {
	            Class.forName("org.sqlite.JDBC");
	            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
	            return conn;
        		
	        } catch (SQLException ex) {
	            log.log(Level.SEVERE,"SQLite exception on initialize", ex);
	        } catch (ClassNotFoundException ex) {
	        	log.log(Level.SEVERE, "You need the SQLite library.", ex);
	        }
	    }
		return null;
	}	
	public void initialize(Achievement plugin){
		YamlConfiguration Config = (YamlConfiguration) plugin.getConfig();
		String mysqlTable = Config.getString("mysql-table");
		String logip = Config.getString("mysql-table-ip");
		SQLDatabases.plugin = plugin;
		Connection conn = getSQLConnection();
		
		
		if (conn == null) {
			log.log(Level.SEVERE, "[Achievement] Could not establish SQL connection. Disabling Achievement");
			log.log(Level.SEVERE, "[Achievement] Adjust Settings in Config or set MySql: False");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		} else {
			PreparedStatement ps = null;
			ResultSet rs = null;
			Statement st = null;
			
			try {
				ps = conn.prepareStatement("SELECT * FROM " + mysqlTable + " WHERE (type = 0 OR type = 1 OR type = 9) AND (temptime > ? OR temptime = 0)");
				ps.setLong(1, System.currentTimeMillis()/1000);
				try{
					
					DatabaseMetaData dbm = conn.getMetaData();
					rs = dbm.getTables(null, null, "banlist", null);
		            	if (!rs.next()){
		            		conn.setAutoCommit(false);
		            		st = conn.createStatement();
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
		            		log.log(Level.INFO, "[Achievement]: Table " + mysqlTable + " created.");
		            		log.log(Level.INFO, "[Achievement]: Table " + logip + " created.");
		            	}
		            	rs = ps.executeQuery();
							            
				} catch (SQLException ex) {
					log.log(Level.SEVERE, "[Achievement] Database Error: No Table Found");
                }
				
				try {
					while (rs.next()){
					//String pName = rs.getString("name").toLowerCase();
					}
				}catch (NullPointerException ex){
					log.log(Level.SEVERE, "[Achievement] Detected Major issues with database.");
					plugin.getServer().getPluginManager().disablePlugin(plugin);
					log.log(Level.SEVERE, "[Achievement] Attempting Restart.");
					plugin.getServer().getPluginManager().enablePlugin(plugin);
					return;
				}
			} catch (SQLException ex) {
				log.log(Level.SEVERE, "[Achievement] Couldn't execute MySQL statement: ", ex);
				return;
			} finally {
				try {
					if (ps != null)
						ps.close();
					if (rs != null)
						rs.close();
					if (conn != null)
						conn.close();
				} catch (SQLException ex) {
					log.log(Level.SEVERE, "[Achievement] Failed to close MySQL connection: ", ex);
				}
			}	

			try {
				if (!plugin.isEnabled()){
					return;
				}
				conn.close();
				log.log(Level.INFO, "[Achievement] Initialized db connection" );
			} catch (SQLException e) {
				e.printStackTrace();
				plugin.getServer().getPluginManager().disablePlugin(plugin);
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
				return;
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
			return;
		}
	}
}
