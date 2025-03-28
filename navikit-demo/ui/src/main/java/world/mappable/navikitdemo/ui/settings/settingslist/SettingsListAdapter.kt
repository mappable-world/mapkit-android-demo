package world.mappable.navikitdemo.ui.settings.settingslist

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import world.mappable.navikitdemo.ui.settings.settingslist.delegates.CheckListDelegate
import world.mappable.navikitdemo.ui.settings.settingslist.delegates.DetailsDelegate
import world.mappable.navikitdemo.ui.settings.settingslist.delegates.EditFloatDelegate
import world.mappable.navikitdemo.ui.settings.settingslist.delegates.MultipleCheckListDelegate
import world.mappable.navikitdemo.ui.settings.settingslist.delegates.NextScreenDelegate
import world.mappable.navikitdemo.ui.settings.settingslist.delegates.SectionTitleDelegate
import world.mappable.navikitdemo.ui.settings.settingslist.delegates.SpeedLimitsDelegate
import world.mappable.navikitdemo.ui.settings.settingslist.delegates.ToggleDelegate
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@AssistedFactory
interface SettingsListAdapterFactory {
    fun create(
        openSettingsScreenCallback: (SettingsScreen) -> Unit
    ): SettingsListAdapter
}

class SettingsListAdapter @AssistedInject constructor (
    sectionTitleDelegate: SectionTitleDelegate,
    checkListDelegate: CheckListDelegate,
    multipleCheckListDelegate: MultipleCheckListDelegate,
    editFloatDelegate: EditFloatDelegate,
    toggleDelegate: ToggleDelegate,
    detailsDelegate: DetailsDelegate,
    speedLimitsDelegate: SpeedLimitsDelegate,
    @Assisted openSettingsScreenCallback: (SettingsScreen) -> Unit,
) : ListDelegationAdapter<List<SettingsItem>>() {

    init {
        delegatesManager.apply {
            addDelegate(sectionTitleDelegate)
            addDelegate(checkListDelegate)
            addDelegate(multipleCheckListDelegate)
            addDelegate(NextScreenDelegate(openSettingsScreenCallback))
            addDelegate(editFloatDelegate)
            addDelegate(toggleDelegate)
            addDelegate(detailsDelegate)
            addDelegate(speedLimitsDelegate)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<SettingsItem>) {
        this.setItems(newItems)
        notifyDataSetChanged()
    }
}
