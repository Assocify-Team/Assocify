package com.github.se.assocify.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.github.se.assocify.R

sealed class Destination(
    val route: String,
    @StringRes val labelId: Int? = null,
    @DrawableRes val iconId: Int? = null
) {

  data object Treasury :
      Destination("treasury", R.string.treasury_tab_label, R.drawable.treasury_tab_icon)

  data object Event : Destination("event", R.string.event_tab_label, R.drawable.event_tab_icon)

  data object Profile :
      Destination("profile", R.string.profile_tab_label, R.drawable.profile_tab_icon)

  data object ProfilePreferences : Destination("profile/preferences")

  data object ProfileMembers : Destination("profile/members")

  data object ProfileTreasuryTags : Destination("profile/treasuryTags")

  data object ProfileEvents : Destination("profile/events")

  data object Login : Destination("login/authentication")

  data object SelectAsso : Destination("login/selectAsso")

  data object CreateAsso : Destination("login/createAsso")

  data object NewReceipt : Destination("treasury/receipt")

  data class EditReceipt(val receiptUid: String) : Destination("treasury/receipt/$receiptUid")

  data class BudgetDetailed(val subCategoryUid: String) :
      Destination("treasury/budget/$subCategoryUid")

  data class BalanceDetailed(val subCategoryUid: String) :
      Destination("treasury/balance/$subCategoryUid")

  data object NewTask : Destination("event/task")

  data class EditTask(val taskUid: String) : Destination("event/task/$taskUid")
}

val MAIN_TABS_LIST = listOf(Destination.Treasury, Destination.Event, Destination.Profile)
