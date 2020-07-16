package control;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Task;
import model.TaskSchedule;

/**
 * Manages the addition and removal of dependencies between the dependency editor GUI
 * and the corresponding task models.
 *
 */
public class DependencyListeners implements EventHandler<MouseEvent>{

	private ListView<Task> lvTasks;
	private ListView<Task> lvDependencies;
	private ComboBox<Task> cbTasks;

	private TaskSchedule schedule;

	/**
	 * Initialises the controller by taking as parameters; the schedule being used for the system, the combo-box used to select a task,
	 * and the lists that display the available tasks and the current selected task's dependencies.
	 * @param schedule
	 * @param lvTasks
	 * @param lvDependencies
	 * @param cbTasks
	 */
	public DependencyListeners(TaskSchedule schedule, ListView<Task> lvTasks, ListView<Task> lvDependencies, ComboBox<Task> cbTasks) {
		this.schedule = schedule;
		this.lvTasks = lvTasks;
		this.lvDependencies = lvDependencies;
		this.cbTasks = cbTasks;
	}

	/**
	 * Interprets which list was clicked and then acts accordingly.
	 * If the available tasks list was clicked, the task is added as a dependency to the selected task and
	 * it is checked whether this is valid (to avoid dependency loops). If it is invalid, the task is removed from
	 * being a dependency and an error message is show to the user.
	 * If the dependencies list is clicked, the clicked task is removed from the selected task's dependencies.
	 * @SuppressWarnings("unchecked")
	 * @Override
	 */
    public void handle(MouseEvent event) {
    	if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
            ListView<Task> lvEvent = (ListView<Task>) event.getSource();

            if(lvEvent.getId().equals("ListOfTasks")){
            	Task selectedTask = (Task) lvTasks.getSelectionModel().getSelectedItem();
               	if(selectedTask != null){
               		cbTasks.getSelectionModel().getSelectedItem().addDependency(selectedTask);
               		selectedTask.addDependent(cbTasks.getSelectionModel().getSelectedItem());
               		schedule.setNewScheduleRequired(true);
               		if(schedule.scheduleTasks()){
               			updateTaskListViews();
               		}else{
               			cbTasks.getSelectionModel().getSelectedItem().removeDependency(selectedTask);
                   		selectedTask.removeDependent(cbTasks.getSelectionModel().getSelectedItem());
                   		Alert alert = new Alert(AlertType.INFORMATION);
                   		alert.setTitle("Task Scheduling");
                   		alert.setHeaderText("Dependency could not be established");
                   		alert.setContentText("This is either due to a dependency loop, or the \n" + "fact that no people currently exist in the system.");
                   		alert.showAndWait();
               		}
               	}
            }else{
            	Task selectedTask = (Task) lvDependencies.getSelectionModel().getSelectedItem();
               	if(selectedTask != null){
               		cbTasks.getSelectionModel().getSelectedItem().removeDependency(selectedTask);
               		selectedTask.removeDependent(cbTasks.getSelectionModel().getSelectedItem());
                   	updateTaskListViews();
               	}
            }
        }
    }

    /*
     * Updates the lists to display the latest data in the system.
     */
    private void updateTaskListViews(){
    	lvTasks.setVisible(true);
		lvDependencies.setVisible(true);

		schedule.setNewScheduleRequired(true);
    	ArrayList<Task> availableTasks = new ArrayList<Task>(schedule.getTasks());
    	Task selectedTask = (Task) cbTasks.getSelectionModel().getSelectedItem();
    	availableTasks.remove(selectedTask);
    	availableTasks.removeAll(selectedTask.getDependencies());
    	availableTasks.removeAll(selectedTask.getDependents());

    	lvTasks.getItems().clear();
    	lvTasks.setItems(FXCollections.observableArrayList(availableTasks));
    	lvDependencies.getItems().clear();
		lvDependencies.setItems(FXCollections.observableArrayList(selectedTask.getDependencies()));
    }

    /**
     * Ensures that if no task has been selected, the lists are not displayed.
     *
     */
    public class DependencyComboBoxListener implements EventHandler<ActionEvent>{

    	/**
    	 * If no task has been selected using the combo-box, the lists are not displayed.
    	 *@Override
    	 */
    	public void handle(ActionEvent event) {
    		if(cbTasks.getSelectionModel().getSelectedItem() != null) updateTaskListViews();

    	}

    }
}
