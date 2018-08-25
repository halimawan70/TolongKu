package edu.bluejack17_2.tolongku;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class ChatActivity extends AppCompatActivity {


    private List<Message> messages;

    private Toolbar layoutChatTop;
    private TextView txtChatName;
    private ImageView imgChatProfile;
    private DatabaseReference rootRef;

    private String receiverID;
    private User u;

    private ImageButton imgChatImage,imgChatSend;
    private EditText txtChatContent;

    private FirebaseAuth mAuth;
    private String messageSenderId;

    private RecyclerView rvChat;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager llm;
    private MessageAdapter ma;


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

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);


        //actionBar.setCustomView(view, params);
        txtChatName = findViewById(R.id.txtChatName);
        txtChatName.setText( getIntent().getExtras().get("username").toString());
        imgChatProfile = findViewById(R.id.imgChatProfile);
        imgChatProfile.setImageResource(R.drawable.default_profile);

        imgChatImage = findViewById(R.id.imgChatImage);
        imgChatImage.setVisibility(View.GONE);
        imgChatSend = findViewById(R.id.imgChatSend);
        txtChatContent = findViewById(R.id.txtChatContent);

        rvChat = findViewById(R.id.rvChat);

        ma = new MessageAdapter(messageList);
        llm = new LinearLayoutManager(this);
        rvChat.setHasFixedSize(true);
        rvChat.setLayoutManager(llm);
        rvChat.setAdapter(ma);




        rootRef  = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        //messageSenderId = mAuth.getCurrentUser().getUid();
        messageSenderId = MainActivity.authID;
        //Toast.makeText(getApplicationContext(), messageSenderId,Toast.LENGTH_SHORT).show();

        getHistory();

        fetchMessages();

        rootRef.child("Messages").child(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //u = dataSnapshot.getValue(User.class);

               // Message m = dataSnapshot.getValue(Message.class);
                //messageList.add(m);
                //ma.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        imgChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }

            private void SendMessage()
            {
                final String messageText = txtChatContent.getText().toString();
                if(TextUtils.isEmpty(messageText))
                {
                    Toast.makeText(getApplicationContext(),"Message cannot be empty",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String message_sender_ref = "Messages/"+messageSenderId+"/"+receiverID;
                    String message_receiver_ref = "Messages/"+receiverID+"/"+messageSenderId;

                    DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId).child(receiverID).push();
                    String message_push_id = user_message_key.getKey();

                    Map messageTextBody = new HashMap();
                    messageTextBody.put("sender",MainActivity.authID);
                    messageTextBody.put("message",messageText);
                    messageTextBody.put("seen",false);
                    messageTextBody.put("type","text");
                    messageTextBody.put("time", new Timestamp(new Date().getTime()).toString() );

                    Map messageBodyDetail = new HashMap();
                    messageBodyDetail.put(message_sender_ref+"/"+message_push_id,messageTextBody);
                    messageBodyDetail.put(message_receiver_ref+"/"+message_push_id,messageTextBody);


                    rootRef.updateChildren(messageBodyDetail, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null)
                            {
                                Log.d("ChatActivity",databaseError.getMessage().toString());
                            }
                            txtChatContent.setText("");
                        }
                    });


                    rootRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                Log.d("ChatActivity",ds.child("userID").getValue(String.class));
                                if(ds.child("userID").getValue(String.class).compareTo(messageSenderId) ==0)
                                {
                                    Log.d("ChatActivity",receiverID);
                                    u = ds.getValue(User.class);
                                    HashMap<String,String> notificationData = new HashMap<String, String>();
                                    notificationData.put("content",messageText);
                                    notificationData.put("name",u.getUserName());
                                    DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference().child("Notifications2");
                                    notifRef.child(receiverID).push().setValue(notificationData);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
            }
        });



    }

    private void getHistory()
    {
        messages =(List<Message>) getIntent().getExtras().getSerializable("messages");
        for(Message m : messages)
        {
            messageList.add(m);
            ma.notifyDataSetChanged();
        }

    }

    private void fetchMessages() {
        rootRef.child("Messages").child(messageSenderId).child(receiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message m = dataSnapshot.getValue(Message.class);
                messageList.add(m);
                ma.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
