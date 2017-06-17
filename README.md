# jTunes Player

Introduction
------------
This project implements a media manager that mimics `iTunes`. This tool supports adding Mp3 files to the song library and creating playlists. It offers different features such as removing songs, creating playlists with songs in the library, and some other feautres. Songs can only be added via drag and drop, and other information such as title, artist, album etc. is extracted from the Mp3 header via `ID3v tags`. The library used is [mp3agic](https://github.com/mpatric/mp3agic) by Michael Patricios (Copyright (c) 2006-2017). 

![sample](https://user-images.githubusercontent.com/19142014/26969942-1df3797e-4d25-11e7-95f7-1090005288f0.png)

Motivation behind this project
------------------------------
This project was created when I had the idea of implementing a small-scale version of iTunes. I created the jTunes player to test my knowledge of Java, and put object-oriented paradigm along with MVC design pattern into the context of a real life application. 


Media Components
----------------

#### Songs
A song is associated with the following information:
```
  1). Title: Name of the song. 
  2). Artist: The Artist/singer of the song. 
  3). Album: The Album of which this song is a part. 
  4). Duration: Play time of the track in seconds. 
  5). Release Date: The year in which the song was released. 
  6). Genre: The genre to which the song belongs.
  7). Path: The local path of the song on the computer.
```

#### Playlist
A playlist is associated with the following information:
```
  1). Name: The title of the playlist
  2). Playlist: A list of Songs that are a part of the playlist. 
```
#### Media Manager
The Media Manager manages different songs and playlists that are a part of the library. It is associated with the following information:
```
  1). Database: A list of all songs that are contained in the library.
  2). Playlists: A list of all playlists that are a part of the library. 
```

Measures for efficiency
-----------------------
```
  1). Map: The database is stored as a map with song name as key and song object as the value.
  2). Playlists: Playlists only store the name of the song, the actual song object is retrived from the database. 
       Since, the database is a map, retrival operation occurs in O(1) (mostly).
```

Safety and Security
------------------
The best part of this media player is that it's safe. Every measure has been taken to handel unexpected cases to prevent crashes. Moreover, since songs can be added only via drag/drop, and menu (in case of playlists) it is extremely safe in terms og usage. As far as security is concerned, it can only read and write data files associated with this program. It has no acess to anything outside the .jTunes folder.

File Paths
-----------
Data files are stored in .jTunes folder in the user's home directory by fetching the info using `System.getProperty("user.home")`.

Error Logging
-------------

All exceptions and basic operations are tracked by the `ErrorLog` class object, which has the task of creating a log file that may help in debugging bugs. This file is saved along with other data files in `.jTunes folder`.
