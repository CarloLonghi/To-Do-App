package com.carlolonghi.todo;

import android.app.Fragment;
import android.app.ListFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

public class NonCheckedItemsFragment extends android.support.v4.app.ListFragment {

    private List<String> items;
    private MyViewModel model;
    private String listTitle;

    //The static method to instatiate a new Fragment
    public static NonCheckedItemsFragment newInstance(String listTitle){
        Bundle bundle = new Bundle();
        bundle.putString("listTitle", listTitle);
        NonCheckedItemsFragment fragment=new NonCheckedItemsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    //Private method to read the bundle passed when creating the fragment
    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            listTitle = bundle.getString("listTitle");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //Here set the divider color and height between two elements of the ListView
        ColorDrawable dividerColor = new ColorDrawable(this.getResources().getColor(R.color.listViewWhiteDivider,null));
        this.getListView().setDivider(dividerColor);
        this.getListView().setDividerHeight(50);

        readBundle(getArguments());

        //Get the ViewModel that provides the application data and read the list of non checked items for this fragment
        this.model = ViewModelProviders.of((MainActivity)getActivity()).get(MyViewModel.class);
        this.items=model.getItems().get(listTitle).getNonCheckedItems();

        //Creates and set this fragment's adapter
        NonCheckedItemsAdapter adapter=new NonCheckedItemsAdapter(this.getContext(),items);
        setListAdapter(adapter);
    }

    //The public method used to add a new item to the ListView
    public void addItem(String item){
        ((NonCheckedItemsAdapter)getListAdapter()).add(item);
        ((NonCheckedItemsAdapter) getListAdapter()).notifyDataSetChanged();
        updateFragmentHeight(this.getListView());
    }

    //The utility method to increase the fragment size when a new item is added to the ListView
    public boolean updateFragmentHeight(ListView listView){
        ListAdapter listAdapter=listView.getAdapter();
        if(listAdapter!=null){
            int numOfItems=listAdapter.getCount();

            int totalItemsHeight=0;
            for(int itemPos=0;itemPos<numOfItems;itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            int totalDividersHeight=listView.getDividerHeight()*(numOfItems-1);
            ViewGroup.LayoutParams params=listView.getLayoutParams();
            params.height=totalItemsHeight+totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;
        }
        else
            return false;
    }
}
