package control;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import model.TaskSchedule;
import view.Panes;

/**
 * Manages the navigation of the application; interprets navigation buttons clicks.
 */
public class NavigationController implements EventHandler<ActionEvent>{

	private Panes panes;
	private BorderPane mainLayout;
	private TaskSchedule schedule;

	/**
	 * Initialises the controller by taking as parameters; the class containing all panes, the active pane that
	 * will be used to display only a single pane at a time, and the schedule being used for the system.
	 * @param panes
	 * @param mainLayout
	 * @param schedule
	 */
	public NavigationController(Panes panes, BorderPane mainLayout, TaskSchedule schedule) {
		this.panes = panes;
		this.mainLayout = mainLayout;
		this.schedule = schedule;
	}

	/**
	 * Identifies the navigation button clicked and switches the pane currently being displayed.
	 * If the "show schedule" button is clicked and no schedule can be produced, an error message is shown.
	 * @Override
	 */
	public void handle(ActionEvent arg0) {
		Button btnEvent = (Button) arg0.getSource();

		switch (btnEvent.getId()) {
			case "Person":
				mainLayout.setCenter(panes.getPersonFormPane());
				break;
			case "Task":
				mainLayout.setCenter(panes.getTaskFormPane());
				break;
			case "Dependencies":
				mainLayout.setCenter(panes.getDependenciesPane());
				break;
			case "Schedule": {
				if (schedule.scheduleTasks()) {
					mainLayout.setCenter(panes.getSchedulePane());
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Task Scheduling");
					alert.setHeaderText("Task Scheduling was not able to produce a result");
					alert.setContentText("The Task Scheduling Application has detected an issue regarding the data inserted: \n\n"
							+ "When trying to run the Application, there was no task without dependencies or people to work on tasks.");
					alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
					alert.showAndWait();
				}
				break;
			}
		}
	}
	/**
	 * Manages the closure of the application.
	 */
	public class NavigationWindowListener implements EventHandler<WindowEvent>{

		/**
		 * Displays options to the user when they attempt to exit the application.
		 * The options allow users to save their data, discard any changes, clear task and/or person data.
		 * @Override
		 */
		public void handle(WindowEvent event) {
			List<String> choices = Arrays.asList("Save Changes", "Discard Changes", "Clear Task data", "Clear Person data", "Clear All data");

			ChoiceDialog<String> dialog = new ChoiceDialog<>("Save Changes", choices);
			dialog.setTitle("Exit");
			dialog.setHeaderText(null);
			dialog.setContentText("Select an Option");

			Optional<String> result = dialog.showAndWait();

			if(result.isPresent()){
				if(result.get() == "Save Changes"){
				    schedule.writeFiles();
				}else if(result.get() == "Clear Task data"){
					schedule.getTasks().clear();
					schedule.writeFiles();
				}else if(result.get() == "Clear Person data"){
					schedule.getPeople().clear();
					schedule.writeFiles();
				}else if(result.get() == "Clear All data"){
					schedule.getTasks().clear();
					schedule.getPeople().clear();
					schedule.writeFiles();
				}
			}else{
				event.consume();
			}
		}

    }
}
