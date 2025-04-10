package world.mappable.navikitdemo.data.smartroute

import world.mappable.mapkit.directions.driving.AvoidanceFlags
import world.mappable.mapkit.directions.driving.DrivingOptions
import world.mappable.navikitdemo.domain.SettingsManager
import world.mappable.navikitdemo.domain.VehicleOptionsManager
import world.mappable.navikitdemo.domain.models.SmartRouteOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class SmartRouteSettingsProvider @Inject constructor(
    private val settingsManager: SettingsManager,
    private val vehicleOptionsManager: VehicleOptionsManager,
) {
    fun changes(): Flow<SmartRouteOptions> {
        return with(settingsManager) {
            combine(
                fuelConnectorTypes.changes(),
                maxTravelDistance.changes(),
                currentRangeLvl.changes(),
                thresholdDistance.changes(),
                avoidTolls.changes(),
                avoidUnpaved.changes(),
                avoidPoorCondition.changes(),
                avoidRailwayCrossing.changes(),
                avoidBoatFerry.changes(),
                avoidFordCrossing.changes(),
                avoidTunnel.changes(),
                avoidHighway.changes(),
            ) { _ ->
                smartRouteOptions()
            }
        }
    }
    private fun SettingsManager.smartRouteOptions(): SmartRouteOptions {
        return SmartRouteOptions(
            chargingType.value,
            fuelConnectorTypes.value,
            maxTravelDistance.value.toMeters(),
            currentRangeLvl.value.toMeters(),
            thresholdDistance.value.toMeters(),
            DrivingOptions().also { options ->
                options.avoidanceFlags = AvoidanceFlags(
                    avoidTolls.value,
                    avoidUnpaved.value,
                    avoidPoorCondition.value,
                    avoidRailwayCrossing.value,
                    avoidBoatFerry.value,
                    avoidFordCrossing.value,
                    avoidTunnel.value,
                    avoidHighway.value
                )
            },
            vehicleOptionsManager.vehicleOptions(),
        )
    }

    private fun Float.toMeters(): Double = this * 1000.0
}
