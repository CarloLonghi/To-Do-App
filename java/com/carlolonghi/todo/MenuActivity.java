package com.carlolonghi.todo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuActivity extends Activity {

    SharedPreferences prefs = null;
    private List<String> lists;
    private Map<String,List<String>> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        prefs = getSharedPreferences("com.carlolonghi.todo", MODE_PRIVATE);


        File listFile = new File(getApplicationInfo().dataDir, "lists.dat");
        File itemsFile = new File(getApplicationInfo().dataDir, "items.dat");
        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            try {
                listFile.createNewFile();
                itemsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
        }


        lists = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(listFile)
            );
            if (br.readLine() != null) {
                File data = new File(getApplicationInfo().dataDir, "lists.dat");
                FileInputStream inputStream = new FileInputStream(data);
                ObjectInputStream reader = new ObjectInputStream(inputStream);
                lists = (ArrayList<String>) reader.readObject();
                for (String list : lists) {
                    ViewGroup insertPoint = (LinearLayout) findViewById(R.id.ListTitles);
                    Button newItemAddedButton = new Button(this);
                    newItemAddedButton.setText(list);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(48, 48, 48, 0);
                    insertPoint.addView(newItemAddedButton, layoutParams);
                    newItemAddedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            avoidDoubleClicks(view);
                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                            String list = ((Button) view).getText().toString();
                            intent.putExtra("com.carlolonghi.todo.TITLE", list);
                            startActivity(intent);
                        }
                    });
                }
                inputStream.close();
                reader.close();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        items=new HashMap<>();
        File data=new File(getApplicationInfo().dataDir,"items.dat");
        try{
            BufferedReader br = new BufferedReader(new FileReader(data));
            if (br.readLine() != null) {
                FileInputStream inputStream = new FileInputStream(data);
                ObjectInputStream reader = new ObjectInputStream(inputStream);
                items = (Map<String, List<String>>) reader.readObject();
                inputStream.close();
                reader.close();
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onClick1(final View addButton) {
        addButton.setClickable(false);

        //Add the edittext where the user has to type the title of the new list
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = vi.inflate(R.layout.new_list_to_add_layout, null);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
        insertPoint.addView(v1);
        insertPoint.findViewById(R.id.addNewList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton.setClickable(true);

                //Get the title and remove the edittext
                EditText editText = (EditText) findViewById(R.id.newListToAdd);
                String newListTitle = editText.getText().toString();

                //Delete the add editText and Button from layout
                ((ViewManager) editText.getParent()).removeView(editText);
                ((ViewManager) v.getParent()).removeView(v);

                //Add the new list to the page
                ViewGroup insertPoint = (LinearLayout) findViewById(R.id.ListTitles);
                Button newItemAddedButton = new Button(v.getContext());
                newItemAddedButton.setText(newListTitle);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(48, 48, 48, 0);
                //newItemAddedButton.setLayoutParams(layoutParams);
                insertPoint.addView(newItemAddedButton, layoutParams);

                //insertPoint.addView(v2);
                newItemAddedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        avoidDoubleClicks(view);
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        String list = ((Button) view).getText().toString();
                        intent.putExtra("com.carlolonghi.todo.TITLE", list);
                        startActivity(intent);
                    }
                });

                lists.add(newListTitle);
                try {
                    //Save the new list to lists.dat
                    File data = new File(getApplicationInfo().dataDir, "lists.dat");
                    data.delete();
                    data.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(data);
                    ObjectOutputStream writer = new ObjectOutputStream(outputStream);
                    writer.writeObject(lists);
                    writer.close();
                    outputStream.close();

                    //Save the new list to items.dat
                    data = new File(getApplicationInfo().dataDir, "items.dat");
                    data.delete();
                    data.createNewFile();
                    outputStream = new FileOutputStream(data);
                    writer = new ObjectOutputStream((outputStream));
                    items.put(newListTitle,new ArrayList<String>());
                    writer.writeObject(items);
                    writer.close();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //To prevent from double clicking the row item and so prevents overlapping fragment.
    public static void avoidDoubleClicks(final View view) {
        final long DELAY_IN_MS = 900;
        if (!view.isClickable()) {
            return;
        }
        view.setClickable(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
            }
        }, DELAY_IN_MS);
    }

}