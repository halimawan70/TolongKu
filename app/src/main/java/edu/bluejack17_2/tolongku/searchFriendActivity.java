package edu.bluejack17_2.tolongku;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class searchFriendActivity extends AppCompatActivity {

    ListView lvSearch;
    EditText txtSearch;
    User[] users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        lvSearch = (ListView) findViewById(R.id.lvSearch);
        if(lvSearch == null)
        {
            Log.d("searchFriendActivity","null euy");
        }
        txtSearch = findViewById(R.id.txtSearch);
        CustomAdapter ca = new CustomAdapter();
        lvSearch.setAdapter(ca);

    }

    class CustomAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 3;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.searchfriendlayout,null);
            setContentView(R.layout.searchfriendlayout);
            ImageView imgSearchFriend = findViewById(R.id.imgAddFriend);
            TextView lblSearchFriend = findViewById(R.id.lblAddFriend);
            Button btnSearchFriend = findViewById(R.id.btnAddFriend);

            imgSearchFriend.setImageResource(R.drawable.default_profile);
            lblSearchFriend.setText("Test");

            return view;
        }
    }

}
