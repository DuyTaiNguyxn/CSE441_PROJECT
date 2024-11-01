package com.duytai.cse441_project.fragment;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.Manifest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.duytai.cse441_project.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectLocationFragment extends Fragment implements LocationEngineCallback<LocationEngineResult> {
    private MapView mapView;
    private FloatingActionButton focusLocationButton;
    private Button selectLocationButton;
    private TextView locationTextView; // Khai báo TextView cho địa chỉ
    private MapboxMap mapboxMap;
    private LocationEngine locationEngine;
    private Location lastLocation;
    private Bundle bundle = new Bundle();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getContext(), "sk.eyJ1IjoibGViaW5odHYyMDAzIiwiYSI6ImNtMng2djVubDAxczUya3MxZjJ2NWZlMTAifQ.OI5sQxGbr_jUaa59BmacWg");
        locationEngine = LocationEngineProvider.getBestLocationEngine(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_location, container, false);

        mapView = view.findViewById(R.id.mapView);
        focusLocationButton = view.findViewById(R.id.focus_location);
        selectLocationButton = view.findViewById(R.id.bt_select_location);
        locationTextView = view.findViewById(R.id.txt_Location); // Khởi tạo TextView

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap map) {
                mapboxMap = map;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                    // Lấy vị trí hiện tại một lần
                    getCurrentLocation();
                });

                // Lấy vị trí ở trung tâm bản đồ khi người dùng dừng kéo bản đồ
                mapboxMap.addOnCameraIdleListener(() -> {
                    LatLng centerLatLng = mapboxMap.getCameraPosition().target;
                    updateLocationTextView(centerLatLng);
                });
            }
        });

        focusLocationButton.setOnClickListener(v -> getCurrentLocation()); // Thêm nút để lấy vị trí nếu cần

        selectLocationButton.setOnClickListener(v -> {
            String addressText = locationTextView.getText().toString();
            bundle.putString("location", addressText);
            getParentFragmentManager().setFragmentResult("location", bundle);
            Log.d("Mapbox", "Location selected: " + addressText);
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationEngine.getLastLocation(this); // Chỉ lấy vị trí một lần
    }

    private void updateLocationTextView(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.getLatitude(), latLng.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0); // Get the full address
                locationTextView.setText(addressText); // Cập nhật địa chỉ lên TextView
            } else {
                Log.d("Mapbox", "No address found for the location.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Mapbox", "Geocoder failed: " + e.getMessage());
        }
    }

    @Override
    public void onSuccess(LocationEngineResult result) {
        lastLocation = result.getLastLocation();
        if (lastLocation != null) {
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
            updateLocationTextView(latLng);
        }
    }

    @Override
    public void onFailure(@NonNull Exception exception) {
        Log.e("Mapbox", "Error getting location: " + exception.getMessage());
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(this);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}