package edu.bluejack17_2.tolongku;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


public class ChatActivity extends AppCompatActivity {


    private Toolbar layoutChatTop;
    private TextView txtChatName;
    private ImageView imgChatProfile;
    private DatabaseReference rootRef;

    private String receiverID;
    private User u;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        receiverID = getIntent().getExtras().get("id").toString();
        layoutChatTop = findViewById(R.id.layoutChatTop);
        setSupportActionBar(layoutChatTop);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =inflater.inflate(R.layout.chatcustombar,null);
        actionBar.setCustomView(view);
        txtChatName = findViewById(R.id.txtChatName);
        txtChatName.setText( getIntent().getExtras().get("username").toString());
        imgChatProfile = findViewById(R.id.imgChatProfile);
        imgChatProfile.setImageResource(R.drawable.default_profile);

        rootRef  = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Users").child(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                u = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
