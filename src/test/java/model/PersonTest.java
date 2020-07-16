package model;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashSet;
import model.Person;
import model.Task;

public class PersonTest extends TestDataGenerator {

    private String[][] inputs = new String[][] {
        {"First", "Last", "first.last@ex.com"}, // all valid inputs
        {"a", "b", "ab@cd.co.uk"}, // positive-test email regex
        {"invalid", "email", "invalid@email"}, // negative-test email regex
        {"", "", ""} // empty-test email regex, name fields
        //{"toolongtoolongtoolongtoolongtoolongtoolongtoolongtoolong", "#123d{}';/'", "mal.for@m.ed"}
    };

    private String[][] outputs = new String[][] {
        {"First", "Last", "first.last@ex.com"},
        {"a", "b", "ab@cd.co.uk"},
        {"invalid", "email", null},
        {"", "", null}
    };

    private String[] emailsIn = new String[] {
      "me@",
      "@example.com",
      "me.@example.com",
      ".me@example.com",
      "me@example..com",
      "me.example@com",
      "me@example.com",
      "ab@example.co.uk",
      "a@b.io"
    };

    private String[] emailsOut = new String[] {
      null,
      null,
      "me.@example.com",
      ".me@example.com",
      "me@example..com",
      null,
      "me@example.com",
      "ab@example.co.uk",
      "a@b.io"
    };

    @Test (timeout = 100)
    public void testConstruction() {

        assertEquals(inputs.length, outputs.length);

        Person p;

        for (int i = 0; i < inputs.length; i++) {

            String[] fieldsIn = inputs[i];
            String[] fieldsOut = outputs[i];

            p = getPerson(fieldsIn[0], fieldsIn[1], fieldsIn[2]);
            assertEquals(fieldsOut[0], p.getFirstName());
            assertEquals(fieldsOut[1], p.getLastName());
            assertEquals(fieldsOut[2], p.getEmail());

        }

    }

    @Test (timeout = 100)
    public void testEmailValidation() {

      assertEquals(emailsIn.length, emailsOut.length);

      Person p;

      for (int i = 0; i < emailsIn.length; i++) {

          p = getPerson(randomName(), randomName(), emailsIn[i]);
          assertEquals(emailsOut[i], p.getEmail());

      }

    }

    @Test (timeout = 100)
    public void testIdUniqueness() {

      ArrayList<Person> personArr = new ArrayList<Person>();

      for (int i = 0; i < 20; i++) {
        personArr.add(randomPerson());
      }

      HashSet<Integer> ids = new HashSet<Integer>();

      for (Person p : personArr) {
        assertEquals(false, ids.contains(p.getID()));
        ids.add(p.getID());
      }

    }

}
