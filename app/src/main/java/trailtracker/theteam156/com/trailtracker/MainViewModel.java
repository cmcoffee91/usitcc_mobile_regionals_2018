package trailtracker.theteam156.com.trailtracker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();
    TrailDatabase database = TrailDatabase.getInstance(this.getApplication());

    private LiveData<List<Trail>> items;

    public MainViewModel(Application application) {
        super(application);

        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        items = database.taskDao().loadAllItems();
    }

    public LiveData<List<Trail>> getItems() {
        return items;
    }

    public LiveData<List<Trail>> getItemsDesc() {
        return database.taskDao().loadAllItemsDesc();
    }

    public LiveData<List<Trail>> getItemsNameSearch(String nameToSearch) {
        return database.taskDao().loadAllItemsBySearch(nameToSearch);
    }
}
