package world.mappable.mapkitdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import world.mappable.mapkit.Animation;
import world.mappable.mapkit.MapKitFactory;
import world.mappable.mapkit.RequestPoint;
import world.mappable.mapkit.RequestPointType;
import world.mappable.mapkit.geometry.Point;
import world.mappable.mapkit.geometry.Polyline;
import world.mappable.mapkit.geometry.SubpolylineHelper;
import world.mappable.mapkit.map.CameraPosition;
import world.mappable.mapkit.map.MapObjectCollection;
import world.mappable.mapkit.map.PolylineMapObject;
import world.mappable.mapkit.mapview.MapView;
import world.mappable.mapkit.transport.TransportFactory;
import world.mappable.mapkit.transport.masstransit.TransitOptions;
import world.mappable.mapkit.transport.masstransit.FilterVehicleTypes;
import world.mappable.mapkit.transport.masstransit.MasstransitRouter;
import world.mappable.mapkit.transport.masstransit.Route;
import world.mappable.mapkit.transport.masstransit.Section;
import world.mappable.mapkit.transport.masstransit.SectionMetadata;
import world.mappable.mapkit.transport.masstransit.Session;
import world.mappable.mapkit.transport.masstransit.TimeOptions;
import world.mappable.mapkit.transport.masstransit.Transport;
import world.mappable.runtime.Error;
import world.mappable.runtime.network.NetworkError;
import world.mappable.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This example shows how to build public transport routes between two points,
 * and how to handle route sections and vehicle types lists
 * Note: Masstransit routing API calls count towards MapKit daily usage limits.
 */
public class MasstransitRoutingActivity extends Activity
    implements Session.RouteListener {
    private final Point TARGET_LOCATION = new Point(25.229762, 55.289311);

    private final Point ROUTE_START_LOCATION = new Point(25.217344, 55.361048);
    private final Point ROUTE_END_LOCATION = new Point(25.210210, 55.266554);

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private MasstransitRouter mtRouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TransportFactory.initialize(this);

        setContentView(R.layout.map);
        super.onCreate(savedInstanceState);
        mapView = (MapView)findViewById(R.id.mapview);

        // And to show what can be done with it, we move the camera to the center of Saint Petersburg.
        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 12.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5),
                null);

        mapObjects = mapView.getMap().getMapObjects().addCollection();


        TransitOptions options = new TransitOptions(FilterVehicleTypes.NONE.value, new TimeOptions());
        List<RequestPoint> points = new ArrayList<RequestPoint>();
        points.add(new RequestPoint(ROUTE_START_LOCATION, RequestPointType.WAYPOINT, null));
        points.add(new RequestPoint(ROUTE_END_LOCATION, RequestPointType.WAYPOINT, null));
        mtRouter = TransportFactory.getInstance().createMasstransitRouter();
        mtRouter.requestRoutes(points, options, this);
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
    public void onMasstransitRoutes(List<Route> routes) {
        // In this example we consider first alternative only
        if (routes.size() > 0) {
            for (Section section : routes.get(0).getSections()) {
                drawSection(
                        section.getMetadata().getData(),
                        SubpolylineHelper.subpolyline(
                                routes.get(0).getGeometry(), section.getGeometry()));
            }
        }
    }

    @Override
    public void onMasstransitRoutesError(Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void drawSection(SectionMetadata.SectionData data,
                             Polyline geometry) {
        // Draw a section polyline on a map
        // Set its color depending on the information which the section contains
        PolylineMapObject polylineMapObject = mapObjects.addPolyline(geometry);
        // Masstransit route section defines exactly one on the following
        // 1. Wait until public transport unit arrives
        // 2. Walk
        // 3. Transfer to a nearby stop (typically transfer to a connected
        //    underground station)
        // 4. Ride on a public transport
        // Check the corresponding object for null to get to know which
        // kind of section it is
        if (data.getTransports() != null) {
            // A ride on a public transport section contains information about
            // all known public transport lines which can be used to travel from
            // the start of the section to the end of the section without transfers
            // along a similar geometry
            for (Transport transport : data.getTransports()) {
                // Some public transport lines may have a color associated with them
                // Typically this is the case of underground lines
                if (transport.getLine().getStyle() != null) {
                    polylineMapObject.setStrokeColor(
                            // The color is in RRGGBB 24-bit format
                            // Convert it to AARRGGBB 32-bit format, set alpha to 255 (opaque)
                            transport.getLine().getStyle().getColor() | 0xFF000000
                    );
                    return;
                }
            }
            // Let us draw bus lines in green and tramway lines in red
            // Draw any other public transport lines in blue
            HashSet<String> knownVehicleTypes = new HashSet<>();
            knownVehicleTypes.add("bus");
            knownVehicleTypes.add("tramway");
            for (Transport transport : data.getTransports()) {
                String sectionVehicleType = getVehicleType(transport, knownVehicleTypes);
                if (sectionVehicleType.equals("bus")) {
                    polylineMapObject.setStrokeColor(0xFF00FF00);  // Green
                    return;
                } else if (sectionVehicleType.equals("tramway")) {
                    polylineMapObject.setStrokeColor(0xFFFF0000);  // Red
                    return;
                }
            }
            polylineMapObject.setStrokeColor(0xFF0000FF);  // Blue
        } else {
            // This is not a public transport ride section
            // In this example let us draw it in black
            polylineMapObject.setStrokeColor(0xFF000000);  // Black
        }
    }

    private String getVehicleType(Transport transport, HashSet<String> knownVehicleTypes) {
        // A public transport line may have a few 'vehicle types' associated with it
        // These vehicle types are sorted from more specific (say, 'histroic_tram')
        // to more common (say, 'tramway').
        // Your application does not know the list of all vehicle types that occur in the data
        // (because this list is expanding over time), therefore to get the vehicle type of
        // a public line you should iterate from the more specific ones to more common ones
        // until you get a vehicle type which you can process
        // Some examples of vehicle types:
        // "bus", "minibus", "trolleybus", "tramway", "underground", "railway"
        for (String type : transport.getLine().getVehicleTypes()) {
            if (knownVehicleTypes.contains(type)) {
                return type;
            }
        }
        return null;
    }
}
