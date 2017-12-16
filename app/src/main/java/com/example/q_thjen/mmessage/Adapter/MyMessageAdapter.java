package com.example.q_thjen.mmessage.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.q_thjen.mmessage.Model.MyMessage;
import com.example.q_thjen.mmessage.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.MessageViewHolder> {

    private List<MyMessage> mMyMessageList;
    private FirebaseAuth mAuth;

    public MyMessageAdapter(List<MyMessage> mMyMessageList) {
        this.mMyMessageList = mMyMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_layout, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

        String currentUser = mAuth.getInstance().getCurrentUser().getUid();

        holder.tv_message.setText(mMyMessageList.get(position).getMessage());

        String fromUser = mMyMessageList.get(position).getFrom();

        if ( fromUser.equals(currentUser) ) {

            holder.tv_message.setBackgroundColor(Color.WHITE);
            holder.tv_message.setTextColor(Color.BLACK);

        } else {

            holder.tv_message.setBackgroundColor(R.drawable.message);
            holder.tv_message.setTextColor(Color.WHITE);

        }

    }

    @Override
    public int getItemCount() {
        return mMyMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_message;
        public CircleImageView civ_userChat;

        public MessageViewHolder(View itemView) {
            super(itemView);

            tv_message = itemView.findViewById(R.id.tv_userChatLayout);
            civ_userChat = itemView.findViewById(R.id.civ_userChatLayout);

        }

    }

}
