import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Creates the model component of the MVC design. Creates the GUI that user can
 * see, and handles all the communication with the back end
 * {@code MediaManager.class} 
 * This media manager is safe as it handles the tasks
 * of adding songs to database and playlist in such a manner, it reduces the
 * number of possible exceptions that can occur.
 * 
 * <p> The library used for Mp3 header parsing is mp3agic Copyright (c) 
 * 2006-2017 Michael Patricios. This library is provided under the MIT license.
 * The library can be found in the source code compiled into a jar file. The
 * internal structure of the library was not modified while compiling it into
 * a jar file. @see https://github.com/mpatric/mp3agic </p>
 * @version 1.00
 * @author RaghavBhasin
 * @see https://github.com/raghavbhasin97/jTunes-Player
 * @serial 1L
 *
 */
public class Model extends JFXPanel {
	private static final long serialVersionUID = 1L;

	MediaManager manager = new MediaManager();
	MediaPlayer mediaPlayer = null;
	String song_to_play = "";
	JToggleButton play;
	JToggleButton stop;
	View song_frame;
	JTable table;
	JScrollPane scrollPane;
	JMenuBar toolbar;
	JMenu playlist;
	String playlist_active = null;
	JTextField playing;
	JSlider volume;
	View about_frame;
	private String source = System.getProperty("user.home") + "/.jTunes/";

	/**
	 * Method to setup the main GUI that user sees and attach ActionListeners
	 * to handle user interactions.
	 */
	public Model() {
		// Initialize the main setup
		setLayout(null);

		// Create a toolbar to prove user different options
		toolbar = new JMenuBar();
		// Create all menu items
		// Setup File Menu
		JMenu file = new JMenu("File");
		file.setBorderPainted(false);
		
		// Adding items to the File menu
		JMenuItem newpl = new JMenuItem("Create Playlist");
		JMenuItem exit = new JMenuItem("Exit");

		newpl.addActionListener(new playlist());
		exit.addActionListener(new ActionListener() {

			@Override
			/**
			 * Allows an exit action to be created.
			 */
			public void actionPerformed(ActionEvent e) {
			System.exit(ABORT);

			}

		});

		JMenuItem remove_playlist = new JMenuItem("Remove Playlist");
		remove_playlist.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			// Gets name of the playlist that has to be deleted.
			String playlist = JOptionPane.showInputDialog(null
					, "Playlist name: ", "Remove Playlist",
					JOptionPane.INFORMATION_MESSAGE);
			manager.removepl(playlist);
			playlist_update();
			playlist_active = null;
			update_table(Table_with_data(manager.get_songs()));
			}

		});

		file.add(newpl);
		file.add(remove_playlist);
		file.add(exit);

		
		// Setup Songs Menu
		JMenu songs = new JMenu("Songs");
		JMenuItem clear = new JMenuItem("Clear all songs");
		clear.addActionListener(new ActionListener()
				{

				@Override
				/**
				 * Handles clearing the database.
				 */
				public void actionPerformed(ActionEvent e) {
					clear();
				}
			
				});
		songs.add(clear);
		JMenuItem add = new JMenuItem("Add Song");
		add.addActionListener(new Song(new Add_song()));
		songs.add(add);
		JMenuItem remove_song = new JMenuItem("Remove this song");
		remove_song.addActionListener(new remove_song());
		songs.add(remove_song);
		JMenuItem add_to_pl = new JMenuItem("Add to Playlist");
		add_to_pl.addActionListener(new ActionListener() {

			@Override
			/**
			 * This allows setting up an action for adding a new song to playlist.
			 */
			public void actionPerformed(ActionEvent e) {
				// Get user input for name of the playlist.
			String play_list_name = JOptionPane.showInputDialog(null, 
					"Playlist name: ", "Add to Playlist",
					JOptionPane.INFORMATION_MESSAGE);
			//Get the name of the song to be added.
			String name = (String) table.getValueAt(table.getSelectedRow(), 0);
			// Try adding the song.
			try {
				// Throws exception if the playlist doesn't exist
				manager.addSongToPlaylist(play_list_name, name);
			} catch (ClassNotFoundException | IOException e1) {
				try {
					// Creates a new playlist and then adds the song to it.
					manager.createPlaylist(play_list_name);
					manager.addSongToPlaylist(play_list_name, name);
				} catch (IOException e2) {
					e2.printStackTrace();
				} catch (ClassNotFoundException e2) {
					e2.printStackTrace();
				}
				// Update the playlist menu
				playlist_update();
				playlist.revalidate();
				playlist.repaint();
			}

			}

		});
		songs.add(add_to_pl);
		songs.setBorderPainted(false);

		// Setup Playlist menu
		playlist = new JMenu("Playlists");
		// Handles setting up the dynamic menu depending on which 
		playlist_update();
		playlist.setBorderPainted(false);

		// Setup the help menu
		JMenu help = new JMenu("Help");
		help.setBorderPainted(false);
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new About(new AboutGui()));
		help.add(about);

		// Add and possiton the toolbar.
		toolbar.add(file);
		toolbar.add(songs);
		toolbar.add(playlist);
		toolbar.add(help);
		toolbar.setBounds(0, 0, 650, 20);
		add(toolbar);

		// Add buttons
		play = new JToggleButton("Play");
		play.setBounds(120, 40, 100, 30);
		play.addActionListener(new ActionListener() {
			@Override
			/**
			 * Allows songs to be played
			 */
			public void actionPerformed(ActionEvent e) {
			play_start();
			play.setSelected(false);
			}
		});
		stop = new JToggleButton("Stop");
		stop.setBounds(220, 40, 100, 30);
		stop.addActionListener(new ActionListener() {
			@Override
			/**
			 * Allows playing media to be stopped
			 */
			public void actionPerformed(ActionEvent e) {
			stop.setSelected(false);
			play_stop();
			}
		});
		add(play);
		add(stop);
		
		
		// Add volume control
		volume = new JSlider(JSlider.VERTICAL);
		volume.setBounds(616, 15, 20, 100);
		volume.setValue(100);
		volume.addChangeListener(new ChangeListener()
				{

				@Override
				/**
				 * Allows changing the volume of playing song
				 */
				public void stateChanged(ChangeEvent e) {
					Double vol = ((double) volume.getValue())/100;
					if(mediaPlayer != null)
					{
						mediaPlayer.setVolume(vol);
					}
				}			
				});
		add(volume);

		// Add Label
		JLabel playing_label = new JLabel("Playing: ");
		playing_label.setBounds(340, 40, 100, 30);
		add(playing_label);

		// Add textField
		playing = new JTextField(220);
		playing.setBounds(390, 40, 220, 30);
		playing.setEnabled(false);
		playing.setText("Nothing");
		add(playing);

		// Scroll Pane added
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 120, 630, 250);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane
				.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
		table = Table_with_data(manager.get_songs());
		scrollPane.getViewport().add(table);
		add(scrollPane);

	}

	/**
	 * Updates the playlist dynamic menu
	 */
	void playlist_update() {
		// Remove all components.
		playlist.removeAll();
		// Get all the playlists and add the to the menu
		for (String name : manager.getPlaylists()) {
			JMenuItem list = new JMenuItem(name);
			// Add ActionListener with name of the playlist.
			list.addActionListener(new playlist_handler(name));
			playlist.add(list);
		}

		// Adds the all song menu
		JMenuItem all_songs = new JMenuItem("All Songs");
		all_songs.addActionListener(new ActionListener() {

			@Override
			/**
			 * Allows the JTable to be updated with all songs in the database
			 */
			public void actionPerformed(ActionEvent e) {
			playlist_active = null;
			update_table(Table_with_data(manager.get_songs()));
			}

		});
		playlist.add(all_songs);
	}

	/**
	 * 
	 * This class implements ActionListener for creating a new playlist.
	 * @version 1.00
	 * @author RaghavBhasin
	 * @see https://github.com/raghavbhasin97/jTunes-Player
	 *
	 */
	private class playlist implements ActionListener {

		@Override
		/**
		 * Handles adding new playlist and updating the menu.
		 */
		public void actionPerformed(ActionEvent e) {
			// Get the playlist name
			String new_playlist = JOptionPane.showInputDialog(null, 
					"Playlist name: ", "Create Playlist",
				JOptionPane.INFORMATION_MESSAGE);
			try {
				// Create the new playlist.
			manager.createPlaylist(new_playlist);
			// Update the playlist menu.
			playlist_update();
			playlist.revalidate();
			playlist.repaint();
			} catch (IOException e1) {
			e1.printStackTrace();
			}

		}

	}

	/**
	 * This class implements ActionListener for removing a song.
	 * @version 1.00
	 * @author RaghavBhasin
	 * @see https://github.com/raghavbhasin97/jTunes-Player
	 *
	 */
	private class remove_song implements ActionListener {

		@Override
		/**
		 * Handles removing the song.
		 */
		public void actionPerformed(ActionEvent e) {
			//Gets the song name from JTable.
			String name = (String) table.getValueAt(table.getSelectedRow(), 0);
			//Passes the song and active playlist name to the media manager.
			manager.removeSong(name, playlist_active);
			// Update the table depending on the playlist_active value.
			if(playlist_active == null){
				update_table(Table_with_data(manager.get_songs()));
			} else {
				try {
				update_table(Table_with_data(manager.
				get_playlist_data(playlist_active)));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			}
		}

	}

	/**
	 * This class implements ActionListener for sub-menu in the playlist menu.
	 * This class allows jTable to be populated with data according to the 
	 * playlist.
	 * @version 1.00
	 * @author RaghavBhasin
	 * @see https://github.com/raghavbhasin97/jTunes-Player
	 *
	 */
	private class playlist_handler implements ActionListener {
		String name;

		/**
		 * Create a new playlist_handler object with name of the playlist.
		 * @param name of the playlist
		 */
		playlist_handler(String name) {
			this.name = name;
		}

		@Override
		/**
		 * Allows populating JTable with data corresponding to this playlist.
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				// Get data from the manager for a particular list.
			String[][] data = manager.get_playlist_data(name);
			// Change active playlist.
			playlist_active = name;
			// Update the table.
			update_table(Table_with_data(data));

			} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
			}
		}

	}

	/**
	 * This method allows switching table by reinitializing it with the given
	 * table.
	 * @param table the JTable to initialize the main table with.
	 */
	private void update_table(JTable table) {
		// Set the new table
		this.table = table;
		// Update the view in the GUI
		scrollPane.getViewport().removeAll();
		scrollPane.getViewport().add(table);
	}

	/**
	 * This method allows playing the selected media file.
	 */
	private void play_start() {
		// First stop any previous playing file.
		play_stop();
		// Get the name of the song to play.
		String name = (String) table.getValueAt(table.getSelectedRow(), 0);
		//Set playing text field
		playing.setText(name);
		// Get the path for song to be played.
		song_to_play = manager.getPathByName(name);
		// Init a new media player.
		Media hit = new Media(new File(song_to_play).toURI().toString());
		mediaPlayer = new MediaPlayer(hit);
		// Play begins.
		mediaPlayer.play();
		// Set volume
		Double vol = (double) volume.getValue();
		mediaPlayer.setVolume(vol/100);
	}

	/**
	 * This method allows stopping the active media player
	 */
	private void play_stop() {
		// Set the playing text field.
		playing.setText("Nothing");
		if (mediaPlayer != null)
			// Stop playing and allow the object to be garbage collected.
			mediaPlayer.stop();
			mediaPlayer = null;
	}

	/**
	 * This method allows clearing the entire songs database.
	 */
	private void clear() {
		try {
			// Invoke the clear on media manager.
			manager.clear_songs();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		// update the table of songs.
		update_table(Table_with_data(manager.get_songs()));
		// Stop and destroy the media player if playing a song.
		play_stop();
	}

	/**
	 * This method creates a JTable with the provide data. It can 
	 * @param data: The songs data which can belong to either all songs or 
	 * specific ones in the playlist. Used to update the GUI if the database
	 * changed.
	 * @return
	 */
	private JTable Table_with_data(String[][] data) {
		// Setup the column data
		String column[] = { "Title", "Duration(Secs)", "Artist", "Album",
				"Genre", "Release Date" };
		// If the data is null then a blank table is returned.
		if (data == null) {
			return new JTable();
		}
		// Otherwise create a new JTable with the above data
		JTable table = new JTable(data, column) {
			private static final long serialVersionUID = 1L;
			// Editing for any cell is disabled.
			@Override
			public boolean isCellEditable(int row, int column) {
			return false;
			};
		};

		// Setup the table view
		table.setPreferredScrollableViewportSize(new Dimension(600, 250));
		table.setFillsViewportHeight(true);
		table.setGridColor(Color.BLACK);
		table.setForeground(Color.BLUE);
		table.setSelectionBackground(Color.GREEN);
		table.setSelectionForeground(Color.BLACK);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		return table;

	}


	/**
	 * This class creates GUI to add new song. User is allowed to add songs only
	 *  by dragging them onto the add image. The mp3agic library parses the
	 *  ID3v tags to get info to add song to our database.
	 * @version 1.00
	 * @author RaghavBhasin
	 * @see https://github.com/raghavbhasin97/jTunes-Player
	 *
	 */
	class Add_song extends JFXPanel {
		private static final long serialVersionUID = 1L;
		JLabel add_song;
		int duration = 0, release = 0;
		String artist = "", title = "", album = "", genre = "", path = "";

		/**
		 * Create the Add song GUI visible to the user
		 */
		public Add_song() {

			//Gets the image to add for songs to be dragged on.
			BufferedImage song = null;
			
			//Try reading the file from source directory 
			 try {
			song = ImageIO.read(new File(source + "addsong.png"));
			} catch (IOException e) {
				//If fails, try reading from the web and save it locally
				URL songs;
				try {
					songs = new URL("https://i.imgsafe.org/fbc076f79c.png");
					song = ImageIO.read(songs);
					ImageIO.write(song, "png", new File(source + "addsong.png"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
			}
		
			// Add the image
			JLabel add_song = new JLabel(new ImageIcon(song));
			add_song.setBounds(175, 15, 50, 50);
			// Setting up the drop target
			add_song.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;
			/**
			 * Allows to create a drop event that extracts ID3v tags and 
			 * adds the song to database.
			 */
			public void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					@SuppressWarnings("unchecked")
					// Get the list of all files
					List<File> droppedFile = (List<File>) evt.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					// Get attributes for the song file
					for (File file : droppedFile) {
						// Get path
						path = file.getAbsolutePath();
						try {
							// Init the Mp3File object. If it fails then the file is 
							// Invalid and not added to library.
						Mp3File mp3file = new Mp3File(path);
						duration = (int) mp3file.getLengthInSeconds();
						parse_mp3(mp3file);
						} catch (InvalidDataException e) {
						JOptionPane.showMessageDialog(null, "Invalid MP3 header", 
								"Alert", JOptionPane.WARNING_MESSAGE);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			});
			add(add_song);
			
			//Place add button
			JButton add = new JButton("Add");
			add.setBounds(150, 65, 100, 30);
			add.addActionListener(new add());
			add(add);

		}

		/**
		 * This method extracts information about the Mp3 file
		 * by parsing the header using mp3agic library.
		 * @param mp3file: The file to be parsed
		 */
		private void parse_mp3(Mp3File mp3file) {
			// If it has ID3v1 tags get the info
			if (mp3file.hasId3v1Tag()) {
			ID3v1 id3v1Tag = mp3file.getId3v1Tag();
			artist = id3v1Tag.getArtist();
			title = id3v1Tag.getTitle();
			album = id3v1Tag.getAlbum();
			release = Integer.parseInt(id3v1Tag.getYear());
			genre = id3v1Tag.getGenreDescription();
			} 
			// Else if it has the ID3v2 tags get the info
			else if (mp3file.hasId3v2Tag()) {
			ID3v2 id3v2Tag = mp3file.getId3v2Tag();
			artist = id3v2Tag.getArtist();
			title = id3v2Tag.getTitle();
			album = id3v2Tag.getAlbum();
			release = Integer.parseInt(id3v2Tag.getYear());
			genre = id3v2Tag.getGenreDescription();
			} 
			// If no tags then the file is not added to library.
			else {
			JOptionPane.showMessageDialog(null, 
			"Invalid MP3 header", "Alert", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		/**
		 * Clears the values after an invalid file is tried to be parsed or the 
		 * add fails.
		 */
		private void clear_values() {
			artist = "";
			title = "";
			album = "";
			release = 0;
			genre = "";
			path = "";
			duration = 0;
		}

		/**
		 * This class implements action listener that invokes the add operation.
		 * @version 1.00
		 * @author RaghavBhasin
		 * @see https://github.com/raghavbhasin97/jTunes-Player
		 *
		 */
		class add implements ActionListener {

			@Override
			/**
			 * This method invokes the add Mp3 file action
			 */
			public void actionPerformed(ActionEvent e) {
				// If the artist is blank this implies that tag parsing failed or 
				// blank parameters provided and hence song is not added.
			if (artist == "") {
				JOptionPane.showMessageDialog(null, 
				"Invalid MP3 header", "Alert", JOptionPane.WARNING_MESSAGE);

			} else {
				// Add the song
				manager.addSong(title, artist, release, album, duration, 
						genre, path);
			}
			// Change active playlist
			playlist_active = null;
			// Update the JTable view in GUI to all songs library
			update_table(Table_with_data(manager.get_songs()));
			clear_values();
			// Dispose the frame
			song_frame.dispose();
			}

		}

	}

	/**
	 * Allows adding the song by creating a new window and initializing the GUI
	 * provided by the Add_song class.
	 * @version 1.00
	 * @author RaghavBhasin
	 * @see https://github.com/raghavbhasin97/jTunes-Player
	 *
	 */
	class Song implements ActionListener {
		JFXPanel gui;
		
		// Get the main GUI as object init parameter.
		Song(JFXPanel gui) {
			this.gui = gui;
		}

		/**
		 * This allows new window to be displayed.
		 */
		public void actionPerformed(ActionEvent e) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int frameWidth = 400, frameHeight = 130;
				song_frame = new View("Drag Mp3 file", gui, frameWidth
						, frameHeight);
				song_frame.setVisible(true);
				song_frame.setResizable(false);
			}
			});

		}
	}

	
	/**
	 * Controls the popup that displays the about us information regarding this
	 * media player such as license and version.
	 * @version 1.00
	 * @author RaghavBhasin
	 * @see https://github.com/raghavbhasin97/jTunes-Player
	 *
	 */
	class About implements ActionListener {
		JFXPanel gui;
		
		// Get the main GUI as object init parameter.
		About(JFXPanel gui) {
			this.gui = gui;
		}

		/**
		 * This allows new window to be displayed.
		 */
		public void actionPerformed(ActionEvent e) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int frameWidth = 400, frameHeight = 320;
				about_frame = new View("About", gui, frameWidth
						, frameHeight);
				about_frame.setVisible(true);
				about_frame.setResizable(false);
			}
			});

		}
	}
	/**
	 * This class sets up the GUI for about page 
	 * @version 1.00
	 * @author RaghavBhasin
	 * @see https://github.com/raghavbhasin97/jTunes-Player
	 *
	 */
	class AboutGui extends JFXPanel {
		private static final long serialVersionUID = 1L;
		
		AboutGui()
		{	
			// Initialize the main setup
			setLayout(null);
			// Setup labels with add text
			JLabel version = new JLabel("jTunes Player Version 1.0.0");
			JLabel copyright = new JLabel("Copyright © 2017 Raghav Bhasin");
			JLabel rights = new JLabel("All Rights Reserved");
			JLabel description = new JLabel("A program to manage and play your"
					+ " favourite songs");
			JLabel report = new JLabel("Report any problems to email address belo"
					+ "w.");
			JLabel email = new JLabel("raghavbhasin97@gmail.com");
			JLabel disclamer = new JLabel("Disclaimer: This software is provided "
					+ "\"As is\"");
			JLabel disclaimer2 = new JLabel("without any expressed or implied war"
					+ "ranty.");
			JButton cancel = new JButton("Cancel");
			
			// Set bound (place the labels) 
			version.setBounds(30, 20, 300, 20);
			copyright.setBounds(30, 35, 300, 20);
			rights.setBounds(30,50,300,20);
			description.setBounds(30, 80, 350, 20);
			report.setBounds(30,125,350,20);
			email.setBounds(30,140,350,20);
			disclamer.setBounds(30, 190, 350, 20);
			disclaimer2.setBounds(30, 205, 350, 20);
			cancel.setBounds(300, 250, 80, 30);
			
			//Setup Action listener
			cancel.addActionListener(new ActionListener()
					{

				  /**
				   * This allows cancel button to be active and dismiss the about
				   * frame.
				   */
					public void actionPerformed(ActionEvent e) {
						about_frame.dispose();
					}
				
					});
			
			//Add to the frame 
			add(version);
			add(copyright);
			add(rights);
			add(description);
			add(report);
			add(email);
			add(disclamer);
			add(disclaimer2);
			add(cancel);
		}
	}
	
}