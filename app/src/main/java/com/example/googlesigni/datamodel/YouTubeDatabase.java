package com.example.googlesigni.datamodel;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {ChannelInfo.class},
        version = 1,
        exportSchema = false
)
public abstract class YouTubeDatabase extends RoomDatabase {
    private static YouTubeDatabase database;
    public abstract YouTubeDatabaseAccessObject getDatabaseDao();

    public static synchronized YouTubeDatabase getDBInstance(Context context){
        if(database == null){
            database = Room.databaseBuilder(
                    context.getApplicationContext(),
                    YouTubeDatabase.class,
                    "YTDatabase"
            )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return database;
    }
}
