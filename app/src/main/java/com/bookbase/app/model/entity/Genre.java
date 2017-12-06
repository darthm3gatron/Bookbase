package com.bookbase.app.model.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Genre {

    @PrimaryKey(autoGenerate = true)
    private int genreId;
    private String genreName;
    private String genreDescription;

    public Genre(){

    }

    public int getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public String getGenreDescription() {
        return genreDescription;
    }
    
    public void setGenreId(int id) {
        this.genreId = id;
    }
    
    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public void setGenreDescription(String genreDescription) {
        this.genreDescription = genreDescription;
    }
}