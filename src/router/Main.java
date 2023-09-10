package router;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    final static String[] typeNames = {"nullType", "Bug", "Dragon", "Electric", "Fighting", "Fire", "Flying", "Ghost", "Grass", "Ground", 
                                "Ice", "Normal", "Poison", "Psychic", "Rock", "Water", "Dark", "Steel", "Fairy"};
    Map<Tab, Route> routeTabMap = new HashMap<Tab, Route>();
    Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        
        window.setTitle("Pokémon Solo Run Router");
        window.setMinHeight(300);
        window.setMinWidth(600);

        // Set behavior for closing the program
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram(window);
        });

        // Create the layout for the window in a BorderPane 
        BorderPane mainWindowLayout = new BorderPane();

        // Initialize the tab pane in the middle of the screen and set properties
        TabPane routeTabs = new TabPane();
        routeTabs.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        routeTabs.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        mainWindowLayout.setCenter(routeTabs);

        // Initialize the tree structure that displays routes on the left of the layout
        TreeView<String> routeTree = generateRouteTree(routeTabs);
        mainWindowLayout.setLeft(routeTree);

        // Initialize the menu bar for the top of the layout
        MenuBar menuBar = generateMenu(window);
        mainWindowLayout.setTop(menuBar);

        // Initialize the main scene using mainWindowLayout
        Scene scene = new Scene(mainWindowLayout, 1024, 768);
        window.setScene(scene);
        window.show();
    }

    /*
     * Creates and returns the menu bar that is displayed at the top of the layout.
     * Its current functionality includes:
     * File
     * View
     */
    private MenuBar generateMenu(Stage window) {
        /*
         * Create the "File" menu with the following functionality:
         * Exit - exits the program
         */
        Menu fileMenu = new Menu("_File");
        MenuItem exitProgramItem = new MenuItem("E_xit");
        exitProgramItem.setOnAction(e -> closeProgram(window));
        fileMenu.getItems().add(exitProgramItem);

        /*
         * Create the "View" menu with the following functionality:
         * Appearance - a submenu that currently does nothing but will allow various layout pieces to be hidden
         */
        Menu viewMenu = new Menu("_View");
        Menu appearanceItem = new Menu("_Appearance");
        viewMenu.getItems().add(appearanceItem);

        // Create the menu bar and add all relevant menus to the bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, viewMenu);

        return menuBar;
    }

    /*
     * Initializes a tree that shows the routes that have been created 
     */
    private TreeView<String> generateRouteTree(TabPane routeTabs) {    
        TreeItem<String> routeList = new TreeItem<>("Routes");
        routeList.setExpanded(true);

        // Create tree nodes for routes of different games
        TreeItem<String> rbBranch = createRouteBranch("RB Routes", routeList);
        TreeItem<String> yellowBranch = createRouteBranch("Yellow Routes", routeList);
        TreeItem<String> gscBranch = createRouteBranch("GSC Routes", routeList);

        // Populate the tree with all available routes
        searchRoutes(rbBranch, "resources\\routes\\rb_routes");
        searchRoutes(yellowBranch, "resources\\routes\\yellow_routes");
        searchRoutes(gscBranch, "resources\\routes\\gsc_routes");

        // Initialize the tree with the root TreeItem and return it
        TreeView<String> routeTree = new TreeView<>(routeList);
        routeTree.setMinWidth(75);
        routeTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent event) {

                // Handle double click events by opening the route into the TabPane
                if(event.getClickCount() == 2) {
                    TreeItem<String> item = routeTree.getSelectionModel().getSelectedItem();
                    try {
                        TreeItem<String> parent = item.getParent();
                        if (item.isLeaf()) {
                            String tabName = parent.getValue();
                            tabName = tabName.substring(0, tabName.length() - 7) + ": " + item.getValue();
                            loadNewRoute(routeTabs, tabName);
                        }
                    }
                    catch (Exception e) {}
                }
            }
            
        });
        return routeTree;
    }

    /*
     * Make a branch of a TreeItem<String> given a parent and a name
     */
    private TreeItem<String> createRouteBranch(String routeName, TreeItem<String> parent) {
        TreeItem<String> newBranch = new TreeItem<>(routeName);
        parent.getChildren().add(newBranch);
        return newBranch;
    }

    /*
     * Search through the routes directory and create a branch for each route found 
     */
    private void searchRoutes(TreeItem<String> parent, String directory) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            String routeName = file.getName();
            routeName = routeName.substring(4, routeName.length() - 4);
            createRouteBranch(routeName, parent);
        }
    }

    /*
     * Load a new route into the tab pane if it is not already loaded
     * Set the new tab to be the active tab
     */
    private void loadNewRoute(TabPane routes, String routeName) throws SQLException {
        
        /* 
         * Check if the route is already open
         * If so, set it to be the focused route
         */
        for (Tab tab : routes.getTabs()) {
            if (tab.getText().equals(routeName)) {
                routes.getSelectionModel().select(tab);
                return;
            }
        }

        // Create a new Route object for the opened route and add its Tab to routes
        Route newRoute = new Route(routeName);
        routes.getTabs().add(newRoute.getRouteTab());
        routes.getSelectionModel().select(newRoute.getRouteTab());
        routeTabMap.put(newRoute.getRouteTab(), newRoute);

        // Set tab closing behavior
        newRoute.getRouteTab().setOnCloseRequest(e -> closeTab(newRoute.getRouteTab()));
    }

    /*
     * A function that closes the program. At the moment it always just automatically closes,
     * but in the future it will prompt the user to take action if a route needs to be saved.
     */
    private void closeProgram(Stage window) {
        boolean confirmation = true;            // will add more functionality to this later
        if (confirmation) window.close();
    }

    /*
     * A function that closes a tab in the routeTabs TabPane. 
     * It currently just closes the tab with no action, but will later prompt the user to save changes to the route.
     */
    private void closeTab(Tab closingTab) {
        /*
         * Add something here later to save the route
         */
        routeTabMap.remove(closingTab);
    }

}