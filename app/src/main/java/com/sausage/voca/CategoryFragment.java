package com.sausage.voca;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CategoryFragment extends Fragment {

    TextView wordbook1, wordbook2, wordbook3;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category, null);
        wordbook1 = v.findViewById(R.id.wordbook1);
        wordbook2 = v.findViewById(R.id.wordbook2);
        wordbook3 = v.findViewById(R.id.wordbook3);

        wordbook1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                Intent intent = new Intent(getActivity(), wordbook.class);
                //TODO v.getContext()를 하나 getActivity()를 하나 둘 다 멀정하게 나옴. 뭐가 다를까?
                startActivity(intent);
            }
        });

        return v;
    }

}