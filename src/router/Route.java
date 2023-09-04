package router;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Route {

    private String game;            // the game for the route
    private String pokemonName;     // name of the Pokemon being routed
    private String pokemonID;       // ID of the Pokemon being routed
    private boolean evolution;      // True if evolution allowed in the route
    private Tab routeTab;
    private BorderPane tabLayout;
    private VBox moveListHolder;
    private Label movepoolLabel;
    private ArrayList<DisplayedMove> movepool;
    private TableView<DisplayedMove> moveTable;

    /*
     * things that the route needs to have
     * tab (for the main tabpane)
     * borderpane (for inside the tab)
     * vbox (for the move list on the right of the borderpane)
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

    private void generateRouteLayout() {
        evolution = true;
        routeTab = new Tab(game + ": " + pokemonName);
        tabLayout = new BorderPane();
        moveListHolder = new VBox();
        movepoolLabel = new Label("Moves");
        routeTab.setContent(tabLayout);
        tabLayout.setRight(moveListHolder);

        try {
            movepool = DatabaseConnection.getPokemonMovepool(Integer.parseInt(pokemonID), 1);
        } catch (NumberFormatException e) { }
          catch (SQLException e) { }

        moveTable = new TableView<DisplayedMove>();
        
        TableColumn<DisplayedMove, String> methodColumn = new TableColumn<DisplayedMove, String>("Way");
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("methodValue"));
        
        TableColumn<DisplayedMove, String> moveColumn = new TableColumn<DisplayedMove, String>("Move");
        moveColumn.setCellValueFactory(new PropertyValueFactory<>("moveName"));

        TableColumn<DisplayedMove, String> typeColumn = new TableColumn<DisplayedMove, String>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        moveTable.getColumns().add(methodColumn);
        moveTable.getColumns().add(moveColumn);
        moveTable.getColumns().add(typeColumn);

        for (DisplayedMove item : movepool) {
            if (item.getMethod() == "Level" || item.getMethod() == "TM" || item.getMethod() == "HM")
                moveTable.getItems().add(item);
        }

        moveListHolder.getChildren().add(movepoolLabel);
        moveListHolder.getChildren().add(moveTable);

    }

    public String getGame() {
        return game;
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
}
