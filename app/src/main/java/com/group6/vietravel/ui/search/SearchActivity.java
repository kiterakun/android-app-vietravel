package com.group6.vietravel.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.group6.vietravel.R;
import com.group6.vietravel.adapters.PlaceAdapter;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.ui.detail.DetailActivity;
import com.group6.vietravel.ui.main.MainActivity;
import com.group6.vietravel.ui.main.MainViewModel;
import com.group6.vietravel.ui.main.dialog.FilterBottomSheetFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchViewModel searchViewModel;
    private MainViewModel mainViewModel;
    private PlaceAdapter adapter;
    private RecyclerView rv_search_results;
    private ImageButton btn_filter;
    private ImageButton btn_back;
    private EditText edt_search;
    private MaterialButton btn_search;
    private TextView tv_result_count;
    private  String name ,category, province, district = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adapter = new PlaceAdapter(this,new ArrayList<>());
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        rv_search_results = findViewById(R.id.rv_search_results);
        btn_filter = findViewById(R.id.btn_filter);
        btn_back = findViewById(R.id.btn_back);
        edt_search = findViewById(R.id.edt_search);
        btn_search = findViewById(R.id.btn_search);
        tv_result_count = findViewById(R.id.tv_result_count);

        rv_search_results.setAdapter(adapter);
        rv_search_results.setLayoutManager(new LinearLayoutManager(this));
        searchViewModel.getListSearch().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(List<Place> places) {
                adapter.updateData(places);
                tv_result_count.setText("Tìm thấy " + String.valueOf(places.size()) + " địa điểm");
            }
        });

        btn_filter.setOnClickListener(v -> {
            FilterBottomSheetFragment filterPopup = new FilterBottomSheetFragment();
            filterPopup.setOnFilterAppliedListener(new FilterBottomSheetFragment.OnFilterAppliedListener() {
                @Override
                public void onFilterApplied(String cate, String prov, String dist) {
                    Log.d("FilterResult", "Category: " + category + ", Place: " + province + " - " + district);
                    category = cate;
                    province = prov;
                    district = dist;
                }
            });
            filterPopup.show(getSupportFragmentManager(), "FilterBottomSheet");
        });

        btn_search.setOnClickListener(v->{
            name = edt_search.getText().toString().trim();
            searchViewModel.setListSearch(name,category,province,district);
        });

        adapter.setOnItemClickListener(new PlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Place place) {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);

                intent.putExtra("PLACE_OBJECT",place);
                startActivity(intent);
            }
        });

        btn_back.setOnClickListener(v->{
            finish();
        });

    }
}