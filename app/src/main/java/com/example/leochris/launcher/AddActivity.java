package com.example.leochris.launcher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class AddActivity extends AppCompatActivity {

    TextView name;
    TextView data;
    Spinner spinner;
    Button choose;
    Uri selectedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        name = (TextView) findViewById(R.id.editText);
        data = (TextView) findViewById(R.id.editUri);
        spinner = (Spinner) findViewById(R.id.spinner);
        choose = (Button) findViewById(R.id.choose);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick photo
                if(spinner.getSelectedItemPosition() == 0) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 0);
                }

                //pick video
                if(spinner.getSelectedItemPosition() == 1) {
                    Intent pickVideo = new Intent(Intent.ACTION_PICK,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickVideo, 1);
                }
            }
        });

        //load tab types from strings resources
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                R.array.fragment_list, android.R.layout.simple_spinner_item);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*type values
                * 0:Image Tab
                * 1:Video Tab
                * 2:Text Tab
                * 3:Apps Grid Fragment
                * 4:Weather Tab*/
                switch(position){
                    case 0:
                    case 1:
                        choose.setVisibility(View.VISIBLE);
                        data.setVisibility(View.VISIBLE);
                        data.setHint("URI");
                        break;
                    case 2:
                        data.setHint("Text");
                        data.setVisibility(View.VISIBLE);
                        choose.setVisibility(View.GONE);
                        break;
                    case 3:
                    case 4:
                        choose.setVisibility(View.GONE);
                        data.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                choose.setVisibility(View.GONE);
                data.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                if(TextUtils.isEmpty(name.getText()))
                    name.setError("Name cannot be empty.");
                if(TextUtils.isEmpty(data.getText()) && data.getVisibility() == View.VISIBLE)
                    data.setError("This field cannot be empty.");
                if(!TextUtils.isEmpty(name.getText()) && !(TextUtils.isEmpty(data.getText()) && data.getVisibility() == View.VISIBLE)) {
                    //parse infos back to SettingActivity
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("data", data.getText().toString());
                    returnIntent.putExtra("name", name.getText().toString());
                    returnIntent.putExtra("type", spinner.getSelectedItemPosition() + 1);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    //image
                    selectedUri = imageReturnedIntent.getData();
                    data.setText(selectedUri.toString());
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    //video
                    selectedUri = imageReturnedIntent.getData();
                    data.setText(selectedUri.toString());
                }

                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
