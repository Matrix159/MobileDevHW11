package cis.gvsu.edu.geocalculator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.parceler.Parcels;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationSearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE1 = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE2 = 2;
    private static final String TAG = "LocationSearchActivity";


    @BindView(R.id.location1) TextView location1;
    @BindView(R.id.location2) TextView location2;
    @BindView(R.id.calc_date) TextView calcDateView;
    private LocationLookup locationLookup;
    private DateTime calcDate;
    private DatePickerDialog dpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DateTime today = DateTime.now();
        dpDialog = DatePickerDialog.newInstance(this,
                today.getYear(), today.getMonthOfYear() - 1, today.getDayOfMonth());

        calcDateView.setText(formatted(today));
        locationLookup = new LocationLookup();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        locationLookup.setTimestamp(fmt.print(DateTime.now()));
    }

    @OnClick(R.id.location1)
    public void location1Pressed() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE1);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.location2)
    public void location2Pressed() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE2);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.calc_date)
    public void datePressed() {
        dpDialog.show(getFragmentManager(), "daterangedialog");
    }

    @OnClick(R.id.fab)
    public void FABPressed() {
        Intent result = new Intent();

        // add more code to initialize the rest of the fields
        Parcelable parcel = Parcels.wrap(locationLookup);
        result.putExtra("LOCATION_LOOKUP", parcel);
        setResult(RESULT_OK, result);
        finish();
    }

    private String formatted(DateTime d) {
        return d.monthOfYear().getAsShortText(Locale.getDefault()) + " " +
                d.getDayOfMonth() + ", " + d.getYear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE1) {
            if (resultCode == RESULT_OK) {
                Place pl = PlaceAutocomplete.getPlace(this, data);
                LatLng latLng = pl.getLatLng();
                locationLookup.setOrigLat(latLng.latitude);
                locationLookup.setOrigLng(latLng.longitude);
                location1.setText(pl.getAddress());
                Log.i(TAG, "onActivityResult: " + pl.getName() + "/" + pl.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status stat = PlaceAutocomplete.getStatus(this, data);
                Log.d(TAG, "onActivityResult: ");
            }
            else if (requestCode == RESULT_CANCELED){
                System.out.println("Cancelled by the user");
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE2) {
            if (resultCode == RESULT_OK) {
                Place pl = PlaceAutocomplete.getPlace(this, data);
                LatLng latLng = pl.getLatLng();
                locationLookup.setEndLat(latLng.latitude);
                locationLookup.setEndLng(latLng.longitude);
                location2.setText(pl.getAddress());
                Log.i(TAG, "onActivityResult: " + pl.getName() + "/" + pl.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status stat = PlaceAutocomplete.getStatus(this, data);
                Log.d(TAG, "onActivityResult: ");
            }
            else if (requestCode == RESULT_CANCELED){
                System.out.println("Cancelled by the user");
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        /*startDate = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
        endDate = new DateTime(yearEnd, monthOfYearEnd + 1, dayOfMonthEnd, 0, 0);
        startDateView.setText(formatted(startDate));
        endDateView.setText(formatted(endDate));*/
    }
}