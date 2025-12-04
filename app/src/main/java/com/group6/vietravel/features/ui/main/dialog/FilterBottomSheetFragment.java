package com.group6.vietravel.ui.main.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.group6.vietravel.R;
import com.group6.vietravel.core.utils.ProvinceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {
    TextInputLayout tilDistrict;
    AutoCompleteTextView actvProvince, actvDistrict;
    Button btnApply;
    private OnFilterAppliedListener mListener;
    private ChipGroup chipGroup;
    Map<String, List<String>> provinceData = new HashMap<>();

    public interface OnFilterAppliedListener {
        void onFilterApplied(String category, String province, String district);
    }

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_filter_bottom_sheet, container, false);

        tilDistrict = view.findViewById(R.id.til_district);
        actvProvince = view.findViewById(R.id.actv_province);
        actvDistrict = view.findViewById(R.id.actv_district);
        btnApply = view.findViewById(R.id.btn_apply_filter);
        chipGroup = view.findViewById(R.id.chip_group_category);

        setupDummyData();

        List<String> provinces = new ArrayList<>(provinceData.keySet());
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, provinces);
        actvProvince.setAdapter(provinceAdapter);

        actvProvince.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedProvince = provinceAdapter.getItem(position);

            tilDistrict.setEnabled(true);

            actvDistrict.setText("");

            List<String> districts = provinceData.get(selectedProvince);

            if (districts != null) {
                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, districts);
                actvDistrict.setAdapter(districtAdapter);
            }
        });

        btnApply.setOnClickListener(v -> {
            String selectedCategory = null;
            int selectedChipId = chipGroup.getCheckedChipId();

            if (selectedChipId != View.NO_ID) {
                Chip selectedChip = view.findViewById(selectedChipId);
                selectedCategory = selectedChip.getContentDescription().toString();
            }

            String selectedProvince = actvProvince.getText().toString();

            String selectedDistrict = actvDistrict.getText().toString();

            List<String> prov_dist = ProvinceUtils.getCodesByName(getContext(), selectedProvince, selectedDistrict);

            if (mListener != null) {
                mListener.onFilterApplied(selectedCategory, prov_dist.get(0), prov_dist.get(1));
            }

            dismiss();
        });

        return view;
    }

    private void setupDummyData() {
        provinceData = ProvinceUtils.getProvinceDistrictMap(getContext());
    }
}
