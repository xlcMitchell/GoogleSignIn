package com.example.googlesigni;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlesigni.datamodel.ChannelInfo;

public class ChannelViewHolder extends RecyclerView.ViewHolder {
    CardView cardView;
    TextView titleTv, subscribersTv;
    ChannelInfo channel;
    public ChannelViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.channelCard);
        titleTv = itemView.findViewById(R.id.channelTitleTv);
        subscribersTv = itemView.findViewById(R.id.channelSubscribersTv);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(itemView.getContext(), ChannelInfoActivity.class);
                intent.putExtra("CHANNEL_INFO", channel);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}

