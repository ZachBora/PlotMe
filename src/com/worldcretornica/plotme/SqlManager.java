package com.worldcretornica.plotme;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SqlManager {

	private static Connection conn = null;
	
	public final static String sqlitedb = "/plots.db";
	
    private final static String PLOT_TABLE = "CREATE TABLE `plotmePlots` ("
		    + "`idX` INTEGER,"
		    + "`idZ` INTEGER,"
		    + "`owner` varchar(32) NOT NULL,"
	        + "`world` varchar(32) NOT NULL DEFAULT '0',"
	        + "`topX` INTEGER NOT NULL DEFAULT '0',"
	        + "`bottomX` INTEGER NOT NULL DEFAULT '0',"
	        + "`topZ` INTEGER NOT NULL DEFAULT '0',"
	        + "`bottomZ` INTEGER NOT NULL DEFAULT '0',"
	        + "`biome` varchar(32) NOT NULL DEFAULT '0',"
	        + "`expireddate` DATETIME NULL,"
	        + "`finished` boolean NOT NULL DEFAULT '0',"
	        + "PRIMARY KEY (idX, idZ, world) "
	        + ");";
    
    private final static String COMMENT_TABLE = "CREATE TABLE `plotmeComments` ("
	    	+ "`idX` INTEGER,"
		    + "`idZ` INTEGER,"
	    	+ "`world` varchar(32) NOT NULL,"
		    + "`commentid` INTEGER,"
		    + "`player` varchar(32) NOT NULL,"
		    + "`comment` text,"
	        + "PRIMARY KEY (idX, idZ, world, commentid) "
	    	+ ");";
    
    private final static String ALLOWED_TABLE = "CREATE TABLE `plotmeAllowed` ("
    		+ "`idX` INTEGER,"
    	    + "`idZ` INTEGER,"
    	    + "`world` varchar(32) NOT NULL,"
    	    + "`player` varchar(32) NOT NULL,"
	        + "PRIMARY KEY (idX, idZ, world, player) "
    	    + ");";
		
    public static Connection initialize() {
        try {
        	if(PlotMe.usemySQL == true) {
        		Class.forName("com.mysql.jdbc.Driver");
        		conn = DriverManager.getConnection(PlotMe.mySQLconn, PlotMe.mySQLuname, PlotMe.mySQLpass);
        		conn.setAutoCommit(false);
        	} else {
        		Class.forName("org.sqlite.JDBC");
        		conn = DriverManager.getConnection("jdbc:sqlite:" + PlotMe.configpath + "/plots.db");
        		conn.setAutoCommit(false);
        	}
        } catch (SQLException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + "SQL exception on initialize :");
        	PlotMe.logger.severe("  " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + "You need the SQLite/MySQL library. :");
        	PlotMe.logger.severe("  " + ex.getMessage());
        }
        
        if (!tableExists()) {
            createTable();
        }
        
        return conn;
    }

	public static Connection getConnection()
	{
		if(conn == null) conn = initialize();
		if(PlotMe.usemySQL) {
			try {
				if(!conn.isValid(10)) conn = initialize();
			} catch (SQLException ex) {
				PlotMe.logger.severe(PlotMe.PREFIX + "Failed to check SQL status :");
				PlotMe.logger.severe("  " + ex.getMessage());
			}
		}
		return conn;
	}

    public static void closeConnection()
    {
		if(conn != null) {
			try {
				if(PlotMe.usemySQL){
					if(conn.isValid(10)) {
						conn.close();
					}
					conn = null;
				} else {
					conn.close();
					conn = null;
				}
			} catch (SQLException ex) {
				PlotMe.logger.severe(PlotMe.PREFIX + "Error on Connection close :");
				PlotMe.logger.severe("  " + ex.getMessage());
			}
		}
    }
    
    private static boolean tableExists()
    {
        ResultSet rs = null;
        try {
            Connection conn = getConnection();

            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, "plotmePlots", null);
            if (!rs.next())
                return false;
            return true;
        } catch (SQLException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + " Table Check Exception :");
        	PlotMe.logger.severe("  " + ex.getMessage());
            return false;
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException ex) {
            	PlotMe.logger.severe(PlotMe.PREFIX + " Table Check SQL Exception (on closing) :");
            	PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
    }
    
    private static void createTable()
    {
    	Statement st = null;
    	try {
    		PlotMe.logger.info(PlotMe.PREFIX + " Creating Database...");
    		Connection conn = getConnection();
    		st = conn.createStatement();
    		st.executeUpdate(PLOT_TABLE);
    		conn.commit();
    		st.executeUpdate(COMMENT_TABLE);
    		conn.commit();
    		st.executeUpdate(ALLOWED_TABLE);
    		conn.commit();
    		
    		if(PlotMe.usemySQL){ 
    			
    			PlotMe.logger.info(PlotMe.PREFIX + " Modifying database for MySQL support");
    			
    			File sqlitefile = new File(PlotMe.configpath + sqlitedb);
    			if (!sqlitefile.exists()) {
    				PlotMe.logger.info(PlotMe.PREFIX + " Could not find old " + sqlitedb);
    				return;
    			} else {
    				PlotMe.logger.info(PlotMe.PREFIX + " Trying to import plots from plots.db");
	        		Class.forName("org.sqlite.JDBC");
	        		Connection sqliteconn = DriverManager.getConnection("jdbc:sqlite:" + PlotMe.configpath + sqlitedb);
	        		sqliteconn.setAutoCommit(false);
	        		Statement slstatement = sqliteconn.createStatement();
	        		ResultSet setPlots = slstatement.executeQuery("SELECT * FROM plotmePlots");
	        		ResultSet setAllowed = null;
	        		ResultSet setComments = null;
	        		
	        		int size = 0;
	        		while (setPlots.next()) {
	        			size++;
	        			int idX = setPlots.getInt("idX");
	        			int idZ = setPlots.getInt("idZ");
	        			String owner = setPlots.getString("owner");
	        			String world = setPlots.getString("world");
	        			int topX = setPlots.getInt("topX");
	        			int bottomX = setPlots.getInt("bottomX");
	        			int topZ = setPlots.getInt("topZ");
	        			int bottomZ = setPlots.getInt("bottomZ");
	        			String biome = setPlots.getString("biome");
	        			java.sql.Date expireddate = setPlots.getDate("expireddate");
	        			boolean finished = setPlots.getBoolean("finished");
	        			HashSet<String> allowed = new HashSet<String>();
	        			List<String[]> comments = new ArrayList<String[]>();
	        			
	        			setAllowed = slstatement.executeQuery("SELECT * FROM plotmeAllowed WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND world = '" + world + "'");
	        			
	        			while (setAllowed.next()) {
	        				allowed.add(setAllowed.getString("player"));
	        			}
	        			
	        			if (setAllowed != null) {
	        				setAllowed.close();
	        			}
	        			
	        			setComments = slstatement.executeQuery("SELECT * FROM plotmeComments WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND world = '" + world + "'");
	        			
	        			while (setComments.next()) {
	        				String[] comment = new String[2];
	        				comment[0] = setComments.getString("player");
	        				comment[1] = setComments.getString("comment");
	        				comments.add(comment);
	        			}
	        			
	        			Plot plot = new Plot(owner, world, topX, bottomX, topZ, bottomZ, biome, expireddate, finished, allowed, comments);
	        			addPlot(plot, idX, idZ);
	        		}
	        		PlotMe.logger.info(PlotMe.PREFIX + " Imported " + size + " plots from " + sqlitedb);
	        		PlotMe.logger.info(PlotMe.PREFIX + " Renaming " + sqlitedb + " to " + sqlitedb + ".old");
	        		if (!sqlitefile.renameTo(new File(PlotMe.configpath, sqlitedb + ".old"))) {
	        			PlotMe.logger.warning(PlotMe.PREFIX + " Failed to rename " + sqlitedb + "! Please rename this manually!");
	    			}
	        		if (slstatement != null)
        				slstatement.close();
        			if (setPlots != null)
        				setPlots.close();
        			if (setComments != null)
                    	setComments.close();
                    if (setAllowed != null)
                    	setAllowed.close();
    				if (sqliteconn != null)
        				sqliteconn.close();
    			}
    		}
    	} catch (SQLException ex) {
    		PlotMe.logger.severe(PlotMe.PREFIX + " Create Table Exception :");
    		PlotMe.logger.severe("  " + ex.getMessage());
    	} catch (ClassNotFoundException ex) {
    		PlotMe.logger.severe(PlotMe.PREFIX + " You need the SQLite library :");
    		PlotMe.logger.severe("  " + ex.getMessage());
    	} finally {
    		try {
    			if (st != null) {
    				st.close();
    			}
    		} catch (SQLException ex) {
    			PlotMe.logger.severe(PlotMe.PREFIX + " Could not create the table (on close) :");
    			PlotMe.logger.severe("  " + ex.getMessage());
    		}
    	}
    }
    
    public static void addPlot(Plot plot, int idX, int idZ)
    {
        PreparedStatement ps = null;
        Connection conn;
        
        //Plots
        try {
            conn = getConnection();

            ps = conn.prepareStatement("INSERT INTO plotmePlots (idX, idZ, owner, world, topX, bottomX, topZ, bottomZ, biome, expireddate, finished) " +
            						   "VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, plot.owner);
            ps.setString(4, plot.world);
            ps.setInt(5, plot.topX);
            ps.setInt(6, plot.bottomX);
            ps.setInt(7, plot.topZ);
            ps.setInt(8, plot.bottomZ);
            ps.setString(9, plot.biome.name());
            ps.setDate(10, plot.expireddate);
            ps.setBoolean(11, plot.finished);
            
            ps.executeUpdate();
            conn.commit();
                        
        } catch (SQLException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + " Insert Exception :");
        	PlotMe.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            	PlotMe.logger.severe(PlotMe.PREFIX + " Insert Exception (on close) :");
            	PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void updatePlot(int idX, int idZ, String world, String field, Object value)
    {
        PreparedStatement ps = null;
        Connection conn;
        
        //Plots
        try {
            conn = getConnection();

            ps = conn.prepareStatement("UPDATE plotmePlots SET " + field + " = ? " +
            						   "WHERE idX = ? AND idZ = ? AND world = ?");
            ps.setObject(1, value);
            ps.setInt(2, idX);
            ps.setInt(3, idZ);
            ps.setString(4, world);
            
            ps.executeUpdate();
            conn.commit();
                        
        } catch (SQLException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + " Insert Exception :");
        	PlotMe.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            	PlotMe.logger.severe(PlotMe.PREFIX + " Insert Exception (on close) :");
            	PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void addPlotAllowed(String player, int idX, int idZ, String world)
    {
    	PreparedStatement ps = null;
        Connection conn;
        
    	//Allowed
        try {
            conn = getConnection();
            
            ps = conn.prepareStatement("INSERT INTO plotmeAllowed (idX, idZ, player, world) " +
					   "VALUES (?,?,?,?)");
            
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world);
            
            ps.executeUpdate();
            conn.commit();
            
        } catch (SQLException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + " Insert Exception :");
        	PlotMe.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            	PlotMe.logger.severe(PlotMe.PREFIX + " Insert Exception (on close) :");
            	PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void addPlotComment(String[] comment, int commentid, int idX, int idZ, String world)
    {
    	PreparedStatement ps = null;
        Connection conn;
        
    	//Comments
        try {
            conn = getConnection();
            
            ps = conn.prepareStatement("INSERT INTO plotmeComments (idX, idZ, commentid, player, comment, world) " +
					   "VALUES (?,?,?,?,?,?)");
            
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setInt(3, commentid);
            ps.setString(4, comment[0]);
            ps.setString(5, comment[1]);
            ps.setString(6, world);
            
            ps.executeUpdate();
            conn.commit();
            
        } catch (SQLException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + " Insert Exception :");
        	PlotMe.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            	PlotMe.logger.severe(PlotMe.PREFIX + " Insert Exception (on close) :");
            	PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void deletePlot(int idX, int idZ, String world)
    {
        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeComments WHERE idX = ? and idZ = ? and world = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3,	world);
            ps.executeUpdate();
            conn.commit();
            
            ps = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE idX = ? and idZ = ? and world = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            
            ps = conn.prepareStatement("DELETE FROM plotmePlots WHERE idX = ? and idZ = ? and world = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            
        } catch (SQLException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + " Delete Exception :");
        	PlotMe.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
            	PlotMe.logger.severe(PlotMe.PREFIX + " Delete Exception (on close) :");
            	PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void deletePlotComment(int idX, int idZ, int commentid, String world)
    {
        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeComments WHERE idX = ? and idZ = ? and commentid = ? and world = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setInt(3, commentid);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();
            
        } catch (SQLException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + " Delete Exception :");
        	PlotMe.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
            	PlotMe.logger.severe(PlotMe.PREFIX + " Delete Exception (on close) :");
            	PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
    }
    
    public static void deletePlotAllowed(int idX, int idZ, String player, String world)
    {
        PreparedStatement ps = null;
        ResultSet set = null;
        
        try {
            Connection conn = getConnection();

            ps = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE idX = ? and idZ = ? and player = ? and world = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();
            
        } catch (SQLException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + " Delete Exception :");
        	PlotMe.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            } catch (SQLException ex) {
            	PlotMe.logger.severe(PlotMe.PREFIX + " Delete Exception (on close) :");
            	PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
    }
    
    public static HashMap<String, Plot> getPlots(String world) {
        HashMap<String, Plot> ret = new HashMap<String, Plot>();
        Statement statementPlot = null;
        Statement statementAllowed = null;
        Statement statementComment = null;
        ResultSet setPlots = null;
        ResultSet setAllowed = null;
		ResultSet setComments = null;
		
        try {
            Connection conn = getConnection();

            statementPlot = conn.createStatement();
            setPlots = statementPlot.executeQuery("SELECT * FROM plotmePlots WHERE world = '" + world + "'");
            int size = 0;
            while (setPlots.next()) {
            	size++;
    			int idX = setPlots.getInt("idX");
    			int idZ = setPlots.getInt("idZ");
    			String owner = setPlots.getString("owner");
    			int topX = setPlots.getInt("topX");
    			int bottomX = setPlots.getInt("bottomX");
    			int topZ = setPlots.getInt("topZ");
    			int bottomZ = setPlots.getInt("bottomZ");
    			String biome = setPlots.getString("biome");
    			java.sql.Date expireddate = setPlots.getDate("expireddate");
    			boolean finished = setPlots.getBoolean("finished");
    			HashSet<String> allowed = new HashSet<String>();
    			List<String[]> comments = new ArrayList<String[]>();
    			
    			statementAllowed = conn.createStatement();
    			setAllowed = statementAllowed.executeQuery("SELECT * FROM plotmeAllowed WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND world = '" + world + "'");
    			
    			while (setAllowed.next()) {
    				allowed.add(setAllowed.getString("player"));
    			}
    			
    			if (setAllowed != null) {
    				setAllowed.close();
    			}
    			
    			statementComment = conn.createStatement();
    			setComments = statementComment.executeQuery("SELECT * FROM plotmeComments WHERE idX = '" + idX + "' AND idZ = '" + idZ + "' AND world = '" + world + "'");
    			
    			while (setComments.next()) {
    				String[] comment = new String[2];
    				comment[0] = setComments.getString("player");
    				comment[1] = setComments.getString("comment");
    				comments.add(comment);
    			}
    			
    			Plot plot = new Plot(owner, world, topX, bottomX, topZ, bottomZ, biome, expireddate, finished, allowed, comments);
                ret.put("" + idX + ";" + idZ, plot);
            }
            PlotMe.logger.info(PlotMe.PREFIX + " " + size + " plots loaded");
        } catch (SQLException ex) {
        	PlotMe.logger.severe(PlotMe.PREFIX + " Load Exception :");
        	PlotMe.logger.severe("  " + ex.getMessage());
        } finally {
            try {
                if (statementPlot != null)
                	statementPlot.close();
                if (statementAllowed != null)
                	statementAllowed.close();
                if (statementComment != null)
                	statementComment.close();
                if (setPlots != null)
                	setPlots.close();
                if (setComments != null)
                	setComments.close();
                if (setAllowed != null)
                	setAllowed.close();
            } catch (SQLException ex) {
            	PlotMe.logger.severe(PlotMe.PREFIX + " Load Exception (on close) :");
            	PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
        return ret;
    }
    
}
