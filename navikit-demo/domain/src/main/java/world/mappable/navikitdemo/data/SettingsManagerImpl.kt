package world.mappable.navikitdemo.data

import world.mappable.mapkit.annotations.AnnotationLanguage
import world.mappable.mapkit.directions.driving.VehicleType
import world.mappable.mapkit.road_events.EventTag
import world.mappable.navikitdemo.domain.SettingModel
import world.mappable.navikitdemo.domain.SettingsManager
import world.mappable.navikitdemo.domain.helpers.KeyValueStorage
import world.mappable.navikitdemo.domain.models.AnnotatedEventsType
import world.mappable.navikitdemo.domain.models.AnnotatedRoadEventsType
import world.mappable.navikitdemo.domain.models.EcoClass
import world.mappable.navikitdemo.domain.models.FuelConnectorType
import world.mappable.navikitdemo.domain.models.JamsMode
import world.mappable.navikitdemo.domain.models.StyleMode
import world.mappable.navikitdemo.domain.models.ChargingType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManagerImpl @Inject constructor(
    private val keyValueStorage: KeyValueStorage,
    defaultAnnotationsLanguage: AnnotationLanguage,
) : SettingsManager {

    // Style
    override val styleMode = enum("styleMode", StyleMode.SYSTEM, StyleMode::class.java)

    // Vehicle options
    override val vehicleType = enum("vehicleType", VehicleType.DEFAULT, VehicleType::class.java)
    override val weight = float("weight", 10f)
    override val axleWeight = float("axleWeight", 5f)
    override val maxWeight = float("maxWeight", 10f)
    override val height = float("height", 3.5f)
    override val width = float("width", 2.2f)
    override val length = float("length", 8.0f)
    override val payload = float("payload", 7f)
    override val ecoClass = enum("ecoClass", EcoClass.EURO_4, EcoClass::class.java)
    override val hasTrailer = boolean("hasTrailer", false)
    override val buswayPermitted = boolean("buswayPermitted", false)

    // Road events
    override val roadEventsOnRouteEnabled = boolean("roadEventsOnRouteEnabled", true)
    override val roadEventsOnRoute = createRoadEventsSettings("roadEventsOnRoute")

    // Annotations
    override val annotatedEvents = buildMap {
        AnnotatedEventsType.values().forEach {
            this[it] = boolean("annotatedEvents_$it", it == AnnotatedEventsType.EVERYTHING)
        }
    }
    override val annotatedRoadEvents = buildMap {
        AnnotatedRoadEventsType.values().forEach {
            this[it] = boolean("annotatedRoadEvents_$it", it == AnnotatedRoadEventsType.EVERYTHING)
        }
    }
    override val annotationLanguage =
        enum("annotationLanguage", defaultAnnotationsLanguage, AnnotationLanguage::class.java)
    override val muteAnnotations = boolean("muteAnnotations", false)
    override val textAnnotations = boolean("textAnnotations", true)
    override val preRecordedAnnotations = boolean("preRecordedAnnotations", false)

    // Driving Options
    override val avoidTolls = boolean("avoidTolls", false)
    override val avoidUnpaved = boolean("avoidUnpaved", false)
    override val avoidPoorCondition = boolean("avoidPoorCondition", false)
    override val avoidRailwayCrossing = boolean("avoidRailwayCrossing", false)
    override val avoidBoatFerry = boolean("avoidBoatFerry", false)
    override val avoidFordCrossing = boolean("avoidFordCrossing", false)
    override val avoidTunnel = boolean("avoidTunnel", false)
    override val avoidHighway = boolean("avoidHighway", false)

    // Navigation Layer
    override val jamsMode =
        enum("jamsMode", JamsMode.ENABLED_FOR_CURRENT_ROUTE, JamsMode::class.java)
    override val balloons = boolean("balloons", true)
    override val trafficLight = boolean("trafficLight", true)
    override val showPredicted = boolean("showPredicted", false)
    override val balloonsGeometry = boolean("balloonsGeometry", false)
    override val focusRectsAutoUpdate = boolean("focusRectsAutoUpdate", true)
    override val hdMode = boolean("hdMode", true)
    override val poi3DModels = boolean("poi3DModels", true)

    // Camera
    override val autoZoom = boolean("autoZoom", true)
    override val autoRotation = boolean("autoRotation", true)
    override val autoCamera = boolean("autoCamera", true)
    override val zoomOffset = float("zoomOffset", 0f)

    // Guidance
    override val alternatives = boolean("alternatives", true)
    override val simulation = boolean("simulation", true)
    override val simulationSpeed = float(
        "simulationSpeed", DEFAULT_SIMULATION_SPEED.toFloat(),
        range = MIN_SIMULATION_SPEED..MAX_SIMULATION_SPEED
    )
    override val background = boolean("background", true)
    override val speedLimitTolerance = float("speedLimitTolerance", 0.8f)
    override val restoreGuidanceState = boolean("restoreGuidanceState", false)
    override val serializedNavigation = string("serializedNavigation")


    // Smart Route Planning Options
    override val smartRoutePlanningEnabled = boolean("smartRoutePlanningEnabled", false)
    override val chargingType = enum("chargingType", ChargingType.ELECTRO, ChargingType::class.java)
    override val fuelConnectorTypes =
        enumSet("fuelConnectorTypes", setOf(FuelConnectorType.TYPE_2), FuelConnectorType::class.java)

    override val maxTravelDistance = float("maxTravelDistance", 300f)
    override val currentRangeLvl = float("currentRangeLvl", 50f)
    override val thresholdDistance = float("thresholdDistance", 5f)

    private fun createRoadEventsSettings(baseKey: String): Map<EventTag, SettingModel<Boolean>> {
        return buildMap {
            EventTag.values().forEach {
                this[it] = boolean("${baseKey}_$it", true)
            }
        }
    }

    private fun float(
        key: String,
        default: Float,
        range: ClosedFloatingPointRange<Float>? = null,
    ): SettingModel<Float> {
        return object : SettingModel<Float> {
            private var valueImpl = MutableStateFlow(value)

            override var value: Float
                get() = keyValueStorage.readFloat(key, default)
                set(value) {
                    val actualValue = range?.let { value.coerceIn(it) } ?: value
                    keyValueStorage.putFloat(key, actualValue)
                    valueImpl.value = actualValue
                }

            override fun changes(): Flow<Float> = valueImpl
        }
    }

    private fun <T : Enum<T>> enum(key: String, default: T, classItem: Class<T>): SettingModel<T> {
        return object : SettingModel<T> {
            private var valueImpl = MutableStateFlow(value)

            override var value: T
                get() = keyValueStorage.readEnum(key, default, classItem)
                set(value) {
                    keyValueStorage.putEnum(key, value)
                    valueImpl.value = value
                }

            override fun changes(): Flow<T> = valueImpl
        }
    }

    private fun <T : Enum<T>> enumSet(
        key: String,
        default: Set<T>,
        classItem: Class<T>
    ): SettingModel<Set<T>> {
        return object : SettingModel<Set<T>> {
            private var valueImpl = MutableStateFlow(value)

            override var value: Set<T>
                get() = keyValueStorage.readEnumSet(key, default, classItem)
                set(value) {
                    keyValueStorage.putEnumSet(key, value)
                    valueImpl.value = value
                }

            override fun changes(): Flow<Set<T>> = valueImpl
        }
    }

    private fun boolean(key: String, default: Boolean): SettingModel<Boolean> {
        return object : SettingModel<Boolean> {
            private var valueImpl = MutableStateFlow(value)

            override var value: Boolean
                get() = keyValueStorage.readBoolean(key, default)
                set(value) {
                    keyValueStorage.putBoolean(key, value)
                    valueImpl.value = value
                }

            override fun changes(): Flow<Boolean> = valueImpl
        }
    }

    private fun string(key: String, default: String = ""): SettingModel<String> {
        return object : SettingModel<String> {
            private var valueImpl = MutableStateFlow(value)

            override var value: String
                get() = keyValueStorage.readString(key, default)
                set(value) {
                    keyValueStorage.putString(key, value)
                    valueImpl.value = value
                }

            override fun changes(): Flow<String> = valueImpl
        }
    }
}
