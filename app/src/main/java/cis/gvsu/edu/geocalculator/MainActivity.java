package cis.gvsu.edu.geocalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.location.Location;
import java.text.DecimalFormat;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{

    public static int SETTINGS_RESULT = 1;
    public static int HISTORY_RESULT = 2;
    public static int LOCATION_SEARCH_RESULT = 3;
    private String bearingUnits = "degrees";
    private String distanceUnits = "kilometers";

    DatabaseReference topRef;
    public static List<LocationLookup> allHistory;

    private EditText p1Lat = null;
    private EditText p1Lng = null;
    private EditText p2Lat = null;
    private EditText p2Lng = null;
    private TextView distance = null;
    private TextView bearing = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        Button clearButton = (Button)this.findViewById(R.id.clear);
        Button calcButton = (Button)this.findViewById(R.id.calc);
        Button searchButton = (Button) this.findViewById(R.id.search_button);

        searchButton.setOnClickListener((event) -> {
            Intent intent = new Intent(MainActivity.this, LocationSearchActivity.class);
            startActivityForResult(intent, LOCATION_SEARCH_RESULT);
        });
//        Button settingsButton = (Button) this.findViewById(R.id.settings);

        allHistory = new ArrayList<LocationLookup>();

        p1Lat = (EditText) this.findViewById(R.id.p1Lat);
        p1Lng = (EditText) this.findViewById(R.id.p1Lng);
        p2Lat = (EditText) this.findViewById(R.id.p2Lat);
        p2Lng = (EditText) this.findViewById(R.id.p2Lng);
        distance = (TextView) this.findViewById(R.id.distance);
        bearing = (TextView) this.findViewById(R.id.bearing);


        /*
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p1Lat.getText().clear();
                p1Lng.getText().clear();
                p2Lat.getText().clear();
                p2Lng.getText().clear();
            }
        });
 */
        clearButton.setOnClickListener(v -> {
            hideKeyboard();
            p1Lat.getText().clear();
            p1Lng.getText().clear();
            p2Lat.getText().clear();
            p2Lng.getText().clear();
            distance.setText("Distance:");
            bearing.setText("Bearing:");
        });

//        settingsButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, MySettingsActivity.class);
//            startActivityForResult(intent, SETTINGS_RESULT );
//        });

        calcButton.setOnClickListener(v -> {
            updateScreen();
        });
    }

    private void updateScreen()
    {
        try {
            Double lat1 = Double.parseDouble(p1Lat.getText().toString());
            Double lng1 = Double.parseDouble(p1Lng.getText().toString());
            Double lat2 = Double.parseDouble(p2Lat.getText().toString());
            Double lng2 = Double.parseDouble(p2Lng.getText().toString());


            Location p1 = new Location("");//provider name is unecessary
            p1.setLatitude(lat1);//your coords of course
            p1.setLongitude(lng1);

            Location p2 = new Location("");
            p2.setLatitude(lat2);
            p2.setLongitude(lng2);

            double b = p1.bearingTo(p2);
            double d = p1.distanceTo(p2) / 1000.0d;

            if (this.distanceUnits.equals("Miles")) {
                d *= 0.621371;
            }

            if (this.bearingUnits.equals("Mils")) {
                b *= 17.777777778;
            }

            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);


            String dStr = "Distance: " + df.format(d) + " " + this.distanceUnits;
            distance.setText(dStr);

            String bStr = "Bearing: " + df.format(b) + " " + this.bearingUnits;
            bearing.setText(bStr);
            hideKeyboard();
            /*HistoryContent.HistoryItem item = new HistoryContent.HistoryItem(lat1.toString(),
                    lng1.toString(), lat2.toString(), lng2.toString(), DateTime.now());
            HistoryContent.addItem(item);*/
            LocationLookup entry = new LocationLookup();
            entry.setOrigLat(lat1);
            entry.setOrigLng(lng1);
            entry.setEndLat(lat2);
            entry.setEndLng(lng2);
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            entry.setTimestamp(fmt.print(DateTime.now()));

            topRef.push().setValue(entry);
        } catch (Exception e) {
            return;
        }

    }

    private void hideKeyboard()
    {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            //this.getSystemService(Context.INPUT_METHOD_SERVICE);
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == SETTINGS_RESULT) {
            this.bearingUnits = data.getStringExtra("bearingUnits");
            this.distanceUnits = data.getStringExtra("distanceUnits");
            updateScreen();
        }  else if (resultCode == HISTORY_RESULT) {
            if (data != null && data.hasExtra("LOCATION_LOOKUP")) {
                Parcelable parcel = data.getParcelableExtra("LOCATION_LOOKUP");
                LocationLookup locLookup = Parcels.unwrap(parcel);
                this.p1Lat.setText(String.valueOf(locLookup.getOrigLat()));
                this.p1Lng.setText(String.valueOf(locLookup.getOrigLng()));
                this.p2Lat.setText(String.valueOf(locLookup.getEndLat()));
                this.p2Lng.setText(String.valueOf(locLookup.getEndLng()));
                this.updateScreen();
            }
        } else if(requestCode == LOCATION_SEARCH_RESULT) {
            if (data != null && data.hasExtra("LOCATION_LOOKUP"))
            {
                Parcelable parcel = data.getParcelableExtra("LOCATION_LOOKUP");
                LocationLookup locLookup = Parcels.unwrap(parcel);
                this.p1Lat.setText(String.valueOf(locLookup.getOrigLat()));
                this.p1Lng.setText(String.valueOf(locLookup.getOrigLng()));
                this.p2Lat.setText(String.valueOf(locLookup.getEndLat()));
                this.p2Lng.setText(String.valueOf(locLookup.getEndLng()));
                this.updateScreen();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, MySettingsActivity.class);
            startActivityForResult(intent, SETTINGS_RESULT );
            return true;
        } else if(item.getItemId() == R.id.action_history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivityForResult(intent, HISTORY_RESULT );
            return true;
        }

        return false;
    }

    private ChildEventListener chEvListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            LocationLookup entry = (LocationLookup) dataSnapshot.getValue(LocationLookup.class);
            entry._key = dataSnapshot.getKey();
            allHistory.add(entry);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            LocationLookup entry = (LocationLookup) dataSnapshot.getValue(LocationLookup.class);
            List<LocationLookup> newHistory = new ArrayList<LocationLookup>();
            for (LocationLookup t : allHistory) {
                if (!t._key.equals(dataSnapshot.getKey())) {
                    newHistory.add(t);
                }
            }
            allHistory = newHistory;
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public void onResume(){
        super.onResume();
        allHistory.clear();
        topRef = FirebaseDatabase.getInstance().getReference("history");
        topRef.addChildEventListener (chEvListener);
        //topRef.addValueEventListener(valEvListener);
    }

    @Override
    public void onPause(){
        super.onPause();
        topRef.removeEventListener(chEvListener);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
}
