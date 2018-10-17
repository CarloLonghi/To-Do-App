package com.carlolonghi.todo;

import android.app.Fragment;
import android.app.ListFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Canvas;
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

    public static NonCheckedItemsFragment newInstance(String listTitle){
        Bundle bundle = new Bundle();
        bundle.putString("listTitle", listTitle);
        NonCheckedItemsFragment fragment=new NonCheckedItemsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            listTitle = bundle.getString("listTitle");
        }
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.noncheckeditems_fragment,container,false);
        readBundle(getArguments());
        return view;
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        readBundle(getArguments());
        this.model = ViewModelProviders.of((MainActivity)getActivity()).get(MyViewModel.class);
        this.items=model.getItems().get(listTitle).getNonCheckedItems();
        NonCheckedItemsAdapter adapter=new NonCheckedItemsAdapter(this.getContext(),items);
        setListAdapter(adapter);
    }

    public void addItem(String item){
        ((NonCheckedItemsAdapter)getListAdapter()).add(item);
        ((NonCheckedItemsAdapter) getListAdapter()).notifyDataSetChanged();
        updateFragmentHeight(this.getListView());
    }

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
