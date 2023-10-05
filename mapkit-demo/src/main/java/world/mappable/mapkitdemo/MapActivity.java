package world.mappable.mapkitdemo;

import static world.mappable.mapkitdemo.ConstantsUtils.DEFAULT_POINT;

import android.app.Activity;
import android.os.Bundle;

import world.mappable.mapkit.Animation;
import world.mappable.mapkit.MapKitFactory;
import world.mappable.mapkit.map.CameraPosition;
import world.mappable.mapkit.mapview.MapView;

/**
 * This is a basic example that displays a map and sets camera focus on the target location.
 * Note: When working on your projects, remember to request the required permissions.
 */
public class MapActivity extends Activity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Now MapView can be created.
        setContentView(R.layout.map);
        super.onCreate(savedInstanceState);
        mapView = findViewById(R.id.mapview);

        // And to show what can be done with it, we move the camera to the center of the target location.
        mapView.getMapWindow().getMap().move(
                new CameraPosition(DEFAULT_POINT, 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null);
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
