package world.mappable.mapkitdemo.objects

enum class PlacemarkType {
    YELLOW,
    GREEN,
    RED
}

data class PlacemarkUserData(
    val name: String,
    val type: PlacemarkType,
)
