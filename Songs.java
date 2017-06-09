/**
 * This class represents a song in the context of the jTunes media manager. A
 * song consists of: 
 * 1). Title: Name of the song. 
 * 2). Artist: The Artist/singer of the song. 
 * 3). Album: The Album of which this song is a part. 
 * 4). Duration: Play time of the track in seconds. 
 * 5). Release Date: The year in which the song was released. 
 * 6). Genre: The genre to which the song belongs.
 * 7). Path: The local path of the song on the computer.
 * 
 * All these details are extracted from the ID3v tags from file's Mp3 header.
 * 
 * @version 1.00
 * @author RaghavBhasin
 * @see https://github.com/raghavbhasin97/jTunes-Player
 * @serial 1L
 */

import java.io.Serializable;

public class Songs implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title, artist, album, genre;
	private int duration, release;
	private String path;

	/**
	 * Constructor that initializes a song object with the above mentioned 
	 * parameters.
	 * @param title
	 * @param artistName
	 * @param releaseDate
	 * @param albumName
	 * @param durationInSecs
	 * @param genre
	 * @param path
	 */
	Songs(String title, String artistName, int releaseDate, String albumName, 
			int durationInSecs, String genre,
			String path) {
		this.title = new String(title);
		this.album = new String(albumName);
		this.artist = new String(artistName);
		this.duration = durationInSecs;
		this.genre = genre;
		this.release = releaseDate;
		this.path = path;

	}

	/**
	 * This method is outer world's access to the genre of a song object.
	 * @return the genre of the song
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * This method is outer world's access to the duration of a song object.
	 * @return the duration of the song
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * This method is outer world's access to the artist of a song object. 
	 * @return the artist of the song
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * This method is outer world's access to the album of a song object.
	 * @return the album of the song
	 */
	public String getAlbum() {
		return album;
	}

	/**
	 * This method is outer world's access to the title of a song object.
	 * @return the title of the song
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * This method is outer world's access to the local path of a song object.
	 * @return the path of the song
	 */
	public String getPath() {
		return path;
	}

	/**
	 * This method is outer world's access to the release year of a song object.
	 * @return the release year of the song
	 */
	public int getRelease() {
		return release;
	}

	/**
	 * This method returns an array representation of the song object. 
	 * @return a string array representation of the song object.
	 */
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

	/**
	 * Returns the song object as a string
	 * @see Object
	 * @return a string representation of the object.
	 */
	public String toString() {

		return title + " " + Integer.toString(duration) + " " + artist + " " 
		+ album + " " + Integer.toString(release) + " " + genre + " " + path;

	}

}
