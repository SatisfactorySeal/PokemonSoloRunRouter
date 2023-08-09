package main;

import java.io.File;

import javafx.application.Application;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {
        window.setTitle("Pokémon Solo Run Router");
        window.setMinHeight(300);
        window.setMinWidth(400);

        // Set behavior for closing the program
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram(window);
        });

        // Create the layout for the window in a BorderPane 
        BorderPane mainWindowLayout = new BorderPane();

        // Initialize the tree structure that displays routes on the left of the layout
        TreeView<String> routeTree = generateRouteTree();

        // Initialize the menu bar for the top of the layout
        MenuBar menuBar = generateMenu(window);
        mainWindowLayout.setTop(menuBar);
        mainWindowLayout.setLeft(routeTree);

        /*
         * Create the left portion of the menu. 
         */

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
    private TreeView<String> generateRouteTree() {    
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
     * A function that closes the program. At the moment it always just automatically closes,
     * but in the future it will prompt the user to take action if a route needs to be saved.
     */
    private void closeProgram(Stage window) {
        boolean confirmation = true;            // will add more functionality to this later
        if (confirmation) window.close();
    }

}