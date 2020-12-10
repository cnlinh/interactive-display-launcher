package com.example.leochris.launcher.announcement;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leochris.launcher.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 7/9/2017.
 */

public class AnnounFrag extends Fragment {

    private ArrayList<Event> objList = new ArrayList<>();
    private Map<String, Event> keyToEvent = new HashMap<>();

    private DatabaseReference mDataReference;
    private BroadcastReceiver br;

    // Ensure that curDate is consistent and initialized only once
    Long curDate;
    Calendar curDateCal;
    ViewGroup rootView;

    EventsAdapter adapter;

    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_announcement, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        //Get current date
        curDate = Long.parseLong(Event.df.format(new Date()));

        // For debug purpose
        val = new Date().getMinutes();

        setDataChangedListener();
        bindToAdapter();
        scheduleAlarm();

        return rootView;
    }

    int val = 0;
    private String broadcastName = "com.launcher.resetdate";
    private PendingIntent pi;

    private void scheduleAlarm(){
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                //Toast.makeText(c, "" + i.getLongExtra("newDate",0), Toast.LENGTH_LONG).show();
                curDate = i.getLongExtra("newDate",0);
                setDataChangedListener();
                scheduleAlarm();
                //Log.d(broadcastName,"hi");
                //Log.d(broadcastName,""+curDate);
            }
        };
        getActivity().registerReceiver(br, new IntentFilter(broadcastName) );
        Intent intent = new Intent(broadcastName);
        //Log.d(broadcastName,"scheduled");

        AlarmManager am = (AlarmManager)(getActivity().getSystemService( Context.ALARM_SERVICE ));

        Calendar cal = Calendar.getInstance();
        try {
            Date d = Event.df.parse(""+curDate);
            cal.setTime(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Set to reset every midnight
        cal.add(Calendar.DAY_OF_MONTH, 1); //add a day
        //Log.d(broadcastName,cal.toString());
        long millis = cal.getTimeInMillis();

        intent.putExtra("newDate",Long.parseLong(Event.df.format(cal.getTime())));

        pi = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        am.setExact(AlarmManager.RTC_WAKEUP, millis, pi);
    }

    private void bindToAdapter(){
        adapter = new EventsAdapter(getContext(), objList);

        mRecyclerView.setAdapter(adapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
    }

    // Check if the current date is between the event's start and end date
    private boolean shouldShow(Event e) {
        return e.getSDate() <= curDate && e.getEDate() >= curDate;
    }

    private void setDataChangedListener() {
        if(mDataReference!=null){
            objList.clear();
            synchronized(adapter) {
                adapter.notify();
            }
            mDataReference = null;
        }
        // Dynamically updates data of RecyclerView
        mDataReference = FirebaseDatabase.getInstance().getReference().child("data");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Event e = dataSnapshot.getValue(Event.class);
                //Log.d(broadcastName,e.toString());
                if (shouldShow(e)) {
                    objList.add(e);
                    keyToEvent.put(dataSnapshot.getKey(), e);

                    adapter.notifyItemInserted(objList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Event e = dataSnapshot.getValue(Event.class);
                String key = dataSnapshot.getKey();

                if(keyToEvent.containsKey(key)) {
                    Event curEvent = keyToEvent.get(key);
                    int pos = objList.indexOf(curEvent);
                    if (shouldShow(e)) {
                        objList.set(pos, e);
                        keyToEvent.put(dataSnapshot.getKey(), e);
                        adapter.notifyItemChanged(pos);
                    } else {
                        objList.remove(curEvent);
                        keyToEvent.remove(key);
                        adapter.notifyItemRemoved(pos);
                        adapter.notifyItemRangeChanged(pos, adapter.getItemCount());
                    }
                }
                else if(shouldShow(e)){
                    objList.add(e);
                    keyToEvent.put(dataSnapshot.getKey(), e);
                    adapter.notifyItemInserted(objList.size() - 1);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(keyToEvent.containsKey(dataSnapshot.getKey())) {
                    Event e = keyToEvent.get(dataSnapshot.getKey());
                    int pos = objList.indexOf(e);
                    objList.remove(pos);
                    keyToEvent.remove(dataSnapshot.getKey());
                    adapter.notifyItemRemoved(pos);
                    adapter.notifyItemRangeChanged(pos, adapter.getItemCount());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDataReference.addChildEventListener(childEventListener);
    }

    class EventsAdapter extends
            RecyclerView.Adapter<EventsAdapter.ViewHolder> {
        private ArrayList<Event> mEvents;
        private Context mContext;

        public EventsAdapter(Context context, ArrayList<Event> events) {
            mEvents = events;
            mContext = context;
        }

        private Context getContext() {
            return mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View eventView = inflater.inflate(R.layout.fragment_announc_view_holder, parent, false);

            return new ViewHolder(eventView);
        }

        // Set item views to event details
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Event event = mEvents.get(position);

            holder.titleTextView.setText(event.getTitle());
            holder.textTextView.setText(event.getText());
            if (event.getEvent()) holder.dateTextView.setText(event.getDate());
            else holder.dateTextView.setVisibility(View.GONE);
            if (!event.getImportant()) holder.importantImageView.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView titleTextView;
            public TextView textTextView;
            public TextView dateTextView;
            public ImageView importantImageView;

            public ViewHolder(View itemView) {
                super(itemView);

                titleTextView = (TextView) itemView.findViewById(R.id.titletv);
                textTextView = (TextView) itemView.findViewById(R.id.texttv);
                dateTextView = (TextView) itemView.findViewById(R.id.datetv);
                importantImageView = (ImageView) itemView.findViewById(R.id.impiv);
            }
        }
    }

    public void onClose(){
        AlarmManager am = (AlarmManager)(getActivity().getSystemService( Context.ALARM_SERVICE ));
        try {
            am.cancel(pi);
        } catch (Exception e) {
        }
    }
}