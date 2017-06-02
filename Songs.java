import java.io.Serializable;

public class Songs implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title, artist, album;
	private int duration, release;
	private String genre;

	private String path;

	Songs(String title, String artistName, int releaseDate, String albumName, int durationInSecs, String genre,
			String path) {
		this.title = new String(title);
		this.album = new String(albumName);
		this.artist = new String(artistName);
		this.duration = durationInSecs;
		this.genre = genre;
		this.release = releaseDate;
		this.path = path;

	}

	public String getGenre() {
		return genre;
	}

	public int getDuration() {
		return duration;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public String getTitle() {
		return title;
	}

	public String getPath() {
		return path;
	}

	public int getRelease() {
		return release;
	}

	public String[] get_data() {
		String[] data = new String[6];
		data[0] = title;
		data[1] = Integer.toString(duration);
		data[2] = artist;
		data[3] = album;
		data[4] = genre.toString();
		data[5] = Integer.toString(release);
		return data;
	}

	public String toString() {

		return title + " " + Integer.toString(duration) + " " + artist + " " + album + " " + Integer.toString(release)
			+ " " + genre + " " + path;

	}

}
