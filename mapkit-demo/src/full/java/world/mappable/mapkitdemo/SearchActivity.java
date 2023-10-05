package world.mappable.mapkitdemo;

import static world.mappable.mapkitdemo.ConstantsUtils.DEFAULT_POINT;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import world.mappable.mapkit.GeoObjectCollection;
import world.mappable.mapkit.MapKitFactory;
import world.mappable.mapkit.geometry.Point;
import world.mappable.mapkit.map.CameraListener;
import world.mappable.mapkit.map.CameraPosition;
import world.mappable.mapkit.map.CameraUpdateReason;
import world.mappable.mapkit.map.Map;
import world.mappable.mapkit.map.MapObjectCollection;
import world.mappable.mapkit.map.VisibleRegionUtils;
import world.mappable.mapkit.mapview.MapView;
import world.mappable.mapkit.search.Response;
import world.mappable.mapkit.search.SearchFactory;
import world.mappable.mapkit.search.SearchManagerType;
import world.mappable.mapkit.search.SearchOptions;
import world.mappable.mapkit.search.SearchManager;
import world.mappable.mapkit.search.Session;
import world.mappable.runtime.Error;
import world.mappable.runtime.image.ImageProvider;
import world.mappable.runtime.network.NetworkError;
import world.mappable.runtime.network.RemoteError;

/**
 * This example shows how to add and interact with a layer that displays search results on the map.
 * Note: search API calls count towards MapKit daily usage limits.
 */
public class SearchActivity extends Activity implements Session.SearchListener, CameraListener {
    private MapView mapView;
    private EditText searchEdit;
    private SearchManager searchManager;
    private Session searchSession;

    private void submitQuery(String query) {
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                new SearchOptions(),
                this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SearchFactory.initialize(this);

        setContentView(R.layout.search);
        super.onCreate(savedInstanceState);

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

        mapView = findViewById(R.id.mapview);
        mapView.getMap().addCameraListener(this);

        searchEdit = findViewById(R.id.search_edit);
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    submitQuery(searchEdit.getText().toString());
                }

                return false;
            }
        });

        mapView.getMap().move(
                new CameraPosition(DEFAULT_POINT, 14.0f, 0.0f, 0.0f));

        submitQuery(searchEdit.getText().toString());
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
    public void onSearchResponse(Response response) {
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        mapObjects.clear();
        final ImageProvider searchResultImageProvider = ImageProvider.fromResource(this, R.drawable.search_result);
        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            final Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
            if (resultLocation != null) {
                mapObjects.addPlacemark(placemark -> {
                    placemark.setGeometry(resultLocation);
                    placemark.setIcon(searchResultImageProvider);
                });
            }
        }
    }

    @Override
    public void onSearchError(Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPositionChanged(
            Map map,
            CameraPosition cameraPosition,
            CameraUpdateReason cameraUpdateReason,
            boolean finished) {
        if (finished) {
            submitQuery(searchEdit.getText().toString());
        }
    }
}
