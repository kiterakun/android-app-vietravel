package com.group6.vietravel.utils;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group6.vietravel.data.models.place.District;
import com.group6.vietravel.data.models.place.Province;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvinceUtils {

    private static List<Province> cachedProvinces = null;

    public static List<String> getCodesByName(Context context, String provinceName, String districtName) {
        if (cachedProvinces == null) {
            cachedProvinces = loadProvincesFromAssets(context);
        }

        List<String> result = new ArrayList<>();

        for (Province p : cachedProvinces) {
            if (p.getName().equals(provinceName)) {
                result.add(String.valueOf(p.getCode()));
                if (p.getDistricts() != null) {
                    for (District d : p.getDistricts()) {
                        if (d.getName().equals(districtName)) {
                            result.add(String.valueOf(d.getCode()));
                            return result;
                        }
                    }
                }
                result.add("all");
                break;
            }
        }

        result.add("all");
        result.add("all");
        return result;
    }
    public static Map<String, List<String>> getProvinceDistrictMap(Context context) {

        Map<String, List<String>> provinceData = new HashMap<>();

        List<Province> allProvinces = loadProvincesFromAssets(context);

        for (Province province : allProvinces) {
            List<String> districtNames = new ArrayList<>();

            if (province.getDistricts() != null) {
                for (District district : province.getDistricts()) {
                    districtNames.add(district.getName());
                }
            }

            provinceData.put(province.getName(), districtNames);
        }

        return provinceData;
    }

    private static List<Province> loadProvincesFromAssets(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Province>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}