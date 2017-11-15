package cis.gvsu.edu.geocalculator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.parceler.Parcels;


public class HistoryActivity extends AppCompatActivity
        implements HistoryFragment.OnListFragmentInteractionListener  {

    @Override
    public void onListFragmentInteraction(LocationLookup item) {
        System.out.println("Interact!");

        Intent intent = new Intent();
        LocationLookup locLookup = new LocationLookup();
        locLookup.setOrigLat(item.origLat);
        locLookup.setOrigLng(item.origLng);
        locLookup.setEndLat(item.endLat);
        locLookup.setEndLng(item.endLng);
        locLookup.setTimestamp(item.timestamp);

        Parcelable parcel = Parcels.wrap(locLookup);
        intent.putExtra("LOCATION_LOOKUP", parcel);
        //String[] vals = {item.origLat, item.origLng, item.destLat, item.destLng};
        //intent.putExtra("item", vals);
        setResult(MainActivity.HISTORY_RESULT,intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
