package world.mappable.navikitdemo.domain.models

import world.mappable.mapkit.directions.driving.DrivingOptions
import world.mappable.mapkit.directions.driving.VehicleOptions

data class SmartRouteOptions(
    val chargingType: ChargingType,
    val fuelConnectorTypes: Set<FuelConnectorType>,
    val maxTravelDistanceInMeters: Double,
    val currentRangeLvlInMeters: Double,
    val thresholdDistanceInMeters: Double,
    val drivingOptions: DrivingOptions,
    val vehicleOptions: VehicleOptions,
)
