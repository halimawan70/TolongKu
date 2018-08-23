package edu.bluejack17_2.tolongku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerActions extends AppCompatActivity {

    public static final int DO_NOTHING = 1;
    public static final int REMOVE_CIRCLE = 2;
    public static final int REMOVE_ALL = 3;
    int action;
    MarkerData markerData;
    TextView lblStatus;
    Button btnCall;
    Button btnMessage;
    Button btnDelete;
    Button btnCancel;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_actions);

        Intent startingIntent = getIntent();

        status = startingIntent.getIntExtra("status", -1);

        lblStatus = findViewById(R.id.txtAreaStatus);
        btnCall = findViewById(R.id.btnCallForHelp);
        btnMessage = findViewById(R.id.btnMessageForHelp);
        btnDelete = findViewById(R.id.btnDeleteMarker);
        btnCancel = findViewById(R.id.btnCancel);

        if(status != -1){

            if(status == MarkerData.DANGEROUS){

                lblStatus.setText("Dangerous");

                btnCall.setEnabled(false);
                btnMessage.setEnabled(false);

                btnCall.setVisibility(View.GONE);
                btnMessage.setVisibility(View.GONE);

            }else if(status == MarkerData.SHELTER){

                lblStatus.setText("Safe Shelter");

            }else if(status == MarkerData.HELP){

                lblStatus.setText("Offering Help");
            }

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MarkerActions.this, "Marker Action Cancelled",
                            Toast.LENGTH_SHORT).show();

                    action = DO_NOTHING;
                    returnResults();
                }
            });

            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO - Message the user for help
                    action = DO_NOTHING;
                    returnResults();
                }
            });

            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO - Call the user for help
                    action = DO_NOTHING;
                    returnResults();
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // TODO - Validate is user is the creator of the marker itself and is
                    // authorized to delete the markar

                    if(status == MarkerData.DANGEROUS){
                        action = REMOVE_ALL;
                    }else if(status == MarkerData.SHELTER || status == MarkerData.HELP){
                        action = REMOVE_CIRCLE;
                    }else{
                        action = DO_NOTHING;
                        Log.d("Map", "Status is erroneous.");
                    }
                    returnResults();
                }
            });
        }else{
            Log.d("Map", "Marker data is NULL");
        }
    }

    private void returnResults(){
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra("status", status);
        intent.putExtra("action", action);
        setResult(MapFragment.MARKER_ACTION_REQUEST_CODE, intent);
        finish();
    }
}
