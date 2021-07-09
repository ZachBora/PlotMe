package com.worldcretornica.plotme;

import org.bukkit.entity.Player;
import java.io.IOException;
import org.bukkit.plugin.Plugin;
import java.util.Map;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.sql.PreparedStatement;
import org.bukkit.World;
import java.util.UUID;
import java.util.List;
import java.sql.Date;
import com.worldcretornica.plotme.utils.UUIDFetcher;
import java.util.ArrayList;
import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;

public class SqlManager {
    private static Connection conn;
    public static final String sqlitedb = "/plots.db";
    
    public static Connection initialize() {
        try {
            if (PlotMe.usemySQL) {
                Class.forName("com.mysql.jdbc.Driver");
                (SqlManager.conn = DriverManager.getConnection(PlotMe.mySQLconn, PlotMe.mySQLuname, PlotMe.mySQLpass)).setAutoCommit(false);
            }
            else {
                Class.forName("org.sqlite.JDBC");
                (SqlManager.conn = DriverManager.getConnection("jdbc:sqlite:" + PlotMe.configpath + "/plots.db")).setAutoCommit(false);
            }
        }
        catch (SQLException exception) {
            PlotMe.logger.severe("SQL exception on initialize :");
            PlotMe.logger.severe("  " + exception.getMessage());
        }
        catch (ClassNotFoundException exception) {
            PlotMe.logger.severe("You need the SQLite/MySQL library. :");
            PlotMe.logger.severe("  " + exception.getMessage());
        }
        createTable();
        return SqlManager.conn;
    }
    
    public static String getSchema() {
        final String conn = PlotMe.mySQLconn;
        if (conn.lastIndexOf("/") > 0) {
            return conn.substring(conn.lastIndexOf("/") + 1);
        }
        return "";
    }
    
    public static void UpdateTables() {
        Statement statement = null;
        ResultSet set = null;
        try {
            final Connection conn = getConnection();
            statement = conn.createStatement();
            final String schema = getSchema();
            if (PlotMe.usemySQL) {
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmePlots' AND column_name='customprice'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD customprice double NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmePlots' AND column_name='forsale'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD forsale boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmePlots' AND column_name='finisheddate'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD finisheddate varchar(16) NULL;");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmePlots' AND column_name='protected'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD protected boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmePlots' AND column_name='auctionned'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD auctionned boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmePlots' AND column_name='auctionenddate'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD auctionenddate varchar(16) NULL;");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmePlots' AND column_name='currentbidder'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbidder varchar(32) NULL;");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmePlots' AND column_name='currentbid'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbid double NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmePlots' AND column_name='ownerid'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD ownerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmeAllowed' AND column_name='playerid'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmeAllowed ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmeDenied' AND column_name='playerid'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmeDenied ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmeComments' AND column_name='playerid'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmeComments ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                set = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME='plotmePlots' AND column_name='currentbidderId'");
                if (!set.next()) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbidderId blob(16) NULL;");
                    conn.commit();
                }
                set.close();
            }
            else {
                boolean found;
                String column;
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("customprice")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD customprice double NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("forsale")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD forsale boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("finisheddate")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD finisheddate varchar(16) NULL;");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("protected")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD protected boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("auctionned")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD auctionned boolean NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("auctionenddate")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD auctionenddate varchar(16) NULL;");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("currentbidder")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbidder varchar(32) NULL;");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("currentbid")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbid double NOT NULL DEFAULT '0';");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("ownerid")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD ownerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmeAllowed`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("playerid")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmeAllowed ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmeDenied`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("playerid")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmeDenied ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmeComments`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("playerid")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmeComments ADD playerid blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                for (found = false, set = statement.executeQuery("PRAGMA table_info(`plotmePlots`)"); set.next() && !found; found = true) {
                    column = set.getString(2);
                    if (column.equalsIgnoreCase("currentbidderId")) {}
                }
                if (!found) {
                    statement.execute("ALTER TABLE plotmePlots ADD currentbidderId blob(16) NULL;");
                    conn.commit();
                }
                set.close();
                found = false;
            }
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Update table exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (statement != null) {
                    statement.close();
                }
                if (set != null) {
                    set.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Update table exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (set != null) {
                    set.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Update table exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    public static Connection getConnection() {
        if (SqlManager.conn == null) {
            SqlManager.conn = initialize();
        }
        if (PlotMe.usemySQL) {
            try {
                if (!SqlManager.conn.isValid(10)) {
                    SqlManager.conn = initialize();
                }
            }
            catch (SQLException ex) {
                PlotMe.logger.severe("Failed to check SQL status :");
                PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
        return SqlManager.conn;
    }
    
    public static void closeConnection() {
        if (SqlManager.conn != null) {
            try {
                if (PlotMe.usemySQL) {
                    if (SqlManager.conn.isValid(10)) {
                        SqlManager.conn.close();
                    }
                    SqlManager.conn = null;
                }
                else {
                    SqlManager.conn.close();
                    SqlManager.conn = null;
                }
            }
            catch (SQLException ex) {
                PlotMe.logger.severe("Error on Connection close :");
                PlotMe.logger.severe("  " + ex.getMessage());
            }
        }
    }
    
    private static boolean tableExists(final String name) {
        ResultSet rs = null;
        try {
            final Connection conn = getConnection();
            final DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables((String)null, (String)null, name, (String[])null);
            if (!rs.next()) {
                return false;
            }
            return true;
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Table Check Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            return false;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Table Check SQL Exception (on closing) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    private static void createTable() {
        Statement st = null;
        try {
            final Connection conn = getConnection();
            st = conn.createStatement();
            if (!tableExists("plotmePlots")) {
                st.executeUpdate("CREATE TABLE `plotmePlots` (`idX` INTEGER,`idZ` INTEGER,`owner` varchar(100) NOT NULL,`world` varchar(32) NOT NULL DEFAULT '0',`topX` INTEGER NOT NULL DEFAULT '0',`bottomX` INTEGER NOT NULL DEFAULT '0',`topZ` INTEGER NOT NULL DEFAULT '0',`bottomZ` INTEGER NOT NULL DEFAULT '0',`biome` varchar(32) NOT NULL DEFAULT '0',`expireddate` DATETIME NULL,`finished` boolean NOT NULL DEFAULT '0',`customprice` double NOT NULL DEFAULT '0',`forsale` boolean NOT NULL DEFAULT '0',`finisheddate` varchar(16) NULL,`protected` boolean NOT NULL DEFAULT '0',`auctionned` boolean NOT NULL DEFAULT '0',`auctionenddate` varchar(16) NULL,`currentbid` double NOT NULL DEFAULT '0',`currentbidder` varchar(32) NULL,`currentbidderId` blob(16),`ownerId` blob(16),PRIMARY KEY (idX, idZ, world));");
                conn.commit();
            }
            if (!tableExists("plotmeComments")) {
                st.executeUpdate("CREATE TABLE `plotmeComments` (`idX` INTEGER,`idZ` INTEGER,`world` varchar(32) NOT NULL,`commentid` INTEGER,`player` varchar(32) NOT NULL,`comment` text,`playerid` blob(16),PRIMARY KEY (idX, idZ, world, commentid));");
                conn.commit();
            }
            if (!tableExists("plotmeAllowed")) {
                st.executeUpdate("CREATE TABLE `plotmeAllowed` (`idX` INTEGER,`idZ` INTEGER,`world` varchar(32) NOT NULL,`player` varchar(32) NOT NULL,`playerid` blob(16),PRIMARY KEY (idX, idZ, world, player));");
                conn.commit();
            }
            if (!tableExists("plotmeDenied")) {
                st.executeUpdate("CREATE TABLE `plotmeDenied` (`idX` INTEGER,`idZ` INTEGER,`world` varchar(32) NOT NULL,`player` varchar(32) NOT NULL,`playerid` blob(16),PRIMARY KEY (idX, idZ, world, player));");
                conn.commit();
            }
            UpdateTables();
            if (PlotMe.usemySQL) {
                final File sqlitefile = new File(PlotMe.configpath + "/plots.db");
                if (!sqlitefile.exists()) {
                    return;
                }
                PlotMe.logger.info("Modifying database for MySQL support");
                PlotMe.logger.info("Trying to import plots from plots.db");
                Class.forName("org.sqlite.JDBC");
                final Connection sqliteconn = DriverManager.getConnection("jdbc:sqlite:" + PlotMe.configpath + "/plots.db");
                sqliteconn.setAutoCommit(false);
                final Statement slstatement = sqliteconn.createStatement();
                final ResultSet setPlots = slstatement.executeQuery("SELECT * FROM plotmePlots");
                final Statement slAllowed = sqliteconn.createStatement();
                ResultSet setAllowed = null;
                final Statement slDenied = sqliteconn.createStatement();
                ResultSet setDenied = null;
                final Statement slComments = sqliteconn.createStatement();
                ResultSet setComments = null;
                int size = 0;
                while (setPlots.next()) {
                    final int idX = setPlots.getInt("idX");
                    final int idZ = setPlots.getInt("idZ");
                    final String owner = setPlots.getString("owner");
                    final String world = setPlots.getString("world").toLowerCase();
                    final int topX = setPlots.getInt("topX");
                    final int bottomX = setPlots.getInt("bottomX");
                    final int topZ = setPlots.getInt("topZ");
                    final int bottomZ = setPlots.getInt("bottomZ");
                    final String biome = setPlots.getString("biome");
                    final Date expireddate = setPlots.getDate("expireddate");
                    final boolean finished = setPlots.getBoolean("finished");
                    final PlayerList allowed = new PlayerList();
                    final PlayerList denied = new PlayerList();
                    final List<String[]> comments = new ArrayList<String[]>();
                    final double customprice = setPlots.getDouble("customprice");
                    final boolean forsale = setPlots.getBoolean("forsale");
                    final String finisheddate = setPlots.getString("finisheddate");
                    final boolean protect = setPlots.getBoolean("protected");
                    final boolean auctionned = setPlots.getBoolean("auctionned");
                    final String currentbidder = setPlots.getString("currentbidder");
                    final double currentbid = setPlots.getDouble("currentbid");
                    final byte[] byOwner = setPlots.getBytes("ownerId");
                    final byte[] byBidder = setPlots.getBytes("currentbidderid");
                    UUID ownerId = null;
                    UUID currentbidderid = null;
                    if (byOwner != null) {
                        ownerId = UUIDFetcher.fromBytes(byOwner);
                    }
                    if (byBidder != null) {
                        currentbidderid = UUIDFetcher.fromBytes(byBidder);
                    }
                    setAllowed = slAllowed.executeQuery(new StringBuilder().append("SELECT * FROM plotmeAllowed WHERE idX = '").append(idX).append("' AND idZ = '").append(idZ).append("' AND world = '").append(world).append("'").toString());
                    while (setAllowed.next()) {
                        final byte[] byPlayerId = setAllowed.getBytes("playerid");
                        if (byPlayerId == null) {
                            allowed.put(setAllowed.getString("player"));
                        }
                        else {
                            allowed.put(setAllowed.getString("player"), UUIDFetcher.fromBytes(byPlayerId));
                        }
                    }
                    if (setAllowed != null) {
                        setAllowed.close();
                    }
                    setDenied = slDenied.executeQuery(new StringBuilder().append("SELECT * FROM plotmeDenied WHERE idX = '").append(idX).append("' AND idZ = '").append(idZ).append("' AND world = '").append(world).append("'").toString());
                    while (setDenied.next()) {
                        final byte[] byPlayerId = setDenied.getBytes("playerid");
                        if (byPlayerId == null) {
                            denied.put(setDenied.getString("player"));
                        }
                        else {
                            denied.put(setDenied.getString("player"), UUIDFetcher.fromBytes(byPlayerId));
                        }
                    }
                    if (setDenied != null) {
                        setDenied.close();
                    }
                    setComments = slComments.executeQuery(new StringBuilder().append("SELECT * FROM plotmeComments WHERE idX = '").append(idX).append("' AND idZ = '").append(idZ).append("' AND world = '").append(world).append("'").toString());
                    while (setComments.next()) {
                        final String[] comment = new String[3];
                        final byte[] byPlayerId2 = setComments.getBytes("playerid");
                        if (byPlayerId2 != null) {
                            comment[2] = UUIDFetcher.fromBytes(byPlayerId2).toString();
                        }
                        else {
                            comment[2] = null;
                        }
                        comment[0] = setComments.getString("player");
                        comment[1] = setComments.getString("comment");
                        comments.add(comment);
                    }
                    final Plot plot = new Plot(owner, ownerId, world, topX, bottomX, topZ, bottomZ, biome, expireddate, finished, allowed, comments, new StringBuilder().append("").append(idX).append(";").append(idZ).toString(), customprice, forsale, finisheddate, protect, currentbidder, currentbidderid, currentbid, auctionned, denied);
                    addPlot(plot, idX, idZ, topX, bottomX, topZ, bottomZ);
                    ++size;
                }
                PlotMe.logger.info(new StringBuilder().append("Imported ").append(size).append(" plots from ").append("/plots.db").toString());
                if (slstatement != null) {
                    slstatement.close();
                }
                if (slAllowed != null) {
                    slAllowed.close();
                }
                if (slComments != null) {
                    slComments.close();
                }
                if (slDenied != null) {
                    slDenied.close();
                }
                if (setPlots != null) {
                    setPlots.close();
                }
                if (setComments != null) {
                    setComments.close();
                }
                if (setAllowed != null) {
                    setAllowed.close();
                }
                if (sqliteconn != null) {
                    sqliteconn.close();
                }
                PlotMe.logger.info("Renaming /plots.db to /plots.db.old");
                if (!sqlitefile.renameTo(new File(PlotMe.configpath, "/plots.db.old"))) {
                    PlotMe.logger.warning("Failed to rename /plots.db! Please rename this manually!");
                }
            }
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Create Table Exception :");
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex2) {
            PlotMe.logger.severe("You need the SQLite library :");
            PlotMe.logger.severe("  " + ex2.getMessage());
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (SQLException ex3) {
                PlotMe.logger.severe("Could not create the table (on close) :");
                PlotMe.logger.severe("  " + ex3.getMessage());
            }
        }
    }
    
    public static void addPlot(final Plot plot, final int idX, final int idZ, final World w) {
        addPlot(plot, idX, idZ, PlotManager.topX(plot.id, w), PlotManager.bottomX(plot.id, w), PlotManager.topZ(plot.id, w), PlotManager.bottomZ(plot.id, w));
    }
    
    public static void addPlot(final Plot plot, final int idX, final int idZ, final int topX, final int bottomX, final int topZ, final int bottomZ) {
        PreparedStatement ps = null;
        final StringBuilder strSql = new StringBuilder();
        try {
            final Connection conn = getConnection();
            strSql.append("INSERT INTO plotmePlots (idX, idZ, owner, world, topX, bottomX, topZ, bottomZ, ");
            strSql.append("biome, expireddate, finished, customprice, forsale, finisheddate, protected,");
            strSql.append("auctionned, auctionenddate, currentbid, currentbidder, currentbidderId, ownerId) ");
            strSql.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps = conn.prepareStatement(strSql.toString());
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, plot.owner);
            ps.setString(4, plot.world.toLowerCase());
            ps.setInt(5, topX);
            ps.setInt(6, bottomX);
            ps.setInt(7, topZ);
            ps.setInt(8, bottomZ);
            ps.setString(9, plot.biome.name());
            ps.setDate(10, plot.expireddate);
            ps.setBoolean(11, plot.finished);
            ps.setDouble(12, plot.customprice);
            ps.setBoolean(13, plot.forsale);
            ps.setString(14, plot.finisheddate);
            ps.setBoolean(15, plot.protect);
            ps.setBoolean(16, plot.auctionned);
            ps.setDate(17, (Date)null);
            ps.setDouble(18, plot.currentbid);
            ps.setString(19, plot.currentbidder);
            if (plot.currentbidderId != null) {
                ps.setBytes(20, UUIDFetcher.toBytes(plot.currentbidderId));
            }
            else {
                ps.setBytes(20, (byte[])null);
            }
            if (plot.ownerId != null) {
                ps.setBytes(21, UUIDFetcher.toBytes(plot.ownerId));
            }
            else {
                ps.setBytes(21, (byte[])null);
            }
            ps.executeUpdate();
            conn.commit();
            if (plot.allowed != null && plot.allowed.getAllPlayers() != null) {
                final HashMap<String, UUID> allowed = plot.allowed.getAllPlayers();
                for (final String key : allowed.keySet()) {
                    addPlotAllowed(key, (UUID)allowed.get(key), idX, idZ, plot.world);
                }
            }
            if (plot.denied != null && plot.denied.getAllPlayers() != null) {
                final HashMap<String, UUID> denied = plot.denied.getAllPlayers();
                for (final String key : denied.keySet()) {
                    addPlotDenied(key, (UUID)denied.get(key), idX, idZ, plot.world);
                }
            }
            if (plot.comments != null && plot.comments.size() > 0) {
                int commentid = 1;
                for (final String[] comments : plot.comments) {
                    String strUUID = "";
                    UUID uuid = null;
                    if (comments.length >= 3) {
                        strUUID = comments[2];
                        try {
                            uuid = UUID.fromString(strUUID);
                        }
                        catch (Exception ex3) {}
                    }
                    addPlotComment(comments, commentid, idX, idZ, plot.world, uuid);
                    ++commentid;
                }
            }
            if (plot.owner != null && !plot.owner.equals("") && plot.ownerId == null) {
                fetchOwnerUUIDAsync(idX, idZ, plot.world.toLowerCase(), plot.owner);
            }
            if (plot.currentbidder != null && !plot.currentbidder.equals("") && plot.currentbidderId == null) {
                fetchBidderUUIDAsync(idX, idZ, plot.world.toLowerCase(), plot.currentbidder);
            }
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Insert Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    public static void updatePlot(final int idX, final int idZ, final String world, final String field, final Object value) {
        PreparedStatement ps = null;
        try {
            final Connection conn = getConnection();
            ps = conn.prepareStatement("UPDATE plotmePlots SET " + field + " = ? WHERE idX = ? AND idZ = ? AND world = ?");
            if (value instanceof UUID) {
                ps.setBytes(1, UUIDFetcher.toBytes((UUID)value));
            }
            else {
                ps.setObject(1, value);
            }
            ps.setInt(2, idX);
            ps.setInt(3, idZ);
            ps.setString(4, world.toLowerCase());
            ps.executeUpdate();
            conn.commit();
            if (field.equalsIgnoreCase("owner")) {
                fetchOwnerUUIDAsync(idX, idZ, world, value.toString());
            }
            else if (field.equalsIgnoreCase("currentbidder")) {
                fetchBidderUUIDAsync(idX, idZ, world, value.toString());
            }
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Insert Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    public static void updateTable(final String tablename, final int idX, final int idZ, final String world, final String field, final Object value) {
        PreparedStatement ps = null;
        try {
            final Connection conn = getConnection();
            ps = conn.prepareStatement("UPDATE " + tablename + " SET " + field + " = ? WHERE idX = ? AND idZ = ? AND world = ?");
            if (value instanceof UUID) {
                ps.setBytes(1, UUIDFetcher.toBytes((UUID)value));
            }
            else {
                ps.setObject(1, value);
            }
            ps.setInt(2, idX);
            ps.setInt(3, idZ);
            ps.setString(4, world.toLowerCase());
            ps.executeUpdate();
            conn.commit();
            if (field.equalsIgnoreCase("owner")) {
                fetchOwnerUUIDAsync(idX, idZ, world, value.toString());
            }
            else if (field.equalsIgnoreCase("currentbidder")) {
                fetchBidderUUIDAsync(idX, idZ, world, value.toString());
            }
            else if (field.equalsIgnoreCase("player") && tablename.equalsIgnoreCase("plotmeallowed")) {}
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Insert Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    public static void addPlotAllowed(final String player, final int idX, final int idZ, final String world) {
        addPlotAllowed(player, null, idX, idZ, world);
    }
    
    public static void addPlotAllowed(final String player, final UUID playerid, final int idX, final int idZ, final String world) {
        PreparedStatement ps = null;
        try {
            final Connection conn = getConnection();
            ps = conn.prepareStatement("INSERT INTO plotmeAllowed (idX, idZ, player, world, playerid) VALUES (?,?,?,?,?)");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world.toLowerCase());
            if (playerid != null) {
                ps.setBytes(5, UUIDFetcher.toBytes(playerid));
            }
            else {
                ps.setBytes(5, (byte[])null);
            }
            ps.executeUpdate();
            conn.commit();
            if (playerid == null) {
                fetchAllowedUUIDAsync(idX, idZ, world, player);
            }
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Insert Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    @Deprecated
    public static void addPlotDenied(final String player, final int idX, final int idZ, final String world) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            addPlotDenied(player, null, idX, idZ, world);
        }
        else {
            addPlotDenied(player, op.getUniqueId(), idX, idZ, world);
        }
    }
    
    public static void addPlotDenied(final String player, final UUID playerid, final int idX, final int idZ, final String world) {
        PreparedStatement ps = null;
        try {
            final Connection conn = getConnection();
            ps = conn.prepareStatement("INSERT INTO plotmeDenied (idX, idZ, player, world, playerid) VALUES (?,?,?,?,?)");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, player);
            ps.setString(4, world.toLowerCase());
            if (playerid != null) {
                ps.setBytes(5, UUIDFetcher.toBytes(playerid));
            }
            else {
                ps.setBytes(5, (byte[])null);
            }
            ps.executeUpdate();
            conn.commit();
            if (playerid == null) {
                fetchDeniedUUIDAsync(idX, idZ, world, player);
            }
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Insert Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    public static void addPlotComment(final String[] comment, final int commentid, final int idX, final int idZ, final String world) {
        UUID uuid = null;
        if (comment.length > 2) {
            try {
                uuid = UUID.fromString(comment[2]);
            }
            catch (IllegalArgumentException ex) {}
        }
        addPlotComment(comment, commentid, idX, idZ, world, uuid);
    }
    
    public static void addPlotComment(final String[] comment, final int commentid, final int idX, final int idZ, final String world, final UUID uuid) {
        PreparedStatement ps = null;
        try {
            final Connection conn = getConnection();
            ps = conn.prepareStatement("INSERT INTO plotmeComments (idX, idZ, commentid, player, comment, world, playerid) VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setInt(3, commentid);
            ps.setString(4, comment[0]);
            ps.setString(5, comment[1]);
            ps.setString(6, world.toLowerCase());
            if (uuid != null) {
                ps.setBytes(7, UUIDFetcher.toBytes(uuid));
            }
            else {
                ps.setBytes(7, (byte[])null);
            }
            ps.executeUpdate();
            conn.commit();
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Insert Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Insert Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    public static void deletePlot(final int idX, final int idZ, final String world) {
        PreparedStatement ps = null;
        try {
            final Connection conn = getConnection();
            ps = conn.prepareStatement("DELETE FROM plotmeComments WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            ps.close();
            ps = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            ps.close();
            ps = conn.prepareStatement("DELETE FROM plotmeDenied WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            ps.close();
            ps = conn.prepareStatement("DELETE FROM plotmePlots WHERE idX = ? and idZ = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(3, world);
            ps.executeUpdate();
            conn.commit();
            ps.close();
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Delete Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Delete Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Delete Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    public static void deletePlotComment(final int idX, final int idZ, final int commentid, final String world) {
        PreparedStatement ps = null;
        final ResultSet set = null;
        try {
            final Connection conn = getConnection();
            ps = conn.prepareStatement("DELETE FROM plotmeComments WHERE idX = ? and idZ = ? and commentid = ? and LOWER(world) = ?");
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setInt(3, commentid);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Delete Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Delete Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Delete Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    @Deprecated
    public static void deletePlotAllowed(final int idX, final int idZ, final String player, final String world) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            deletePlotAllowed(idX, idZ, player, null, world);
        }
        else {
            deletePlotAllowed(idX, idZ, player, op.getUniqueId(), world);
        }
    }
    
    public static void deletePlotAllowed(final int idX, final int idZ, final String player, final UUID playerid, final String world) {
        PreparedStatement ps = null;
        final ResultSet set = null;
        try {
            final Connection conn = getConnection();
            if (playerid == null) {
                ps = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE idX = ? and idZ = ? and player = ? and LOWER(world) = ?");
                ps.setString(3, player);
            }
            else {
                ps = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE idX = ? and idZ = ? and playerid = ? and LOWER(world) = ?");
                ps.setBytes(3, UUIDFetcher.toBytes(playerid));
            }
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Delete Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Delete Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Delete Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    @Deprecated
    public static void deletePlotDenied(final int idX, final int idZ, final String player, final String world) {
        final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            deletePlotDenied(idX, idZ, player, null, world);
        }
        else {
            deletePlotDenied(idX, idZ, player, op.getUniqueId(), world);
        }
    }
    
    public static void deletePlotDenied(final int idX, final int idZ, final String player, final UUID playerid, final String world) {
        PreparedStatement ps = null;
        final ResultSet set = null;
        try {
            final Connection conn = getConnection();
            if (playerid == null) {
                ps = conn.prepareStatement("DELETE FROM plotmeDenied WHERE idX = ? and idZ = ? and player = ? and LOWER(world) = ?");
                ps.setString(3, player);
            }
            else {
                ps = conn.prepareStatement("DELETE FROM plotmeDenied WHERE idX = ? and idZ = ? and playerid = ? and LOWER(world) = ?");
                ps.setBytes(3, UUIDFetcher.toBytes(playerid));
            }
            ps.setInt(1, idX);
            ps.setInt(2, idZ);
            ps.setString(4, world);
            ps.executeUpdate();
            conn.commit();
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Delete Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Delete Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (set != null) {
                    set.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Delete Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
    }
    
    public static HashMap<String, Plot> getPlots(final String world) {
        final HashMap<String, Plot> ret = new HashMap<String, Plot>();
        Statement statementPlot = null;
        Statement statementAllowed = null;
        Statement statementDenied = null;
        Statement statementComment = null;
        ResultSet setPlots = null;
        ResultSet setAllowed = null;
        ResultSet setDenied = null;
        ResultSet setComments = null;
        try {
            final Connection conn = getConnection();
            statementPlot = conn.createStatement();
            setPlots = statementPlot.executeQuery("SELECT * FROM plotmePlots WHERE LOWER(world) = '" + world + "'");
            int size = 0;
            while (setPlots.next()) {
                ++size;
                final int idX = setPlots.getInt("idX");
                final int idZ = setPlots.getInt("idZ");
                final String owner = setPlots.getString("owner");
                final int topX = setPlots.getInt("topX");
                final int bottomX = setPlots.getInt("bottomX");
                final int topZ = setPlots.getInt("topZ");
                final int bottomZ = setPlots.getInt("bottomZ");
                final String biome = setPlots.getString("biome");
                final Date expireddate = setPlots.getDate("expireddate");
                final boolean finished = setPlots.getBoolean("finished");
                final PlayerList allowed = new PlayerList();
                final PlayerList denied = new PlayerList();
                final List<String[]> comments = new ArrayList<String[]>();
                final double customprice = setPlots.getDouble("customprice");
                final boolean forsale = setPlots.getBoolean("forsale");
                final String finisheddate = setPlots.getString("finisheddate");
                final boolean protect = setPlots.getBoolean("protected");
                final String currentbidder = setPlots.getString("currentbidder");
                final double currentbid = setPlots.getDouble("currentbid");
                final boolean auctionned = setPlots.getBoolean("auctionned");
                final byte[] byOwner = setPlots.getBytes("ownerId");
                final byte[] byBidder = setPlots.getBytes("currentbidderid");
                UUID ownerId = null;
                UUID currentbidderid = null;
                if (byOwner != null) {
                    ownerId = UUIDFetcher.fromBytes(byOwner);
                }
                if (byBidder != null) {
                    currentbidderid = UUIDFetcher.fromBytes(byBidder);
                }
                statementAllowed = conn.createStatement();
                setAllowed = statementAllowed.executeQuery(new StringBuilder().append("SELECT * FROM plotmeAllowed WHERE idX = '").append(idX).append("' AND idZ = '").append(idZ).append("' AND LOWER(world) = '").append(world).append("'").toString());
                while (setAllowed.next()) {
                    final byte[] byPlayerId = setAllowed.getBytes("playerid");
                    if (byPlayerId == null) {
                        allowed.put(setAllowed.getString("player"));
                    }
                    else {
                        allowed.put(setAllowed.getString("player"), UUIDFetcher.fromBytes(byPlayerId));
                    }
                }
                if (setAllowed != null) {
                    setAllowed.close();
                }
                statementDenied = conn.createStatement();
                setDenied = statementDenied.executeQuery(new StringBuilder().append("SELECT * FROM plotmeDenied WHERE idX = '").append(idX).append("' AND idZ = '").append(idZ).append("' AND LOWER(world) = '").append(world).append("'").toString());
                while (setDenied.next()) {
                    final byte[] byPlayerId = setDenied.getBytes("playerid");
                    if (byPlayerId == null) {
                        denied.put(setDenied.getString("player"));
                    }
                    else {
                        denied.put(setDenied.getString("player"), UUIDFetcher.fromBytes(byPlayerId));
                    }
                }
                if (setDenied != null) {
                    setDenied.close();
                }
                statementComment = conn.createStatement();
                setComments = statementComment.executeQuery(new StringBuilder().append("SELECT * FROM plotmeComments WHERE idX = '").append(idX).append("' AND idZ = '").append(idZ).append("' AND LOWER(world) = '").append(world).append("'").toString());
                while (setComments.next()) {
                    final String[] comment = { setComments.getString("player"), setComments.getString("comment"), null };
                    final byte[] byPlayerId2 = setComments.getBytes("playerid");
                    if (byPlayerId2 != null) {
                        comment[2] = UUIDFetcher.fromBytes(byPlayerId2).toString();
                    }
                    comments.add(comment);
                }
                final Plot plot = new Plot(owner, ownerId, world, topX, bottomX, topZ, bottomZ, biome, expireddate, finished, allowed, comments, new StringBuilder().append("").append(idX).append(";").append(idZ).toString(), customprice, forsale, finisheddate, protect, currentbidder, currentbidderid, currentbid, auctionned, denied);
                ret.put(new StringBuilder().append("").append(idX).append(";").append(idZ).toString(), plot);
            }
            PlotMe.logger.info(new StringBuilder().append(" ").append(size).append(" plots loaded").toString());
        }
        catch (SQLException ex) {
            PlotMe.logger.severe("Load Exception :");
            PlotMe.logger.severe("  " + ex.getMessage());
            ex.printStackTrace();
            try {
                if (statementPlot != null) {
                    statementPlot.close();
                }
                if (statementAllowed != null) {
                    statementAllowed.close();
                }
                if (statementComment != null) {
                    statementComment.close();
                }
                if (setPlots != null) {
                    setPlots.close();
                }
                if (setComments != null) {
                    setComments.close();
                }
                if (setAllowed != null) {
                    setAllowed.close();
                }
            }
            catch (SQLException exception2) {
                PlotMe.logger.severe("Load Exception (on close) :");
                PlotMe.logger.severe("  " + exception2.getMessage());
            }
        }
        finally {
            try {
                if (statementPlot != null) {
                    statementPlot.close();
                }
                if (statementAllowed != null) {
                    statementAllowed.close();
                }
                if (statementComment != null) {
                    statementComment.close();
                }
                if (setPlots != null) {
                    setPlots.close();
                }
                if (setComments != null) {
                    setComments.close();
                }
                if (setAllowed != null) {
                    setAllowed.close();
                }
            }
            catch (SQLException ex2) {
                PlotMe.logger.severe("Load Exception (on close) :");
                PlotMe.logger.severe("  " + ex2.getMessage());
            }
        }
        return ret;
    }
    
    public static void plotConvertToUUIDAsynchronously() {
        Bukkit.getServer().getScheduler().runTaskAsynchronously((Plugin)PlotMe.self, (Runnable)new Runnable() {
            public void run() {
                PlotMe.logger.info("Checking if conversion to UUID needed...");
                boolean boConversion = false;
                Statement statementPlayers = null;
                PreparedStatement psOwnerId = null;
                PreparedStatement psCurrentBidderId = null;
                PreparedStatement psAllowedPlayerId = null;
                PreparedStatement psDeniedPlayerId = null;
                PreparedStatement psCommentsPlayerId = null;
                PreparedStatement psDeleteOwner = null;
                PreparedStatement psDeleteCurrentBidder = null;
                PreparedStatement psDeleteAllowed = null;
                PreparedStatement psDeleteDenied = null;
                PreparedStatement psDeleteComments = null;
                ResultSet setPlayers = null;
                int nbConverted = 0;
                String sql = "";
                int count = 0;
                try {
                    final Connection conn = SqlManager.getConnection();
                    statementPlayers = conn.createStatement();
                    sql = "SELECT LOWER(owner) as Name FROM plotmePlots WHERE NOT owner IS NULL AND Not owner LIKE 'group:%' AND Not owner LIKE '%*%' AND ownerid IS NULL GROUP BY LOWER(owner) ";
                    sql += "UNION SELECT LOWER(currentbidder) as Name FROM plotmePlots WHERE NOT currentbidder IS NULL AND currentbidderid IS NULL GROUP BY LOWER(currentbidder) ";
                    sql += "UNION SELECT LOWER(player) as Name FROM plotmeAllowed WHERE NOT player IS NULL AND Not player LIKE 'group:%' AND Not player LIKE '%*%' AND playerid IS NULL GROUP BY LOWER(player) ";
                    sql += "UNION SELECT LOWER(player) as Name FROM plotmeDenied WHERE NOT player IS NULL AND Not player LIKE 'group:%' AND Not player LIKE '%*%' AND playerid IS NULL GROUP BY LOWER(player) ";
                    sql += "UNION SELECT LOWER(player) as Name FROM plotmeComments WHERE NOT player IS NULL AND Not player LIKE 'group:%' AND Not player LIKE '%*%' AND playerid IS NULL GROUP BY LOWER(player)";
                    PlotMe.logger.info("Verifying if database needs conversion");
                    setPlayers = statementPlayers.executeQuery(sql);
                    if (setPlayers.next()) {
                        final List<String> names = new ArrayList<String>();
                        psDeleteOwner = conn.prepareStatement("UPDATE plotmePlots SET owner = '' WHERE owner = ? ");
                        psDeleteCurrentBidder = conn.prepareStatement("UPDATE plotmePlots SET currentbidder = null WHERE currentbidder = ? ");
                        psDeleteAllowed = conn.prepareStatement("DELETE FROM plotmeAllowed WHERE player = ? ");
                        psDeleteDenied = conn.prepareStatement("DELETE FROM plotmeDenied WHERE player = ? ");
                        psDeleteComments = conn.prepareStatement("DELETE FROM plotmeComments WHERE player = ? ");
                        PlotMe.logger.info("Starting to convert plots to UUID");
                        do {
                            final String name = setPlayers.getString("Name");
                            if (!name.equals("")) {
                                if (name.matches("^[a-zA-Z0-9_]{1,16}$")) {
                                    names.add(name);
                                }
                                else {
                                    PlotMe.logger.warning("Invalid name found : " + name + ". Removing from database.");
                                    psDeleteOwner.setString(1, name);
                                    psDeleteOwner.executeUpdate();
                                    psDeleteCurrentBidder.setString(1, name);
                                    psDeleteCurrentBidder.executeUpdate();
                                    psDeleteAllowed.setString(1, name);
                                    psDeleteAllowed.executeUpdate();
                                    psDeleteDenied.setString(1, name);
                                    psDeleteDenied.executeUpdate();
                                    psDeleteComments.setString(1, name);
                                    psDeleteComments.executeUpdate();
                                    conn.commit();
                                }
                            }
                        } while (setPlayers.next());
                        psDeleteOwner.close();
                        psDeleteCurrentBidder.close();
                        psDeleteAllowed.close();
                        psDeleteDenied.close();
                        psDeleteComments.close();
                        final UUIDFetcher fetcher = new UUIDFetcher(names);
                        Map<String, UUID> response = null;
                        try {
                            PlotMe.logger.info(new StringBuilder().append("Fetching ").append(names.size()).append(" UUIDs from Mojang servers...").toString());
                            response = fetcher.call();
                            PlotMe.logger.info(new StringBuilder().append("Finished fetching ").append(response.size()).append(" UUIDs. Starting database update.").toString());
                        }
                        catch (Exception e) {
                            PlotMe.logger.warning("Exception while running UUIDFetcher");
                            e.printStackTrace();
                        }
                        if (response.size() > 0) {
                            psOwnerId = conn.prepareStatement("UPDATE plotmePlots SET ownerid = ? WHERE LOWER(owner) = ? AND ownerid IS NULL");
                            psCurrentBidderId = conn.prepareStatement("UPDATE plotmePlots SET currentbidderid = ? WHERE LOWER(currentbidder) = ? AND currentbidderid IS NULL");
                            psAllowedPlayerId = conn.prepareStatement("UPDATE plotmeAllowed SET playerid = ? WHERE LOWER(player) = ? AND playerid IS NULL");
                            psDeniedPlayerId = conn.prepareStatement("UPDATE plotmeDenied SET playerid = ? WHERE LOWER(player) = ? AND playerid IS NULL");
                            psCommentsPlayerId = conn.prepareStatement("UPDATE plotmeComments SET playerid = ? WHERE LOWER(player) = ? AND playerid IS NULL");
                            for (final String key : response.keySet()) {
                                count = 0;
                                psOwnerId.setBytes(1, UUIDFetcher.toBytes((UUID)response.get(key)));
                                psOwnerId.setString(2, key.toLowerCase());
                                count += psOwnerId.executeUpdate();
                                psCurrentBidderId.setBytes(1, UUIDFetcher.toBytes((UUID)response.get(key)));
                                psCurrentBidderId.setString(2, key.toLowerCase());
                                count += psCurrentBidderId.executeUpdate();
                                psAllowedPlayerId.setBytes(1, UUIDFetcher.toBytes((UUID)response.get(key)));
                                psAllowedPlayerId.setString(2, key.toLowerCase());
                                count += psAllowedPlayerId.executeUpdate();
                                psDeniedPlayerId.setBytes(1, UUIDFetcher.toBytes((UUID)response.get(key)));
                                psDeniedPlayerId.setString(2, key.toLowerCase());
                                count += psDeniedPlayerId.executeUpdate();
                                psCommentsPlayerId.setBytes(1, UUIDFetcher.toBytes((UUID)response.get(key)));
                                psCommentsPlayerId.setString(2, key.toLowerCase());
                                psCommentsPlayerId.executeUpdate();
                                conn.commit();
                                if (count > 0) {
                                    ++nbConverted;
                                }
                                else {
                                    PlotMe.logger.warning("Unable to update player '" + key + "'");
                                }
                            }
                            psOwnerId.close();
                            psCurrentBidderId.close();
                            psAllowedPlayerId.close();
                            psDeniedPlayerId.close();
                            psCommentsPlayerId.close();
                            for (final PlotMapInfo pmi : PlotMe.plotmaps.values()) {
                                for (final Plot plot : pmi.plots.values()) {
                                    for (final Map.Entry<String, UUID> player : response.entrySet()) {
                                        if (plot.ownerId == null && plot.owner != null && plot.owner.equalsIgnoreCase((String)player.getKey())) {
                                            plot.owner = (String)player.getKey();
                                            plot.ownerId = (UUID)player.getValue();
                                        }
                                        if (plot.currentbidderId == null && plot.currentbidder != null && plot.currentbidder.equalsIgnoreCase((String)player.getKey())) {
                                            plot.currentbidder = (String)player.getKey();
                                            plot.currentbidderId = (UUID)player.getValue();
                                        }
                                        plot.allowed.replace((String)player.getKey(), (UUID)player.getValue());
                                        plot.denied.replace((String)player.getKey(), (UUID)player.getValue());
                                        for (final String[] comment : plot.comments) {
                                            if (comment.length > 2 && comment[2] == null && comment[0] != null && comment[0].equalsIgnoreCase((String)player.getKey())) {
                                                comment[0] = (String)player.getKey();
                                                comment[2] = ((UUID)player.getValue()).toString();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        boConversion = true;
                        PlotMe.logger.info(new StringBuilder().append(nbConverted).append(" players converted").toString());
                    }
                    setPlayers.close();
                    statementPlayers.close();
                    if (boConversion) {
                        PlotMe.logger.info("Plot conversion finished");
                    }
                    else {
                        PlotMe.logger.info("No plot conversion needed");
                    }
                }
                catch (SQLException ex) {
                    PlotMe.logger.severe("Conversion to UUID failed :");
                    PlotMe.logger.severe("  " + ex.getMessage());
                    for (final StackTraceElement e2 : ex.getStackTrace()) {
                        PlotMe.logger.severe("  " + e2.toString());
                    }
                    try {
                        if (statementPlayers != null) {
                            statementPlayers.close();
                        }
                        if (psOwnerId != null) {
                            psOwnerId.close();
                        }
                        if (psCurrentBidderId != null) {
                            psCurrentBidderId.close();
                        }
                        if (psAllowedPlayerId != null) {
                            psAllowedPlayerId.close();
                        }
                        if (psDeniedPlayerId != null) {
                            psDeniedPlayerId.close();
                        }
                        if (psCommentsPlayerId != null) {
                            psCommentsPlayerId.close();
                        }
                        if (setPlayers != null) {
                            setPlayers.close();
                        }
                        if (psDeleteOwner != null) {
                            psDeleteOwner.close();
                        }
                        if (psDeleteCurrentBidder != null) {
                            psDeleteCurrentBidder.close();
                        }
                        if (psDeleteAllowed != null) {
                            psDeleteAllowed.close();
                        }
                        if (psDeleteDenied != null) {
                            psDeleteDenied.close();
                        }
                        if (psDeleteComments != null) {
                            psDeleteComments.close();
                        }
                    }
                    catch (SQLException exception2) {
                        PlotMe.logger.severe("Conversion to UUID failed (on close) :");
                        PlotMe.logger.severe("  " + exception2.getMessage());
                        for (final StackTraceElement ele : exception2.getStackTrace()) {
                            PlotMe.logger.severe("  " + ele.toString());
                        }
                    }
                }
                finally {
                    try {
                        if (statementPlayers != null) {
                            statementPlayers.close();
                        }
                        if (psOwnerId != null) {
                            psOwnerId.close();
                        }
                        if (psCurrentBidderId != null) {
                            psCurrentBidderId.close();
                        }
                        if (psAllowedPlayerId != null) {
                            psAllowedPlayerId.close();
                        }
                        if (psDeniedPlayerId != null) {
                            psDeniedPlayerId.close();
                        }
                        if (psCommentsPlayerId != null) {
                            psCommentsPlayerId.close();
                        }
                        if (setPlayers != null) {
                            setPlayers.close();
                        }
                        if (psDeleteOwner != null) {
                            psDeleteOwner.close();
                        }
                        if (psDeleteCurrentBidder != null) {
                            psDeleteCurrentBidder.close();
                        }
                        if (psDeleteAllowed != null) {
                            psDeleteAllowed.close();
                        }
                        if (psDeleteDenied != null) {
                            psDeleteDenied.close();
                        }
                        if (psDeleteComments != null) {
                            psDeleteComments.close();
                        }
                    }
                    catch (SQLException ex2) {
                        PlotMe.logger.severe("Conversion to UUID failed (on close) :");
                        PlotMe.logger.severe("  " + ex2.getMessage());
                        for (final StackTraceElement e3 : ex2.getStackTrace()) {
                            PlotMe.logger.severe("  " + e3.toString());
                        }
                    }
                }
            }
        });
    }
    
    public static void fetchOwnerUUIDAsync(final int idX, final int idZ, final String world, final String owner) {
        _fetchUUIDAsync(idX, idZ, world, "owner", owner);
    }
    
    public static void fetchBidderUUIDAsync(final int idX, final int idZ, final String world, final String bidder) {
        _fetchUUIDAsync(idX, idZ, world, "bidder", bidder);
    }
    
    public static void fetchAllowedUUIDAsync(final int idX, final int idZ, final String world, final String allowed) {
        _fetchUUIDAsync(idX, idZ, world, "allowed", allowed);
    }
    
    public static void fetchDeniedUUIDAsync(final int idX, final int idZ, final String world, final String denied) {
        _fetchUUIDAsync(idX, idZ, world, "denied", denied);
    }
    
    private static void _fetchUUIDAsync(final int idX, final int idZ, final String world, final String Property, final String name) {
        if (PlotMe.self.initialized) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously((Plugin)PlotMe.self, (Runnable)new Runnable() {
                public void run() {
                    PreparedStatement ps = null;
                    try {
                        final Connection conn = SqlManager.getConnection();
                        final Player p = Bukkit.getPlayerExact(name);
                        UUID uuid = null;
                        String newname = name;
                        if (p != null) {
                            uuid = p.getUniqueId();
                            newname = p.getName();
                        }
                        else {
                            final List<String> names = new ArrayList<String>();
                            names.add(name);
                            final UUIDFetcher fetcher = new UUIDFetcher(names);
                            Map<String, UUID> response = null;
                            try {
                                PlotMe.logger.info(new StringBuilder().append("Fetching ").append(names.size()).append(" UUIDs from Mojang servers...").toString());
                                response = fetcher.call();
                                PlotMe.logger.info(new StringBuilder().append("Received ").append(response.size()).append(" UUIDs. Starting database update...").toString());
                                if (response.size() > 0) {
                                    uuid = ((UUID[])response.values().toArray((Object[])new UUID[0]))[0];
                                    newname = ((String[])response.keySet().toArray((Object[])new String[0]))[0];
                                }
                            }
                            catch (IOException e4) {
                                PlotMe.logger.warning("Unable to connect to Mojang server!");
                            }
                            catch (Exception e) {
                                PlotMe.logger.warning("Exception while running UUIDFetcher");
                                e.printStackTrace();
                            }
                        }
                        final String val$Property = Property;
                        switch (val$Property) {
                            case "owner": {
                                ps = conn.prepareStatement(new StringBuilder().append("UPDATE plotmePlots SET ownerid = ?, owner = ? WHERE LOWER(owner) = ? AND idX = '").append(idX).append("' AND idZ = '").append(idZ).append("' AND LOWER(world) = '").append(world).append("'").toString());
                                break;
                            }
                            case "bidder": {
                                ps = conn.prepareStatement(new StringBuilder().append("UPDATE plotmePlots SET currentbidderid = ?, currentbidder = ? WHERE LOWER(currentbidder) = ? AND idX = '").append(idX).append("' AND idZ = '").append(idZ).append("' AND LOWER(world) = '").append(world).append("'").toString());
                                break;
                            }
                            case "allowed": {
                                ps = conn.prepareStatement(new StringBuilder().append("UPDATE plotmeAllowed SET playerid = ?, player = ? WHERE LOWER(player) = ? AND idX = '").append(idX).append("' AND idZ = '").append(idZ).append("' AND LOWER(world) = '").append(world).append("'").toString());
                                break;
                            }
                            case "denied": {
                                ps = conn.prepareStatement(new StringBuilder().append("UPDATE plotmeDenied SET playerid = ?, player = ? WHERE LOWER(player) = ? AND idX = '").append(idX).append("' AND idZ = '").append(idZ).append("' AND LOWER(world) = '").append(world).append("'").toString());
                                break;
                            }
                            default: {
                                return;
                            }
                        }
                        if (uuid != null) {
                            ps.setBytes(1, UUIDFetcher.toBytes(uuid));
                        }
                        else {
                            ps.setBytes(1, (byte[])null);
                        }
                        ps.setString(2, newname);
                        ps.setString(3, name.toLowerCase());
                        ps.executeUpdate();
                        conn.commit();
                        ps.close();
                        if (uuid != null) {
                            final Plot plot = PlotManager.getPlotById(world, new StringBuilder().append("").append(idX).append(";").append(idZ).toString());
                            if (plot != null) {
                                final String val$Property2 = Property;
                                switch (val$Property2) {
                                    case "owner": {
                                        plot.owner = newname;
                                        plot.ownerId = uuid;
                                        break;
                                    }
                                    case "bidder": {
                                        plot.currentbidder = newname;
                                        plot.currentbidderId = uuid;
                                        break;
                                    }
                                    case "allowed": {
                                        plot.allowed.remove(name);
                                        plot.allowed.put(newname, uuid);
                                        break;
                                    }
                                    case "denied": {
                                        plot.denied.remove(name);
                                        plot.denied.put(newname, uuid);
                                        break;
                                    }
                                    default: {
                                        return;
                                    }
                                }
                            }
                            if (p == null) {
                                PlotMe.logger.info("UUID updated to Database!");
                            }
                        }
                    }
                    catch (SQLException ex) {
                        PlotMe.logger.severe("Conversion to UUID failed :");
                        PlotMe.logger.severe("  " + ex.getMessage());
                        for (final StackTraceElement e2 : ex.getStackTrace()) {
                            PlotMe.logger.severe("  " + e2.toString());
                        }
                        try {
                            if (ps != null) {
                                ps.close();
                            }
                        }
                        catch (SQLException exception2) {
                            PlotMe.logger.severe("Conversion to UUID failed (on close) :");
                            PlotMe.logger.severe("  " + exception2.getMessage());
                            for (final StackTraceElement element : exception2.getStackTrace()) {
                                PlotMe.logger.severe("  " + element.toString());
                            }
                        }
                    }
                    finally {
                        try {
                            if (ps != null) {
                                ps.close();
                            }
                        }
                        catch (SQLException ex2) {
                            PlotMe.logger.severe("Conversion to UUID failed (on close) :");
                            PlotMe.logger.severe("  " + ex2.getMessage());
                            for (final StackTraceElement e3 : ex2.getStackTrace()) {
                                PlotMe.logger.severe("  " + e3.toString());
                            }
                        }
                    }
                }
            });
        }
    }
    
    public static void updatePlotsNewUUID(final UUID uuid, final String newname) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously((Plugin)PlotMe.self, (Runnable)new Runnable() {
            public void run() {
                final PreparedStatement[] pss = new PreparedStatement[5];
                try {
                    final Connection conn = SqlManager.getConnection();
                    pss[0] = conn.prepareStatement("UPDATE plotmePlots SET owner = ? WHERE ownerid = ?");
                    pss[1] = conn.prepareStatement("UPDATE plotmePlots SET currentbidder = ? WHERE currentbidderid = ?");
                    pss[2] = conn.prepareStatement("UPDATE plotmeAllowed SET player = ? WHERE playerid = ?");
                    pss[3] = conn.prepareStatement("UPDATE plotmeDenied SET player = ? WHERE playerid = ?");
                    pss[4] = conn.prepareStatement("UPDATE plotmeComments SET player = ? WHERE playerid = ?");
                    for (final PreparedStatement ps : pss) {
                        ps.setString(1, newname);
                        ps.setBytes(2, UUIDFetcher.toBytes(uuid));
                        ps.executeUpdate();
                    }
                    conn.commit();
                    for (final PreparedStatement ps : pss) {
                        ps.close();
                    }
                }
                catch (SQLException ex) {
                    PlotMe.logger.severe("Update player in database from uuid failed :");
                    PlotMe.logger.severe("  " + ex.getMessage());
                    for (final StackTraceElement e : ex.getStackTrace()) {
                        PlotMe.logger.severe("  " + e.toString());
                    }
                    try {
                        for (final PreparedStatement ps2 : pss) {
                            if (ps2 != null) {
                                ps2.close();
                            }
                        }
                    }
                    catch (SQLException exception2) {
                        PlotMe.logger.severe("Update player in database from uuid failed (on close) :");
                        PlotMe.logger.severe("  " + exception2.getMessage());
                        for (final StackTraceElement element : exception2.getStackTrace()) {
                            PlotMe.logger.severe("  " + element.toString());
                        }
                    }
                }
                finally {
                    try {
                        for (final PreparedStatement ps3 : pss) {
                            if (ps3 != null) {
                                ps3.close();
                            }
                        }
                    }
                    catch (SQLException ex2) {
                        PlotMe.logger.severe("Update player in database from uuid failed (on close) :");
                        PlotMe.logger.severe("  " + ex2.getMessage());
                        for (final StackTraceElement e2 : ex2.getStackTrace()) {
                            PlotMe.logger.severe("  " + e2.toString());
                        }
                    }
                }
            }
        });
    }
    
    static {
        SqlManager.conn = null;
    }
}
