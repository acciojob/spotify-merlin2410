package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }


    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        List<Album> albumList = new ArrayList<>();
        artistAlbumMap.put(artist,albumList);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        boolean found = false;
        Album album = new Album(title);
        albums.add(album);
        List<Song> songList = new ArrayList<>();
        albumSongMap.put(album,songList);
        Artist artist;
        for(Artist artist1: artists){
            if(artist1.getName().equals(artistName)){
                found = true;
                artist = artist1;
                artistAlbumMap.get(artist).add(album);
                break;
            }
        }
        if(found==false){
            artist = new Artist(artistName);
            List<Album> albumList = new ArrayList<>();
            albumList.add(album);
            artistAlbumMap.put(artist,albumList);
        }
        return album;

    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean found = false;
        Song song = new Song(title, length);
        songs.add(song);
        List<User> userList = new ArrayList<>();
        songLikeMap.put(song,userList);
        for(Album album: albums){
            if(album.getTitle().equals(albumName)){
                found = true;
                albumSongMap.get(album).add(song);
                return song;
            }
        }
        if(found==false){
            throw new Exception("Album does not exist");
        }
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = doesUserExist(mobile);
        if(user==null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        List<Song> songList = new ArrayList<>();
        for(Song song: songs){
            if(song.getLength()==length){
                songList.add(song);
            }
        }
        playlists.add(playlist);
        playlistSongMap.put(playlist,songList);
        List<User> listenerList = new ArrayList<>();
        listenerList.add(user);
        playlistListenerMap.put(playlist,listenerList);
        creatorPlaylistMap.put(user,playlist);
        List<Playlist> playlistsList = new ArrayList<>();
        playlistsList.add(playlist);
        userPlaylistMap.put(user,playlistsList);

        return playlist;
    }

    //Check whether user exists. if exist return user or return null
    public User doesUserExist(String mobile){
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                return user;
            }
        }
        return null;
    }
    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = doesUserExist(mobile);
        if(user==null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        List<Song> songList = new ArrayList<>();
        for(Song song: songs){
            for(String songTitle: songTitles){
                if(song.getTitle().equals(songTitle)){
                    songList.add(song);
                }
            }
        }
        playlistSongMap.put(playlist,songList);
        List<User> listenerList = new ArrayList<>();
        listenerList.add(user);
        playlistListenerMap.put(playlist,listenerList);
        creatorPlaylistMap.put(user,playlist);
        List<Playlist> playlistsList = new ArrayList<>();
        playlistsList.add(playlist);
        userPlaylistMap.put(user,playlistsList);
        return playlist;

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = doesUserExist(mobile);
        if(user==null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = doesPlaylistExist(playlistTitle);
        if(playlist==null){
            throw new Exception("Playlist does not exist");
        }
        List<User> userList = playlistListenerMap.get(playlist);
        if(isUserInList(userList,user)==false){
            userList.add(user);
        }
        playlistListenerMap.put(playlist,userList);
        return playlist;


    }

    public Playlist doesPlaylistExist(String playlistTitle){
        for(Playlist playlist: playlists){
            if(playlist.getTitle().equals(playlistTitle)){
                return playlist;
            }
        }
        return null;
    }

    //Function to check whether user is in the list
    public boolean isUserInList(List<User> userList, User user){
        for(User user1: userList){
            if(user.equals(user1))
                return true;
        }
        return false;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = doesUserExist(mobile);
        if(user==null){
            throw new Exception("User does not exist");
        }
        Song song = doesSongExist(songTitle);
        if(song==null){
            throw new Exception("Song does not exist");
        }
        Album album = songInAlbum(songTitle);
        Artist artist = artistOfAlbum(album);
        if(songLikeMap.containsKey(song)){
            List<User> userList = songLikeMap.get(song);
            if(isUserInList(userList,user)==false){
                userList.add(user);
                song.setLikes(song.getLikes()+1);
                artist.setLikes(artist.getLikes()+1);
            }
            songLikeMap.put(song,userList);
        }
        else{
            List<User> userList = new ArrayList<>();
            userList.add(user);
            songLikeMap.put(song,userList);
        }
        return song;
    }

    public Artist artistOfAlbum(Album album){
        for(Artist artist: artistAlbumMap.keySet()){
            List<Album> albumList = artistAlbumMap.get(album);
            for(Album album1: albumList){
                if(album1.equals(album)){
                    return artist;
                }
            }
        }
        return null;
    }

    public Song doesSongExist(String songTitle){
        for(Song song: songs){
            if(song.getTitle().equals(songTitle)){
                return song;
            }
        }
        return null;
    }

    public Album songInAlbum(String songTitle){
        for(Album album: albumSongMap.keySet()){
            List<Song> songList = albumSongMap.get(album);
            for(Song song: songList){
                if(song.getTitle().equals(songTitle)){
                    return album;
                }
            }
        }
        return null;
    }

    public String mostPopularArtist() {
        int maxLikes = Integer.MIN_VALUE;
        Artist artist = new Artist();
        for (Artist artist1: artists){
            if(artist1.getLikes()>maxLikes){
                maxLikes = artist1.getLikes();
                artist = artist1;
            }
        }
        return artist.getName();
    }

    public String mostPopularSong() {
        int maxLikes = Integer.MIN_VALUE;
        Song song = new Song();
        for(Song song1: songs){
            if(song1.getLikes()>maxLikes){
                maxLikes = song1.getLikes();
                song = song1;
            }
        }
        return song.getTitle();
    }
}
