package com.example.leochris.launcher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static android.R.attr.name;
import static com.example.leochris.launcher.R.array.uri;

public class SettingActivity extends AppCompatActivity {

    private ListView mListView;
    List<FragmentInfo> listItems,changes;
    SettingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mListView = (ListView) findViewById(R.id.list_fragment);
        //load fragment infos created from previous session if any
        if(savedInstanceState != null)
            listItems = savedInstanceState.getParcelableArrayList("fragment infos");
        else
            listItems = getIntent().getParcelableArrayListExtra("listitems");
        changes= new ArrayList<>();

        adapter = new SettingAdapter(this, listItems);
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                adapter.clearSelection();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub

                nr = 0;
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // TODO Auto-generated method stub
                switch (item.getItemId()) {

                    case R.id.item_delete:
                        Set<Integer> checked= adapter.getCurrentCheckedPosition();
                        Iterator<Integer> it=checked.iterator();
                        while(it.hasNext()){
                            Integer pos=it.next();
                            FragmentInfo fragmentInfo=listItems.get(pos);
                            fragmentInfo.setToBeDeleted(true);
                            changes.add(fragmentInfo);
                        }
                        for(int x=0; x<listItems.size(); x++){
                            if(listItems.get(x).isToBeDeleted())
                                listItems.remove(x--);
                        }
                        nr = 0;
                        adapter.clearSelection();
                        adapter.notifyDataSetChanged();
                        mode.finish();
                }
                return false;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // TODO Auto-generated method stub
                if (checked) {
                    nr++;
                    adapter.setNewSelection(position, checked);
                } else {
                    nr--;
                    adapter.removeSelection(position);
                }
                mode.setTitle(nr + " selected");

            }
        });
        adapter.notifyDataSetChanged();

        //highlight items on long click
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                // TODO Auto-generated method stub

                mListView.setItemChecked(position, !adapter.isPositionChecked(position));
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(this, AddActivity.class);
                startActivityForResult(intent, 1);
                return true;

            case R.id.action_done:
                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) changes);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("fragment infos", (ArrayList<? extends Parcelable>) listItems);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String name = intent.getStringExtra("name");
                String data = intent.getStringExtra("data");
                int type = intent.getIntExtra("type", 1);
                System.out.println(data);
                System.out.println(name);
                FragmentInfo fragmentInfo=new FragmentInfo(name, type);
                if(type <= 3)
                    fragmentInfo.setData(data);
                listItems.add(fragmentInfo);
                changes.add(fragmentInfo);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
