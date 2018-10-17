package com.carlolonghi.todo;


import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.LineNumberInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.arch.lifecycle.ViewModelProviders;



public class MainActivity extends FragmentActivity {

    private String listTitle;
    private Map<String,Items> items;
    private MyViewModel model;
    private NonCheckedItemsFragment nonCheckedItemsFragment;


    //LA PROVA DELLA VERITA'

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.model = ViewModelProviders.of(this).get(MyViewModel.class);
        this.items=model.getItems();

        //Get the title of the list
        Intent intent = getIntent();
        String title = intent.getStringExtra("com.carlolonghi.todo.TITLE");
        this.listTitle=title.toUpperCase();
        setTitle(listTitle.toUpperCase());

        FrameLayout fragmentContainer=(FrameLayout)findViewById(R.id.fragmentContainer);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        nonCheckedItemsFragment=NonCheckedItemsFragment.newInstance(listTitle);
        //fragment.getView().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fragmentTransaction.add(fragmentContainer.getId(),nonCheckedItemsFragment);
        fragmentTransaction.commit();


        EditText newItem=(EditText)findViewById(R.id.addNewText);
        newItem.setHorizontallyScrolling(false);
        newItem.setMaxLines(Integer.MAX_VALUE);
        newItem.setRawInputType(InputType.TYPE_CLASS_TEXT);

        newItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId){
                    case EditorInfo.IME_ACTION_DONE:
                        onClick(findViewById(R.id.addNewButton));
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public String getListTitle(){
        return this.listTitle;
    }

    @Override
    public void onResume(){
        super.onResume();

        nonCheckedItemsFragment.updateFragmentHeight(nonCheckedItemsFragment.getListView());
    }

    //This method saves an InstanceState when the activity is destroyed
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        savedInstanceState.putString("LIST_TITLE", this.listTitle);
        EditText editText=(EditText)findViewById(R.id.addNewText);
        savedInstanceState.putString("ENTERING_TEXT", editText.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //Restores the activity from the Instance State
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        this.listTitle = savedInstanceState.getString("LIST_TITLE");
        EditText editText=(EditText)findViewById(R.id.addNewText);
        String txt=savedInstanceState.getString("ENTERING_TEXT");
        editText.setText(txt);
        editText.setSelection(txt.length());
    }

    public void onClick(View view){
        EditText newItemField=(EditText) findViewById(R.id.addNewText);
        String newItem=newItemField.getText().toString();
        if(newItem.equals("")){
            Context context = getApplicationContext();
            CharSequence text = "You can't add an empty item";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        if(items.get(listTitle).getNonCheckedItems().contains(newItem)){
            Context context = getApplicationContext();
            CharSequence text = "Item already exists";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else {
            //Save the new item
            items.get(listTitle).addNonCheckedItem(newItem);
            model.updateItemsOnFile(this.getBaseContext());

            //Add the new item to the fragment here
            FrameLayout frameLayout=(FrameLayout)findViewById(R.id.fragmentContainer);
            NonCheckedItemsFragment nonCheckedItemsFragment=(NonCheckedItemsFragment)getSupportFragmentManager().findFragmentById(frameLayout.getId());
            nonCheckedItemsFragment.addItem(newItem);
            //updateFragmentSize(false);

            newItemField.setText("");
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        model.updateItemsOnFile(this.getBaseContext());
    }
}


