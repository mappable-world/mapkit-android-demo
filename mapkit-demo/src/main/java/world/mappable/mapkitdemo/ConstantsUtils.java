package world.mappable.mapkitdemo;

import world.mappable.mapkit.geometry.Point;

import java.util.Arrays;
import java.util.List;

public class ConstantsUtils {

    static final Point DEFAULT_POINT = new Point(25.229762, 55.289311);

    // Map Objects
    static final Point ANIMATED_RECTANGLE_CENTER = new Point(25.234, 55.294);
    static final Point TRIANGLE_CENTER = new Point(25.224, 55.284);
    static final Point POLYLINE_CENTER = DEFAULT_POINT;
    static final Point CIRCLE_CENTER = new Point(25.235, 55.289);
    static final Point DRAGGABLE_PLACEMARK_CENTER = new Point(25.224, 55.289);
    static final Point ANIMATED_PLACEMARK_CENTER = new Point(25.229, 55.300);

    // Driving
    static final Point DRIVING_ROUTE_START_LOCATION = new Point(24.925953, 55.003317);
    static final Point DRIVING_ROUTE_END_LOCATION = new Point(25.101977, 55.155337);

    // Custom Layer
    static final String LOGO_URL = "https://raw.githubusercontent.com/MappableWorld/mapkit-android-demo/master/src/main/res/drawable/ic_launcher.png";

    // Masstransit
    static final Point MASSTRANSIT_POINT = new Point(25.229762, 55.289311);
    static final Point MASSTRANSIT_ROUTE_START_LOCATION = new Point(25.217344, 55.361048);
    static final Point MASSTRANSIT_ROUTE_END_LOCATION = new Point(25.210210, 55.266554);

    // Clustering
    static final List<Point> CLUSTER_CENTERS = Arrays.asList(
            new Point(25.229, 55.289),
            new Point(24.079, 52.653),
            new Point(40.715, -74.004)
    );
}
