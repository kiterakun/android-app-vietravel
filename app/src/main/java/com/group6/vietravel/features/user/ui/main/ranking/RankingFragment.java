package com.group6.vietravel.features.user.ui.main.ranking;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;
import com.group6.vietravel.R;
import com.group6.vietravel.features.user.ui.adapters.PlaceAdapter;
import com.group6.vietravel.features.user.ui.adapters.RankingAdapter;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.features.ui.auth.AuthViewModel;
import com.group6.vietravel.features.user.ui.main.MainViewModel;
import com.group6.vietravel.core.utils.ProvinceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RankingFragment extends Fragment {

    private RankingViewModel rankingViewModel;
    private MainViewModel mainViewModel;
    private RankingAdapter adapter;
    private TextInputLayout til_district;
    private RecyclerView recycler_view_ranking;
    private AutoCompleteTextView filter_province,filter_district,filter_category;
    private Map<String, List<String>> provinceData = new HashMap<>();
    private List<Pair<String,String>> listPairCategory;
    private String province,district,category;
    public static RankingFragment newInstance() {
        return new RankingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rankingViewModel = new ViewModelProvider(this).get(RankingViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        til_district = view.findViewById(R.id.til_district);
        filter_province = view.findViewById(R.id.filter_province);
        filter_district = view.findViewById(R.id.filter_district);
        filter_category = view.findViewById(R.id.filter_category);

        listPairCategory = Arrays.asList(
                new Pair<>("all", "Tất cả"),
                new Pair<>("entertainment", "Vui chơi giải trí"),
                new Pair<>("food", "Ẩm thực"),
                new Pair<>("historical", "Di tích lịch sử"),
                new Pair<>("nature", "Thiên nhiên"),
                new Pair<>("religious", "Tôn giáo"),
                new Pair<>("shopping", "Mua sắm")
        );

        provinceData = ProvinceUtils.getProvinceDistrictMap(getContext());

        List<String> provinces = new ArrayList<>(provinceData.keySet());
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, provinces);
        filter_province.setAdapter(provinceAdapter);

        filter_province.setOnItemClickListener((parent, view1, position, id) -> {
            loadListPlace();
            String selectedProvince = provinceAdapter.getItem(position);

            til_district.setEnabled(true);

            filter_district.setText("");

            List<String> districts = provinceData.get(selectedProvince);

            if (districts != null) {
                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, districts);
                filter_district.setAdapter(districtAdapter);
            }
        });

        filter_district.setOnItemClickListener((parent, view1, position, id) -> {
            loadListPlace();
        });

        List<String> listCategory = listPairCategory.stream().map(p -> p.second).collect(Collectors.toList());
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, listCategory);
        filter_category.setAdapter(categoryAdapter);

        filter_category.setOnItemClickListener((parent, view1, position, id) -> {
            loadListPlace();
        });

        recycler_view_ranking = view.findViewById(R.id.recycler_view_ranking);
        adapter = new RankingAdapter(getContext(),new ArrayList<>());
        recycler_view_ranking.setAdapter(adapter);
        recycler_view_ranking.setLayoutManager(new LinearLayoutManager(getContext()));

        rankingViewModel.getRankingPlaces().observe(getViewLifecycleOwner(), new Observer<List<Place>>() {
            @Override
            public void onChanged(List<Place> places) {
                adapter.updateData(places);
            }
        });

        adapter.setOnItemClickListener(new RankingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Place place) {
                mainViewModel.selectPlace(place);
            }
        });
    }
    private String findIdCategory(String name){
        String result = null;

        for (Pair<String, String> p : listPairCategory) {
            if (p.second.equals(name)) {
                return result = p.first;
            }
        }
        return "";
    }

    private void loadListPlace(){
        String selectedProvince = filter_province.getText().toString();
        String selectedDistrict = filter_district.getText().toString();
        String selectedCategory = filter_category.getText().toString();

        List<String> prov_dist = ProvinceUtils.getCodesByName(getContext(), selectedProvince, selectedDistrict);

        province = prov_dist.get(0);
        district = prov_dist.get(1);
        category = findIdCategory(selectedCategory);

        rankingViewModel.loadRanking(province,district,category,7);
    }

}