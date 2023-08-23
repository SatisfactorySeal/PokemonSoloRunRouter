package router;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    static Connection conn;
    static Statement stat;
    static ResultSet rs;

    // Load the database for the generation provided
    private static void loadDatabase(int gen) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:resources\\pokemonData\\gen" + gen + "Data.db");
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

    // Retrieve data from the provided column and ID from the appropriate database
    // This is going to have to be reworked when I change the database structure in the future
    private static String getPokemonData(int ID, int gen, String columnName) throws SQLException {
        if (gen != 1) return "failure";   // will change when data for future gens is added
        try {
            loadDatabase(gen);
            rs = stat.executeQuery("Select " + columnName + " FROM rbyStats WHERE ID = " + ID);
            while (rs.next()) {
                return rs.getString(columnName);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeDatabase();
        }
        return "failure";
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
     * Retrieve the Pokemon base HP stat for a given ID and generation
     * Returns 0 if the stat cannot be found for any reason
     */
    public static int getPokemonBaseHP(int ID, int gen) throws SQLException {
        String result = getPokemonData(ID, gen, "BaseHP");
        if (result == "failure") return 0;
        return Integer.parseInt(result);
    }

    /*
     * Retrieve the Pokemon base attack stat for a given ID and generation
     * Returns 0 if the stat cannot be found for any reason
     */
    public static int getPokemonBaseAttack(int ID, int gen) throws SQLException {
        String result = getPokemonData(ID, gen, "BaseAtk");
        if (result == "failure") return 0;
        return Integer.parseInt(result);
    }

    /*
     * Retrieve the Pokemon base defense stat for a given ID and generation
     * Returns 0 if the stat cannot be found for any reason
     */
    public static int getPokemonBaseDefense(int ID, int gen) throws SQLException {
        String result = getPokemonData(ID, gen, "BaseDef");
        if (result == "failure") return 0;
        return Integer.parseInt(result);
    }

    /*
     * Retrieve the Pokemon base speed stat for a given ID and generation
     * Returns 0 if the stat cannot be found for any reason
     */
    public static int getPokemonBaseSpeed(int ID, int gen) throws SQLException {
        String result = getPokemonData(ID, gen, "BaseSpe");
        if (result == "failure") return 0;
        return Integer.parseInt(result);
    }

    /*
     * Retrieve the Pokemon base special attack stat for a given ID and generation
     * Returns 0 if the stat cannot be found for any reason
     * Note: In generation 1, special attack and special defense are combined into one stat: special
     */
    public static int getPokemonBaseSpecialAttack(int ID, int gen) throws SQLException {
        String result;
        if (gen == 1) result = getPokemonData(ID, gen, "BaseSpc");
        else result = getPokemonData(ID, gen, "BaseSpA");
        if (result == "failure") return 0;
        return Integer.parseInt(result);
    }

    /*
     * Retrieve the Pokemon base special defense stat for a given ID and generation
     * Returns 0 if the stat cannot be found for any reason
     * Note: In generation 1, special attack and special defense are combined into one stat: special
     */
    public static int getPokemonBaseSpecialDefense(int ID, int gen) throws SQLException {
        String result;
        if (gen == 1) result = getPokemonData(ID, gen, "BaseSpc");
        else result = getPokemonData(ID, gen, "BaseSpD");
        if (result == "failure") return 0;
        return Integer.parseInt(result);
    }

    // Eventually add a function to retrieve all base stats at once, not always good reason to do it separately
    // public static int[] getPokemonBaseStats(int ID, int gen) throws SQLException {}

    /*
     * Retrieve the Pokemon experience group for a given ID and generation
     * Returns "Medium Fast" as the default if exp group cannot be found for any reason
     */
    public static String getPokemonExperienceGroup(int ID, int gen) throws SQLException {
        String result = getPokemonData(ID, gen, "ExpGroup");
        if (result == "failure") return "Medium Fast";
        return result;
    }

    /*
     * Retrieve the Pokemon base experience yield for a given ID and generation
     * Returns 0 if the stat cannot be found for any reason
     */
    public static int getPokemonExperienceYield(int ID, int gen) throws SQLException {
        String result = getPokemonData(ID, gen, "ExpYield");
        if (result == "failure") return 0;
        return Integer.parseInt(result);
    }

    // Later: add ways to obtain base form and evolution methods

}
