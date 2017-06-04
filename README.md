# iTunes.jr

Introduction
------------
This project implements a media manager that mimics `iTunes`. The tool allows Mp3 files to added to song library and playlists created with those songs. It offers different features such as removing songs, creating playlists with songs and other feautres. Songs can only be added via drag and drop, and other information such as title, artist, album etc. is extracted from the Mp3 header via `ID3v tags`. The library used is [mp3agic](https://github.com/mpatric/mp3agic) by Michael Patricios (Copyright (c) 2006-2017). 

![sample](https://cloud.githubusercontent.com/assets/19142014/26754326/6cf2180c-4896-11e7-9cba-ea0d5b0c3ddc.png)



Media Components
----------------

#### Songs
A songs is associated with the following information:
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
