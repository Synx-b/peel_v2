package banana.com.thepeel;


import android.content.Context;
import android.media.MediaRouter;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.ContentHandler;
import java.net.URLEncoder;
import java.util.ArrayList;


public class direction_helper {

    private direction_handler handler;

    // API Json string Requests
    private json_helper json_helper = new json_helper();
    private static final String GOOGLE_API_KEY = "AIzaSyAeeZwRJSZ9uuPlCbOQqjyUhzfcb5w6o1k";
    private static final String directions_request = "https://maps.googleapis.com/maps/api/directions/json?";

    // Locations in String
    private String source_location;
    private String destination_location;

    // JSON Tags
    private String TAG_OVERVIEW_POLYLINE = "overview_polyline";
    private String TAG_LEGS = "legs";
    private String TAG_DISTANCE = "distance";
    private String TAG_DURATION = "duration";
    private String TAG_START_LOCATION = "start_location";
    private String TAG_END_LOCATION = "end_location";
    private String TAG_END_ADDRESS = "end_address";
    private String TAG_START_ADDRESS = "start_address";


    public direction_helper(direction_handler handler, String source, String destination){
        this.source_location = source;
        this.destination_location = destination;
        this.handler = handler;
    }

    public void execute() throws UnsupportedEncodingException{
        handler.clear_previous_polylines();
        new direction_helper_async().execute();
    }

    private String createURL() {
        try{
            String urlOrigin = URLEncoder.encode(source_location, "utf-8");
            String urlDestination = URLEncoder.encode(destination_location, "utf-8");
            return directions_request + "origin=" + urlOrigin + "&destination=" + urlDestination +"&region=uk" + "&key=" + GOOGLE_API_KEY;

        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }

    private void parseJSON(String jsonLink) throws JSONException{
        if(jsonLink == null){
            Log.d("tit", "invalud json link");
        }

        Log.d("tet", "1");
        JSONObject json = new JSONObject(jsonLink);
        ArrayList<route> routes = new ArrayList<>();
        JSONArray jsonRoutes = json.getJSONArray("routes");
        for(int i = 0; i < jsonRoutes.length(); i++){
            Log.d("tet", "parsing JSON");
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            route route = new route();

            JSONObject overview_polyLines = jsonRoute.getJSONObject(TAG_OVERVIEW_POLYLINE);
            JSONArray jsonLegs = jsonRoute.getJSONArray(TAG_LEGS);
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject(TAG_DISTANCE);
            JSONObject jsonDuration = jsonLeg.getJSONObject(TAG_DURATION);
            JSONObject jsonStartLocation = jsonLeg.getJSONObject(TAG_START_LOCATION);
            JSONObject jsonEndLocation = jsonLeg.getJSONObject(TAG_END_LOCATION);
            Log.d("tet", "finished creating jsonObjects");
            route.distance = new json_distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new json_duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString(TAG_END_ADDRESS);
            route.startAddress = jsonLeg.getString(TAG_START_ADDRESS);
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polyLines.getString("points"));
            routes.add(route);
        }
        Log.d("tet", "json has been parsed");
        handler.plot_polylines(routes);
    }

    private ArrayList<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        ArrayList<LatLng> decoded = new ArrayList<>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }

    private class direction_helper_async extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("tet", createURL());
            return json_helper.getJsonFromURL(createURL());
        }

        @Override
        protected void onPostExecute(String jsonObject) {
            try{
                parseJSON(jsonObject);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}

