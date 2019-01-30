package com.carlolonghi.todo;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

//This is the listener for the lists' buttons
public class ListButtonListener implements View.OnClickListener {
    public void onClick(View view){
        avoidDoubleClicks(view);
        Intent intent = new Intent(view.getContext(), ItemsActivity.class);
        String list = ((Button) view).getText().toString();
        intent.putExtra("com.carlolonghi.todo.TITLE", list);
        view.getContext().startActivity(intent);
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
