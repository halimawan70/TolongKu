package edu.bluejack17_2.tolongku;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */


public class EmergencyFragment extends Fragment implements View.OnClickListener {


    SmsManager sms;
    public Button btnEmergencySend, btnEmergencyCall;
    DatabaseReference dbReference;
    final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 0;
    String phoneNumber;

    public EmergencyFragment() {
        dbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.authID);

        // Required empty public constructor

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PhoneCallListener pcl = new PhoneCallListener();
        TelephonyManager tm = (TelephonyManager) this.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(pcl,PhoneStateListener.LISTEN_CALL_STATE);
        btnEmergencySend = getView().findViewById(R.id.btnEmergencySend);
        btnEmergencyCall = getView().findViewById(R.id.btnEmergencyCall);
        btnEmergencyCall.setOnClickListener(this);
        btnEmergencySend.setOnClickListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_emergency, container, false);
    }

    public void sendSms()
    {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("EmergencyFragment","mau send sms");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        else
        {

            sms = SmsManager.getDefault();
            dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    //phoneNumber = dataSnapshot.getValue(String.class);
                    //087893484547
                    Log.d("EmergencyFragment", u.printData());
                    sms.sendTextMessage(u.getUserContactNumber(),null,"Message from tolongku : "+u.getUserMessage(),null,null);
                    Toast.makeText(getContext(),"SMS Sent to : "+u.getUserContactNumber(), Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
                sms = SmsManager.getDefault();
                dbReference.child("userContactNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        //phoneNumber = dataSnapshot.getValue(String.class);
                        //087893484547
                        sms.sendTextMessage(u.getUserContactNumber(),null,"Message from tolongku : "+u.getUserMessage(),null,null);
                        Toast.makeText(getContext(),"SMS Sent to : "+u.getUserContactNumber(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case MY_PERMISSIONS_REQUEST_PHONE_CALL:
                dbReference.child("userContactNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        phoneNumber = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
                break;


        }
    }

    public void sendEmail()
    {
        //Toast.makeText(getContext(), dbReference.child("userContactEmail").toString(), Toast.LENGTH_SHORT);
        String[] TO = {dbReference.child("userContactEmail").toString()};
        //String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "TolongKu user need help!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Help me im in danger :(");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Toast.makeText(getContext(),"Finished sending email",Toast.LENGTH_LONG);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }

    }

    public void makeCall()
    {
        dbReference.child("userContactNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneNumber = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_PHONE_CALL);
        }
        else
        {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+phoneNumber));
            startActivity(callIntent);
        }

    }



    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btnEmergencySend:
                sendSms();
                sendEmail();
                break;
            case R.id.btnEmergencyCall:
                makeCall();
        }
    }

    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getActivity().getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }
}



