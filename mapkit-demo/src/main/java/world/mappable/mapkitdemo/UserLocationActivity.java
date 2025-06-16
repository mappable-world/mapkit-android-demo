package world.mappable.mapkitdemo;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import world.mappable.mapkit.MapKit;
import world.mappable.mapkit.MapKitFactory;
import world.mappable.mapkit.geometry.Point;
import world.mappable.mapkit.layers.ObjectEvent;
import world.mappable.mapkit.map.CameraPosition;
import world.mappable.mapkit.map.CompositeIcon;
import world.mappable.mapkit.map.IconStyle;
import world.mappable.mapkit.map.RotationType;
import world.mappable.mapkit.mapview.MapView;
import world.mappable.mapkit.user_location.UserLocationLayer;
import world.mappable.mapkit.user_location.UserLocationObjectListener;
import world.mappable.mapkit.user_location.UserLocationView;
import world.mappable.runtime.image.ImageProvider;

/**
 * This example shows how to display and customize user location arrow on the map.
 */
public class UserLocationActivity extends Activity implements UserLocationObjectListener {
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    private MapView mapView;
    private UserLocationLayer userLocationLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.user_location);
        super.onCreate(savedInstanceState);
        mapView = findViewById(R.id.mapview);
        mapView.getMapWindow().getMap().setRotateGesturesEnabled(false);
        mapView.getMapWindow().getMap().move(new CameraPosition(new Point(0, 0), 14, 0, 0));

        requestLocationPermission();

        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingModeActive(true);

        userLocationLayer.setObjectListener(this);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.5)),
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this, R.drawable.user_arrow));

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

        pinIcon.setIcon(
                "icon",
                ImageProvider.fromResource(this, R.drawable.icon),
                new IconStyle().setAnchor(new PointF(0f, 0f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(0f)
                        .setScale(1f)
        );

        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(this, R.drawable.search_result),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)
        );

        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
    }

    @Override
    public void onObjectRemoved(UserLocationView view) {
    }

    @Override
    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {
    }
}
