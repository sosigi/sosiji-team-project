package com.sausage.voca;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    TextView textView_title;
    Button EditBtn;
    CategoryViewHolder(View itemView){
        super(itemView);
        textView_title = itemView.findViewById(R.id.list_category_title_text);
        EditBtn = itemView.findViewById(R.id.list_category_title_edit_btn);
    }
}
