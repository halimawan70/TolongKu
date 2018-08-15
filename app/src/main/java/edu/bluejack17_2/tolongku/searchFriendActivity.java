package edu.bluejack17_2.tolongku;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.Touch;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

public class searchFriendActivity extends AppCompatActivity implements View.OnClickListener{

    ListView lvSearch;
    EditText txtSearch;
    Button btnSearch;
    Vector<User> users;
    TextView lblSearchFriendID;

    DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        lvSearch = (ListView) findViewById(R.id.lvSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        txtSearch = findViewById(R.id.txtSearch);
        users = new Vector<>();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        if(lvSearch == null)
        {
            Log.d("searchFriendActivity","null euy");
        }
        txtSearch = findViewById(R.id.txtSearch);
        //CustomAdapter ca = new CustomAdapter();
        //lvSearch.setAdapter(ca);

    }

    void doSearch()
    {
        Log.d("searchFriendActivity","users count : "+users.size());
        users.clear();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    User u = ds.getValue(User.class);

                    if(u.getUserName().contains(txtSearch.getText().toString()))
                    {
                        users.add(u);
                    }

                }
                CustomAdapter ca = new CustomAdapter();
                lvSearch.setAdapter(ca);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.btnSearch:
                doSearch();
                break;
        }
    }

    class CustomAdapter extends BaseAdapter implements View.OnClickListener
    {
        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 3;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.searchfriendlayout,null);
            setContentView(R.layout.searchfriendlayout);
            ImageView imgSearchFriend = findViewById(R.id.imgAddFriend);
            TextView lblSearchFriend = findViewById(R.id.lblAddFriend);
            lblSearchFriendID = findViewById(R.id.lblAddFriendID);
            Button btnSearchFriend = findViewById(R.id.btnAddFriend);


            imgSearchFriend.setImageResource(R.drawable.default_profile);
            lblSearchFriendID.setText(users.get(i).getUserID());
            lblSearchFriend.setText(users.get(i).getUserName());
            btnSearchFriend.setText("add as friend");

            btnSearchFriend.setOnClickListener(this);

            return view;
        }

        @Override
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.btnAddFriend:
                    addToFriend();

            }
        }

        public void addToFriend()
        {
            Toast.makeText(getApplicationContext(),MainActivity.authID,Toast.LENGTH_SHORT).show();
            dbRef.child(MainActivity.authID).addListenerForSingleValueEvent(new ValueEventListener() {
                boolean flag = true;
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dbRef.child(MainActivity.authID).child("UserFriend").addListenerForSingleValueEvent(new ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                Log.d("searchFriendActivity",ds.getValue().toString()+":"+lblSearchFriendID.getText().toString());
                                if(ds.getValue().toString().equals(lblSearchFriendID.getText().toString()))
                                {
                                    Log.d("searchFriendActivity","false");

                                    flag = false;
                                }
                            }
                            if(flag)
                            {
                                dbRef.child(MainActivity.authID).child("UserFriend").push().setValue(lblSearchFriendID.getText().toString());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

}
