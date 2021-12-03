package com.sausage.voca;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView englishWord_holder;
    TextView wordMean1_holder;
    TextView wordMean2_holder;
    TextView wordMean3_holder;
    ImageView memorizeCheck_holder;


    ViewHolder(View itemView) {
        super(itemView);

        englishWord_holder = itemView.findViewById(R.id.list_english_word);
        memorizeCheck_holder = itemView.findViewById(R.id.list_memorization_check);
        wordMean1_holder = itemView.findViewById(R.id.list_word_mean1);
        wordMean2_holder = itemView.findViewById(R.id.list_word_mean2);
        wordMean3_holder = itemView.findViewById(R.id.list_word_mean3);
    }
}

