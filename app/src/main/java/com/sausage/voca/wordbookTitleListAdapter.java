package com.sausage.voca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//class ViewHolder(String string);

//public class wordbookTitleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    private ArrayList<WordbookTitle> myTitleDataList = null;
//    wordbookTitleListAdapter(ArrayList<WordbookTitle> dataList){
//        myTitleDataList = dataList;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
//        Context context = parent.getContext();
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        //전개자(Inflater)를 통해 얻은 참조 객체를 통해 뷰홀더 객체 생성
//        View view = inflater.inflate(R.layout.wordbook_title_list_layout, parent, false);
//        RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(view);
//
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
//    {
//        //ViewHolder가 관리하는 View에 position에 해당하는 데이터 바인딩
//        viewHolder.title.setText(myTitleDataList.get(position).getWordbookTitle());
//    }
//
//    @Override
//    public int getItemCount()
//    {
//        //Adapter가 관리하는 전체 데이터 개수 반환
//        return myTitleDataList.size();
//    }
//}
public class wordbookTitleListAdapter extends RecyclerView.Adapter<Holder> {
    ArrayList<String> list;

    wordbookTitleListAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.wordbook_title_list_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tv.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class Holder extends RecyclerView.ViewHolder {
    TextView tv;

    public Holder(@NonNull View itemView) {
        super(itemView);
        tv = itemView.findViewById(R.id.wordbook_title);
    }
}


