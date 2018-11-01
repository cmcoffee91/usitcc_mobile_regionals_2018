package trailtracker.theteam156.com.trailtracker;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TrailDao {


    @Query("SELECT * FROM Trail ORDER BY date")
    LiveData<List<Trail>> loadAllItems();


    @Query("SELECT * FROM Trail ORDER BY date desc")
    LiveData<List<Trail>> loadAllItemsDesc();

    //@Query("SELECT * FROM Item ORDER BY date desc")
    @Query("SELECT * FROM Trail where name LIKE  :itemName  ")
    LiveData<List<Trail>> loadAllItemsBySearch(String itemName);

    @Query("SELECT * FROM Trail where picLocation = :location  ")
    Trail loadTrailItemsByPic(String location);



    @Insert
    void insertItem(Trail itemEntry);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItem(Trail itemEntry);

    @Delete
    void deleteItem(Trail itemEntry);
}
