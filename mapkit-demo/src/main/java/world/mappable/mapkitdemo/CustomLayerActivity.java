package world.mappable.mapkitdemo;

import static world.mappable.mapkitdemo.ConstantsUtils.LOGO_URL;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;

import world.mappable.mapkit.MapKitFactory;
import world.mappable.mapkit.layers.Layer;
import world.mappable.mapkit.layers.LayerOptions;
import world.mappable.mapkit.layers.TileFormat;
import world.mappable.mapkit.map.MapType;
import world.mappable.mapkit.map.TileDataSourceBuilder;
import world.mappable.mapkit.map.CreateTileDataSource;
import world.mappable.mapkit.tiles.UrlProvider;
import world.mappable.mapkit.images.DefaultImageUrlProvider;
import world.mappable.mapkit.mapview.MapView;

/**
 * This example shows how to add a user-defined layer to the map.
 * We use the UrlProvider class to format requests to a remote server that renders
 * tiles. For simplicity, we ignore map coordinates and zoom here, and
 * just provide a URL for the static image.
 */
public class CustomLayerActivity extends Activity {
    private UrlProvider urlProvider;
    private DefaultImageUrlProvider imageUrlProvider;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.initialize(this);
        setContentView(R.layout.custom_layer);
        super.onCreate(savedInstanceState);

        urlProvider = (tileId, version, features) -> LOGO_URL;
        imageUrlProvider = new DefaultImageUrlProvider();

        mapView = (MapView)findViewById(R.id.mapview);
        mapView.getMapWindow().getMap().setMapType(MapType.NONE);
        mapView.getMapWindow().getMap().addTileLayer(
            "mapkit_logo",
            new LayerOptions().setVersionSupport(false),
            new CreateTileDataSource() {
                @Override
                public void createTileDataSource(@NonNull TileDataSourceBuilder tileDataSourceBuilder) {
                    tileDataSourceBuilder.setTileFormat(TileFormat.PNG);
                    tileDataSourceBuilder.setTileUrlProvider(urlProvider);
                    tileDataSourceBuilder.setImageUrlProvider(imageUrlProvider);
                }
        });
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
}
