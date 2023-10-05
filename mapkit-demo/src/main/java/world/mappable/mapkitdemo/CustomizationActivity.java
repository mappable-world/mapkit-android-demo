package world.mappable.mapkitdemo;

import static world.mappable.mapkitdemo.ConstantsUtils.DEFAULT_POINT;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import world.mappable.mapkit.Animation;
import world.mappable.mapkit.MapKitFactory;
import world.mappable.mapkit.map.Map;
import world.mappable.mapkit.geometry.Point;
import world.mappable.mapkit.map.CameraPosition;

import world.mappable.mapkit.map.MapType;
import world.mappable.mapkit.mapview.MapView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This is a basic example that displays a map and sets camera focus on the target location.
 * Note: When working on your projects, remember to request the required permissions.
 */
public class CustomizationActivity extends Activity {
    private static final String TAG = "CustomizationActivity";
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.map);
        super.onCreate(savedInstanceState);
        mapView = (MapView)findViewById(R.id.mapview);

        Map map = mapView.getMap();
        map.setMapType(MapType.VECTOR_MAP);

        // Apply customization
        try {
            map.setMapStyle(style());
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to read customization style", e);
        }

        // And to show what can be done with it, we move the camera to the center of the target location.
        map.move(new CameraPosition(DEFAULT_POINT, 15.0f, 0.0f, 0.0f));
    }

    private String readRawResource(String name) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final int resourceIdentifier =
                getResources().getIdentifier(name,"raw", getPackageName());
        InputStream is = getResources().openRawResource(resourceIdentifier);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException ex) {
            Log.e(TAG, "Cannot read raw resource " + name);
            throw ex;
        }

        return builder.toString();
    }

    private String style() throws IOException {
        return readRawResource("customization_example");
    }

    @Override
    protected void onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        // Activity onStart call must be passed to both MapView and MapKit instance.
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
}
