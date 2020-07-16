package control;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import java.util.concurrent.atomic.AtomicInteger;
import model.Person;
import model.TaskSchedule;

/**
 * Manages the creation of people between the Person form GUI & the Person model,
 * and the addition of newly created people to the Task Scheduler.
 *
 */
public class PersonController implements EventHandler<ActionEvent>, ChangeListener<String>{

	private TaskSchedule schedule;
	private AtomicInteger id_allocator;

	private Button btnCreate;
	private TextField tfFName;
	private TextField tfLName;
	private TextField tfEmail;
	private final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	private int max_input;
	private int start_id;
	private final int MAX_PEOPLE = 20;

	/**
	 * Initialises the controller by taking as parameters; the schedule being used for the system, the max input allowed for the text fields
	 * on the Person form, the text fields themselves to extract the data, and the confirmation button to prevent it from being enabled before
	 * valid data has been entered.
	 * @param schedule
	 * @param max_input
	 * @param tfFName
	 * @param tfLName
	 * @param tfEmail
	 * @param btnCreate
	 */
	public PersonController(TaskSchedule schedule, int max_input, TextField tfFName, TextField tfLName, TextField tfEmail, Button btnCreate) {
		this.schedule = schedule;
		this.start_id = schedule.getMaxPersonId();
		this.id_allocator = new AtomicInteger(start_id);
		this.max_input = max_input;
		this.tfFName = tfFName;
		this.tfLName = tfLName;
		this.tfEmail = tfEmail;
		this.btnCreate = btnCreate;
	}

	/**
	 * Checks to see if the number of people limit has been met, if it hasn't
	 * a new person with data taken from the GUI is created and added to the Task Scheduler.
	 * If the limit has been met, the user is informed of the limit via a pop-up.
	 * @Override
	 */
	public void handle(ActionEvent event) {
		if(schedule.getPeople().size() < MAX_PEOPLE){
			schedule.addPerson(new Person(tfFName.getText(), tfLName.getText(), tfEmail.getText(), id_allocator.incrementAndGet()));
			schedule.setNewScheduleRequired(true);
			tfFName.setText("");
			tfLName.setText("");
			tfEmail.setText("");
		}else{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("New Person");
			alert.setHeaderText(null);
			alert.setContentText("The maximum number of allowed people '" + MAX_PEOPLE + "' has been met");
			alert.showAndWait();
		}
	}
	/**
	 * Ensures that the text fields do not exceed the max input, also checks to see
	 * if valid data has been entered, if so, the submit button is enabled.
	 * @Override
	 */
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		if(newValue.length() > max_input) {
			int l = newValue.length();
			if(l == tfFName.getText().length()) tfFName.setText(tfFName.getText().substring(0, max_input));
			else if(l == tfLName.getText().length()) tfLName.setText(tfLName.getText().substring(0, max_input));
			else if(l == tfEmail.getText().length()) tfEmail.setText(tfEmail.getText().substring(0, max_input));
	    }
		btnCreate.setDisable(!validateFormInput(tfFName.getText(), tfLName.getText(), tfEmail.getText()));
	}

	private boolean validateFormInput(String firstname, String lastName, String email) {
        Matcher matcher = EMAIL_REGEX.matcher(email.trim());
        if(firstname.isEmpty() || lastName.isEmpty() || !matcher.find()) return false;
        else return true;
    }

}
