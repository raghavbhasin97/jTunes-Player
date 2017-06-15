import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Creates a class to handle logging errors that may occur during the course
 * of this software's lifetime. All records are appended which means that all 
 * error logs are kept. This software/tool is safe and hence errors are not
 * going to occur often, however, if they do it tracks them. This will help
 * in debugging errors and fixing bugs in this software.
 * 
 * @version 1.00
 * @author RaghavBhasin
 * @see https://github.com/raghavbhasin97/jTunes-Player
 * @serial 1L
 *
 */
public class ErrorLog {
	private String source = System.getProperty("user.home") + "/.jTunes/";
	BufferedWriter writer = null;

	/**
	 * Creates a new ErrorLog object by opening the log file
	 */
	ErrorLog() {
		//Try reading data
		try {
			writer = new BufferedWriter(new FileWriter(source + "error.log", true));
		} catch (IOException e) {
			// Marks the first run 
			// Making a new directory in the source to save all data.
			File dir = new File(source);
			dir.mkdir();
			try {
			writer = new BufferedWriter(new FileWriter(source + "error.log"));
			write("Created the file base");
			} catch (IOException e1) {
				// Unresolvable error not likely to ever happen.
			e1.printStackTrace();
			}

		}
	}

	/**
	 * Adds the given entry to error log along with time stamp of when the event 
	 * Occurred.
	 * @param entry to add to the log
	 */
	public void write(String entry) {
		//Get the time stamp
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		try {
			//Write entry
			writer.write(timestamp + ":  ");
			writer.write(entry + "\n");
			//Flushing output from buffer
			writer.flush();
		} catch (IOException e) {
		// Unresolvable error not likely to ever happen.
			e.printStackTrace();
		}
	}

	/**
	 * Closes the error logger when the software exits.
	 */
	public void close() {
		try {
			write("Terminating jTunes player.");
			writer.close();
		} catch (IOException e) {
		// Unresolvable error not likely to ever happen.
		}
	}

}
