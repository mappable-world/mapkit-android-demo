package world.mappable.navikitdemo.ui.utils

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.google.android.material.snackbar.Snackbar

inline fun View.showSnackbar(
    message: String,
    @Duration length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}
