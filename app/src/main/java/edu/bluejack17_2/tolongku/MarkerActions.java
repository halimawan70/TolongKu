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
    public static final int MESSAGE_USER = 4;
    public static final int CALL_USER = 5;
    int action;
    TextView lblStatus;
    TextView lblRadius;
    TextView lblHostName;
    Button btnCall;
    Button btnMessage;
    Button btnDelete;
    Button btnCancel;
    int status;
    int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_actions);

        Intent startingIntent = getIntent();

        status = startingIntent.getIntExtra("status", -1);
        radius = startingIntent.getIntExtra("radius", -1);

        lblStatus = findViewById(R.id.txtAreaStatus);
        lblRadius = findViewById(R.id.lblAreaRadius);
        lblHostName = findViewById(R.id.lblHostName);
        lblHostName.setText(startingIntent.getStringExtra("host"));
        btnCall = findViewById(R.id.btnCallForHelp);
        btnMessage = findViewById(R.id.btnMessageForHelp);
        btnDelete = findViewById(R.id.btnDeleteMarker);
        btnCancel = findViewById(R.id.btnCancel);

        lblRadius.setText(radius + "m");

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
                    action = MESSAGE_USER;
                    returnResults();
                }
            });

            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    action = CALL_USER;
                    returnResults();
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
