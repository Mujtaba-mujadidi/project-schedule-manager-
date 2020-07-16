package view;

import java.util.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.chart.XYChart.Data;
import model.Person;
import model.Task;
import model.TaskSchedule;

import java.util.HashMap;
import java.util.Observer;
import java.util.Random;

import control.DependencyListeners;
import control.PersonController;
import control.TaskController;

import view.GanttChart.ExtraData;

/**
 * Sets up and returns all four panes that will be available to the user by clicking the different
 * buttons on the navigation bar.
 * Also updates the dependency pane and schedule pane whenever new data is inserted into the system.
 *
 */
public class Panes implements Observer{

	private final int MAX_INPUT = 28;

	private GridPane personFormPane;
	private GridPane taskFormPane;
	private GridPane dependenciesPane;
	private GridPane schedulePane;

	private ComboBox<Task> cbTasks;
	private ListView<Task> lvTasks;
	private ListView<Task> lvDependencies;

	private PersonController personController;
	private TaskController taskController;
	private DependencyListeners dependencyListListener;
	private DependencyListeners.DependencyComboBoxListener dependencyComboBoxListener;

	private TaskSchedule schedule;

	private NumberAxis xAxisTasks;
	private CategoryAxis yAxisTasks;

	private NumberAxis xAxisPeople;
	private CategoryAxis yAxisPeople;

	private GanttChart<Number, String> chartTasks;
	private GanttChart<Number, String> chartPeople;
	
	/**
 	 * Sets up the person form, task form, dependency, and schedule panes.
 	 * Observes the passed in task scheduler, and calls update on it. This is to ensure that the panes
 	 * are displaying the correct information in the event that data was loaded from a previous session.
 	 * @param taskSchedule
 	 */
	public Panes(TaskSchedule taskSchedule){
		this.schedule = taskSchedule; schedule.addObserver(this);

		this.personFormPane = setUpAndGetPersonForm();
		this.taskFormPane = setUpAndGetTaskForm();
		this.dependenciesPane = setUpAndGetDependenciesForm();
		this.schedulePane = setUpAndGetScheduleForm();

		this.schedule.updateView();//to update the view after reading the files.
	}

	private GridPane setUpAndGetPersonForm(){
		GridPane personPane = new GridPane();
		personPane.setAlignment(Pos.CENTER);
		personPane.setVgap(10);
		personPane.setHgap(10);
		personPane.setPadding(new Insets(10));

		Label lblPersonTitle = new Label("Create Person");
		lblPersonTitle.getStyleClass().add("label-title");

		TextField tfFName = new TextField();
		TextField tfLName = new TextField();
		TextField tfEmail = new TextField();

		Button btnCreatePerson = new Button("Create Person");
		btnCreatePerson.setDisable(true);
		btnCreatePerson.getStyleClass().add("button-create");

		personPane.add(lblPersonTitle, 0, 0);
		personPane.add(new Label("First Name:"), 0, 1);
		personPane.add(tfFName, 1, 1);
		personPane.add(new Label("Last Name:"), 0, 2);
		personPane.add(tfLName, 1, 2);
		personPane.add(new Label("Email:"), 0, 3);
		personPane.add(tfEmail, 1, 3);
		personPane.add(btnCreatePerson, 1, 4);

		personController = new PersonController(schedule, MAX_INPUT, tfFName, tfLName, tfEmail, btnCreatePerson);

		tfFName.textProperty().addListener(personController);
		tfLName.textProperty().addListener(personController);
		tfEmail.textProperty().addListener(personController);
		btnCreatePerson.setOnAction(personController);
		return personPane;
	}


	private GridPane setUpAndGetTaskForm(){
		GridPane taskPane = new GridPane();
		taskPane.setAlignment(Pos.CENTER);
		taskPane.setVgap(10);
		taskPane.setHgap(10);
		taskPane.setPadding(new Insets(10));

		Label lblTaskTitle = new Label("Create Task");
		lblTaskTitle.getStyleClass().add("label-title");

		TextField tfTaskName = new TextField();
		tfTaskName.setId("tfTaskName");

		Slider sEffort;
		sEffort = new Slider();
		sEffort.setMin(1.0);
		sEffort.setMax(99.0);
		sEffort.setShowTickLabels(true);
		sEffort.setShowTickMarks(true);
		sEffort.setMajorTickUnit(49);
		sEffort.setMinorTickCount(4);

		Label lblEffortValue = new Label("50.0");
		lblEffortValue.setPrefWidth(50);
		DoubleProperty sliderValue = new SimpleDoubleProperty(50.0);
		sEffort.valueProperty().bindBidirectional(sliderValue);
		lblEffortValue.textProperty().bind(sliderValue.asString("%.2f"));

		Button btnCreateTask = new Button("Create Task");
		btnCreateTask.setDisable(true);
		btnCreateTask.getStyleClass().add("button-create");
		BooleanBinding validateInput = tfTaskName.textProperty().isEmpty();
		btnCreateTask.disableProperty().bind(validateInput);
		btnCreateTask.setId("CreateTask");

		taskPane.add(lblTaskTitle, 0, 0);
		taskPane.add(new Label("Task Name:"), 0, 1);
		taskPane.add(tfTaskName, 1, 1);
		taskPane.add(new Label("Effort Estimate:"), 0, 2);
		taskPane.add(sEffort, 1, 2);
		taskPane.add(lblEffortValue, 2, 2);
		taskPane.add(btnCreateTask, 1, 3);

		taskController = new TaskController(schedule, MAX_INPUT, tfTaskName, sEffort);

		tfTaskName.textProperty().addListener(taskController);
		btnCreateTask.setOnAction(taskController);

		return taskPane;
	}

	private GridPane setUpAndGetDependenciesForm(){
		GridPane dependenciesPane = new GridPane();
		dependenciesPane.setAlignment(Pos.CENTER);
		dependenciesPane.setVgap(10);
		dependenciesPane.setHgap(10);
		dependenciesPane.setPadding(new Insets(10));

		Label lblDependencyTitle = new Label("Manage Dependencies");
		lblDependencyTitle.getStyleClass().add("label-title");

		cbTasks = new ComboBox<Task>();

		lvTasks = new ListView<Task>(); lvTasks.setId("ListOfTasks");
		lvTasks.setVisible(false);
		lvTasks.setPrefWidth(650);
		lvTasks.setPrefHeight(850);
		lvDependencies = new ListView<Task>(); lvDependencies.setId("ListOfDependencies");
		lvDependencies.setVisible(false);
		lvDependencies.setPrefWidth(650);
		lvDependencies.setPrefHeight(850);

		dependenciesPane.add(lblDependencyTitle, 0, 0);
		dependenciesPane.add(new Label("Select Task:"), 0, 1);
		dependenciesPane.add(new Label("All Tasks"), 0, 2);
		dependenciesPane.add(cbTasks, 1, 1);
		dependenciesPane.add(new Label("Dependencies"), 1, 2);
		dependenciesPane.add(lvTasks, 0, 3);
		dependenciesPane.add(lvDependencies, 1, 3);

		dependencyListListener = new DependencyListeners(schedule, lvTasks, lvDependencies, cbTasks);
		dependencyComboBoxListener = dependencyListListener.new DependencyComboBoxListener();

		lvTasks.setOnMouseClicked(dependencyListListener);
		lvDependencies.setOnMouseClicked(dependencyListListener);
		cbTasks.setOnAction(dependencyComboBoxListener);

		return dependenciesPane;
	}

	private GridPane setUpAndGetScheduleForm(){
		GridPane schedulePane = new GridPane();
		schedulePane.setAlignment(Pos.CENTER);
		schedulePane.setVgap(0);
		schedulePane.setHgap(0);
		schedulePane.setPadding(new Insets(0));

		xAxisTasks = new NumberAxis();
		yAxisTasks = new CategoryAxis();

		chartTasks = new GanttChart<Number,String>(xAxisTasks,yAxisTasks);
        	xAxisTasks.setLabel("");
        	xAxisTasks.setTickLabelFill(Color.ORANGE);
        	xAxisTasks.setMinorTickCount(4);

        	yAxisTasks.setLabel("");
	        yAxisTasks.setTickLabelFill(Color.ORANGE);
        	yAxisTasks.setTickLabelGap(5);
        	yAxisTasks.setCategories(FXCollections.<String>observableArrayList(schedule.getScheduledTasksName()));

        	chartTasks.setTitle("Task Scheduling");
        	chartTasks.setLegendVisible(false);
        	chartTasks.setBlockHeight(25);

        	xAxisPeople = new NumberAxis();
		yAxisPeople = new CategoryAxis();

		chartPeople = new GanttChart<Number,String>(xAxisPeople,yAxisPeople);
        	xAxisPeople.setLabel("");
        	xAxisPeople.setTickLabelFill(Color.ORANGE);
        	xAxisPeople.setMinorTickCount(4);

        	yAxisPeople.setLabel("");
        	yAxisPeople.setTickLabelFill(Color.ORANGE);
        	yAxisPeople.setTickLabelGap(5);
        	yAxisPeople.setCategories(FXCollections.<String>observableArrayList(schedule.getWorkingPeopleName()));

        	chartPeople.setTitle("Team allocation on tasks");
        	chartPeople.setLegendVisible(false);
        	chartPeople.setBlockHeight(25);

		schedulePane.add(chartTasks, 0, 0);
		schedulePane.add(chartPeople, 0, 1);

		return schedulePane;
	}

	/**
	 * Returns the person form pane
	 * @return {GridPane} personFormPane: Person Form
	 */
	public GridPane getPersonFormPane() {
		return personFormPane;
	}

	/**
	 * Returns the task form pane
	 * @return {GridPane} taskFormPane: Task Form
	 */
	public GridPane getTaskFormPane() {
		return taskFormPane;
	}

	/**
	 * Returns the dependencies pane
	 * @return {GridPane} dependenciesPane: Dependency Editor
	 */
	public GridPane getDependenciesPane() {
		return dependenciesPane;
	}

	/**
	 * Returns the schedule pane
	 * @return {GridPane} schedulePane: Schedule Producer
	 */
	public GridPane getSchedulePane() {
		return schedulePane;
	}
	
	/**
	 * Interprets why the update was sent using the argument passed in and
	 * acts accordingly.
	 * If the argument passed in was "Tasks", then it means that the view should update
	 * the dependency pane's widgets.
	 * If the argument passed in was "Schedule", then it means that there is new data
	 * and that the view should update the Gantt chart on the schedule pane.
	 * @Override
	 */
	public void update(Observable arg0, Object arg1) {
		String type = (String) arg1;

		if(type.equals("Tasks")){
			cbTasks.getItems().clear();
			cbTasks.getItems().addAll(schedule.getTasks());
			lvTasks.setVisible(false); lvDependencies.setVisible(false);
		}
		if(type.equals("Schedule")) {
			chartTasks.getData().clear();
			chartTasks.setBlockHeight(calculateRelativeFontSize(schedule.getTasks().size()));
			yAxisTasks.getCategories().clear();
			yAxisTasks.getCategories().addAll(FXCollections.<String>observableArrayList(schedule.getScheduledTasksName()));
			yAxisTasks.setTickLabelFont(new Font(calculateRelativeFontSize(schedule.getTasks().size())));
			HashMap<Integer, String> idToColor = new HashMap<Integer, String>();
			for(Task t : schedule.getScheduledTasks()) {
				idToColor.put(t.getID(), "#000000");
			}
			for(Task t : schedule.getScheduledTasks()) {
				Random rand = new Random();
				idToColor.put(t.getID(), String.format("#%06X", rand.nextInt(0x1000000)));
				Series taskSeries = new Series();
				String nameString = "Task " + t.getName() + " (ID: " + t.getID() + ")";
				taskSeries.getData().add(new Data(t.getTimeStart(), schedule.getScheduledTasksName().get(schedule.getScheduledTasksName().indexOf(nameString)), new ExtraData(t.getEffortEstimate()*t.getReduction(), "-fx-background-color: " + idToColor.get(t.getID()) + ";")));
				chartTasks.getData().add(taskSeries);
			}
			chartPeople.getData().clear();
			chartPeople.setBlockHeight(calculateRelativeFontSize(schedule.getPeople().size()));
			yAxisPeople.getCategories().clear();
			yAxisPeople.getCategories().addAll(FXCollections.<String>observableArrayList(schedule.getWorkingPeopleName()));
			yAxisPeople.setTickLabelFont(new Font(calculateRelativeFontSize(schedule.getPeople().size())));
			for(Person p : schedule.getPeople().values()) {
				String nameString = p.getFirstName() + " " + p.getLastName() + " (ID: " + p.getID() + ")";
				for(Task t : schedule.getWorkingPeople().get(p.getID())) {
					Series taskSeries = new Series();
					taskSeries.getData().add(new Data(t.getTimeStart(), schedule.getWorkingPeopleName().get(schedule.getWorkingPeopleName().indexOf(nameString)), new ExtraData(t.getEffortEstimate()*t.getReduction()-0.2, "-fx-background-color: " + idToColor.get(t.getID()) + ";")));
					chartPeople.getData().add(taskSeries);
				}
			}
		}
	}

	private int calculateRelativeFontSize(int num){
		if(num < 11) return 11;
		else if(num < 13) return 9;
		else if(num < 15) return 8;
		else if(num < 17) return 7;
		else if(num < 19) return 6;
		else return 5;
	}

}
