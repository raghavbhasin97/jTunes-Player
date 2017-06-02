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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import com.mpatric.mp3agic.*;

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

	public Model() {
		this.setLayout(null);

		toolbar = new JMenuBar();
		// Create all menu items
		// Setup File Menu
		JMenu file = new JMenu("File");
		file.setBorderPainted(false);

		JMenuItem newpl = new JMenuItem("Create Playlist");

		JMenuItem exit = new JMenuItem("Exit");

		newpl.addActionListener(new playlist());
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			System.exit(ABORT);

			}

		});

		JMenuItem remove_playlist = new JMenuItem("Remove Playlist");
		remove_playlist.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			String playlist = JOptionPane.showInputDialog(null, "Playlist name: ", "Remove Playlist",
					JOptionPane.INFORMATION_MESSAGE);
			manager.removepl(playlist);
			playlist_update();
			playlist_active = null;
			update_table(create_Table());
			}

		});

		file.add(newpl);

		file.add(remove_playlist);
		file.add(exit);

		// Setup Songs Menu
		JMenu songs = new JMenu("Songs");
		JMenuItem clear = new JMenuItem("Clear all songs");
		clear.addActionListener(new clear());
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
			public void actionPerformed(ActionEvent e) {
			String play_list_name = JOptionPane.showInputDialog(null, "Playlist name: ", "Add to Playlist",
					JOptionPane.INFORMATION_MESSAGE);
			String name = (String) table.getValueAt(table.getSelectedRow(), 0);
			try {
				manager.addSongToPlaylist(play_list_name, name);
			} catch (ClassNotFoundException | IOException e1) {
				try {
					manager.createPlaylist(play_list_name);
					manager.addSongToPlaylist(play_list_name, name);
				} catch (IOException e2) {
					e2.printStackTrace();
				} catch (ClassNotFoundException e2) {
					e2.printStackTrace();
				}
				playlist_update();
				playlist.revalidate();
				playlist.repaint();
			}

			}

		});
		songs.add(add_to_pl);
		songs.setBorderPainted(false);

		playlist = new JMenu("Playlists");
		playlist_update();
		playlist.setBorderPainted(false);

		JMenu help = new JMenu("Help");
		help.setBorderPainted(false);
		JMenuItem hel = new JMenuItem("google");
		hel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
				java.awt.Desktop.getDesktop().browse(new URI("https://github.com/raghavbhasin97/iTunes.jr"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			}
			
		});
		JMenuItem iTunes = new JMenuItem("iTunes");
		iTunes.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
				java.awt.Desktop.getDesktop().browse(new URI("https://www.apple.com/in/itunes/music/"));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			
			}
			
		});
		help.add(iTunes);
		help.add(hel);
		

		toolbar.add(file);
		toolbar.add(songs);
		toolbar.add(playlist);
		toolbar.add(help);
		toolbar.setBounds(0, 0, 650, 20);

		add(toolbar);

		// Add buttons
		play = new JToggleButton("Play");
		play.setBounds(120, 40, 100, 30);
		play.addActionListener(new play());
		stop = new JToggleButton("Stop");
		stop.setBounds(220, 40, 100, 30);
		stop.addActionListener(new stop());
		add(play);
		add(stop);

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
		scrollPane.setBounds(10, 80, 630, 280);
		scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
		table = create_Table();
		scrollPane.getViewport().add(table);
		add(scrollPane);

	}

	void playlist_update() {
		playlist.removeAll();
		for (String name : manager.getPlaylists()) {
			JMenuItem list = new JMenuItem(name);
			list.addActionListener(new playlist_handler(name));
			playlist.add(list);
		}

		JMenuItem all_songs = new JMenuItem("All Songs");
		all_songs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			playlist_active = null;
			update_table(create_Table());
			}

		});
		playlist.add(all_songs);
	}

	private class playlist implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String new_playlist = JOptionPane.showInputDialog(null, "Playlist name: ", "Create Playlist",
				JOptionPane.INFORMATION_MESSAGE);
			try {
			manager.createPlaylist(new_playlist);
			playlist_update();
			playlist.revalidate();
			playlist.repaint();
			} catch (IOException e1) {
			e1.printStackTrace();
			}

		}

	}

	private class remove_song implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String name = (String) table.getValueAt(table.getSelectedRow(), 0);

			manager.removeSong(name, playlist_active);
			update_table(create_Table());
		}

	}

	private class playlist_handler implements ActionListener {
		String name;

		playlist_handler(String name) {
			this.name = name;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				
			String[][] data = manager.get_playlist_data(name);
			playlist_active = name;
			update_table(create_Table_with_data(data));

			} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
			}
		}

	}

	private class clear implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			clear();

		}

	}

	private void update_table(JTable table) {
		this.table = table;
		scrollPane.getViewport().removeAll();
		scrollPane.getViewport().add(table);
	}

	private void play_start() {
		play_stop();
		String name = (String) table.getValueAt(table.getSelectedRow(), 0);
		playing.setText(name);
		song_to_play = manager.getPathByName(name);
		Media hit = new Media(new File(song_to_play).toURI().toString());
		mediaPlayer = new MediaPlayer(hit);
		mediaPlayer.play();
	}

	// Play media file
	private void play_stop() {
		playing.setText("Nothing");
		if (mediaPlayer != null)
			mediaPlayer.stop();
	}

	private void clear() {

		manager.clear_songs();
		update_table(create_Table());
		play_stop();
	}

	// Create table from database
	private JTable create_Table() {
		String data[][] = manager.get_songs();
		String column[] = { "Title", "Duration(Secs)", "Artist", "Album", "Genre", "Release Date" };
		if (data == null) {
			JTable table = new JTable();
			return table;
		}

		JTable table = new JTable(data, column) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
			return false;
			};
		};

		table.setPreferredScrollableViewportSize(new Dimension(600, 280));
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

	private JTable create_Table_with_data(String[][] data) {
		String column[] = { "Title", "Duration(Secs)", "Artist", "Album", "Genre", "Release Date" };
		if (data == null) {
			JTable table = new JTable();
			return table;
		}

		JTable table = new JTable(data, column) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
			return false;
			};
		};

		table.setPreferredScrollableViewportSize(new Dimension(600, 280));
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

	// Play handles the song play
	private class play implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(" Play!");
			play_start();
			play.setSelected(false);
		}
	}

	// Stop handels the song play stop
	private class stop implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Stopping");
			stop.setSelected(false);
			play_stop();
		}
	}

	// This class is the Songs menu item
	class Add_song extends JFXPanel {
		private static final long serialVersionUID = 1L;
		JLabel add_song;
		int duration = 0, release = 0;
		String artist = "", title = "", album = "", genre = "", path = "";

		public Add_song() {

			BufferedImage song = null;
			try {
			song = ImageIO.read(new File("/Users/RaghavBhasin/Documents/workspace/ItunesJr/Resources/addsong.png"));
			} catch (IOException e) {
			e.printStackTrace();
			}
			JLabel add_song = new JLabel(new ImageIcon(song));
			add_song.setBounds(175, 15, 50, 50);
			add_song.setDropTarget(new DropTarget() {

			private static final long serialVersionUID = -4788235742917964869L;

			public void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					@SuppressWarnings("unchecked")
					List<File> droppedFile = (List<File>) evt.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);

					for (File file : droppedFile) {
						path = file.getAbsolutePath();
						try {
						Mp3File mp3file = new Mp3File(path);
						duration = (int) mp3file.getLengthInSeconds();
						parse_mp3(mp3file);
						} catch (InvalidDataException e) {
						JOptionPane.showMessageDialog(null, "Invalid MP3 header", "Alert", JOptionPane.WARNING_MESSAGE);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("Done adding " + path);
			}
			});
			add(add_song);

			JButton add = new JButton("Add");
			add.setBounds(150, 65, 100, 30);
			add.addActionListener(new add());
			add(add);

		}

		private void parse_mp3(Mp3File mp3file) {
			if (mp3file.hasId3v1Tag()) {
			ID3v1 id3v1Tag = mp3file.getId3v1Tag();
			artist = id3v1Tag.getArtist();
			title = id3v1Tag.getTitle();
			album = id3v1Tag.getAlbum();
			release = Integer.parseInt(id3v1Tag.getYear());
			genre = id3v1Tag.getGenreDescription();
			} else if (mp3file.hasId3v2Tag()) {
			ID3v2 id3v2Tag = mp3file.getId3v2Tag();
			artist = id3v2Tag.getArtist();
			title = id3v2Tag.getTitle();
			album = id3v2Tag.getAlbum();
			release = Integer.parseInt(id3v2Tag.getYear());
			genre = id3v2Tag.getGenreDescription();
			} else {
			JOptionPane.showMessageDialog(null, "Invalid MP3 header", "Alert", JOptionPane.WARNING_MESSAGE);
			}
		}

		private void clear_values() {
			artist = "";
			title = "";
			album = "";
			release = 0;
			genre = "";
			path = "";
			duration = 0;
		}

		class add implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
			if (artist == "") {

				JOptionPane.showMessageDialog(null, "Invalid MP3 header", "Alert", JOptionPane.WARNING_MESSAGE);

			} else {
				manager.addSong(title, artist, release, album, duration, genre, path);
			}
			playlist_active = null;
			update_table(create_Table());
			clear_values();
			song_frame.dispose();
			}

		}

	}

	class Song implements ActionListener {
		JFXPanel gui;

		Song(JFXPanel gui) {
			this.gui = gui;
		}

		public void actionPerformed(ActionEvent e) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int frameWidth = 400, frameHeight = 130;
				song_frame = new View("Drag Mp3 file", gui, frameWidth, frameHeight);
				song_frame.setVisible(true);
				song_frame.setResizable(false);
			}
			});

		}
	}

}
