package banana.com.thepeel.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.input.InputManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import banana.com.thepeel.R;
import banana.com.thepeel.direction_handler;
import banana.com.thepeel.direction_helper;
import banana.com.thepeel.permissions;
import banana.com.thepeel.route;



public class map_page_fragment extends Fragment implements OnMapReadyCallback, direction_handler {

    // For loading a Map Fragment
    private SupportMapFragment mMapFragment;
    private static GoogleMap mGoogleMap;
    private Activity rActivity;
    private ProgressDialog progressDialog;

    // Views on the Page
    private EditText editText_source_location = null;
    private EditText editText_destination_location = null;
    private Button btn_load_route = null;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_page_layout, container, false);
        rActivity = getActivity();

        editText_source_location = (EditText)view.findViewById(R.id.editText_source_location);
        editText_destination_location = (EditText)view.findViewById(R.id.editText_destination_location);

        btn_load_route = (Button)view.findViewById(R.id.btn_load_route);
        btn_load_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_load_route_onClick();
            }
        });

        if(googleServicesAvailable()){
            initMaps();
        }
        return view;
    }

    private void btn_load_route_onClick(){
        String origin = editText_source_location.getText().toString();
        String destination = editText_destination_location.getText().toString();
        InputMethodManager inputManager = (InputMethodManager)rActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            inputManager.hideSoftInputFromWindow(rActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            if(origin.length() != 0 && destination.length() != 0)
                new direction_helper(this, origin, destination).execute();
            else if(origin.length() != 0 && destination.length() == 0)
                Toast.makeText(rActivity, "No Destination Entered",Toast.LENGTH_LONG).show();
            else if(origin.length() == 0 && destination.length() != 0)
                Toast.makeText(rActivity, "No Origin Entered",Toast.LENGTH_LONG).show();
            else if(origin.length() == 0 && destination.length() == 0)
                Toast.makeText(rActivity, "No Destination and Origin Entered",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void clear_previous_polylines() {
        progressDialog = ProgressDialog.show(rActivity, "Please Wait", "Finding Direction", true);
        if(originMarkers != null){
            for(Marker marker : originMarkers){
                marker.remove();
            }
        }
        if(destinationMarkers != null){
            for(Marker marker : destinationMarkers){
                marker.remove();
            }
        }
        if(polylinePaths != null){
            for(Polyline polyline : polylinePaths){
                polyline.remove();
            }
        }
    }

    private void initMaps(){
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mMapFragment.getMapAsync(this);
    }

    public static void change_map_type(int map_type){
        if(mGoogleMap != null)
            mGoogleMap.setMapType(map_type);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if(permissions.checkLocationPermission(rActivity, rActivity)) {
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void plot_polylines(ArrayList<route> routes){
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        Log.d("tet", "plotting polylines");
        for(route route : routes){
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            originMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.GREEN).
                    width(10);
            for(int i = 0; i < route.points.size(); i++){
                polylineOptions.add(route.points.get(i));
                Log.d("tet", "route added");
            }

            polylinePaths.add(mGoogleMap.addPolyline(polylineOptions));

        }
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(getContext());
        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(rActivity, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(rActivity, "GS NA", Toast.LENGTH_LONG).show();
        }
        return false;
    }




}

