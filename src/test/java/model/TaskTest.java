package model;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Arrays;
import model.Task;

public class TaskTest extends TestDataGenerator {

	private final String[] namesIn = new String[] {
		"a", "b", "c", "d", "e",
		"smallest", "medium",
		"high", "high"
	};

	private final double[] effortsIn = new double[] {
		2.0, 40.0, -100, 0, 10, 0, 50.0, 100.0, 100.0
	};

	private void assertIdUniqueness(ArrayList<Task> tasks) {
		HashSet<Integer> ids = new HashSet<Integer>();
		for (Task t : tasks) {
			ids.add(t.getID());
		}
		assertEquals(ids.size(), tasks.size());
	}

	private void assertArrayListSetEquals(HashSet<Task> a, ArrayList<Task> b) {
		assertEquals(a.size(), b.size());
		assertEquals(a, new HashSet<Task>(b));
	}

	private void testDependencyHandling(Task t) {

		HashSet<Task> out = new HashSet<Task>();
		final ArrayList<Task> emptyArr = new ArrayList<Task>();

		assertEquals(emptyArr, t.getDependencies());

		for (int i = 0; i < 100; i++) {

			Task randIn = randomTask();

			switch (randInt(2)) {

				case 0:
					t.addDependency(randIn);
					out.add(randIn);
					break;

				case 1:
					t.removeDependency(randIn);
					out.remove(randIn);
					break;

				default:
					break;

			}

			assertArrayListSetEquals(out, t.getDependencies());

		}

	}

	private void testDependentHandling(Task t) {

		HashSet<Task> out = new HashSet<Task>();
		final ArrayList<Task> emptyArr = new ArrayList<Task>();

		assertEquals(emptyArr, t.getDependents());

		for (int i = 0; i < 100; i++) {

			Task randIn = randomTask();

			switch (randInt(2)) {

				case 0:
					t.addDependent(randIn);
					out.add(randIn);
					break;

				case 1:
					t.removeDependent(randIn);
					out.remove(randIn);
					break;

				default:
					break;

			}

			assertArrayListSetEquals(out, t.getDependents());

		}

	}

	private void testComparator(Task a, Task b, int val) {
		assertEquals(val, a.compareTo(b));
	}

	private void testEquality(Task a, Task b, boolean truthVal) {
		assertEquals(truthVal, a.equals(b));
	}

	@Test (timeout = 100)
	public void testIdUniqueness() {
		HashSet<Integer> ids = new HashSet<Integer>();
		ArrayList<Task> tasks = randomTaskArray(100);
		for (Task t : tasks) {
			ids.add(t.getID());
		}
		assertEquals(ids.size(), tasks.size());

	}

	@Test (timeout = 100)
	public void testDependents() {
		for (Task t : randomTaskArray(20)) {
			testDependentHandling(t);
		}
	}

	@Test (timeout = 100)
	public void testDependencies() {
		for (Task t : randomTaskArray(20)) {
			testDependencyHandling(t);
		}
	}

	@Test (timeout = 100)
	public void testComparator() {
		ArrayList<Task> tests = new ArrayList<Task>();
		for (int i = 0; i < namesIn.length; i++) {
			tests.add(getTask(namesIn[i], effortsIn[i]));
		}
		testComparator(tests.get(5), tests.get(6), -1);
		testComparator(tests.get(7), tests.get(6), 1);
		testComparator(tests.get(6), tests.get(6), 0);
	}

	@Test (timeout = 100)
	public void testEquality() {
		ArrayList<Task> tests = new ArrayList<Task>();
		for (int i = 0; i < namesIn.length; i++) {
			tests.add(getTask(namesIn[i], effortsIn[i]));
		}
		testEquality(tests.get(0), tests.get(0), true);
		testEquality(tests.get(0), null, false);
		testEquality(tests.get(7), tests.get(8), false);
	}

}
