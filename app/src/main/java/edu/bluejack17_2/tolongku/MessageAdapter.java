package edu.bluejack17_2.tolongku;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Message> userMessageList;

    public MessageAdapter(List<Message> userMessageList)
    {
        this.userMessageList = userMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutmessages,parent,false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        if(userMessageList.get(position).getSender().compareTo(MainActivity.authID)==0)
        {

            holder.messageText.setText("test");
            holder.messageText.setVisibility(View.GONE);
            holder.messageThumb.setVisibility(ImageView.VISIBLE);
            holder.messageText2.setVisibility(View.VISIBLE);
            holder.messageThumb2.setVisibility(ImageView.GONE);

            holder.messageText2.setText(userMessageList.get(position).getMessage());
            Log.d("MessageAdapter","Message from self");


        }
        else
        {
            holder.messageText.setVisibility(View.VISIBLE);
            holder.messageThumb.setVisibility(ImageView.GONE);
            holder.messageText2.setVisibility(View.GONE);
            holder.messageThumb2.setVisibility(ImageView.VISIBLE);

            holder.messageText.setText(userMessageList.get(position).getMessage());
            Log.d("MessageAdapter", "message from other");

        }

    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageText,messageText2;
        public ImageView messageThumb,messageThumb2;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.txtChatText);
            messageThumb = itemView.findViewById(R.id.imgChatProfile);
            messageThumb.setImageResource(R.drawable.default_profile);
            messageText2 = itemView.findViewById(R.id.txtChatText2);
            messageThumb2 = itemView.findViewById(R.id.imgChatProfile2);
            messageThumb2.setImageResource(R.drawable.default_profile);
        }
    }


}
