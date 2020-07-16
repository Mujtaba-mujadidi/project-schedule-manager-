package model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Observable;
import java.util.PriorityQueue;

/**
 * Computes and stores a list of scheduled tasks.
 *
 */
public class TaskSchedule extends Observable{

	private HashMap<Integer, ArrayList<Task>> assignedTasks;
	private HashMap<Integer, Person> personMap;

	private PriorityQueue<Task> tasks;
	private ArrayList<Task> scheduledTasks;
	private boolean isNewScheduleRequired;
	private File fileTasks, filePeople;
	private BufferedReader reader;
	private BufferedWriter writer;
	private Gson gsonObj;

	private final int MAX_EFFORT_TASK = 100;


	/**
	 * Constructor that initialises the data structures associated with the Task Schedule.
	 */
	public TaskSchedule() {
		personMap = new HashMap<Integer, Person>();
		assignedTasks = new HashMap<Integer, ArrayList<Task>>();
		tasks = new PriorityQueue<Task>(); // Create the priority queue that stores the unscheduled tasks.
		scheduledTasks = new ArrayList<Task>(); // Create the ArrayList that stores the scheduled tasks.
		fileTasks = new File("src/main/java/model/resources/tasks.txt");
		filePeople = new File("src/main/java/model/resources/people.txt");
		gsonObj = new Gson();
		isNewScheduleRequired = true;
		readFiles();
	}
	/**
	 * Add a task to the priority queue of tasks.
	 * @param task task to be added to the priority queue of unscheduled tasks
	 */
	public void addTask(Task task) {
		tasks.add(task);
		setChanged();
		notifyObservers("Tasks");
	}
	/**
	 * Add a person to the HashMap where <k,v> is <Person.ID, Person>
	 * @param person person to be added to the HashMap of people
	 */
	public void addPerson(Person person) {
		personMap.put(person.getID(), person);
		assignedTasks.put(person.getID(), new ArrayList<Task>());
	}

	/**
	 * Finds and returns the next unique ID for a person by checking all existing IDs for people
	 * @return the next unique ID for a person
	 */
	public int getMaxPersonId() {
		int max = -1;
		for (Person p : personMap.values()) {
			max = p.getID() > max ? p.getID() : max;
		}
		return max;
	}

	/**
	 * Finds and returns the next unique ID for a task by checking all existing IDs for tasks
	 * @return the next unique ID for a task
	 */
	public int getMaxTaskID() {
		int max = -1;
		for (Task t : tasks) {
			max = t.getID() > max ? t.getID() : max;
		}
		return max;
	}

	/**
	 * Updates the ArrayList of scheduled tasks and the HashMap of ArrayList of people that are assigned to tasks, according to the
	 * task scheduling technique of minimising the overall effort of working on tasks.
	 * @return true if successfully created a schedule, false otherwise
	 */
	public boolean scheduleTasks() {
		if(!isNewScheduleRequired) {
			return true;
		}
		double time = 0.0;
		boolean existsInitialTask = false;
		for(Task t: tasks) {
			if(t.getDependencies().size() == 0) {
				existsInitialTask = true;
			}
		}
		if(!existsInitialTask || personMap.size() == 0) {
			return false;
		}
		scheduledTasks.clear();
		for(Task t : tasks) {
			t.setTimeStart(-1);
		}
		for(Person p : personMap.values()) {
			p.setTask(null);
			assignedTasks.get(p.getID()).clear();
		}
		// Creates a deep copy of the unscheduled tasks
		PriorityQueue<Task> schedulingTasks = (PriorityQueue<Task>) deepClone(tasks);
		while(!schedulingTasks.isEmpty()) { // Check if there are tasks remaining to be scheduled
			ArrayList<Task> availableTasks = new ArrayList<Task>();
			PriorityQueue<Task> copyTasks = new PriorityQueue<Task>(schedulingTasks); // Create a copy of the remaining tasks to be scheduled
			while(!copyTasks.isEmpty()) {
				Task currentTask = (Task) copyTasks.poll(); // Get the first task that needs to be scheduled
				ArrayList<Task> dependencyList = currentTask.getDependencies();
				if(dependencyList.size() == 0) { // Check if the task has no dependencies
					if(currentTask.getTimeStart() == -1) { // Check if the task has not been started already.
						boolean isOneEmployeeAvailable = false;
						for(Person p: personMap.values()) {
							if(p.getTask() == null && !isOneEmployeeAvailable) {
								p.setTask(currentTask);
								assignedTasks.get(p.getID()).add(currentTask);
								isOneEmployeeAvailable = true;
							}
						}
						if(isOneEmployeeAvailable) {
							currentTask.setTimeStart(time);
							availableTasks.add(currentTask);
						}
					}else{
						int numberEmployees = 0;
						for(Person p: personMap.values()) {
							if(p.getTask() == currentTask) { numberEmployees++; }
						}
						currentTask.setReduction(calculateReduction(numberEmployees)); // Add workload reduction if more people work on the same task.
						if(time - currentTask.getTimeStart()  >= (currentTask.getEffortEstimate() * currentTask.getReduction())) { // Check if the task has been completed.
							schedulingTasks.remove((Object)currentTask);
							for(Person p: personMap.values()) {
								if(p.getTask() == currentTask) { p.setTask(null); }// Assign no task to the people working on the depending task.
							}
							for(Task t: schedulingTasks) {
								if(t.getDependencies().contains(currentTask)) { t.getDependencies().remove(currentTask); }
							}
						}
					}
				}
			}
			if(!availableTasks.isEmpty()) { // Check if there are tasks available at this iteration
				int indexTask = 0;
				for(Person p : personMap.values()) {
					if(p.getTask() == null) {
						p.setTask(availableTasks.get(indexTask));
						assignedTasks.get(p.getID()).add(availableTasks.get(indexTask));
						indexTask++;
						if(indexTask >= availableTasks.size()) { indexTask = 0; }// Try to attribute the people remaining to the tasks in a circular approach.
					}
				}
				for(Task t: availableTasks) {
					scheduledTasks.add(t); // Add to the ArrayList of scheduled tasks.
				}
			}
			time += 1.0; // Increase time
			if (time > (tasks.size() * MAX_EFFORT_TASK) + 1) { return false; }
		}
		Collections.sort(scheduledTasks, (a,b) -> Double.compare(a.getTimeStart(), b.getTimeStart()));
		isNewScheduleRequired = false;
		setChanged();
		notifyObservers("Schedule");
		return true;
	}

	/**
	* Returns the reduction coefficient calculated based on the number of employees.
	* @return reduction coefficient
	*/
	public double calculateReduction(int numberEmployees) {
		if(numberEmployees > 1 && numberEmployees <= 5) {
			return 0.8;
		}
		else if(numberEmployees > 5 && numberEmployees <= 10) {
			return 0.75;
		}
		else if(numberEmployees > 10 && numberEmployees <= 20) {
			return 0.7;
		}
		else {
			return 1.0;
		}
	}
	/**
	 * Returns the ArrayList of scheduled tasks.
	 * @return ArrayList of scheduled tasks
	 */
	public ArrayList<Task> getScheduledTasks() {
		return scheduledTasks;
	}

	/**
	 * Returns an ArrayList of the names of the scheduled tasks.
	 * @return ArrayList of the names of the scheduled tasks
	 */
	public ArrayList<String> getScheduledTasksName() {
		ArrayList<String> tasksName = new ArrayList<String>();
		String str;
		for(Task t:scheduledTasks) {
			str = "Task " + t.getName() + " (ID: " + t.getID() + ")";
			tasksName.add(str);
		}
		return tasksName;
	}
	/**
	 * Returns the HashMap of ArrayList of people working on tasks.
	 * @return HashMap of ArrayList of people working on tasks
	 */
	public HashMap<Integer, ArrayList<Task>> getWorkingPeople() {
		return assignedTasks;
	}

	/**
	 * If set to true, the function scheduleTasks() will create a new schedule the next time it is executed
	 * @param isNewScheduleRequired
	 */
	public void setNewScheduleRequired(boolean isNewScheduleRequired) {
		this.isNewScheduleRequired = isNewScheduleRequired;
	}

	/**
	 * Returns the HashMap of the people in the system
	 * @return HashMap of the people in the system
	 */
	public HashMap<Integer, Person> getPeople() {
		return personMap;
	}

	/**
	 * Replaces the existing list of people in the system
	 * @param people ArrayList of people who are able to work on tasks
	 */
	public void setPeople(ArrayList<Person> people) {
		personMap = new HashMap<Integer, Person>();
		for (Person p : people) {
			personMap.put(p.getID(), p);
		}
	}

	/**
	 * Returns an ArrayList of the names of the people in the system.
	 * @return ArrayList of the names of the people
	 */
	public ArrayList<String> getWorkingPeopleName() {
		ArrayList<String> ret = new ArrayList<String>();
		for(Person p : personMap.values()) {
			ret.add(p.getFirstName() + " " + p.getLastName() + " (ID: " + p.getID() + ")");
		}
		return ret;
	}
	/**
	 * Returns the priority queue of unscheduled tasks.
	 * @return PriorityQueue of unscheduled tasks
	 */
	public PriorityQueue<Task> getTasks() {
		return tasks;
	}

	/*
	 *Reads files into tasks and people
	 */
	private void readFiles(){
		//Reading the Tasks.txt
		String jsonStr = getJsonString(fileTasks);
		if(jsonStr!=null) {this.tasks = gsonObj.fromJson(jsonStr,  new TypeToken<PriorityQueue<Task>>() {}.getType());}

		//Read from People.txt
		jsonStr = getJsonString(filePeople);
		if(jsonStr!=null){
			ArrayList<Person> adds = gsonObj.fromJson(jsonStr, new TypeToken<ArrayList<Person>>() {}.getType());
			for(Person person : adds){
				this.addPerson(person);
			}
		}

		try {reader.close(); } catch (IOException e) {e.printStackTrace();}
		updateTaskDependencies(); //adds task dependencies.
	}

	private String getJsonString(File file){
		try {
			reader = new BufferedReader(new FileReader(file));
			return  reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/*
	* Compute dependencies from dependents and add them to the corresponding Tasks
	*/
	private void updateTaskDependencies(){
		for(Task task : tasks){
			for(Task taskD : tasks){
				if(taskD.getDependents().contains(task)) task.addDependency(taskD);
			}
		}
	}

	/**
	 * Saves the data currently in the system into two files: tasks and people.
	 */
	public void writeFiles(){
		//Cleans dependencies for each task
		tasks.forEach(task -> {task.getDependencies().clear();});

		//write to Tasks.txt
		writeJson(fileTasks, gsonObj.toJson(this.tasks));

		//write to People.txt
		writeJson(filePeople, gsonObj.toJson(new ArrayList<Person>(this.personMap.values())));
	}

	private void writeJson(File file, String str){
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {writer.close();} catch (IOException e) {e.printStackTrace();}
	}

	/**
	*To Update the view after reading the files.
	*/
	public void updateView(){
		 setChanged();
		 notifyObservers("Tasks");
	}

	/*
	 * Creates a deep clone of any object passed in. The clone will have its own
	 * memory references to any attribute that existed in the original.
	 */
	private Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
