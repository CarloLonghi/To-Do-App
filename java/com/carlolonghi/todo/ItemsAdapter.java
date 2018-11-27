package com.carlolonghi.todo;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MyItemTouchHelper.ItemTouchHelperAdapter {

    public static final int CHECKEDITEM_TYPE=2;
    public static final int NEWITEM_TYPE=1;
    public static final int ADDNEW_TYPE=0;

    private Map<String,Items> items;
    private String listTitle;
    private String editingText;
    private MyViewModel model;

    // Provide a reference to the views for each data item
    public static class ItemsViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout myCheckBoxContainer;
        public ItemsViewHolder(LinearLayout checkBox) {
            super(checkBox);
            myCheckBoxContainer = checkBox;
        }
    }

    public static class AddNewItemViewHolder extends  RecyclerView.ViewHolder{
        public LinearLayout newItemLayout;
        public AddNewItemViewHolder(LinearLayout newItemLayout){
            super(newItemLayout);
            this.newItemLayout=newItemLayout;
        }
    }

    public ItemsAdapter(String listTitle, MyViewModel model) {
        this.model=model;
        this.items=model.loadItems();
        this.listTitle=listTitle;
        this.editingText="";
    }

    @Override
    public int getItemViewType(int position) {
        if(position==model.getItems().get(listTitle).getNonCheckedItems().size())
            return ADDNEW_TYPE;
        else if(position<model.getItems().get(listTitle).getNonCheckedItems().size())
            return NEWITEM_TYPE;
        else
            return CHECKEDITEM_TYPE;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout newItem;
        switch(viewType) {
            case ADDNEW_TYPE:
                final LinearLayout addNew=(LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_layout,parent,false);
                AddNewItemViewHolder vh2=new AddNewItemViewHolder(addNew);
                Button addNewButton=(Button)addNew.findViewById(R.id.addNewButton);
                addNewButton.setOnClickListener(new AddNewClickListener());

                //This block of instructions regulates the correct behaviour of the EditText used to add the new items
                //the text goes newline automatically when gets to the end of it and when the newline button on the keyboard is pressed
                //the item is added to the list as the Add button has been pressed
                EditText newItemText=(EditText)addNew.findViewById(R.id.addNewText);
                newItemText.setHorizontallyScrolling(false);
                newItemText.setMaxLines(Integer.MAX_VALUE);
                newItemText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                newItemText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        switch(actionId){
                            case EditorInfo.IME_ACTION_DONE:
                                //Calls the onClick of the Add button if the newLine is pressed
                                (new AddNewClickListener()).onClick(addNew.findViewById(R.id.addNewButton));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                return vh2;
            case CHECKEDITEM_TYPE:
            case NEWITEM_TYPE:
            default:
                newItem = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                ItemsViewHolder vh1 = new ItemsViewHolder(newItem);
                return vh1;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Get element from your dataset at this position
        //Replace the contents of the view with that element
        int itemType=getItemViewType(position);
        if(itemType==ADDNEW_TYPE){
            EditText editText=((EditText)((AddNewItemViewHolder)holder).newItemLayout.getChildAt(0));
            editText.setText(editingText);
            editText.setSelection(editingText.length());
        }
        else if(itemType==NEWITEM_TYPE){
            String text=model.getItems().get(listTitle).getNonCheckedItems().get(position);
            CheckBox checkBox=((CheckBox)((ItemsViewHolder)holder).myCheckBoxContainer.getChildAt(0));
            checkBox.setText(text);
            checkBox.setChecked(false);
            checkBox.setEnabled(true);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    String text=((CheckBox)v).getText().toString();
                    int position=model.getItems().get(listTitle).getNonCheckedItems().indexOf(text);
                    model.getItems().get(listTitle).getNonCheckedItems().remove(text);
                    RecyclerView.Adapter adapter=((RecyclerView)((LinearLayout)v.getParent()).getParent()).getAdapter();
                    adapter.notifyItemRemoved(position);
                    model.getItems().get(listTitle).addCheckedItem(text);
                    adapter.notifyItemInserted(getItemCount()-1);
                }
            });
        }
        else if(itemType==CHECKEDITEM_TYPE){
            int posOfNewItem=position-model.getItems().get(listTitle).getNonCheckedItems().size()-1;
            CheckBox checkBox=((CheckBox)((ItemsViewHolder)holder).myCheckBoxContainer.getChildAt(0));
            checkBox.setText(model.getItems().get(listTitle).getCheckedItems().get(posOfNewItem));
            checkBox.setChecked(true);
            checkBox.setEnabled(true);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckBox)v).setEnabled(false);
                    String text=((CheckBox)v).getText().toString();
                    int position=model.getItems().get(listTitle).getCheckedItems().indexOf(text)+model.getItems().get(listTitle).getNonCheckedItems().size()+1;
                    model.getItems().get(listTitle).getCheckedItems().remove(text);
                    RecyclerView.Adapter adapter=((RecyclerView)((LinearLayout)v.getParent()).getParent()).getAdapter();
                    adapter.notifyItemRemoved(position);
                    model.getItems().get(listTitle).addNonCheckedItem(text);
                    adapter.notifyItemInserted(model.getItems().get(listTitle).getNonCheckedItems().size()-1);
                }
            });
        }
    }

    @Override
    public void onItemDismiss(int position) {
        model.getItems().get(listTitle).remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        int nonCheckedSize=model.getItems().get(listTitle).getNonCheckedItems().size();
        if(fromPosition<nonCheckedSize){
            if(toPosition>nonCheckedSize-1){
                return true;
            }
            else {
                Collections.swap(model.getItems().get(listTitle).getNonCheckedItems(), fromPosition, toPosition);
                notifyItemMoved(fromPosition, toPosition);
                return true;
            }
        }
        else{
            if(toPosition<nonCheckedSize+1) {
                return true;
            }
            else{
                Collections.swap(model.getItems().get(listTitle).getCheckedItems(), fromPosition - nonCheckedSize - 1, toPosition - nonCheckedSize - 1);
                notifyItemMoved(fromPosition, toPosition);
                return true;
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        LinkedHashMap<String,Items> items=model.getItems();
        Items tmp=model.getItems().get(listTitle);
        return tmp.getTotalSize()+1;
    }

    public void setEditingText(String editingText){
        this.editingText=editingText;
    }

    public class AddNewClickListener implements View.OnClickListener{
        public void onClick(View view){
            LinearLayout container=(LinearLayout)view.getParent();
            EditText editText=(EditText)container.getChildAt(0);
            if(editText.getText().toString().equals("")){
                Context context = view.getContext().getApplicationContext();
                CharSequence text = "You can't add an empty item";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            else if(model.getItems().get(listTitle).getNonCheckedItems().contains(editText.getText().toString())){
                Context context = view.getContext().getApplicationContext();
                CharSequence text = "Item already exists";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            else {
                model.getItems().get(listTitle).addNonCheckedItem(editText.getText().toString());
                ((RecyclerView) container.getParent()).getAdapter().notifyItemInserted(model.getItems().get(listTitle).getNonCheckedItems().size());
                editText.setText("");
                editingText="";
            }
            editText.requestFocus();
            Activity activity = (Activity) view.getContext();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
