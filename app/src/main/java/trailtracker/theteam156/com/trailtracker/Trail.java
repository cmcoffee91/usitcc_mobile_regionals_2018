package trailtracker.theteam156.com.trailtracker;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcelable;

import java.io.Serializable;

@Entity
public class Trail implements Serializable {


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String latCoord;
    private String longCoord;
    private String picLocation;

    public Trail(String name, String date, String latCoord, String longCoord, String picLocation) {
        this.name = name;
        this.date = date;
        this.latCoord = latCoord;
        this.longCoord = longCoord;
        this.picLocation = picLocation;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLatCoord() {
        return latCoord;
    }

    public void setLatCoord(String latCoord) {
        this.latCoord = latCoord;
    }

    public String getLongCoord() {
        return longCoord;
    }

    public void setLongCoord(String longCoord) {
        this.longCoord = longCoord;
    }

    public String getPicLocation() {
        return picLocation;
    }

    public void setPicLocation(String picLocation) {
        this.picLocation = picLocation;
    }




}
