package view;

import java.io.File;

import control.NavigationController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.TaskSchedule;

/**
 * Is the application window itself, creates the basis for the application by
 * setting up the navigation bar as well as the stage, and adding an icon & title
 * to the window.
 *
 */
public class ApplicationWindow extends Application {
    private Stage window;
    private HBox hboxTop;
    private Button btnCreatePerson, btnCreateTask, btnAddTaskDependencies, btnShowSchedule;
    private BorderPane mainLayout;
    private Scene scene;
    private TaskSchedule schedule;
    private Panes panes;

    private NavigationController navController;
    private NavigationController.NavigationWindowListener navWindowListener;

    /**
     * Initialises the main application window by setting up the stage, navigation bar, and creating 
     * an instance of the panes.
     *
     * @Override
     */
    public void start(Stage primaryStage) {
        this.window = primaryStage;
        schedule = new TaskSchedule();
        panes = new Panes(schedule);
        mainLayout = new BorderPane();
        navController = new NavigationController(panes, mainLayout, schedule);
        navWindowListener = navController.new NavigationWindowListener();
        initWidgets();
        window.setScene(scene);
        window.setTitle("Task Scheduling Application");
	 window.getIcons().add(new Image("file:///" + new File("src/main/java/view/icon.png").getAbsolutePath().replace("\\", "/")));
        window.setMinHeight(500);
        window.setMinWidth(800);
        window.show();
        window.setOnCloseRequest(navWindowListener);
    }

    private void initWidgets(){
        setHeader();
        addListeners();
        mainLayout.setCenter(panes.getPersonFormPane());
        scene = new Scene(mainLayout, 800, 500);
        scene.getStylesheets().add("file:///" + new File("src/main/java/view/Style.css").getAbsolutePath().replace("\\", "/"));
    }
    
    /*
     * Sets up the navigation bar
     */
    private void setHeader(){
        hboxTop = new HBox();
        hboxTop.setPadding(new Insets(15, 12, 15, 12));
        hboxTop.setSpacing(10);
        hboxTop.setStyle("-fx-background-color: #ffffff;");

        btnCreatePerson = new Button("New Person Form");
        btnCreatePerson.setId("Person");

        btnCreateTask = new Button("New Task Form");
        btnCreateTask.setId("Task");

        btnAddTaskDependencies = new Button("Add Task Dependencies");
        btnAddTaskDependencies.setId("Dependencies");

        btnShowSchedule = new Button("Show Schedule");
        btnShowSchedule.getStyleClass().add("button-schedule");
        btnShowSchedule.setId("Schedule");

        hboxTop.setAlignment(Pos.CENTER);
        hboxTop.getChildren().addAll(btnCreatePerson, btnCreateTask, btnAddTaskDependencies, btnShowSchedule);
        mainLayout.setTop(hboxTop);
    }

    private void addListeners(){
        btnCreatePerson.setOnAction(navController);
        btnCreateTask.setOnAction(navController);
        btnAddTaskDependencies.setOnAction(navController);
        btnShowSchedule.setOnAction(navController);
    }
}
