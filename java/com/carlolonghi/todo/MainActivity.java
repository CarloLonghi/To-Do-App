package com.carlolonghi.todo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import android.arch.lifecycle.ViewModelProviders;

public class MainActivity extends FragmentActivity {

    private String listTitle;
    private Map<String,LinkedHashMap<String,Boolean>> items;
    private MyViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.model = ViewModelProviders.of(this).get(MyViewModel.class);

        //Get the title of the list
        Intent intent = getIntent();
        String title = intent.getStringExtra("com.carlolonghi.todo.TITLE");
        this.listTitle=title;
        setTitle(listTitle.toUpperCase());

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

        //Fill the activity with the correct items
        this.populateItems();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
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
        // fill in any details dynamically here
        CheckBox checkBox=new CheckBox(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(16, 16, 16, 0);
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
        if(items.get(listTitle).keySet().contains(newItem)){
            Context context = getApplicationContext();
            CharSequence text = "Item already exists";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else {
            newItemField.setText("");
            checkBox.setText(newItem);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeItemStatus(v);
                }
            });

            // insert into main view
            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.itemsList);
            int pos=insertPoint.getChildCount()-numOfCheckedItems()-1;
            insertPoint.addView(checkBox, pos, layoutParams);

            //Save the new item
            LinkedHashMap<String, Boolean> tmp = items.get(this.listTitle);
            tmp.put(checkBox.getText().toString(), false);
            items.put(this.listTitle, tmp);
            this.updateItemsOnFile();
        }
    }

    private int numOfCheckedItems(){
        int count=0;
        LinkedHashMap<String, Boolean> tmp = items.get(this.listTitle);
        for(String item : tmp.keySet()){
            if(tmp.get(item))
                count++;
        }
        return count;
    }

    @Override
    protected void onPause(){
        super.onPause();

        this.updateItemsOnFile();
    }

    private void populateItems(){
        ViewGroup insertPoint = findViewById(R.id.itemsList);
        items=model.getItems();
        try{
            for (String item : this.items.get(this.listTitle).keySet()) {
                CheckBox newItemAdded = new CheckBox(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(16, 16, 16, 0);
                newItemAdded.setText(item);
                newItemAdded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeItemStatus(v);
                    }
                });
                if(items.get(listTitle).get(item)==true) {
                    newItemAdded.setChecked(true);
                    insertPoint.addView(newItemAdded,layoutParams);
                }
                else {
                    insertPoint.addView(newItemAdded,0+uncheckedItems(insertPoint), layoutParams);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private int uncheckedItems(ViewGroup l){
        int count=0;
        for(int i=0;i<l.getChildCount();i++){
            if(l.getChildAt(i).getId()!=R.id.addNewContainer && !((CheckBox)(l.getChildAt(i))).isChecked())
                count++;
        }
        return count;
    }

    private void changeItemStatus(View v){
        CheckBox checkBox = (CheckBox) v;
        if (checkBox.isChecked()) {
            LinkedHashMap<String, Boolean> tmp = items.get(this.listTitle);
            tmp.remove(checkBox.getText().toString());
            tmp.put(checkBox.getText().toString(), true);
            items.put(this.listTitle, tmp);
            this.updateItemsOnFile();
            LinearLayout itemsList=(LinearLayout)findViewById(R.id.itemsList);
            itemsList.removeView(checkBox);
            itemsList.addView(checkBox);
        }
        else{
            LinkedHashMap<String, Boolean> tmp = items.get(this.listTitle);
            tmp.remove(checkBox.getText().toString());
            tmp.put(checkBox.getText().toString(), false);
            items.put(this.listTitle, tmp);
            this.updateItemsOnFile();
            LinearLayout itemsList=(LinearLayout)findViewById(R.id.itemsList);
            itemsList.removeView(checkBox);
            itemsList.addView(checkBox,0+uncheckedItems(itemsList));
        }
    }

    private void updateItemsOnFile(){
        try{
            FileOutputStream outputStream = this.getBaseContext().openFileOutput("items.dat", MODE_PRIVATE);
            ObjectOutputStream writer = new ObjectOutputStream(outputStream);
            writer.writeObject(items);
            outputStream.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


