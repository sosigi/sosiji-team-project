package com.sausage.voca;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WordAdapter extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<Word> myDataList = null;

    WordAdapter(ArrayList<Word> dataList) {
        myDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /*전개자(Inflater)를 통해 얻은 참조 객체를 통해 뷰홀더 객체 생성*/
        View view = inflater.inflate(R.layout.list_wordcard_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        /* ViewHolder가 관리하는 View에 position에 해당하는 데이터 바인딩 */
        viewHolder.englishWord_holder.setText(myDataList.get(position).getEnglishWord());
        viewHolder.wordMean1_holder.setText("1. "+myDataList.get(position).getWordMean1());
        if(myDataList.get(position).getWordMean2()!=""){
            viewHolder.wordMean2_holder.setText("2. "+myDataList.get(position).getWordMean2());
        }else{
            viewHolder.wordMean2_holder.setText("");
        }
        if(myDataList.get(position).getWordMean3()!=""){
            viewHolder.wordMean3_holder.setText("3. "+myDataList.get(position).getWordMean3());
        }else{
            viewHolder.wordMean3_holder.setText("");
        }
        if(myDataList.get(position).getMemorization()==0){
            //holder에서 src선정.
            viewHolder.englishWord_holder.setTextColor(Color.BLACK);
            viewHolder.wordMean1_holder.setTextColor(Color.BLACK);
            viewHolder.wordMean2_holder.setTextColor(Color.BLACK);
            viewHolder.wordMean3_holder.setTextColor(Color.BLACK);
        }else{
            viewHolder.englishWord_holder.setTextColor(Color.LTGRAY);
            viewHolder.wordMean1_holder.setTextColor(Color.LTGRAY);
            viewHolder.wordMean2_holder.setTextColor(Color.LTGRAY);
            viewHolder.wordMean3_holder.setTextColor(Color.LTGRAY);
        }
    }

    @Override
    public int getItemCount() {
        //Adapter가 관리하는 전체 데이터 개수 반환
        return myDataList.size();
    }

}