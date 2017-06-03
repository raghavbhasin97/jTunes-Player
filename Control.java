
import javax.swing.WindowConstants;
import javafx.embed.swing.JFXPanel;

/**
 * Defines the main method that constructs and schedules the GUI to run.
 * @version 1.00
 * @author RaghavBhasin
 * @see https://github.com/raghavbhasin97/iTunes.jr
 * @serial 1L
 *
 */
public class Control {

	

	public static void main(String[] args) {
		/* Scheduling the GUI processing in the EDT */
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int frameWidth = 650, frameHeight = 400;
				JFXPanel actualGUI = new Model();
				View frame = new View("Itunes Jr", actualGUI, frameWidth
						                , frameHeight);
				frame.setVisible(true);
				frame.setResizable(false);
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			}
		});
	}
}
