package com.sausage.voca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryTitleAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private ArrayList<CategoryTitle> titlesDataList = null;

    CategoryTitleAdapter(ArrayList<CategoryTitle> dataList) {
        titlesDataList = dataList;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //전개자(Inflater)를 통해 얻은 참조 객체를 통해 뷰홀더 객체 생성
        View view = inflater.inflate(R.layout.list_category_title_layout, parent, false);
        CategoryViewHolder categoryViewHolder = new CategoryViewHolder(view);

        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder categoryViewHolder, int position) {
        //ViewHolder가 관리하는 View에 position에 해당하는 데이터 바인딩
        categoryViewHolder.textView_title.setText(titlesDataList.get(position).getTitle());
        //titlesDataList.get(position).getID();

    }

    @Override
    public int getItemCount() {
        //Adapter가 관리하는 전체 데이터 개수 반환
        return titlesDataList.size();
    }
}
