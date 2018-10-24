package com.carlolonghi.todo;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class MyItemTouchHelper extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter myAdapter;

    public MyItemTouchHelper(ItemTouchHelperAdapter adapter) {
        myAdapter = adapter;
    }

   /* @Override
    public int getMovementFlags(RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder) {
        int dragFlagsNormal = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int dragFlagsLast=ItemTouchHelper.UP;
        int dragFlagsFirst=ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        int numOfNonChecked=((ItemsAdapter)recyclerView.getAdapter()).getNonCheckedCount();
        if(viewHolder.getAdapterPosition()==numOfNonChecked-1)
            return makeMovementFlags(dragFlagsLast,swipeFlags);
        else if(viewHolder.getAdapterPosition()==numOfNonChecked+1)
            return makeMovementFlags(dragFlagsFirst,swipeFlags);
        else
            return makeMovementFlags(dragFlagsNormal, swipeFlags);
    }*/

    @Override
    public int getMovementFlags(RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder) {
        int dragFlagsNormal = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        if(viewHolder instanceof ItemsAdapter.AddNewItemViewHolder)
            return 0;
        return makeMovementFlags(dragFlagsNormal, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public interface ItemTouchHelperAdapter {

        boolean onItemMove(int fromPosition, int toPosition);

        void onItemDismiss(int position);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        myAdapter.onItemMove(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        myAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

}