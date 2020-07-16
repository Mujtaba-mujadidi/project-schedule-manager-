package model;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Stores the attributes of a task.
 *
 */
public class Task implements Comparable<Task>, Serializable{

    private String name;
    private double effort;
    private ArrayList<Task> dependencies;
    private ArrayList<Task> dependents;
    private int id;
    private double timeStart;
    private double reduction;

    /**
     * Constructor that creates a Task with a given name, effort, and ID that will have by default a starting time of -1.0.
     * @param name task name to be set
     * @param effort effort to be set
     * @param id unique ID for the task
     */
    public Task(String name, double effort, int id){
    	this.name = name;
    	this.effort = effort;
    	this.timeStart = -1.0;
        this.reduction = 1.0;
    	this.id = id;
    	dependencies = new ArrayList<Task>();
    	dependents = new ArrayList<Task>();
    }
    /**
     * Returns the effort of a Task.
     * @return effort of the task
     */
    public double getEffortEstimate(){
        return effort;
    }
    /**
     * Returns the ID of a Task.
     * @return id of the task
     */
    public int getID(){
        return id;
    }
    /**
     * Returns the name of a task.
     * @return name of the task
     */
    public String getName(){
        return name;
    }
    /**
     * Returns the starting time of a task.
     * @return start time of the task
     */
	public double getTimeStart() {
		return timeStart;
	}
	/**
	 * Assigns a starting time to a Task.
	 * @param timeStart start time to be set
	 */
	public void setTimeStart(double timeStart) {
		this.timeStart = timeStart;
	}
    /**
     * Returns the reduction coefficient for a task.
     * @return reduction of the task
     */
    public double getReduction() {
        return reduction;
    }
    /**
     * Assigns a reduction coefficient to a Task.
     * @param reduction coefficient to be set
     */
    public void setReduction(double reduction) {
        this.reduction = reduction;
    }
	/**
	 * Returns the ArrayList of dependencies.
	 * @return ArrayList of dependencies
	 */
	public ArrayList<Task> getDependencies(){
        return dependencies;
    }
	/**
	 * Sets the dependencies for a task.
	 * @param d ArrayList of dependencies
	 */
	public void setDependencies(ArrayList<Task> d){
        dependencies = d;
    }
	/**
	 * Removes a dependency from a task.
	 * @param task Task to be removed
	 * @return true if the removal was successful, false otherwise.
	 */
	public boolean removeDependency(Task task){
		return dependencies.remove(task);
	}
	/**
	 * Adds a dependency to a task.
	 * @param task Task to be added
	 * @return true if the addition was successful, false otherwise (the Task already exists).
	 */
	public boolean addDependency(Task task){
		if(dependencies.contains(task)) return false;
		else {
			dependencies.add(task);
			return true;
		}
	}
	/**
	 * Returns the ArrayList of dependents.
	 * @return ArrayList of dependents
	 */
	public ArrayList<Task> getDependents(){
        return dependents;
    }
	/**
	 * Sets the dependents for a task.
	 * @param d ArrayList of dependents
	 */
	public void setDependents(ArrayList<Task> d){
		dependents = d;
    }
	/**
	 * Removes a dependent from a task.
	 * @param task Task to be removed
	 * @return true if the removal was successful, false otherwise.
	 */
	public boolean removeDependent(Task task){
		return dependents.remove(task);
	}
	/**
	 * Adds a dependent to a task.
	 * @param task Task to be added
	 * @return true if the addition was successful, false otherwise (the Task already exists).
	 */
	public boolean addDependent(Task task){
		if(dependents.contains(task)) return false;
		else {
			dependents.add(task);
			return true;
		}
	}
	/**
	 * Returns a string representation of a task object.
	 */
	public String toString() {
		return "[Task: " + name + ", effort: " + effort + ", dependencies: "  + dependencies.size() + "]";
	}
	/**
	 * Compares two tasks by the heuristic value: the task is chosen if it has a smaller number of dependencies, or, if the number of dependencies is equal,
	 * then the smallest effort is taken into account.
	 */
	@Override
	public int compareTo(Task arg0) {
		if((getDependencies().size() < arg0.getDependencies().size()) || ((getDependencies().size() == arg0.getDependencies().size()) && (getEffortEstimate() < arg0.getEffortEstimate()))) {
			return -1;
		}
		else if((getDependencies().size() > arg0.getDependencies().size()) || ((getDependencies().size() == arg0.getDependencies().size()) && (getEffortEstimate() > arg0.getEffortEstimate()))) {
			return 1;
		}
		return 0;
	}
	/**
	 * Checks if two Task objects are equal.
	 */
	@Override
	public boolean equals(Object other){
		if(!(other instanceof Task)) return false;
		else if(id != ((Task) other).id) return false;
		else return true;
	}

}
