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
import java.util.Map;

public class ItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MyItemTouchHelper.ItemTouchHelperAdapter {

    public static final int CHECKEDITEM_TYPE=2;
    public static final int NEWITEM_TYPE=1;
    public static final int ADDNEW_TYPE=0;

    private Map<String,Items> items;
    private String listTitle;

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

    /*public static class LastItemViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout myCheckBoxContainer;
        public LastItemViewHolder(LinearLayout checkBox) {
            super(checkBox);
            myCheckBoxContainer = checkBox;
        }
    }

    public static class FirstCheckedItemsViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout myCheckBoxContainer;
        public FirstCheckedItemsViewHolder(LinearLayout checkBox) {
            super(checkBox);
            myCheckBoxContainer = checkBox;
        }
    }*/

    public ItemsAdapter(Map<String,Items> items, String listTitle) {
        this.items=items;
        this.listTitle=listTitle;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==items.get(listTitle).getNonCheckedItems().size())
            return ADDNEW_TYPE;
        else if(position<items.get(listTitle).getNonCheckedItems().size())
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
                EditText addNewText=(EditText)addNew.findViewById(R.id.addNewText);
                addNewText.setTag("addNewText");
                Button addNewButton=(Button)addNew.findViewById(R.id.addNewButton);
                addNewButton.setOnClickListener(new addNewClickListener());

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
                                (new addNewClickListener()).onClick(addNew.findViewById(R.id.addNewButton));
                                //onClick(findViewById(R.id.addNewButton));
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        int itemType=getItemViewType(position);
        if(itemType==ADDNEW_TYPE){
            //COMPLETE WITH CODE HERE
        }
        else if(itemType==NEWITEM_TYPE){
            String text=items.get(listTitle).getNonCheckedItems().get(position);
            CheckBox checkBox=((CheckBox)((ItemsViewHolder)holder).myCheckBoxContainer.getChildAt(0));
            checkBox.setText(text);
            checkBox.setChecked(false);
            checkBox.setEnabled(true);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    String text=((CheckBox)v).getText().toString();
                    int position=items.get(listTitle).getNonCheckedItems().indexOf(text);
                    items.get(listTitle).getNonCheckedItems().remove(text);
                    RecyclerView.Adapter adapter=((RecyclerView)((LinearLayout)v.getParent()).getParent()).getAdapter();
                    adapter.notifyItemRemoved(position);
                    items.get(listTitle).getCheckedItems().add(text);
                    adapter.notifyItemInserted(getItemCount()-1);
                }
            });
        }
        else if(itemType==CHECKEDITEM_TYPE){
            int posOfNewItem=position-items.get(listTitle).getNonCheckedItems().size()-1;
            CheckBox checkBox=((CheckBox)((ItemsViewHolder)holder).myCheckBoxContainer.getChildAt(0));
            checkBox.setText(items.get(listTitle).getCheckedItems().get(posOfNewItem));
            checkBox.setChecked(true);
            checkBox.setEnabled(true);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckBox)v).setEnabled(false);
                    String text=((CheckBox)v).getText().toString();
                    int position=items.get(listTitle).getCheckedItems().indexOf(text)+items.get(listTitle).getNonCheckedItems().size()+1;
                    items.get(listTitle).getCheckedItems().remove(text);
                    RecyclerView.Adapter adapter=((RecyclerView)((LinearLayout)v.getParent()).getParent()).getAdapter();
                    adapter.notifyItemRemoved(position);
                    items.get(listTitle).getNonCheckedItems().add(text);
                    adapter.notifyItemInserted(items.get(listTitle).getNonCheckedItems().size()-1);
                }
            });
        }
    }

    @Override
    public void onItemDismiss(int position) {
        items.get(listTitle).remove(position);
        notifyItemRemoved(position);
    }

    /*@Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition<items.get(listTitle).getNonCheckedItems().size()){
            if (fromPosition < toPosition) {
                boolean overBound=false;
                for (int i = fromPosition; i < toPosition; i++) {
                    if(i<(items.get(listTitle).getNonCheckedItems().size()-1))
                        Collections.swap(items.get(listTitle).getNonCheckedItems(), i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(items.get(listTitle).getNonCheckedItems(), i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
        else{
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(items.get(listTitle).getCheckedItems(), i-1, i );
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    if(i>items.get(listTitle).getNonCheckedItems().size()+1)
                        Collections.swap(items.get(listTitle).getCheckedItems(), i-1, i - 2);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
    }*/

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        int nonCheckedSize=items.get(listTitle).getNonCheckedItems().size();
        if(fromPosition<nonCheckedSize){
            if(toPosition>nonCheckedSize-1){
                int dragFlagsLast= ItemTouchHelper.UP;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                //MyItemTouchHelper.makeMovementFlags(dragFlagsLast,swipeFlags);
                return true;
            }
            else {
                int dragFlagsNormal = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                //MyItemTouchHelper.makeMovementFlags(dragFlagsNormal,swipeFlags);
                Collections.swap(items.get(listTitle).getNonCheckedItems(), fromPosition, toPosition);
                notifyItemMoved(fromPosition, toPosition);
                return true;
            }
        }
        else{
            if(toPosition<nonCheckedSize+1) {
                int dragFlagsFirst=ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                //MyItemTouchHelper.makeMovementFlags(dragFlagsFirst,swipeFlags);
                return true;
            }
            else{
                int dragFlagsNormal = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                //MyItemTouchHelper.makeMovementFlags(dragFlagsNormal,swipeFlags);
                Collections.swap(items.get(listTitle).getCheckedItems(), fromPosition - nonCheckedSize - 1, toPosition - nonCheckedSize - 1);
                notifyItemMoved(fromPosition, toPosition);
                return true;
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.get(listTitle).getTotalSize()+1;
    }

    public int getNonCheckedCount(){
        return items.get(listTitle).getNonCheckedItems().size();
    }

    public class addNewClickListener implements View.OnClickListener{
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
            else if(items.get(listTitle).getNonCheckedItems().contains(editText.getText().toString())){
                Context context = view.getContext().getApplicationContext();
                CharSequence text = "Item already exists";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            else {
                items.get(listTitle).getNonCheckedItems().add(editText.getText().toString());
                ((RecyclerView) container.getParent()).getAdapter().notifyItemInserted(items.get(listTitle).getNonCheckedItems().size());
                editText.setText("");
            }
            editText.requestFocus();
            Activity activity = (Activity) view.getContext();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item=inflater.inflate(R.layout.item,parent,false);
        CheckBox checkBox=(CheckBox) item.findViewById(R.id.itemCheckbox);
        checkBox.setText(items.get(position));
        //Add the listener for the checkbox;

        return item;
    }

    @Override
    public void add(String item){
        this.items.add(item);
    }*/
}