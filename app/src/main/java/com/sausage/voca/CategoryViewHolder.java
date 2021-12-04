package com.sausage.voca;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    TextView textView_title;
    CategoryViewHolder(View itemView){
        super(itemView);
        textView_title = itemView.findViewById(R.id.list_category_title_text);
    }
}
