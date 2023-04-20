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
        List<Playlist> playlistList = new ArrayList<>();
        userPlaylistMap.put(user,playlistList);
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
        Artist artist = findArtist(artistName);
        Album album = new Album(title);
        if(artist==null){
            artist = new Artist(artistName);
            artists.add(artist);
            List<Album> albumList = new ArrayList<>();
            albumList.add(album);
            artistAlbumMap.put(artist,albumList);
        }
        else {
            List<Album> albumList = artistAlbumMap.get(artist);
            albumList.add(album);
            artistAlbumMap.put(artist,albumList);
        }
        albums.add(album);
        List<Song> songList = new ArrayList<>();
        albumSongMap.put(album,songList);
        return album;
    }

    //Function to find artist if exists or return null
    public Artist findArtist(String name){
        for(Artist artist: artists){
            if(artist.getName().equals(name)){
                return artist;
            }
        }
        return null;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album album = findAlbum(albumName);
        if(album==null){
            throw new Exception("Album does not exist");
        }
        Song song = new Song(title,length);
        List<User> likedUsers = new ArrayList<>();
        songLikeMap.put(song,likedUsers);
        songs.add(song);

        List<Song> songList =  albumSongMap.get(album);
        songList.add(song);
        albumSongMap.put(album,songList);
        return song;
    }

    //Function to find album if it exists or return null
    public Album findAlbum(String albumName){
        for(Album album: albums){
            if(album.getTitle().equals(albumName)){
                return album;
            }
        }
        return null;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = findUser(mobile);
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
        playlistSongMap.put(playlist,songList);

        List<User> listeners = new ArrayList<>();
        listeners.add(user);
        playlistListenerMap.put(playlist,listeners);

        creatorPlaylistMap.put(user,playlist);
        List<Playlist> playlistList = userPlaylistMap.get(user);
        playlistList.add(playlist);
        userPlaylistMap.put(user,playlistList);
        playlists.add(playlist);
        return playlist;
    }

    //Function to find and return user if exists, else return null
    public User findUser(String mobile){
        for(User user: users){
            if(user.getMobile().equals(mobile)){
                return user;
            }
        }
        return null;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = findUser(mobile);
        if(user==null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        List<Song> songList = new ArrayList<>();
        for(Song song: songs){
            if(songTitles.contains(song.getTitle())){
                songList.add(song);
            }
        }
        playlistSongMap.put(playlist,songList);

        List<User> listeners = new ArrayList<>();
        listeners.add(user);
        playlistListenerMap.put(playlist,listeners);

        creatorPlaylistMap.put(user,playlist);
        List<Playlist> playlistList = userPlaylistMap.get(user);
        playlistList.add(playlist);
        userPlaylistMap.put(user,playlistList);
        playlists.add(playlist);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = findUser(mobile);
        if(user==null){
            throw new Exception("User does not exist");
        }
        Playlist playlist = doesPlaylistExist(playlistTitle);
        if(playlist==null){
            throw new Exception("Playlist does not exist");
        }
        List<User> userList = playlistListenerMap.get(playlist);
        if(!userList.contains(user)){
            userList.add(user);
            List<Playlist> playlistList = userPlaylistMap.get(user);
            playlistList.add(playlist);
            userPlaylistMap.put(user,playlistList);
        }
        playlistListenerMap.put(playlist,userList);
        return playlist;
    }

    //Function to find playlist
    public Playlist doesPlaylistExist(String playlistTitle){

        for(Playlist playlist: playlists){

            if(playlist.getTitle().equals(playlistTitle)){
                return playlist;
            }
        }
        return null;
    }

    //Function to check whether user exists in a list
    public boolean isUserInList(List<User> userList, User user){
        for(User user1: userList){
            if(user.equals(user1))
                return true;
        }
        return false;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = findUser(mobile);
        if(user==null){
            throw new Exception("User does not exist");
        }
        Song song = findSong(songTitle);
        if(song==null){
            throw new Exception("Song does not exist");
        }
        Album album = songInAlbum(songTitle);
        Artist artist = artistOfAlbum(album);
        if(songLikeMap.containsKey(song)){
            List<User> likedUsers = songLikeMap.get(song);
            if(isUserInList(likedUsers,user)==false){
                likedUsers.add(user);
                song.setLikes(song.getLikes()+1);
                artist.setLikes(artist.getLikes()+1);

            }
            songLikeMap.put(song,likedUsers);
        }
        return song;
    }

    public Artist artistOfAlbum(Album album){
        for(Artist artist: artistAlbumMap.keySet()){
            List<Album> albumList = artistAlbumMap.get(artist);
            for(Album album1: albumList){
                if(album1.equals(album)){
                    return artist;
                }
            }
        }
        return null;
    }

    public Song findSong(String songTitle){
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
