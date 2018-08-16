package edu.bluejack17_2.tolongku;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    ListView lvFriendList;
    DatabaseReference dbRef;
    Vector<String> ids;

    public void populateFriendList()
    {
        dbRef.child("userFriend").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String s = ds.getValue(String.class);
                    ids.add(s);
                    lvFriendList.setAdapter(new CustomAdapter());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public FriendsFragment() {
        lvFriendList = getView().findViewById(R.id.lvFriendList);
        Toast.makeText(getActivity().getApplicationContext(),MainActivity.authID,Toast.LENGTH_SHORT).show();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.authID);
        ids = new Vector<>();
        populateFriendList();

        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    class CustomAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return ids.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //view = getLayoutInflater().inflate(R.layout.)
            //return view;
            return null;
        }
    }


}
