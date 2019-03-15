package com.carlolonghi.todo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carlolonghi.todo.data.ItemsViewModel;
import com.carlolonghi.todo.others.ListButtonListener;
import com.carlolonghi.todo.R;

import java.util.ArrayList;
import java.util.List;

public class BMListsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ItemsViewModel model;
    private Button contextMenuList; // this variable keeps the button that has a contextmenu attached
    private Context context;
    private List<String> items;


    private static final int NEWLIST_TYPE = 1;

    private static class ListViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final Button myListButton;

        private ListViewHolder(Button listButton) {
            super(listButton);
            myListButton = listButton;
            myListButton.setOnCreateContextMenuListener(this);
        }

        // Creates the context menu when the lists are long-pressed
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, v.getId(), 0, "Delete");
            menu.add(0, v.getId(), 0, "Remove Bookmark");
        }
    }

    public BMListsAdapter(List<String> items) {
        this.items=items;
    }

    public void setModel(ItemsViewModel model){
        this.model=model;
    }

    public void setContext(Context context){
        this.context=context;
    }

    @Override
    public int getItemViewType(int position) {
        return NEWLIST_TYPE;
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        Button newList = (Button) LayoutInflater.from(parent.getContext()).inflate(R.layout.bm_list_layout, parent, false);
        BMListsAdapter.ListViewHolder vh = new BMListsAdapter.ListViewHolder(newList);
        newList.setOnClickListener(new ListButtonListener());
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull  final RecyclerView.ViewHolder holder, int position) {
        String text = items.get(position);
        Button listButton = ((BMListsAdapter.ListViewHolder) holder).myListButton;
        listButton.setText(text);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setContextMenuList((Button) v);
                return false;
            }
        });
    }

    private void setContextMenuList(Button button) {
        this.contextMenuList = button;
    }

    public void deleteContextMenuList(){
        Button b=new Button(this.context);
        b.setText("");
        this.contextMenuList=b;
    }

    public Button getContextMenuList() {
        return this.contextMenuList;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addList(String listTitle){
        this.items.add(listTitle.toUpperCase());
    }

    public void removeList(String listTitle){
        this.items.remove(listTitle.toUpperCase());
    }
}
