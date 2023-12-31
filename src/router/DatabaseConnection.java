package router;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseConnection {

    private static Connection conn;
    private static Statement stat;
    private static ResultSet rs;

    // Load the database for the generation provided
    private static void loadDatabase() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:resources\\pokemonData\\pokemonData.db");
        stat = conn.createStatement();
    }

    // Close the database connection after completing requested database queries
    private static void closeDatabase() throws SQLException {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) { }
        }
        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException e) { }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) { }
        }
    }

    // Retrieve data from the provided column and ID from the appropriate database table
    // This is going to have to be reworked when I change the database structure in the future
    private static String getPokemonData(int ID, int gen, String columnName) throws SQLException {
        if (gen > 5 || gen < 0) return "failure";
        String tableName = "";
        if (gen == 1) tableName = "rbyStats";
        else if (gen <= 5) tableName = "bwStats";

        try {
            loadDatabase();
            rs = stat.executeQuery("SELECT " + columnName + " FROM " + tableName + " WHERE ID = " + ID);
            while (rs.next()) {
                return rs.getString(columnName);
            }
        }
        catch (SQLException e) { }
        finally { closeDatabase(); }
        return "failure";
    }

    /*
     * Retrieve the Pokemon ID for a given name and generation (right now just gen 1 for testing)
     * Returns 0 if the ID cannot be found for any reason 
     */
    public static String getPokemonIDFromName(String name, int gen) throws SQLException {
        String result = "failure";
        try {
            loadDatabase();
            rs = stat.executeQuery("SELECT ID FROM rbyStats WHERE Name = '" + name + "'");
            while (rs.next()) {
                result = rs.getString("ID");
            }
        } catch (SQLException e) { }
        finally { closeDatabase(); }
        if (result == "failure") return "N/A";
        return result;
    }

    /*
     * Retrieve the Pokemon name for a given ID and generation
     * Returns "N/A" if the name cannot be found for any reason
     */
    public static String getPokemonName(int ID, int gen) throws SQLException {
        String result = getPokemonData(ID, gen, "Name");
        if (result == "failure") return "N/A";
        return result;
    }

    /*
     * Retrieve the Pokemon first type for a given ID and generation
     * Returns 0 if the name cannot be found for any reason, which corresponds to nullType
     */
    public static int getPokemonType1(int ID, int gen) throws SQLException {
        String result = getPokemonData(ID, gen, "Type1");
        if (result == "failure") return 0;
        int type = Integer.parseInt(result);
        if (type > 15 && gen == 1) return 0; // dark, steel, and fairy are not types in gen 1
        if (type > 17 && gen < 6) return 0;  // fairy is not a type before gen 6
        if (type > 18) return 0;             // there are only 18 valid types
        return Integer.parseInt(result);
    }

    /*
     * Retrieve the Pokemon first type for a given ID and generation
     * Returns 0 if the name cannot be found for any reason, which corresponds to nullType
     * Note: 0 can also be returned if the Pokemon does not have a second type
     */
    public static int getPokemonType2(int ID, int gen) throws SQLException {
        String result = getPokemonData(ID, gen, "Type2");
        if (result == null || result == "failure") return 0;
        int type = Integer.parseInt(result);
        if (type > 15 && gen == 1) return 0; // dark, steel, and fairy are not types in gen 1
        if (type > 17 && gen < 6) return 0;  // fairy is not a type before gen 6
        if (type > 18) return 0;             // there are only 18 valid types
        return Integer.parseInt(result);
    }

    /*
     * Retrieve the movepool for a Pokemon with the given ID and generation (gen)
     * At the moment it only grabs data from Red and Blue so I can get that up and running
     */
    public static ArrayList<DisplayedMove> getPokemonMovepool(String ID, int gen) throws SQLException {
        ArrayList<DisplayedMove> pokemonMovepool = new ArrayList<DisplayedMove>();

        // table names for SQL select command, will add more functionality when future generations are added to database
        String movesetTableName = "rbyMovesets";
        String moveTableName = "rbyMoves";

        try {
            loadDatabase();
            rs = stat.executeQuery("SELECT * FROM " + movesetTableName + " AS MS INNER JOIN " 
                                    + moveTableName + " AS M ON MS.MoveID = M.ID "
                                    + "WHERE MS.PokemonID = " + ID 
                                    + " AND MS.Games LIKE '%RB%'" 
                                    + " ORDER BY MS.Method, MS.Condition");
            while (rs.next()) {
                // Generate a DisplayedMove object to add to the movepool list and add it to the movepool ArrayList
                DisplayedMove temp = new DisplayedMove(rs.getString("Method"), 
                                                        rs.getString("Condition"), 
                                                        rs.getString("Move"), 
                                                        Main.typeNames[rs.getInt("Type")], 
                                                        rs.getString("Power"), 
                                                        rs.getString("Accuracy"), 
                                                        rs.getInt("PP"),
                                                        ID);
                pokemonMovepool.add(temp);
            }
        } catch (SQLException e) { } 
        finally { closeDatabase(); }

        return pokemonMovepool;
    }

    /*
     * Retrieve all Pokémon in the evolution path by following the IDs of evolution lines through the database
     * Currently only works for Pokémon with 1 evolution path and only for gen 1 games
     * This means that Eeveelutions are not functional
     */
    public static ArrayList<String> getEvolutionLineIDs(String ID, int gen) throws SQLException {
        ArrayList<String> evolutionLine = new ArrayList<String>();
        String tableName;
        String baseForm;
        int evolutionPaths;

        if (gen == 1) tableName = "rbyStats";
        else if (gen <= 5) tableName = "bwStats";
        else return evolutionLine;

        try {
            loadDatabase();
            rs = stat.executeQuery("SELECT BaseForm, EvolutionPaths FROM " + tableName + " WHERE ID = " + ID);
            baseForm = rs.getString("BaseForm");
            evolutionPaths = rs.getInt("EvolutionPaths");
            do {
                evolutionLine.add(baseForm);
                rs = stat.executeQuery("SELECT EvolveTo, EvolutionPaths FROM " + tableName + " WHERE ID = " + baseForm);
                baseForm = rs.getString("EvolveTo");
                evolutionPaths = rs.getInt("EvolutionPaths");
            } while (evolutionPaths != 0);
        } catch (SQLException e) { }
        finally { closeDatabase(); }

        return evolutionLine;
    }

}
