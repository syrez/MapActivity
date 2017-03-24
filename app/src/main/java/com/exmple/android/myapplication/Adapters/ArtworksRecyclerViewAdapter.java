package com.exmple.android.myapplication.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.exmple.android.myapplication.Model.Artwork;
import com.exmple.android.myapplication.R;

import java.util.List;

/**
 * Created by k3vin on 21-03-17.
 */

public class ArtworksRecyclerViewAdapter extends RecyclerView.Adapter<ArtworksRecyclerViewAdapter.ArtworksViewHolders> {

    private List<Artwork> artworkList;
    private Context context;
    private int position;

    public interface Callback {
        public void getIdFromArtwork(String id);
    }

    public ArtworksRecyclerViewAdapter(Context context, List<Artwork> artworks) {
        this.artworkList = artworks;
        this.context = context;
    }

    @Override
    public ArtworksViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artworks_list, null);
        ArtworksViewHolders rcv = new ArtworksViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ArtworksViewHolders holder, int position) {
        holder.artworkTitle.setText("test");
        holder.artworkId.setText(artworkList.get(position).getObjectId());
        String url = artworkList.get(position).getUrl();
        Glide.with(context)
                .load(url)
                .centerCrop()
                .override(800, Target.SIZE_ORIGINAL)
                .into(holder.artworkPicture);
        this.position = position;
    }

    @Override
    public int getItemCount() {
        return this.artworkList.size();
    }

    public class ArtworksViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView artworkId;
        public TextView artworkTitle;
        public ImageView artworkPicture;

        public ArtworksViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            artworkTitle = (TextView) itemView.findViewById(R.id.Artwork_title);
            artworkId = (TextView) itemView.findViewById(R.id.Artwork_id);
            artworkPicture = (ImageView) itemView.findViewById(R.id.Artwork_picture);
        }

        @Override
        public void onClick(View view) {
           ((Callback) context).getIdFromArtwork(artworkList.get(getAdapterPosition()).getObjectId());
        }
    }

}
