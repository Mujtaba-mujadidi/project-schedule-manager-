package view;

import view.ApplicationWindow;
import com.athaydes.automaton.FXApp;
import com.athaydes.automaton.FXer;
import com.athaydes.automaton.cli.AutomatonScriptRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.Node;

import static com.athaydes.automaton.assertion.AutomatonMatcher.hasText;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ApplicationWindowTest {

  private final FXer fxer = FXer.getUserWith( FXApp.getScene().getRoot() );

  @BeforeClass
  public static void setup() {
    FXApp.startApp(new ApplicationWindow());
    try {
      Thread.sleep(1000);
    } catch (Exception e) {

    }
  }

  private void testPersonForm(FXer user, List<Node> nodes, String[] inputs, String[] outputs, boolean valid) {
    assert(nodes.size() == inputs.length);
    assert(inputs.length == outputs.length);
    List<Node> buttonsStart = user.getAll("type:Button");
    int i = 0;
    for (Node n : nodes) {
      user.clickOn(n).type(inputs[i++]);
    }
    i = 0;
    for (Node n : nodes) {
      assertThat(n, hasText(outputs[i++]));
    }
    List<Node> buttons = user.getAll("type:Button");
    assert(isValidEmail(buttons) == valid);
    user.clickOn(buttons.get(buttons.size() - 1));
  }

  private boolean isValidEmail(List<Node> buttons) {
    return !((Button)buttons.get(4)).isDisabled();
  }

  /*
   * 0 = Person form
   * 1 = Task form
   * 2 = Dependency pane
   * 3 = Schedule pane
   */
  private void switchPane(int i){
    if(i >= 0 && i < 4){
      FXer user = FXer.getUserWith();
      List<Node> nav = user.getAll("type:Button");
      user.clickOn(nav.get(i));
    }
  }

  @Test
  public void testAddPersonValid() {
    FXer user = FXer.getUserWith();
    List<Node> textFields = user.getAll("type:TextField");
    String[] inputs = new String[]{
      "Patrick",
      "Hainge",
      "patrick.hainge@kcl.ac.uk"
    };
    String[] outputs = inputs;
    testPersonForm(user, textFields, inputs, outputs, true);

    try {
      Thread.sleep(500);
    } catch (Exception e) {}

    inputs = new String[]{
      "12345",
      "12345",
      "test@gmail.com"
    };
    outputs = new String[]{
      "12345",
      "12345",
      "test@gmail.com"
    };
    testPersonForm(user, textFields, inputs, outputs, true);
  }

  @Test
  public void testAddPersonInvalid() {
    FXer user = FXer.getUserWith();
    List<Node> textFields = user.getAll("type:TextField");
    String[] inputs = new String[] {
      "Invalid",
      "Email",
      "invalid@.co"
    };
    String[] outputs = inputs;
    testPersonForm(user, textFields, inputs, outputs, false);
  }

  @Test
  public void testAddTaskValid() {
    switchPane(1);

    fxer.pause(500);
    fxer.clickOn("#tfTaskName");
    fxer.type("Prepare Presentation");
    assertThat(fxer.getAt("#tfTaskName"), hasText("Prepare Presentation"));

    fxer.moveTo("type:Slider").dragBy(60,0);
    fxer.pause(500);
    fxer.clickOn("#CreateTask");
    fxer.pause(500);
    assertThat(fxer.getAt("#tfTaskName"), hasText(""));

    fxer.clickOn("#tfTaskName");
    fxer.type("Write up speech");
    assertThat(fxer.getAt("#tfTaskName"), hasText("Write up speech"));
    
    fxer.moveTo("type:Slider").dragBy(-25,0);
    fxer.pause(500);
    fxer.clickOn("#CreateTask");
    fxer.pause(500);
    assertThat(fxer.getAt("#tfTaskName"), hasText(""));
  }

  @Test
  public void testDependencyValid() {
    switchPane(2);

    fxer.pause(500);
    fxer.clickOn("type:ComboBox")
        .pause(500)
        .moveBy(0,20).click();
    fxer.pause(500);

    fxer.clickOn("#ListOfTasks").doubleClickOn("text:[Task: Prepare Presentation, effort: 81.0, dependencies: 0]");
    fxer.pause(500);

    switchPane(3);
    fxer.pause(1500);
  }

}
