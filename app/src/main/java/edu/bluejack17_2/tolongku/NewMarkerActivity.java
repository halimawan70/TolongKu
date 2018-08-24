package edu.bluejack17_2.tolongku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class NewMarkerActivity extends AppCompatActivity {

    RadioButton radioDangerous;
    RadioButton radioShelter;
    RadioButton radioHelp;
    Button btnSaveChanges;
    EditText txtRadius;
    double latitude;
    double longitude;
    int radius;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker);

        Intent startingIntent = getIntent();

        latitude = startingIntent.getDoubleExtra("latitude", -1);
        longitude = startingIntent.getDoubleExtra("longitude", -1);

        Log.d("Map", "Latitude: " + latitude + " Longitude: " + longitude);

        radioDangerous = findViewById(R.id.radioDangerous);
        radioShelter = findViewById(R.id.radioShelter);
        radioHelp = findViewById(R.id.radioHelp);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        txtRadius = findViewById(R.id.txtRadius);
        txtRadius.setText("100");

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                radius = Integer.parseInt(txtRadius.getText().toString());

                if(radius < 10){
                    Toast.makeText(NewMarkerActivity.this,
                            "Radius must not be less than 10 metres.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!radioDangerous.isChecked() && !radioShelter.isChecked() &&
                        !radioHelp.isChecked()){

                    status = MapFragment.AREA_MARKING_CANCELLED;

                    Toast.makeText(NewMarkerActivity.this, "Area Marking Cancelled",
                            Toast.LENGTH_SHORT).show();

                }else if(radioDangerous.isChecked()){

                    status = MapFragment.AREA_DANGEROUS;

                    Toast.makeText(NewMarkerActivity.this, "Area Marked as Dangerous",
                            Toast.LENGTH_SHORT).show();

                }else if(radioShelter.isChecked()){

                    status = MapFragment.AREA_SHELTER;

                    Toast.makeText(NewMarkerActivity.this, "Area Marked as a Shelter",
                            Toast.LENGTH_SHORT).show();

                }else if(radioHelp.isChecked()){

                    status = MapFragment.AREA_HELP;

                    Toast.makeText(NewMarkerActivity.this, "Area Marked as Offering Help",
                            Toast.LENGTH_SHORT).show();

                }

                returnResults();
            }
        });
    }

    private void returnResults(){
        Intent intent = new Intent(this, NavigationActivity.class);

        intent.putExtra("status", status);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("radius", radius);
        setResult(MapFragment.NEW_MARKER_REQUEST_CODE, intent);

        finish();
    }
}
