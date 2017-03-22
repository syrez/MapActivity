package com.exmple.android.myapplication.Helper;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.exmple.android.myapplication.Model.ItemObjects;
import com.exmple.android.myapplication.R;

import java.util.List;

/**
 * Created by k3vin on 21-03-17.
 */

public class ArtwprksRecyclerViewAdapter extends RecyclerView.Adapter<ArtworksViewHolders> {

    private List<ItemObjects> itemList;
    private Context context;

    public ArtwprksRecyclerViewAdapter(Context context, List<ItemObjects> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public ArtworksViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solvent_list, null);
        ArtworksViewHolders rcv = new ArtworksViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ArtworksViewHolders holder, int position) {
        holder.countryName.setText(itemList.get(position).getName());
        int id = itemList.get(position).getPhoto();
        Glide.with(context)
                .load(getUriToDrawable(context, id))
                .centerCrop()
                .override(800, Target.SIZE_ORIGINAL)
                .into(holder.countryPhoto);

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public static final Uri getUriToDrawable(@NonNull Context context,
                                             @AnyRes int drawableId) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId));
        return imageUri;
    }

}
