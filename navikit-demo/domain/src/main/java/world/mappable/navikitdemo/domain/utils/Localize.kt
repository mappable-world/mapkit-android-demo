package world.mappable.navikitdemo.domain.utils

import world.mappable.runtime.i18n.I18nManagerFactory

fun Double.localizeSpeed(): String {
    return I18nManagerFactory.getI18nManagerInstance().localizeSpeed(this)
}

fun Int.localizeDistance(): String {
    return I18nManagerFactory.getI18nManagerInstance().localizeDistance(this)
}
