package banana.com.thepeel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class route{
    public json_duration duration;
    public json_distance distance;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public ArrayList<LatLng> points;
}
