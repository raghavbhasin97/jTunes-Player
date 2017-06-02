import java.io.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class MediaManager {

	private TreeMap<String, Songs> database;
	private ArrayList<String> playlists;
  private String source = "/Users/RaghavBhasin/playlists/";
	MediaManager() {
		try {
			read_data();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			database = new TreeMap<String, Songs>();
			playlists = new ArrayList<String>();
			File dir = new File(source);
			dir.mkdir();
		}

		try {
			read_playlist();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			playlists = new ArrayList<String>();
		}
	}

	public ArrayList<String> getPlaylists() {
		return playlists;
	}

	void addSong(String title, String artistName, int releaseDate, String albumName, int durationInSecs, String genre,
			String path) {
		if (!exists(title)) {
			database.put(title, new Songs(title, artistName, releaseDate, albumName, durationInSecs, genre, path));
			try {
			save_data();
			} catch (IOException e) {
			e.printStackTrace();
			}
		}

	}

	private boolean exists(String name) {
		return database.get(name) == null ? false : true;
	}

	void save_data() throws FileNotFoundException, IOException {
		ObjectOutputStream object_writer = new ObjectOutputStream(new FileOutputStream("database.itunesjr"));
		object_writer.writeObject(database);
		object_writer.close();
	}

	void save_playlist() throws FileNotFoundException, IOException {
		ObjectOutputStream object_writer_pl = new ObjectOutputStream(new FileOutputStream(source + "playlists.itunesjr"));
		object_writer_pl.writeObject(playlists);
		object_writer_pl.close();
	}

	TreeMap<String, Songs> getDB() {
		return database;
	}

	void read_data() throws ClassNotFoundException, IOException {
		ObjectInputStream object_reader = new ObjectInputStream(new FileInputStream(new File("database.itunesjr")));
		@SuppressWarnings("unchecked")
		TreeMap<String, Songs> readObject = (TreeMap<String, Songs>) object_reader.readObject();
		database = readObject;
		object_reader.close();
	}

	void read_playlist() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream object_reader_pl = new ObjectInputStream(new FileInputStream(new File(source + "playlists.itunesjr")));
		@SuppressWarnings("unchecked")
		ArrayList<String> readObject_pl = (ArrayList<String>) object_reader_pl.readObject();
		playlists = readObject_pl;
		object_reader_pl.close();

	}

	ArrayList<String> read_particular_playlist(String name)
			throws FileNotFoundException, IOException, ClassNotFoundException {

		ObjectInputStream object_reader_pl = new ObjectInputStream(new FileInputStream(new File(source + name + ".plst")));

		Playlist playlist = (Playlist) object_reader_pl.readObject();
		object_reader_pl.close();

		return playlist.getPlaylist();
	}

	public String[][] get_playlist_data(String name) throws FileNotFoundException, ClassNotFoundException, IOException {
		String[][] songs = null;
		ArrayList<String> song_names = read_particular_playlist(name);
		int len = song_names.size();
		if (len > 0) {
			songs = new String[len][6];
			for (int i = 0; i < len; i++) {
			songs[i] = search_song(song_names.get(i));
			}
		}

		return songs;
	}

	private String[] search_song(String name) {
		Songs item = database.get(name);
		return item == null ? null : item.get_data();
	}

	void createPlaylist(String playlistName) throws FileNotFoundException, IOException {

		if (playlistName == null || playlistName.equals("") || duplicate_pl(playlistName) == true) {
			return;
		}
		playlists.add(playlistName);
		Playlist new_Playlist = new Playlist(playlistName);
		new_Playlist.save();
		save_playlist();
	}

	public String getPathByName(String name) {
		Songs item = database.get(name);
		return item == null ? null : item.getPath();
	}

	public void removepl(String name) {
		if (name == null)
			return;

		for (int i = 0; i < playlists.size(); i++) {
			if (playlists.get(i).equals(name)) {
			playlists.remove(i);
			File pl = new File(source + name + ".plst");
			pl.delete();
			try {
				this.save_playlist();
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}
	}

	boolean duplicate_pl(String name) {
		for (String s : playlists) {
			if (s.equals(name))
			return true;
		}
		return false;
	}

	void removeSong(String name, String playlist) {
		if (playlist != null) {
			try {
			removesongFromPl(name, playlist);
			} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			}
			return;
		}

		database.remove(name);
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

		try {
			save_data();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void removesongFromPl(String name, String playlist)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream object_reader_pl = new ObjectInputStream(new FileInputStream(new File(source + playlist + ".plst")));
		Playlist readObject_pl = (Playlist) object_reader_pl.readObject();
		readObject_pl.remove(name);
		readObject_pl.save();
		object_reader_pl.close();
	}

	void addSongToPlaylist(String playlistName, String title)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream object_reader_pl = new ObjectInputStream(
			new FileInputStream(new File(source + playlistName + ".plst")));
		Playlist readObject_pl = (Playlist) object_reader_pl.readObject();
		readObject_pl.addSong(title);
		readObject_pl.save();
		object_reader_pl.close();

	}

	public String[][] get_songs() {
		int len = database.size();
		String[][] songs = null;
		if (len > 0) {
			songs = new String[len][6];
			int i = 0;
			for (String name : database.keySet()) {
			songs[i] = database.get(name).get_data();
			i++;
			}
		}
		return songs;
	}

	public void clear_songs() {
		for (String s : database.keySet()) {
			for (int i = 0; i < playlists.size(); i++) {
			try {
				this.removesongFromPl(s, playlists.get(i));
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			}
		}
		this.database.clear();
		try {
			this.save_data();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
