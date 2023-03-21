package world.mappable.mapkitdemo;

import android.os.Bundle;
import android.app.Activity;

import world.mappable.mapkit.Animation;
import world.mappable.mapkit.MapKitFactory;
import world.mappable.mapkit.geometry.Point;
import world.mappable.mapkit.map.CameraPosition;

import world.mappable.mapkit.mapview.MapView;

/**
 * This is a basic example that displays a map and sets camera focus on the target location.
 * Note: When working on your projects, remember to request the required permissions.
 */
public class MapActivity extends Activity {
    private final Point TARGET_LOCATION = new Point(25.229762, 55.289311);
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Now MapView can be created.
        setContentView(R.layout.map);
        super.onCreate(savedInstanceState);
        mapView = (MapView)findViewById(R.id.mapview);

        // And to show what can be done with it, we move the camera to the center of Saint Petersburg.
        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f),
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
