package chapter30;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/* DG: (from Chap. 30, Liang, 10th ed.)
 * The program creates a Runnable  object in an anonymous inner class (lines 34–57). 
 * This object is started in line 57 and runs continuously to change the text in the label. 
 * It sets a text in the label if the label is blank (line 40), and sets its text blank (line 42) 
 * if the label has a text. The text is set and unset to simulate a flashing effect.
 * 
 * The JavaFX GUI is run from the JavaFX 'application' thread. 
 * The flashing control is run from a separate thread. 
 * 
 * The code in a non-application thread cannot update GUI in the application thread. 
 * To update the text in the label, a new Runnable  object is created in lines 44–49. 
 *
 * Invoking Platform.runLater(Runnable r)  tells the system to run this Runnable  
 * object in the application thread.
 */
public class FlashText extends Application {
  private String text = "";
  
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {   
    StackPane pane = new StackPane();
    Label lblText = new Label("Programming is fun!");
    pane.getChildren().add(lblText);
    
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          while (true) {
            if (lblText.getText().trim().length() == 0)
              text = "Welcome";
            else
              text = "";
  
            Platform.runLater(new Runnable() {
              @Override 
              public void run() {
                lblText.setText(text);
              }
            });
            
            Thread.sleep(1000);
          }
        }
        catch (InterruptedException ex) {
        }
      }
    }).start();
   
    // Create a scene and place it in the stage
    Scene scene = new Scene(pane, 200, 100);
    primaryStage.setTitle("FlashText"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
  }

  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
