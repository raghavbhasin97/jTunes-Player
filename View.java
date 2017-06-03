/**
 * 
 * Creates a window and adds the JPanel object representing
 * the actual GUI.
 * 
 * @version 1.00
 * @author RaghavBhasin
 * @see https://github.com/raghavbhasin97/iTunes.jr
 * @serial 1L
 */

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javafx.embed.swing.JFXPanel;


public class View extends JFrame{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the view object to represent the actual GUI.
	 * 
	 * @param title
	 * @param panel
	 * @param width
	 * @param height
	 */
	public View(String title, JFXPanel panel, int width, int height){
		super(title);
		
		setSize(new Dimension(width, height));
		
		/* Adds panel to the frame */
		setContentPane(panel); 
		
		/* Center the frame */
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int upperLeftCornerX = (screenSize.width - getWidth()) / 2;
		int upperLeftCornerY = (screenSize.height - getHeight()) / 2;
		setLocation(upperLeftCornerX, upperLeftCornerY);

	;
	}
}
