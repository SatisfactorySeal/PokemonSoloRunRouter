package router;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Route {

    final int methodColumnTableSize = 25;
    final int typeColumnTableSize = 44;

    private String game;            // the game for the route
    private int generation;         // the generation the game exists in
    private String pokemonName;     // name of the Pokemon being routed
    private String pokemonID;       // ID of the Pokemon being routed
    private boolean evolution;      // True if evolution allowed in the route
    private ArrayList<String> evolutionIDs;     // list of IDs of all Pokemon in the evolution line
    private String activePokemon;   // ID of the current Pokemon in the evolution line
    private Tab routeTab;
    private BorderPane tabLayout;
    private VBox moveListHolder;
    private HBox movepoolLabelAndBox;
    private Label movepoolLabel;
    private ArrayList<DisplayedMove> movepool;
    private ObservableList<DisplayedMove> movesAcrossEvolutionLine;
    private FilteredList<DisplayedMove> movesForOneEvolution;
    private TableView<DisplayedMove> moveTable;
    private ComboBox<String> pokemonSelector;

    private int[] IVs = new int[6];

    /*
     * things that need to be added to the route
     * ArrayList or LinkedList of RouteEvent objects
     */

    /*
     * Default constructor for the Route class
     */
    public Route() {
        game = "RB";
        pokemonID = "3";
        pokemonName = "Venusaur";
        generateRouteLayout();
    }

    /*
     * Constructor for the Route class with input routeName
     * routeName will be of the format Game: PokemonName
     * where Game is the game the route is for and
     * PokemonName is the name of the final Pokemon
     */
    public Route(String routeName) throws SQLException {
        String[] routeNameParts = routeName.split(": ");
        game = routeNameParts[0];
        pokemonName = routeNameParts[1];
        pokemonID = DatabaseConnection.getPokemonIDFromName(pokemonName, 1);
        generateRouteLayout();
    }

    /*
     * Sets the generation variable based on the String game
     * Generation 1: "RB" or "Yellow"
     * Generation 2: "GSC"
     * Generation 3: "RS" or "FRLG" or "Emerald"
     *          Note: Colosseum and XD might be added eventually?
     * Generation 4: "DP" or "HGSS" or "Plat"
     * Generation 5: "BW" or "B2W2"
     * Generation 6: "XY" or "ORAS"
     * Generation 7: "SuMo" or "USUM"
     *          Note: Let's Go might be added eventually?
     * Generation 8: "SwSh" or "BDSP" or "LA"
     * Generation 9: "SV"
     */
    private void setGameGeneration() {
        switch(game) {
            case "GSC":
                generation = 2;
                break;
            case "RS":
            case "FRLG":
            case "Emerald":
                generation = 3;
                break;
            case "DP":
            case "HGSS":
            case "Plat":
                generation = 4;
                break;
            case "BW":
            case "B2W2":
                generation = 5;
                break;
            case "XY":
            case "ORAS":
                generation = 6;
                break;
            case "SuMo":
            case "USUM":
                generation = 7;
                break;
            case "SwSh":
            case "BDSP":
            case "LA":
                generation = 8;
                break;
            case "SV":
                generation = 9;
                break;
            case "RB":
            case "Yellow":
            default:
                generation = 1;
                break;
        }
    }

    /*
     * Generates all extra data needed for the route layout.
     * It takes care of setting game generation, retrieving information
     * on the evolution line, generating the JavaFX controls for the
     * tab, and displaying the movepool for the active Pokémon. 
     */
    private void generateRouteLayout() {
        setGameGeneration();
        activePokemon = pokemonID;

        // Set Pokémon IVs to maximum (can be changed later for different Hidden Power types)
        if (generation <= 2) {
            for (int i = 0; i < 6; i++) {
                this.IVs[i] = 15;
            }
        }
        else
            for (int i = 0; i < 6; i++) {
                this.IVs[i] = 31;
        }

        // Get a list of IDs for any Pokémon in the evolution line
        evolution = true;
        if (evolution) {
            try {
                evolutionIDs = DatabaseConnection.getEvolutionLineIDs(pokemonID, generation);
            } 
            // If there is a SQLException just add the active Pokémon ID
            catch (SQLException e) { 
                evolutionIDs = new ArrayList<String>();
                evolutionIDs.add(pokemonID);
            }
        }
        else {
            evolutionIDs = new ArrayList<String>();
            evolutionIDs.add(pokemonID);
        }

        // Evolution variable should always be off if the Pokémon has no evolution line
        if (evolutionIDs.size() == 1) evolution = false;

        // Generate the tab for the main JavaFX window and set up its layout
        routeTab = new Tab(game + ": " + pokemonName);
        tabLayout = new BorderPane();
        moveListHolder = new VBox();
        routeTab.setContent(tabLayout);
        tabLayout.setRight(moveListHolder);

        // Get the movepool for all Pokémon in the evolution line
        movesAcrossEvolutionLine = FXCollections.observableArrayList();
        
        // Add each learnable move for each Pokémon in the evolution line
        try {
            for (String id : evolutionIDs) {
                movepool = DatabaseConnection.getPokemonMovepool(id, generation);
                movepool.forEach(move -> movesAcrossEvolutionLine.add(move));
            }
        } catch (SQLException e) {  }

        // Get a list of the learnable level up, TM, and HM moves for the currently active Pokémon
        movesForOneEvolution = new FilteredList<>(movesAcrossEvolutionLine, 
                        i -> i.getPokemonID().equals(pokemonID) && ((i.getMethod() == "Level")
                                                                    || (i.getMethod() == "TM")
                                                                    || (i.getMethod() == "HM")));

        /*
         * Create the movepool table to display all the learnable moves
         * Eventually this will likely be moved to its own function
         */ 
        moveTable = new TableView<DisplayedMove>();
        moveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        // Column for the method of learning the move
        TableColumn<DisplayedMove, String> methodColumn = new TableColumn<DisplayedMove, String>("Way");
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("methodValue"));
        methodColumn.setStyle("-fx-alignment: CENTER");
        methodColumn.setPrefWidth(methodColumnTableSize);
        
        // Column for the move name
        TableColumn<DisplayedMove, String> moveColumn = new TableColumn<DisplayedMove, String>("Move");
        moveColumn.setCellValueFactory(new PropertyValueFactory<>("moveName"));

        // Column for the type of the move
        TableColumn<DisplayedMove, String> typeColumn = new TableColumn<DisplayedMove, String>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setStyle("-fx-alignment: CENTER");
        typeColumn.setPrefWidth(typeColumnTableSize);

        // Add all columns to moveTable and add the moves to the table
        moveTable.getColumns().add(methodColumn);
        moveTable.getColumns().add(moveColumn);
        moveTable.getColumns().add(typeColumn);
        moveTable.setItems(movesForOneEvolution);

        // A comboBox that allows the user to select the movepool of any Pokémon in the evolution line 
        pokemonSelector = new ComboBox<String>();
        for (String item : evolutionIDs) {
            pokemonSelector.getItems().add(item);
        }
        pokemonSelector.getSelectionModel().selectLast();

        // Action for selecting the movepool to be displayed
        pokemonSelector.setOnAction(e -> changeActivePokemon(pokemonSelector.getValue()));

        // Creating the HBox that displays the header and the selector
        movepoolLabel = new Label("Moves");
        movepoolLabelAndBox = new HBox(10);
        movepoolLabelAndBox.getChildren().add(movepoolLabel);
        if (evolution) movepoolLabelAndBox.getChildren().add(pokemonSelector);
        movepoolLabelAndBox.setAlignment(Pos.CENTER);

        moveListHolder.getChildren().add(movepoolLabelAndBox);
        moveListHolder.getChildren().add(moveTable);
    }

    /*
     * Changes the active Pokémon ID to newID
     * Also re-filters the movepool to show moves learned by the Pokémon with the active ID
     */
    private void changeActivePokemon(String newID) {
        activePokemon = newID;
        movesForOneEvolution.setPredicate(i -> i.getPokemonID().equals(activePokemon) 
                                                && ((i.getMethod() == "Level")
                                                    || (i.getMethod() == "TM")
                                                    || (i.getMethod() == "HM")));
    }

    // Getters for some of the identifying data stored in a Route object 
    
    public String getGame() {
        return game;
    }

    public int getGeneration() {
        return generation;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public String getPokemonID() {
        return pokemonID;
    }

    public boolean getEvolution() {
        return evolution;
    }

    public Tab getRouteTab() {
        return routeTab;
    }

    public int[] getIVs() {
        return this.IVs;
    }

    public void setIVs(int[] IVs) {
        this.IVs = IVs;
    }
}
