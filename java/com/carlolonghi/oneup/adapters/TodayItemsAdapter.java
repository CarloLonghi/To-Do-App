package com.carlolonghi.oneup.adapters;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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

import com.carlolonghi.oneup.others.MyItemTouchHelper;
import com.carlolonghi.oneup.data.ItemsViewModel;
import com.carlolonghi.oneup.R;
import com.carlolonghi.oneup.data.ItemWithDate;
import com.carlolonghi.oneup.data.TodayItems;
import com.carlolonghi.oneup.widget.ToDoWidgetProvider;

import java.util.Calendar;
import java.util.Collections;

public class TodayItemsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MyItemTouchHelper.ItemTouchHelperAdapter {

    private static final int CHECKEDITEM_TYPE=2;
    private static final int NEWITEM_TYPE=1;
    private static final int ADDNEW_TYPE=0;

    private final TodayItems items;
    private String editingText;
    private final Context context;

    // The ViewHolder for the items
    private static class ItemsViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout myCheckBoxContainer;
        private ItemsViewHolder(LinearLayout checkBox) {
            super(checkBox);
            myCheckBoxContainer = checkBox;
        }
    }

    // The ViewHolder for edittext and button to add new items
    private static class AddNewItemViewHolder extends  RecyclerView.ViewHolder{
        private final LinearLayout newItemLayout;
        private AddNewItemViewHolder(LinearLayout newItemLayout){
            super(newItemLayout);
            this.newItemLayout=newItemLayout;
        }
    }

    public TodayItemsAdapter(ItemsViewModel model,Context context){
        this.editingText="";
        this.context=context;

        this.items=model.getTodaysItems();
    }

    public TodayItems getItems(){
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

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout newItem;
        switch(viewType) {
            case ADDNEW_TYPE:
                final LinearLayout addNew=(LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_reverse_layout,parent,false);
                TodayItemsAdapter.AddNewItemViewHolder vh2=new TodayItemsAdapter.AddNewItemViewHolder(addNew);
                Button addNewButton=(Button)addNew.findViewById(R.id.reverseAddNewButton);
                addNewButton.setOnClickListener(new TodayItemsAdapter.AddNewClickListener());

                // This block of instructions regulates the correct behaviour of the EditText used to add the new items
                // the text goes newline automatically when gets to the end of it and when the newline button on the keyboard is pressed
                // the item is added to the list as the Add button has been pressed
                EditText newItemText=(EditText)addNew.findViewById(R.id.reverseAddNewText);
                newItemText.setHorizontallyScrolling(false);
                newItemText.setMaxLines(Integer.MAX_VALUE);
                newItemText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                newItemText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        switch(actionId){
                            case EditorInfo.IME_ACTION_DONE:
                                (new TodayItemsAdapter.AddNewClickListener()).onClick(addNew.findViewById(R.id.reverseAddNewButton));
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
                TodayItemsAdapter.ItemsViewHolder vh1 = new TodayItemsAdapter.ItemsViewHolder(newItem);
                return vh1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemType=getItemViewType(position);
        if(itemType==ADDNEW_TYPE){
            EditText editText=((EditText)((TodayItemsAdapter.AddNewItemViewHolder)holder).newItemLayout.getChildAt(0));
            editText.setText(editingText);
        }
        else if(itemType==NEWITEM_TYPE){
            final ItemWithDate item=items.getNonCheckedItems().get(position);
            String text=item.getName();
            CheckBox checkBox=((CheckBox)((TodayItemsAdapter.ItemsViewHolder)holder).myCheckBoxContainer.getChildAt(0));
            checkBox.setText(text);
            checkBox.setChecked(false);
            checkBox.setEnabled(true);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    String text=((CheckBox)v).getText().toString();
                    int position=items.getNonCheckedItems().indexOf(item);
                    items.getNonCheckedItems().remove(item);
                    RecyclerView.Adapter adapter=((RecyclerView)((LinearLayout)v.getParent()).getParent()).getAdapter();
                    adapter.notifyItemRemoved(position);
                    items.addCheckedItem(new ItemWithDate(text,item.getDay(),item.getYear()));
                    adapter.notifyItemInserted(getItemCount()-1);
                    updateWidgets();
                }
            });
        }
        else if(itemType==CHECKEDITEM_TYPE){
            int posOfNewItem=position-items.getNonCheckedItems().size()-1;
            final ItemWithDate item=items.getCheckedItems().get(posOfNewItem);
            CheckBox checkBox=((CheckBox)((TodayItemsAdapter.ItemsViewHolder)holder).myCheckBoxContainer.getChildAt(0));
            checkBox.setText(items.getCheckedItems().get(posOfNewItem).getName());
            checkBox.setChecked(true);
            checkBox.setEnabled(true);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckBox)v).setEnabled(false);
                    String text=((CheckBox)v).getText().toString();
                    int position=items.getCheckedItems().indexOf(item)+items.getNonCheckedItems().size()+1;
                    items.getCheckedItems().remove(item);
                    RecyclerView.Adapter adapter=((RecyclerView)((LinearLayout)v.getParent()).getParent()).getAdapter();
                    adapter.notifyItemRemoved(position);
                    items.addNonCheckedItem(new ItemWithDate(text,item.getDay(),item.getYear()));
                    adapter.notifyItemInserted(items.getNonCheckedItems().size()-1);
                    updateWidgets();
                }
            });
        }
    }

    @Override
    public void onItemDismiss(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        updateWidgets();
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

    @Override
    public int getItemCount() {
        return items.getTotalSize()+1;
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
            else if(items.contains(editText.getText().toString())){
                CharSequence text = context.getResources().getString(R.string.duplicateItemError);
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
            else {
                Calendar calendar=Calendar.getInstance();
                items.addNonCheckedItem(new ItemWithDate(editText.getText().toString(),calendar.get(Calendar.DAY_OF_YEAR),calendar.get(Calendar.YEAR)));
                ((RecyclerView) container.getParent()).getAdapter().notifyItemInserted(items.getNonCheckedItems().size());
                editText.setText("");
                editingText="";
                updateWidgets();
            }
            editText.requestFocus();
            Activity activity = (Activity) view.getContext();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void updateWidgets(){
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, ToDoWidgetProvider.class);
        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        widgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.widgetListView);
    }
}

