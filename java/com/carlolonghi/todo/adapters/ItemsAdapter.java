package com.carlolonghi.todo.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.carlolonghi.todo.data.Items;
import com.carlolonghi.todo.others.MyItemTouchHelper;
import com.carlolonghi.todo.data.ItemsViewModel;
import com.carlolonghi.todo.R;

import java.util.Collections;

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MyItemTouchHelper.ItemTouchHelperAdapter {

    private static final int CHECKEDITEM_TYPE=2;
    private static final int NEWITEM_TYPE=1;
    private static final int ADDNEW_TYPE=0;

    private final Items items;
    private final String listTitle;
    private String editingText;
    private final ItemsViewModel model;

    // Provide a reference to the views for each data item
    public static class ItemsViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout myCheckBoxContainer;
        public ItemsViewHolder(LinearLayout checkBox) {
            super(checkBox);
            myCheckBoxContainer = checkBox;
        }
    }

    public static class AddNewItemViewHolder extends  RecyclerView.ViewHolder{
        public final LinearLayout newItemLayout;
        public AddNewItemViewHolder(LinearLayout newItemLayout){
            super(newItemLayout);
            this.newItemLayout=newItemLayout;
        }
    }

    public ItemsAdapter(String listTitle, ItemsViewModel model) {
        this.model=model;
        this.listTitle=listTitle;
        this.editingText="";

        if(model.isBookmark(listTitle))
            this.items=model.getBookmarkItems().get(listTitle);
        else
            this.items=model.getItems().get(listTitle);
    }

    public Items getItems(){
        return items;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==items.getNonCheckedItems().size())
            return ADDNEW_TYPE;
        else if(position<items.getNonCheckedItems().size())
            return NEWITEM_TYPE;
        else
            return CHECKEDITEM_TYPE;
    }

    // Create new views (invoked by the layout manager)
    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout newItem;
        switch(viewType) {
            case ADDNEW_TYPE:
                final LinearLayout addNew=(LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_reverse_layout,parent,false);
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Get element from your dataset at this position
        //Replace the contents of the view with that element
        int itemType=getItemViewType(position);
        if(itemType==ADDNEW_TYPE){
            EditText editText=((EditText)((AddNewItemViewHolder)holder).newItemLayout.getChildAt(0));
            editText.setText(editingText);
            editText.setSelection(editingText.length());
        }
        else if(itemType==NEWITEM_TYPE){
            String text=items.getNonCheckedItems().get(position);
            CheckBox checkBox=((CheckBox)((ItemsViewHolder)holder).myCheckBoxContainer.getChildAt(0));
            checkBox.setText(text);
            checkBox.setChecked(false);
            checkBox.setEnabled(true);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    String text=((CheckBox)v).getText().toString();
                    int position=items.getNonCheckedItems().indexOf(text);
                    items.getNonCheckedItems().remove(text);
                    RecyclerView.Adapter adapter=((RecyclerView)((LinearLayout)v.getParent()).getParent()).getAdapter();
                    adapter.notifyItemRemoved(position);
                    items.addCheckedItem(text);
                    adapter.notifyItemInserted(getItemCount()-1);
                }
            });
        }
        else if(itemType==CHECKEDITEM_TYPE){
            int posOfNewItem=position-items.getNonCheckedItems().size()-1;
            CheckBox checkBox=((CheckBox)((ItemsViewHolder)holder).myCheckBoxContainer.getChildAt(0));
            checkBox.setText(items.getCheckedItems().get(posOfNewItem));
            checkBox.setChecked(true);
            checkBox.setEnabled(true);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckBox)v).setEnabled(false);
                    String text=((CheckBox)v).getText().toString();
                    int position=items.getCheckedItems().indexOf(text)+items.getNonCheckedItems().size()+1;
                    items.getCheckedItems().remove(text);
                    RecyclerView.Adapter adapter=((RecyclerView)((LinearLayout)v.getParent()).getParent()).getAdapter();
                    adapter.notifyItemRemoved(position);
                    items.addNonCheckedItem(text);
                    adapter.notifyItemInserted(items.getNonCheckedItems().size()-1);
                }
            });
        }
    }

    @Override
    public void onItemDismiss(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        int nonCheckedSize=items.getNonCheckedItems().size();
        if(fromPosition<nonCheckedSize){
            if(toPosition>nonCheckedSize-1){
                return true;
            }
            else {
                Collections.swap(items.getNonCheckedItems(), fromPosition, toPosition);
                notifyItemMoved(fromPosition, toPosition);
                return true;
            }
        }
        else{
            if(toPosition<nonCheckedSize+1) {
                return true;
            }
            else{
                Collections.swap(items.getCheckedItems(), fromPosition - nonCheckedSize - 1, toPosition - nonCheckedSize - 1);
                notifyItemMoved(fromPosition, toPosition);
                return true;
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Items tmp=items;
        return tmp.getTotalSize()+1;
    }

    public void setEditingText(String editingText){
        this.editingText=editingText;
    }

    private class AddNewClickListener implements View.OnClickListener{
        public void onClick(View view){
            LinearLayout container=(LinearLayout)view.getParent();
            EditText editText=(EditText)container.getChildAt(0);
            Context context = view.getContext().getApplicationContext();
            if(editText.getText().toString().equals("")){
                CharSequence text = context.getResources().getString(R.string.emptyItemError);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            else if(items.getNonCheckedItems().contains(editText.getText().toString())){
                CharSequence text = context.getResources().getString(R.string.duplicateItemError);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            else {
                items.addNonCheckedItem(editText.getText().toString());
                ((RecyclerView) container.getParent()).getAdapter().notifyItemInserted(items.getNonCheckedItems().size());
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
