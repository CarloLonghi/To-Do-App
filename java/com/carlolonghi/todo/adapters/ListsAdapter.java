package com.carlolonghi.todo.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carlolonghi.todo.activities.ItemsActivity;
import com.carlolonghi.todo.data.ItemsViewModel;
import com.carlolonghi.todo.others.ListButtonListener;
import com.carlolonghi.todo.R;

import java.util.List;

public class ListsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isAddNewPresent;
    private String editingText;
    private ItemsViewModel model;
    private List<String> items;
    private Button contextMenuList;
    private RecyclerView.LayoutManager myLayoutManager;
    private Context context;

    private static final int NEWLIST_TYPE=1;
    private static final int ADDNEW_TYPE=2;

    private static class ListViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final Button myListButton;
        private ListViewHolder(Button listButton) {
            super(listButton);
            myListButton = listButton;
            myListButton.setOnCreateContextMenuListener(this);
        }

        //Creates the context menu when the lists are long-pressed
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
            menu.add(0, v.getId(), 0, "Delete");
            menu.add(0, v.getId(), 0, "Bookmark");
        }
    }


    private static class AddNewListHolder extends RecyclerView.ViewHolder{
        private final LinearLayout addNewLayout;
        private AddNewListHolder(LinearLayout addLayout){
            super(addLayout);
            addNewLayout=addLayout;
        }
    }

    public ListsAdapter(List<String> items){
        this.items=items;
        this.isAddNewPresent=false;
        this.editingText="";
    }

    @Override
    public int getItemViewType(int position) {
        int itemsSize=items.size();
        if(position==itemsSize-1 && items.get(itemsSize-1).equals("ADDNEW"))
            return ADDNEW_TYPE;
        else return NEWLIST_TYPE;
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        switch(viewType){
            case NEWLIST_TYPE:
                Button newList=(Button) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout,parent,false);
                ListViewHolder vh=new ListViewHolder(newList);
                newList.setOnClickListener(new ListButtonListener());
                return vh;
            case ADDNEW_TYPE:
            default:
                setAddNewPresent(true);
                LinearLayout addNew=(LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_layout,parent,false);
                AddNewListHolder vh1=new AddNewListHolder(addNew);
                Button addButton=(Button)addNew.findViewById(R.id.addNewButton);
                addButton.setOnClickListener(new AddNewClickListener());
                //This block of instructions regulates the correct behaviour of the EditText used to add the new items
                //the text goes newline automatically when gets to the end of it and when the newline button on the keyboard is pressed
                //the item is added to the list as the Add button has been pressed
                EditText newListText=(EditText)addNew.findViewById(R.id.addNewText);
                newListText.setHorizontallyScrolling(false);
                newListText.setMaxLines(Integer.MAX_VALUE);
                newListText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                newListText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        switch(actionId){
                            case EditorInfo.IME_ACTION_DONE:
                                //Calls the onClick of the Add button if the newLine is pressed
                                Button button=(Button)((LinearLayout)v.getParent()).getChildAt(1);
                                //(new ListsAdapter.AddNewClickListener()).onClick(addNew.findViewById(R.id.addNewButton));
                                (new ListsAdapter.AddNewClickListener()).onClick(button);
                                //onClick(findViewById(R.id.addNewButton));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //Sets the cursor on the edittext and opens the keyboard
                newListText.requestFocus();
                Activity activity = (Activity)newListText.getContext();
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newListText, InputMethodManager.SHOW_IMPLICIT);
                return vh1;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        //Get element from your dataset at this position
        int itemType=getItemViewType(position);
        if(itemType==NEWLIST_TYPE){
            String text=items.get(position);
            Button listButton=((ListViewHolder)holder).myListButton;
            listButton.setText(text);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //setPosition(holder.getPosition());
                    setContextMenuList((Button)v);
                    return false;
                }
            });
        }
        else if(itemType==ADDNEW_TYPE){
            EditText editText=((EditText)((AddNewListHolder)holder).addNewLayout.getChildAt(1));
            editText.setText(editingText);
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(editText.getContext().INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            editText.setSelection(editingText.length());
        }
    }

    //Used to set the variable contextMenuList when a list has been longpressed
    private void setContextMenuList(Button button){
        this.contextMenuList=button;
    }

    //Delete the button previously stored for the contextmenu
    public void deleteContextMenuList(){
        Button b=new Button(this.context);
        b.setText("");
        this.contextMenuList=b;
    }

    //Functions used to get the Button which has been longpressed
    public Button getContextMenuList(){
        return this.contextMenuList;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }


    public void setAddNewPresent(boolean isPresent){
        this.isAddNewPresent=isPresent;
    }

    //The listener for the lists' buttons
    private class AddNewClickListener implements View.OnClickListener{
        public void onClick(View view){
            LinearLayout container=(LinearLayout)view.getParent();
            EditText editText=(EditText)container.getChildAt(1);
            Context context = view.getContext().getApplicationContext();
            if(editText.getText().toString().equals("")){
                CharSequence text = context.getResources().getString(R.string.emptyListError);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            else if(items.contains(editText.getText().toString().toUpperCase())
                    || model.getBookmarkItems().keySet().contains(editText.getText().toString().toUpperCase())){
                CharSequence text = context.getResources().getString(R.string.duplicateListError);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            else {
                String text=editText.getText().toString();
                removeAddNew();
                model.addList(text);
                items.add(text);
                setAddNewPresent(false);
                ((RecyclerView) container.getParent()).getAdapter().notifyDataSetChanged();
                editText.setText("");
                editingText="";
                Intent intent = new Intent(view.getContext(), ItemsActivity.class);
                intent.putExtra("com.carlolonghi.todo.TITLE", text);
                view.getContext().startActivity(intent);
            }
        }
    }

    //Used to remove the field that adds a new list
    public void removeAddNew(){
        if(isAddNewPresent){
            model.removeList("ADDNEW");
            items.remove("ADDNEW");
            setAddNewPresent(false);
            notifyDataSetChanged();
        }
    }

    //Function used to edit the text of the field used for the new lists
    public void setEditingText(String text){
        if(isAddNewPresent){
            EditText editText=(EditText) myLayoutManager.getChildAt(getItemCount()-1);
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }

    public void addList(String listTitle){
        this.items.add(listTitle);
    }

    public void setModel(ItemsViewModel model){
        this.model=model;
    }

    public void setMyLayoutManager(RecyclerView.LayoutManager layoutManager){
        this.myLayoutManager=layoutManager;
    }

    public void setContext(Context context){
        this.context=context;
    }
}
