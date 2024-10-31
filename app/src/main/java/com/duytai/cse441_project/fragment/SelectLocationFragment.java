package com.duytai.cse441_project.fragment;

import android.content.pm.PackageManager;
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

public class SelectLocationFragment extends Fragment implements LocationEngineCallback<LocationEngineResult> {
    private MapView mapView;
    private FloatingActionButton focusLocationButton;
    private Button selectLocationButton;
    private MapboxMap mapboxMap;
    private LocationEngine locationEngine;
    private Location lastLocation;

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

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap map) {
                mapboxMap = map;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                    // Xử lý sau khi bản đồ đã sẵn sàng
                });
            }
        });

        focusLocationButton.setOnClickListener(v -> focusOnCurrentLocation());

        return view;
    }

    private void focusOnCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationEngine.requestLocationUpdates(new LocationEngineRequest.Builder(5)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .build(), this, null);
        locationEngine.getLastLocation(this);
    }

    @Override
    public void onSuccess(LocationEngineResult result) {
        lastLocation = result.getLastLocation();
        if (lastLocation != null) {
            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 15));
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