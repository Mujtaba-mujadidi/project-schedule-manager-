package control;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import java.util.concurrent.atomic.AtomicInteger;
import model.Task;
import model.TaskSchedule;

/**
 * Manages the creation of tasks between the Task form GUI & the Task model,
 * and the addition of newly created tasks to the Task Scheduler.
 *
 */
public class TaskController implements EventHandler<ActionEvent>, ChangeListener<String>{

	private TaskSchedule schedule;
	private AtomicInteger id_allocator;

	private TextField tfTaskName;
	private Slider sEffort;
	private int max_input;
	private int start_id;
	private final int MAX_TASKS = 20;

	/**
	 * Initialises the controller by taking as parameters; the schedule being used for the system, the max input allowed for the task name
	 * text field, the text field itself to extract the task name from, and the slider used to express effort to get it's value.
	 * @param schedule
	 * @param max_input
	 * @param tfTaskName
	 * @param sEffort
	 */
	public TaskController(TaskSchedule schedule, int max_input, TextField tfTaskName, Slider sEffort) {
		this.tfTaskName = tfTaskName;
		this.max_input = max_input;
		this.schedule = schedule;
		this.start_id = schedule.getMaxTaskID();
		this.id_allocator = new AtomicInteger(start_id);
		this.sEffort = sEffort;
	}

	/**
	 * Checks to see if the number of tasks limit has been met, if it hasn't
	 * a new task with data taken from the GUI is created and added to the Task Scheduler.
	 * If the limit has been met, the user is informed of the limit via a pop-up.
	 * @Override
	 */
	public void handle(ActionEvent event) {
		if(schedule.getTasks().size() < MAX_TASKS){
			schedule.addTask(new Task(tfTaskName.getText(),(Math.round(sEffort.getValue() * 100) / 100), id_allocator.incrementAndGet()));
			schedule.setNewScheduleRequired(true);
			tfTaskName.setText("");
			sEffort.setValue(50.0);
		}else{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("New Task");
			alert.setHeaderText(null);
			alert.setContentText("The maximum number of allowed tasks '" + MAX_TASKS + "' has been met");
			alert.showAndWait();
		}
	}

	/**
	 * Ensures that the text field for the task name does not exceed the max input.
	 * @Override
	 */
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		if(newValue.length() > max_input) tfTaskName.setText(tfTaskName.getText().substring(0, max_input));
	}
}
