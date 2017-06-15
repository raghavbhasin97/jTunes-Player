import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a playlist in the context of this media manager.
 * A playlist contains 
 * 1). Name: The title of the playlist
 * 2). Playlist: An Arraylist of String representing the names of included songs.
 * The actual song objects exists only in database and are not duplicated to 
 * prevent memory waste.
 * 
 * @version 1.00
 * @author RaghavBhasin
 * @see https://github.com/raghavbhasin97/jTunes-Player
 * @serial 1L
 */
public class Playlist implements Serializable {

	private static final long serialVersionUID = 1L;
   private String source = System.getProperty("user.home") + "/.jTunes/";
	private String name;
	private ArrayList<String> playlist;

	/**
	 * Initializes a new playlist object with the given name
	 * @param name
	 */
	Playlist(String name) {

		this.name = new String(name);
		playlist = new ArrayList<String>();
	}

	/**
	 * Initializes the current object with the given playlist contents.
	 * @param other An other playlist object
	 */
	private void init(Playlist other) {
		this.name = other.name;
		this.playlist = other.playlist;
	}

	/**
	 * This method is outer world's access to the name of a playlist object.
	 * @return the name of the playlist
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method is outer world's access to the songs contained in a playlist.
	 * @return the list of songs in the playlist
	 */
	public ArrayList<String> getPlaylist() {
		return playlist;
	}

	/**
	 * Adds the song to the current list, if it doesn't already exist. 
	 * @param song: Name of the song to be added
	 */
	public void addSong(String song) {
		if (!exists(song)) {
			playlist.add(song);
		}
	}

	/**
	 * Checks if a given song exists in the current playlist.
	 * @param name
	 * @return true if the given name exists in playlist otherwise false.
	 */
	public boolean exists(String name) {
		for (String s : playlist) {
			if (s.equals(name))
			return true;
		}
		return false;
	}

	/**
	 * This method loads the list of saved songs from storage and initializes the 
	 * current object with those values.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void load() throws FileNotFoundException, IOException, 
	ClassNotFoundException {
		ObjectInputStream object_reader_pl = new ObjectInputStream(new 
				FileInputStream(new File(source+  name + ".plst")));
		Playlist readObject_pl = (Playlist) object_reader_pl.readObject();
		init(readObject_pl);

		object_reader_pl.close();
	}

	/**
	 * Saves the current playlist to local storage as 
	 * {@code name_of_the_playlist.plst}
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void save() throws FileNotFoundException, IOException {
		ObjectOutputStream object_writer_pl = new ObjectOutputStream(new 
				FileOutputStream(source + name + ".plst"));
		object_writer_pl.writeObject(this);
		object_writer_pl.close();

		System.out.println("Playlist " + name + " saved to root");
	}

	/**
	 * Removes the given song from the current playlist.
	 * @param name of the song
	 */
	public void remove(String name) {
		for (int i = 0; i < this.playlist.size(); i++) {
			if (playlist.get(i).equals(name))
			playlist.remove(i);
		}
	}

	/**
	 * Returns playlist object as a string.
	 * @see Object
	 * @return string representation of a playlist object
	 */
	public String toString() {
		String res = "Playlist: " + name + "\n";
		for (String song : playlist) {
			res += song + "\n";
		}

		return res;
	}
}
