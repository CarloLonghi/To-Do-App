package com.carlolonghi.todo;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class addNewListListener implements OnClickListener {
    @Override
    public void onClick(View view){
        Intent intent=new Intent(view.getContext(),MainActivity.class);
        String list=((Button)view).getText().toString();
        intent.putExtra("com.carlolonghi.todo.TITLE",list);
        view.getContext().startActivity(intent);
    }
}
