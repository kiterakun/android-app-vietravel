package com.group6.vietravel.features.user.ui.main.discovery;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.repositories.place.PlaceRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DiscoveryViewModel extends AndroidViewModel {
    private final PlaceRepository placeRepository;
    private final MutableLiveData<List<LatLng>> routePath = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DiscoveryViewModel(Application application){
        super(application);
        placeRepository = PlaceRepository.getInstance(application.getApplicationContext());
    }
    public LiveData<List<LatLng>> getRoutePath() {
        return routePath;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchRoute(LatLng origin, LatLng dest, String apiKey) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {

                String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
                String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
                // Thêm mode=driving để đảm bảo đi theo đường xe chạy
                String params = str_origin + "&" + str_dest + "&mode=driving&key=" + apiKey;
                String url = "https://maps.googleapis.com/maps/api/directions/json?" + params;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray routes = jsonObject.getJSONArray("routes");

                    if (routes.length() > 0) {
                        JSONObject route = routes.getJSONObject(0);

                        List<LatLng> fullDetailedPath = new ArrayList<>();
                        JSONArray legs = route.getJSONArray("legs");

                        // Duyệt qua từng chặng (thường chỉ có 1 leg nếu không có điểm dừng giữa đường)
                        for (int i = 0; i < legs.length(); i++) {
                            JSONObject leg = legs.getJSONObject(i);
                            JSONArray steps = leg.getJSONArray("steps");

                            // Duyệt qua từng bước đi nhỏ (Vd: Rẽ trái tại ngã tư...)
                            for (int j = 0; j < steps.length(); j++) {
                                JSONObject step = steps.getJSONObject(j);
                                JSONObject polyline = step.getJSONObject("polyline");
                                String points = polyline.getString("points");

                                // Giải mã đoạn nhỏ này và thêm vào danh sách tổng
                                fullDetailedPath.addAll(decodePoly(points));
                            }
                        }
                        routePath.postValue(fullDetailedPath);

                    } else {
                        errorMessage.postValue("Không tìm thấy đường đi!");
                    }
                } else {
                    errorMessage.postValue("Lỗi kết nối API!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage.postValue("Lỗi: " + e.getMessage());
            }
        });
    }
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public LiveData<List<Place>> getPlaceList(){
        return placeRepository.getAllPlaces();
    }

    public void loadPlaces(){
        placeRepository.fetchAllPlaces();
    }
}