package model;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Stores the attributes of a person.
 *
 */
public class Person {

    private String firstName, lastName, email;
    private Task task;
    private int id;
    public static final Pattern emailRegexPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Constructor that takes first name, last name, email, and ID then assigns them to the person's details.
     * @param firstName first name to be set for the person
     * @param lastName last name to be set for the person
     * @param email email to be set for the person
     * @param id unique ID for this person
     */
    public Person(String firstName, String lastName, String email, int id){
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id; //receives and assigns a unique ID for each person object.
        if(validateEmail(email)){ //validates the mail format.
            this.email = email;
        }else{
            System.out.println("Invalid email format!");
        }
    }

    /**
     * Returns ID assigned to the person.
     * @return ID of the person
     */
    public int getID() {
		return id;
	}

	/**
     * Assign a task to the person.
     * @param task task to be set
     */
    public void setTask(Task task){
        this.task = task;
    }

    /**
     * Returns task assigned to the person.
     * @return task of the person
     */
    public Task getTask(){
        return task;
    }

    /**
     * Returns first name of the person.
     * @return first name assigned to the person
     */
    public String getFirstName(){
        return firstName;
    }

    /**
     * Returns last name of a person.
     * @return last name assigned to the person
     */
    public String getLastName(){
        return lastName;
    }

    /**
     * Returns email of a person.
     * @return email assigned to the person
     */
    public String getEmail() {
        return email;
    }

    /**
     * Checks if the email provided matches the email regular expression.
     * @param email email to be validated
     * @return true if the email is valid, false otherwise
     */
    private boolean validateEmail(String email){
        Matcher matcher = emailRegexPattern.matcher(email);
        return matcher.find();
    }
    /**
     * Returns a string representation of a person.
     */
    public String toString() {
		return "[FName: " + firstName + ", LName: " + lastName + ", email: "  + email + "]";
	}

}
