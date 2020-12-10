package com.example.leochris.launcher.featured;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import com.example.leochris.launcher.R;

/**
 * Created by shihern on 2017-06-20.
 */

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.ViewHolder>{
    private List<AppDetails> mDataset;

    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context context;
        private final PackageManager packageManager;
        private TextView label;
        private TextView desc;
        private ImageView icon;
        private RelativeLayout layout;
        public AppDetails appDetails;


        public ViewHolder(View itemView) {
            super(itemView);

            // Populate the local view variables
            label = (TextView) itemView.findViewById(R.id.item_label);
            desc = (TextView) itemView.findViewById(R.id.item_desc);
            icon = (ImageView) itemView.findViewById(R.id.item_icon);
            layout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
            itemView.setOnClickListener(this);

            // Populate the context and package manager variables
            context = itemView.getContext();
            packageManager = context.getPackageManager();
        }


        /**
         * Populates the views with data from the given app
         * @param details The app to be set in the tile
         */
        public void setAppDetails(AppDetails details) {
            label.setText(details.label);
            desc.setText(details.description);
            icon.setImageDrawable(details.icon);
            appDetails = details;
        }


        @Override
        public void onClick(View v) {
            // Launch the app on click
            Intent i = packageManager.getLaunchIntentForPackage(appDetails.packageName.toString());
            context.startActivity(i);
        }
    }


    /**
     * Create a new HomeGridAdapter instance
     * @param d List of apps
     */
    public FeaturedAdapter(List<AppDetails> d) {
        mDataset = d;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public FeaturedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from the dataset at this position
        // - replace the contents of the view with that element
        holder.setAppDetails(mDataset.get(position));
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
