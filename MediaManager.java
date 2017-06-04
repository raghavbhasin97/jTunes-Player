import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * This class represents the media manager. A media manager object contains 
 * a database which stores songs and a list of playlists created by the user. 
 * The songs are are stored in a TreeMap as {@code <Key,Value> 
 * = <Title of the song,Songs Object>} for efficient operations. This allows 
 * operation to have {@code O(n)} complexity instead.
 * 
 * @see Map
 * @version 1.00
 * @author RaghavBhasin
 * @see https://github.com/raghavbhasin97/iTunes.jr
 * @serial 1L
 *
 */
public class MediaManager {

	private Map<String, Songs> database;
	private ArrayList<String> playlists;
	private String source = System.getProperty("user.home") + "/playlists/";

	/**
	 * Constructs a media manager object by loading data from storage.
	 * 
	 */
	MediaManager() {
		// Try to load songs data from database.itunesjr
		try {
			read_data();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			// If it fails to read database a new TreeMap is initialized.
			database = new HashMap<String, Songs>();
		}
		
		//Try to load playlist names from playlist.itunesjr
		try {
			read_playlist();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		// If it fails to read the file, a new Arraylist is initialized.
			playlists = new ArrayList<String>();
		// Making a new directory in the source to save all data.
			File dir = new File(source);
			dir.mkdir();
		}
	}

	/**
	 * This method is outer world's access to playlists in database.
	 * @return a list of playlists in the database
	 */
	public ArrayList<String> getPlaylists() {
		return playlists;
	}

	/**
	 * This method adds a new song to the database with all the below parameters.
	 * These parameters are obtained by parsing Mp3 header using mp3agic library.
	 * @param title
	 * @param artistName
	 * @param releaseDate
	 * @param albumName
	 * @param durationInSecs
	 * @param genre
	 * @param path
	 */
	void addSong(String title, String artistName, int releaseDate, String 
			albumName, int durationInSecs, String genre,
			String path) {
		if (!exists(title)) {
			database.put(title, new Songs(title, artistName, releaseDate, 
					albumName, durationInSecs, genre, path));
			try {
			save_data();
			} catch (IOException e) {
			e.printStackTrace();
			}
		}

	}

	/**
	 * This method checks if the song with title same of name parameter already
	 * exists in the database.
	 * @param name of the song
	 * @return true if the song with name exists in the database otherwise it 
	 * returns false.
	 */
	private boolean exists(String name) {
		return database.get(name) == null ? false : true;
	}

	/**
	 * This method saves the current database state (database.itunesjr) to the 
	 * storage.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	void save_data() throws FileNotFoundException, IOException {
		// Opens the file database.itunesjr to write the database state
		// If previous state exists, it is overwritten. 
		ObjectOutputStream object_writer = new ObjectOutputStream(new 
				FileOutputStream("database.itunesjr"));
		object_writer.writeObject(database);
		object_writer.close();
	}

	/**
	 * This method save the current playlist state (playlists.itunesjr) to the
	 * storage.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	void save_playlist() throws FileNotFoundException, IOException {
	// Opens the file playlists.itunesjr to write the playlist state
	// If previous state exists, it is overwritten. 
		ObjectOutputStream object_writer_pl = new ObjectOutputStream(
			new FileOutputStream(source + "playlists.itunesjr"));
		object_writer_pl.writeObject(playlists);
		object_writer_pl.close();
	}

	/**
	 * This method is outer world's access to songs in database.
	 * @return a map of songs in the database
	 */
	Map<String, Songs> getDB() {
		return database;
	}

	/**
	 * This method loads the current database state (database.itunesjr) from the
	 * storage.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	void read_data() throws ClassNotFoundException, IOException {
		//Opens the database.tunesjr file
		ObjectInputStream object_reader = new ObjectInputStream(new 
				FileInputStream(new File("database.itunesjr")));
		@SuppressWarnings("unchecked")
		//Reads the object and initializes the current database from it.
		Map<String, Songs> readObject = (Map<String, Songs>)
		object_reader.readObject();
		database = readObject;
		object_reader.close();
	}

	/**
	 * This method loads the current playlist state (playlists.itunesjr) from the
	 * storage.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	void read_playlist() throws FileNotFoundException, IOException, 
	ClassNotFoundException {
		ObjectInputStream object_reader_pl = new ObjectInputStream(
			new FileInputStream(new File(source + "playlists.itunesjr")));
		@SuppressWarnings("unchecked")
		//Reads the object and initializes the current playlists list from it.
		ArrayList<String> readObject_pl = (ArrayList<String>) 
		object_reader_pl.readObject();
		playlists = readObject_pl;
		object_reader_pl.close();

	}

	/**
	 * This method read a particular playlist and returns the list of songs.
	 * @param name of the playlist
	 * @return ArrayList of songs in the playlist with name same as parameter 
	 * name.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	ArrayList<String> read_particular_playlist(String name)
			throws FileNotFoundException, IOException, ClassNotFoundException {

		ObjectInputStream object_reader_pl = new ObjectInputStream(
			new FileInputStream(new File(source + name + ".plst")));

		Playlist playlist = (Playlist) object_reader_pl.readObject();
		object_reader_pl.close();

		return playlist.getPlaylist();
	}

	/**
	 * This method returns the data about songs contained in a particular 
	 * playlist to initialize JTable.
	 * @param name of the playlist.
	 * @return An array of song's data for a playlist having name same as
	 *  parameter name or null if the playlist is blank.
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public String[][] get_playlist_data(String name) throws FileNotFoundException
	, ClassNotFoundException, IOException {
		String[][] songs = null;
		// Get the list of songs in the playlist.
		ArrayList<String> song_names = read_particular_playlist(name);
		int len = song_names.size();
		if (len > 0) {
			// Get the songs data as String[]
			songs = new String[len][6];
			for (int i = 0; i < len; i++) {
			songs[i] = search_song(song_names.get(i));
			}
		}

		return songs;
	}

	/**
	 * This method gets the data for a particular song.
	 * @param name of the song.
	 * @return the String[] initialized with the data for a song with title 
	 * same as name parameter or null if the song doesn't exist.
	 */
	private String[] search_song(String name) {
		Songs item = database.get(name);
		return item == null ? null : item.get_data();
	}

	/**
	 * This method creates a new playlist with name as that of the parameter 
	 * playlistName.
	 * @param playlistName: The name of the playlist.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	void createPlaylist(String playlistName) throws FileNotFoundException
	, IOException {

		// If the parameter is null or empty string, the create playlist request 
		// is not completed.
		if (playlistName == null || playlistName.equals("") 
				|| duplicate_pl(playlistName) == true) {
			return;
		}
		// Create a new playlist, save it and then save the updated list of 
		// playlists.
		playlists.add(playlistName);
		Playlist new_Playlist = new Playlist(playlistName);
		new_Playlist.save();
		save_playlist();
	}

	/**
	 * Gives back the path for media player to actually start playing the song.
	 * @param name of the song
	 * @return the path of a song with title same of the name parameter or null.
	 */
	public String getPathByName(String name) {
		Songs item = database.get(name);
		return item == null ? null : item.getPath();
	}

	/**
	 * Completely removes the playlist with name same as that of the parameter 
	 * name. 
	 * @param name of the playlist.
	 */
	public void removepl(String name) {
		if (name == null)
			return;
// Removes the name of the playlist from list of playlists if it exists.
		for (int i = 0; i < playlists.size(); i++) {
			if (playlists.get(i).equals(name)) {
			playlists.remove(i);
			File pl = new File(source + name + ".plst");
			// Delete the saved file.
			pl.delete();
			try {
				//save the updated list to storage.
				save_playlist();
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}
	}

	/**
	 * Checks if a playlist with the name same as parameter name already exists.
	 * @param name of the playlist.
	 * @return true if the playlist already exist otherwise returns false.
	 */
	boolean duplicate_pl(String name) {
		for (String s : playlists) {
			if (s.equals(name))
			return true;
		}
		return false;
	}

	/**
	 * Removes the song with title equal to name. The parameter playlist maybe
	 * null, which means the remove is invoked from the main song database.
	 * If it is not null, then it will be the name of active playlist from
	 * which the song is removed.
	 * @param name of the song
	 * @param playlist the name of a playlist or null
	 */
	void removeSong(String name, String playlist) {
		if (playlist != null) {
			try {
	     // Remove the song from a particular playlist.
			removesongFromPl(name, playlist);
			} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			}
			return;
		}

		//Remove from database
		database.remove(name);
		// Remove the song from every playlist, since the song is removed from the
		// main database, it makes no sense to have it in any playlist.
		for (String s : playlists) {
			try {
			removesongFromPl(name, s);
			} catch (FileNotFoundException e) {
			e.printStackTrace();
			} catch (ClassNotFoundException e) {
			e.printStackTrace();
			} catch (IOException e) {
			e.printStackTrace();
			}
		}

		// Now save the updated data.
		try {
			save_data();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method removes the song with title same as name from the playlist
	 * if it exists there.
	 * @param name of song
	 * @param playlist: name of playlist
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	void removesongFromPl(String name, String playlist)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		// Open the respective playlist dta file from storage
		ObjectInputStream object_reader_pl = new ObjectInputStream(
			new FileInputStream(new File(source + playlist + ".plst")));
		// Get the data and call remove on the playlist.
		Playlist readObject_pl = (Playlist) object_reader_pl.readObject();
		readObject_pl.remove(name);
		// Save updated version to the storage.
		readObject_pl.save();
		object_reader_pl.close();
	}
   
	/**
	 * Adds the song to playlist with name playlist if it doesn't exist already.
	 * @param playlistName: Name of the playlist.
	 * @param title: Name of the song.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	void addSongToPlaylist(String playlistName, String title)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		// Open the playlist
		ObjectInputStream object_reader_pl = new ObjectInputStream(
			new FileInputStream(new File(source + playlistName + ".plst")));
		// Load data
		Playlist readObject_pl = (Playlist) object_reader_pl.readObject();
		// Add the song to the playlist
		readObject_pl.addSong(title);
		// Save the updated playlist to storage.
		readObject_pl.save();
		object_reader_pl.close();

	}

	/**
	 * This method gives back data for all songs in the correct format to
	 *  initialize the JTable.
	 * @return A 2-D String array initialized with the data for all songs.
	 */
	public String[][] get_songs() {
		int len = database.size();
		String[][] songs = null;
		if (len > 0) {
			songs = new String[len][6];
			int i = 0;
			// For all songs in the database, get there data
			for (String name : database.keySet()) {
			songs[i] = database.get(name).get_data();
			i++;
			}
		}
		return songs;
	}

	/**
	 * This method clears the entire database. It does this by removing all the 
	 * songs from every single playlist file. Then empties the main database.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 */
	public void clear_songs() throws FileNotFoundException, IOException
	, ClassNotFoundException {
		
		//Open all playlists in the database.
		for (int i = 0; i < playlists.size(); i++) {
			ObjectInputStream object_reader_pl = new ObjectInputStream(
				new FileInputStream(new File(source + playlists.get(i) + ".plst")));
			// Load data
			Playlist readObject_pl = (Playlist) object_reader_pl.readObject();
			// Clear the playlist
			readObject_pl.getPlaylist().clear();
			// Save the updated playlist to storage.
			readObject_pl.save();
			object_reader_pl.close();
		}

		// Clear the main database
		database.clear();
		try {
			this.save_data();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
