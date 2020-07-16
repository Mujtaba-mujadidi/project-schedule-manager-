package model;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;
import model.Task;
import model.Person;
import model.TaskSchedule;

public class TaskScheduleTest extends TestDataGenerator {

    // a valid schedule is one where no task is started before all of its parents are completed
    // can we break writefiles by passing it weird things, or passing weird things to people / tasks
    // can we produce a schedule from an invalid set of tasks / dependencies

    private void link(Task a, Task b) {
        a.addDependent(b);
        b.addDependency(a);
    }

    private HashMap<Integer, Task> getRandomTasksMap() {

        ArrayList<Task> tasks = randomTaskArray(20);

        HashMap<Integer, Task> tasksById = new HashMap<Integer, Task>();

        for (Task t : tasks) {
            tasksById.put(t.getID(), t);
        }

        for (int i = 0; i < tasks.size(); i++) {

            int numLinks = randInt(5);
            List<Task> futureTasks = tasks.subList(i+1, tasks.size());

            for (int j = 0; j < numLinks; j++) {

                int bnd = futureTasks.size();
                if (bnd == 0) continue;
                link(tasks.get(i), tasks.get(i + randInt(1, bnd)));

            }

        }

        return tasksById;

    }

    private TaskSchedule ts;

    private TaskSchedule getTaskScheduleInstance() {
      TaskSchedule ts = new TaskSchedule();
      ts.getPeople().clear();
      ts.getTasks().clear();
      for (int i = 0; i < 3; i++) {
        ts.addPerson(randomPerson());
      }
      return ts;
    }

    private ArrayList<Task> tasks;
    private HashMap<Integer, Task> taskMap;

    @Test (timeout = 1000)
    public void testInit() {

        ts = getTaskScheduleInstance();
        ts.scheduleTasks();

    }

    @Test (timeout = 1000)
    public void testRandom() {

        ts = getTaskScheduleInstance();
        taskMap = getRandomTasksMap();

        for (Task t : taskMap.values())
            ts.addTask(t);

        assertEquals(true, ts.scheduleTasks());
    }

    @Test (timeout = 1000)
    public void testUnlinked() {

        ts = getTaskScheduleInstance();

        for (Task t : randomTaskArray(20))
            ts.addTask(t);

        assertEquals(true, ts.scheduleTasks());

    }

    @Test (timeout = 1000)
    public void testLinear() {

        ts = getTaskScheduleInstance();
        tasks = randomTaskArray(20);

        for (int i = 0; i < tasks.size()-1; i++) {
            link(tasks.get(i), tasks.get(i+1));
        }

        for (Task t : tasks)
            ts.addTask(t);

        assertEquals(true, ts.scheduleTasks());

    }

    @Test (timeout = 1000)
    public void testCircular() {

        ts = getTaskScheduleInstance();
        tasks = randomTaskArray(20);

        for (int i = 0; i < tasks.size()-1; i++) {
            link(tasks.get(i), tasks.get(i+1));
        }

        link(tasks.get(19), tasks.get(0));

        for (Task t : tasks)
            ts.addTask(t);

        assertEquals(false, ts.scheduleTasks());

    }

    @Test (timeout = 1000)
    public void testSelfDependent() {

        ts = getTaskScheduleInstance();
        tasks = randomTaskArray(20);

        for (int i = 0; i < tasks.size()-1; i++) {
            link(tasks.get(i), tasks.get(i+1));
        }

        link(tasks.get(9), tasks.get(9));

        for (Task t : tasks)
            ts.addTask(t);

        assertEquals(false, ts.scheduleTasks());

    }

    @Test (timeout = 1000)
    public void testCycle() {

        ts = getTaskScheduleInstance();
        taskMap = getRandomTasksMap();

        tasks = new ArrayList<Task>(taskMap.values());
        link(tasks.get(4), tasks.get(5));
        link(tasks.get(5), tasks.get(6));
        link(tasks.get(6), tasks.get(4));

        for (Task t : taskMap.values())
            ts.addTask(t);

        assertEquals(false, ts.scheduleTasks());

    }

    @Test (timeout = 10000)
    public void testAll() {

        testInit();
        testRandom();
        testUnlinked();
        testLinear();
        testCircular();
        testSelfDependent();
        testCycle();

    }

}
