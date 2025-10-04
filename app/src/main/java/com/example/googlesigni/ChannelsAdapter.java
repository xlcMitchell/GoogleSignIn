package com.example.googlesigni;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlesigni.datamodel.ChannelInfo;

import java.util.List;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelViewHolder> {

    List <ChannelInfo> channels;

    public ChannelsAdapter(List<ChannelInfo> info){
        this.channels = info;
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_channel_view, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
        holder.channel = channels.get(position);
        holder.titleTv.setText(channels.get(position).getChannelTitle());
        holder.subscribersTv.setText(channels.get(position).getChannelSubscribers());
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public void updateData(List<ChannelInfo> channels){
        this.channels = channels;
        notifyDataSetChanged();
    }
}
