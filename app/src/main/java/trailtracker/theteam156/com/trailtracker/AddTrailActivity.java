package trailtracker.theteam156.com.trailtracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Build.VERSION_CODES.M;

public class AddTrailActivity extends AppCompatActivity implements ImagePickerDialog.HandlePicDialog {

    private String date, name, picLocation;
    private TextView dateView, coordsView;
    private EditText nameView;
    private ImageView picView, trailView;
    private Button takePicBut;
    private boolean canTakePhoto;
    private File mPhotoFile;
    Uri camUri;
    private boolean updateTrail;
    private String TAG = "AddTrailActivity";


    String latCoords = "";
    String longCoords = "";

    private GoogleApiClient mClient;

    TrailDatabase database;

    Trail updateObj;

    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_PHOTO_DIALOG = 2;
    private static final int PICK_IMAGE_REQUEST = 3;

    private static final String[]
            CAMERA_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String[]
            LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private static final int
            REQUEST_CAMERA_PERMISSIONS = 1;

    private static final int
            REQUEST_LOCATION_PERMISSIONS = 203;
    private boolean permGranted = false;
    private boolean permWriteGranted;

    private FusedLocationProviderClient mFusedLocationClient;

// ..


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trail);




        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        database = TrailDatabase.getInstance(this);

        if(getIntent() != null && getIntent().hasExtra("trail"))
        {
            updateTrail = getIntent().getBooleanExtra("update",false);
            picLocation = getIntent().getStringExtra("picLocation");

            updateObj = (Trail) getIntent().getSerializableExtra("trail");

            latCoords = updateObj.getLatCoord();
            longCoords = updateObj.getLongCoord();
            name = updateObj.getName();
            date = updateObj.getDate();
            Log.d(TAG,"update itemName : " + updateObj.getName());
            Log.d(TAG,"UPDATE LAT: " + updateObj.getLatCoord() );

            initView();

           /* AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    updateObj = database.taskDao().loadTrailItemsByPic(picLocation);



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initView();
                        }
                    });




                }
            });
             */
        }
        else
        {
            initView();
        }






        if (hasLocationPermission()) {
            getLocation();
            Log.e("AddItemActivity", "has location permission");
        } else {
            Log.e("AddItemActivity", "no location perms");
            ActivityCompat.requestPermissions(AddTrailActivity.this, LOCATION_PERMISSIONS,
                    REQUEST_LOCATION_PERMISSIONS);
        }


    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("AddItemActivity", "no locatio in getLocation");
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("AddItemAct","location latitude: " + location.getLatitude() );
                        Log.d("AddItemAct","location longitude: " + location.getLongitude() );


                        latCoords = String.valueOf( location.getLatitude() ) ;
                        longCoords = String.valueOf( location.getLongitude() );

                       if(!updateTrail) coordsView.setText("Lat: " + latCoords + ", Long: " + longCoords);
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                        }
                    }
                });
    }



    private boolean hasLocationPermission() {
        int result = ContextCompat
                .checkSelfPermission(AddTrailActivity.this,
                        LOCATION_PERMISSIONS[0]);
        return result ==
                PackageManager.PERMISSION_GRANTED;
    }






    private void initView()
    {
        dateView = findViewById(R.id.date);

        nameView = findViewById(R.id.name);
        coordsView = findViewById(R.id.addTrailCoords);

        picView = findViewById(R.id.imageView);
        takePicBut = findViewById(R.id.takePicBut);

        trailView = findViewById(R.id.goToMapView);
        trailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Uri from an intent string. Use the result to create an Intent.
                String locationString = "google.streetview:cbll=" + latCoords + "," + longCoords;
                Log.d(TAG, "locationString : " + locationString );
                Uri gmmIntentUri = Uri.parse(locationString);

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        });

        picView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(picLocation == null || picLocation.length() < 5) return;

                ImagePreviewFragment imagePreview = new ImagePreviewFragment();

                Bundle info = new Bundle();

                info.putString("imageUrl", picLocation);

                imagePreview.setArguments(info);
               /* FragmentActivity mycontext = (FragmentActivity) mContext;
                imagePreview.show(mycontext.getSupportFragmentManager(), "imagePreview");
                */
                imagePreview.show(getSupportFragmentManager(), "ImagePreviewFrag");
            }
        });

        TrailUtils dates = new TrailUtils();
        date = dates.getCurrentDate() + ", " + dates.getCurrentTime();

       if(!updateTrail) dateView.setText( date );


        takePicBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (canTakePhoto) {

                    if (hasWritePermission()) {

                        showImageGetter();

                    } else {
                        requestWrite();
                    }
                } else {
                    ActivityCompat.requestPermissions(AddTrailActivity.this, CAMERA_PERMISSIONS, REQUEST_CAMERA_PERMISSIONS);
                   /*requestPermissions(CAMERA_PERMISSIONS,
                            REQUEST_CAMERA_PERMISSIONS);*/

                }




            }
        });

        if( updateTrail ){
            String previousCoords = "Lat: " + latCoords + ", Long: " +longCoords;
            coordsView.setText(previousCoords);
            dateView.setText(date);
            nameView.setText(name);
            Glide.with(this)
                    .load(new File(picLocation)) // Uri of the picture
                    .into(picView);
        }


    }

    private void showImageGetter()
    {
        ImagePickerDialog pickerDialog = new ImagePickerDialog();
        Bundle info = new Bundle();
        // pickerDialog.setTargetFragment(thisFrag, REQUEST_PHOTO_DIALOG);
        info.putString("title", "Choose Photo");
        info.putString("message", "Would you like a photo from a gallery or the camera?");
        pickerDialog.setArguments(info);
        pickerDialog.show(getSupportFragmentManager(),"ImagePickerDialog");
        //pickerDialog.show(getFragmentManager(), "SaveConfirmation");
    }


    private void takePic()
    {

        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);


        Uri uri = null;

        try {


            if(Build.VERSION.SDK_INT > M) {
                uri = FileProvider.getUriForFile(AddTrailActivity.this, AddTrailActivity.this.getApplicationContext().getPackageName() + ".trailtracker.theteam156.name.provider", createImageFile());
            } else {

                uri = Uri.fromFile(mPhotoFile);
            }
            camUri = uri;

        }
        catch(IOException e)
        {
            // Log.e("createImageFile","error " + e.getMessage());
        }
        // Continue only if the File was successfully created
        if (mPhotoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            startActivityForResult(cameraIntent, REQUEST_PHOTO);
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

       /* File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        ); */

        File image = new File(storageDir, imageFileName);
        image.createNewFile();

        // Save a file: path for use with ACTION_VIEW intents
        if(Build.VERSION.SDK_INT > M) {
            //  mCurrentPhotoPath = "content:" + image.getAbsolutePath();
        } else {

            //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        }

        Log.e("addItemAct","imagePath: " + image.getAbsolutePath() );
        mPhotoFile = image;

        return image;
    }




    @Override
    public void onResume() {
        super.onResume();

        if (permGranted) {
            requestWrite();
            permGranted = false;
        }

        //  closeKeyboards();
        //  parseLiveQueryClient.connectIfNeeded();


        if(permWriteGranted)
        {
            showImageGetter();
            permWriteGranted = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permGranted = true;
                    Log.d("PermGranted", "cam perm granted");



                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Log.d("CameraPerms", "Camera permission were denied in createQuestFrag");
                    //Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permWriteGranted = true;
                    Log.d("PermGranted", "gallery perm granted");


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //  Log.d("CameraPerms", "Camera permission were denied in createQuestFrag");
                    //Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }

                return;
            }

            case 300: {

            }

            case REQUEST_LOCATION_PERMISSIONS: {


                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    Log.d("PermGranted", "gallery perm granted");
                    if (hasLocationPermission()) {
                        getLocation();
                    }
                }

            }

            default:
                super.onRequestPermissionsResult(requestCode,
                        permissions, grantResults);



                // other 'case' lines to check for other
                // permissions this app might request
        }
    }

    private void requestWrite()
    {
        String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        int permsRequestCode = 200;
        //requestPermissions(perms, permsRequestCode);
        ActivityCompat.requestPermissions(AddTrailActivity.this, perms, permsRequestCode);
    }


    private boolean hasWritePermission() {
        int result = ContextCompat
                .checkSelfPermission(AddTrailActivity.this,
                        CAMERA_PERMISSIONS[1]);
        return result ==
                PackageManager.PERMISSION_GRANTED;
    }


    private void saveItem()
    {

        final String nameText = nameView.getText().toString();


        Log.d("AddTrailActivity", "long: " +  longCoords);
        Log.d("AddTrailActivity", "lat:  " + latCoords );

        Log.d("AddTrailActivity", "name " + nameText );

        Log.d("AddTrailActivity", "date " + date );



        final Trail item = new Trail(nameText, date, latCoords, longCoords, picLocation);




        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {


                if( updateTrail)
                {
                    item.setId(updateObj.getId());
                    database.taskDao().updateItem(item);
                }
                else
                {
                    database.taskDao().insertItem(item);
                }






                setResult(Activity.RESULT_OK);
                finish();
            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_trail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch(item.getItemId())
        {
            case R.id.menu_save:

                saveItem();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }





        if (requestCode == REQUEST_PHOTO) {


            boolean failed = false;

            try {

                File f = new File(mPhotoFile.getAbsolutePath());
                ExifInterface exif = new ExifInterface(f.getPath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                System.out.println("orientation: "+ orientation);

                int angle = 0;

                if (orientation == 6) {
                    angle = 90;
                }
                else if (orientation == 3) {
                    angle = 180;
                }
                else if (orientation == 8) {
                    angle = 270;
                }

                Matrix mat = new Matrix();
                mat.postRotate(angle);

                // Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
                Bitmap bmp=BitmapFactory.decodeStream(AddTrailActivity.this.getContentResolver().openInputStream(camUri));
                Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
                //Bitmap finalBm = PictureUtils.getScaledBitmap(bmp,400,400);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                correctBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                Glide.with(this)
                        .load(new File(f.getPath())) // Uri of the picture
                        .into(picView);

                picLocation = f.getPath();

                //picView.setImageBitmap(correctBmp);

                //   parseMessage = new ParseObject("Message");
                /// Log.e("QuestDetailFrag", "request photo in parse message id: [" + parseMessage.getObjectId() + "]");



            }
            catch (IOException e)
            {
                //  Log.e("fileError", "error getting byte array from file:" + e.getMessage());
                failed = true;

            }





        }

        if(requestCode == PICK_IMAGE_REQUEST)
        {

            Uri uri = data.getData();
            if (uri == null) return;

            try {

                boolean failed = false;

                try {



                    File f = new File(TrailUtils.getPath(AddTrailActivity.this,uri));
                    ExifInterface exif = new ExifInterface(f.getPath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    // System.out.println("orientation: "+ orientation);

                    int angle = 0;

                    if (orientation == 6) {
                        angle = 90;
                    }
                    else if (orientation == 3) {
                        angle = 180;
                    }
                    else if (orientation == 8) {
                        angle = 270;
                    }

                    Matrix mat = new Matrix();
                    mat.postRotate(angle);

                    Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
                    Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
                    //Bitmap finalBm = PictureUtils.getScaledBitmap(bmp,400,400);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    correctBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                   /* ItemUtils itemUtils = new ItemUtils();
                    File mPhotoFile = itemUtils.getPhotoFile(mContext,currentItem);
                    System.out.println(mPhotoFile.getPath());


                    File f = new File(mPhotoFile.getPath());
                    */
                    Glide.with(this)
                            .load(new File(f.getPath())) // Uri of the picture
                            .into(picView);

                    picLocation = f.getPath();

                    // picView.setImageBitmap(correctBmp);


                }
                catch (IOException e)
                {
                    // Log.e("fileError", "error getting byte array from file:" + e.getMessage());
                    failed = true;

                }





            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }




    }


    @Override
    public void onClickIndex(String type) {
        Log.d("AddItemAct", "type of item clicked is " + type);



        if(type.equalsIgnoreCase("camera"))
        {
            takePic();

        }
        else
        {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

        }


    }






}
