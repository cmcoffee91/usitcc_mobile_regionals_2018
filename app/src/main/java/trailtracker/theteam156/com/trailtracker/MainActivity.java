package trailtracker.theteam156.com.trailtracker;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        TrailListAdapter.ItemAdapterOnClickHandler,
        DeleteItemDialog.HandleDeleteDialog{

    TrailDatabase database;
    RecyclerView itemList;
    TrailListAdapter adapter;
    List<Trail> mItems;

    MainViewModel viewModel ;
    Observer<List<Trail>> currentObserver = null;

    private final String TAG = "mainActivity";

    static final int ADD_ITEM_REQUEST = 1;

    private boolean isAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = TrailDatabase.getInstance(this);

        mItems = new ArrayList<>();

        initViews();

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        setupViewModel();

        adapter.setData( mItems );



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/



                Intent intent = new Intent(MainActivity.this, AddTrailActivity.class);
                intent.putExtra("update", false);
                startActivityForResult(intent, ADD_ITEM_REQUEST);



               /* if(maybeEnableArButton()) {
                    Intent intent = new Intent(MainActivity.this, AR_activity.class);

                    MainActivity.this.startActivity(intent);
                }*/
            }
        });
    }

    public  boolean maybeEnableArButton() {
        boolean enableAr =false;
       /* ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(MainActivity.this);
        if (availability.isTransient()) {
            // Re-query at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            // mArButton.setVisibility(View.VISIBLE);
            // mArButton.setEnabled(true);
            Log.e("arFrag","is supported");
            enableAr = true;
            // indicator on the button.
        } else { // Unsupported or unknown.
            //  mArButton.setVisibility(View.INVISIBLE);
            //  mArButton.setEnabled(false);
            Log.e("arFrag","is not supported");
            enableAr = false;
        }*/
        return enableAr;
    }


    private void initViews()
    {
        itemList = findViewById(R.id.mainItemList);



        itemList.setLayoutManager( new LinearLayoutManager(this) );
        adapter = new TrailListAdapter(mItems,this, MainActivity.this);
        itemList.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }


    private void removeObserverIfNeeded()
    {
        if(currentObserver != null) {
            viewModel.getItems().removeObserver(currentObserver);
        }
    }


    private void setupViewModel() {

        removeObserverIfNeeded();
        viewModel.getItems().observe(this, new Observer<List<Trail>>() {
            @Override
            public void onChanged(@Nullable List<Trail> itemEntries) {
                currentObserver = this;
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mItems = itemEntries;
                adapter.setData(mItems);
            }
        });
    }


    private void setupDescViewModel() {
        removeObserverIfNeeded();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getItemsDesc().observe(this, new Observer<List<Trail>>() {
            @Override
            public void onChanged(@Nullable List<Trail> itemEntries) {
                currentObserver = this;
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mItems = itemEntries;
                adapter.setData(mItems);
            }
        });
    }



    private void setupSearchViewModel(String nameToSearch) {
        removeObserverIfNeeded();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getItemsNameSearch(nameToSearch).observe(this, new Observer<List<Trail>>() {
            @Override
            public void onChanged(@Nullable List<Trail> itemEntries) {
                currentObserver = this;
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mItems = itemEntries;
                adapter.setData(mItems);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        // Check which request we're responding to
        if (requestCode == ADD_ITEM_REQUEST) {

            Log.d("MainActivity", "addItemRequest successful");

           /* mItems = database.taskDao().loadAllItems();
            adapter.setData( mItems  );
            */
        }
    }


    public void lookForMember(String query)
    {


        String nameToSearch = query.trim();
        if(!nameToSearch.isEmpty())
        {

            setupSearchViewModel("%" + nameToSearch + "%");


        }
        else
        {

            setupViewModel();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView1 = (SearchView) searchItem.getActionView();


        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("searchQuerySubmit","query is : " + query);
                lookForMember(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("searchQueryOnTextChange","query textchange is : " + newText);
                return false;
            }
        });

        searchView1.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("search","search closed");
                lookForMember("");
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        switch(item.getItemId())
        {
            case R.id.menu_item_search:



                return true;

            case R.id.menu_sort:

                if(isAscending)
                {
                    setupDescViewModel();


                    isAscending = false;
                }
                else
                {

                    setupViewModel();


                    isAscending = true;
                }


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onClick(Trail itemObj, int position, boolean shouldDelete) {

        if(shouldDelete)
        {




            confirmDelete(position);

        }
        else
        {
            Intent intent = new Intent(MainActivity.this, AddTrailActivity.class);
            intent.putExtra("update", true);
            intent.putExtra("picLocation", itemObj.getPicLocation());
            intent.putExtra("trail",itemObj);

            startActivityForResult(intent, ADD_ITEM_REQUEST);
        }

    }

    private void confirmDelete(int position)
    {
        DeleteItemDialog pickerDialog = new DeleteItemDialog();
        Bundle info = new Bundle();
        // pickerDialog.setTargetFragment(thisFrag, REQUEST_PHOTO_DIALOG);
        info.putString("title", "Delete item");
        info.putString("message", "Are you sure you want to delete this trail?");
        info.putInt("itemIndex", position);
        pickerDialog.setArguments(info);
        pickerDialog.show(getSupportFragmentManager(),"DeletePickerDialog");
        //pickerDialog.show(getFragmentManager(), "SaveConfirmation");
    }


    @Override
    public void onClickIndex(boolean shouldDelete, final int itemIndex) {


        if(shouldDelete) {

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    Trail itemObj = mItems.get(itemIndex);

                    database.taskDao().deleteItem(itemObj);
                    // mItems.remove(itemIndex);
//                    adapter.notifyItemRemoved(itemIndex);
                }
            });

        }



    }







}
