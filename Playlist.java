import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {

	private static final long serialVersionUID = 1L;
   private String source = "/Users/RaghavBhasin/playlists/";
	private String name;
	private ArrayList<String> playlist;

	Playlist(String name) {

		this.name = new String(name);
		playlist = new ArrayList<String>();
	}

	private void init(Playlist other) {
		this.name = other.name;
		this.playlist = other.playlist;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getPlaylist() {
		return playlist;
	}

	public void addSong(String song) {
		if (!exists(song)) {
			playlist.add(song);
		}
	}

	public boolean exists(String name) {
		for (String s : playlist) {
			if (s.equals(name))
			return true;
		}
		return false;
	}

	public void load() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream object_reader_pl = new ObjectInputStream(new FileInputStream(new File(source+  name + ".plst")));
		Playlist readObject_pl = (Playlist) object_reader_pl.readObject();
		init(readObject_pl);

		object_reader_pl.close();
	}

	public void save() throws FileNotFoundException, IOException {
		ObjectOutputStream object_writer_pl = new ObjectOutputStream(new FileOutputStream(source + name + ".plst"));
		object_writer_pl.writeObject(this);
		object_writer_pl.close();

		System.out.println("Playlis " + name + " saved to root");
	}

	public void remove(String name) {
		for (int i = 0; i < this.playlist.size(); i++) {
			if (playlist.get(i).equals(name))
			playlist.remove(i);
		}
	}

	public String toString() {
		String res = "Playlist: " + name + "\n";
		for (String song : playlist) {
			res += song + "\n";
		}

		return res;
	}
}
