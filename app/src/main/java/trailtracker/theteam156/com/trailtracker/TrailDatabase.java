package trailtracker.theteam156.com.trailtracker;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {Trail.class}, version = 1, exportSchema = false)
public abstract class TrailDatabase extends RoomDatabase {


    private static final String LOG_TAG = TrailDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "trails";
    private static TrailDatabase sInstance;

    public static TrailDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        TrailDatabase.class, TrailDatabase.DATABASE_NAME)
                        /* remove allowMainThread, only for testing*/
                        //.allowMainThreadQueries()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract TrailDao taskDao();

}
