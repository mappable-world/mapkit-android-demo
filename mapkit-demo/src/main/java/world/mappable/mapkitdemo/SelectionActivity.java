package world.mappable.mapkitdemo;

import static world.mappable.mapkitdemo.ConstantsUtils.DEFAULT_POINT;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;

import world.mappable.mapkit.Animation;
import world.mappable.mapkit.MapKitFactory;
import world.mappable.mapkit.geometry.Point;
import world.mappable.mapkit.layers.GeoObjectTapEvent;
import world.mappable.mapkit.layers.GeoObjectTapListener;
import world.mappable.mapkit.map.CameraPosition;
import world.mappable.mapkit.map.GeoObjectSelectionMetadata;
import world.mappable.mapkit.map.InputListener;
import world.mappable.mapkit.map.Map;
import world.mappable.mapkit.mapview.MapView;

/**
 * This example shows how to activate selection.
 */
public class SelectionActivity extends Activity implements GeoObjectTapListener, InputListener {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.map);
        super.onCreate(savedInstanceState);
        mapView = findViewById(R.id.mapview);

        // And to show what can be done with it, we move the camera to the center of the target location.
        mapView.getMapWindow().getMap().move(
                new CameraPosition(DEFAULT_POINT, 17.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);

        mapView.getMapWindow().getMap().addTapListener(this);
        mapView.getMapWindow().getMap().addInputListener(this);
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

    @Override
    public boolean onObjectTap(@NonNull GeoObjectTapEvent geoObjectTapEvent) {
        final GeoObjectSelectionMetadata selectionMetadata = geoObjectTapEvent
                .getGeoObject()
                .getMetadataContainer()
                .getItem(GeoObjectSelectionMetadata.class);

        if (selectionMetadata != null) {
            mapView.getMapWindow().getMap().selectGeoObject(selectionMetadata);
        }

        return selectionMetadata != null;
    }

    @Override
    public void onMapTap(@NonNull Map map, @NonNull Point point) {
        mapView.getMapWindow().getMap().deselectGeoObject();
    }

    @Override
    public void onMapLongTap(@NonNull Map map, @NonNull Point point) {

    }
}
