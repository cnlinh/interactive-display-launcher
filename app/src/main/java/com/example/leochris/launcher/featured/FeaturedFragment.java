package com.example.leochris.launcher.featured;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leochris.launcher.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeaturedFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private PackageManager packageManager;
    private List<AppDetails> apps; //List of apps


    public FeaturedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_featured, container, false);

        apps = loadApps();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.featured_recyclerview);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a grid layout manager
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Set the adapter
        mAdapter = new FeaturedAdapter(apps);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }


    /**
     * Reads a file in the root of the sdcard directory called "apps_featured.txt" that contains
     * package names and descriptions of the featured apps
     *
     * @return A list of featured apps
     */
    private List<String[]> readFeatured() {
        // Find the directory for the SD Card using the API
        File sdcard = Environment.getExternalStorageDirectory();
        // Get the text file
        File file = new File(sdcard,"apps_featured.txt");

        // Populate a blacklist with the list of package names in the file
        ArrayList<String[]> featured = new ArrayList<String[]>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String packageName;
            String description;
            while ((packageName = br.readLine()) != null) {
                Log.d("featured", packageName);
                if ((description = br.readLine()) != null) {
                    featured.add(new String[]{packageName, description});
                }
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return featured;
    }


    /**
     * Populates a list of AppDetails of all featured apps
     *
     * @return A list of AppDetails of all featrued apps
     */
    private List<AppDetails> loadApps() {
        packageManager = getContext().getPackageManager();
        List<String[]> featuredApps = readFeatured();

        List<AppDetails> returnApps = new ArrayList<AppDetails>();

        // Intent used to acquire the package names
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        // Populate the list with each app
        for (String[] app: featuredApps) {
            AppDetails appDetails = new AppDetails();
            appDetails.packageName = app[0];
//            appDetails.icon = getFullResIcon(app[0], 1);
            appDetails.description = app[1];
            try {
                ResolveInfo ri = packageManager.resolveActivity(packageManager.getLaunchIntentForPackage(app[0]), PackageManager.MATCH_ALL);
//                appDetails.icon = packageManager.getApplicationIcon(app[0]);
                appDetails.icon = getFullResIcon(app[0], ri.activityInfo.getIconResource());
                appDetails.label = packageManager.getApplicationLabel(
                                                    packageManager.getApplicationInfo(app[0], 0));
            } catch (PackageManager.NameNotFoundException e) {
                appDetails.icon = getFullResDefaultActivityIcon();
                appDetails.label = "App";
            }

            returnApps.add(appDetails);
        }

        // Sort the list of apps by label name
        Collections.sort(returnApps, new Comparator<AppDetails>() {
            @Override
            public int compare(AppDetails o1, AppDetails o2) {
                return o1.label.toString().toLowerCase().compareTo(o2.label.toString().toLowerCase());
            }
        });

        return returnApps;
    }


    /**
     * Get the full resolution app icon from the resource
     * @param resources Resource of the icon to be found
     * @param iconId
     * @return A drawable of the icon or default system icon if none is found
     */
    private Drawable getFullResIcon(Resources resources, int iconId) {
        // Get the drawable for extra high density
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, DisplayMetrics.DENSITY_XHIGH);
        } catch (Resources.NotFoundException e) {
            d = null;
        }

        // Return the drawable if not null, otherwise return the default icon
        return (d != null) ? d : getFullResDefaultActivityIcon();
    }


    /**
     * Get the full resolution app icon from the package name
     * @param packageName Name of the package
     * @param iconId
     * @return A drawable of the icon or default system icon if none is found
     */
    private Drawable getFullResIcon(String packageName, int iconId) {
        // Get the resource for the package
        Resources resources;
        try {
            resources = packageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }

        // Call another function to get the full res icon if the resource is found
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }

        // Return the default icon if no resource is found
        return getFullResDefaultActivityIcon();
    }


    /**
     * Returns the default full resolution icon for the system
     * @return A drawable of the icon
     */
    private Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(), android.R.mipmap.sym_def_app_icon);
    }

}
