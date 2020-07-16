package model;

import java.util.Random;
import java.util.ArrayList;
import model.Task;
import model.Person;

public class TestDataGenerator {

    private static final Random random = new Random();
    private static int person_id, task_id;

    public static String randomName() {
        int n = random.nextInt(64);
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ";
        char[] name = new char[n];
        for (int i = 0; i < n; i++) {
            name[i] = alphabet.charAt(random.nextInt(alphabet.length()));
        }
        return new String(name);
    }

    public static double randomEffort() {
        return random.nextDouble() * 100.0d;
    }

    public static Task randomTask() {
        return new Task(randomName(), randomEffort(), task_id++);
    }

    public static Task getTask(String name, double effort) {
        return new Task(name, effort, task_id++);
    }

    public static Person randomPerson() {
        return new Person(randomName(), randomName(), "a@b.c", person_id++);
    }

    public static Person getPerson(String fname, String lname, String email) {
        return new Person(fname, lname, email, person_id++);
    }

	public static ArrayList<Task> randomTaskArray(int n) {
		ArrayList<Task> ret = new ArrayList<Task>();
		for (int i = 0; i < n; i++) {
			ret.add(randomTask());
		}
		return ret;
	}

    public static int randInt(int n) {
        return random.nextInt(n);
    }

    public static int randInt(int from, int to) {
        return from + random.nextInt(to - from + 1);
    }

}
