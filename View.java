

/**
 * Creates a window and adds the JPanel object representing
 * the actual GUI.
 */

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javafx.embed.swing.JFXPanel;


public class View extends JFrame{

	private static final long serialVersionUID = 1L;

	public View(String title, JFXPanel panel, int width, int height){
		super(title);
		
		setSize(new Dimension(width, height));
		
		/* Adds panel to the frame */
		setContentPane(panel); 
		
		/* Centralizing the frame (not necessary but nice) */
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int upperLeftCornerX = (screenSize.width - getWidth()) / 2;
		int upperLeftCornerY = (screenSize.height - getHeight()) / 2;
		setLocation(upperLeftCornerX, upperLeftCornerY);

	;
	}
}
