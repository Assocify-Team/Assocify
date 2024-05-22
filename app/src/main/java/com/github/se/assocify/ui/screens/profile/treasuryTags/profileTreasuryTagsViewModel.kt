package com.github.se.assocify.ui.screens.profile.treasuryTags

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileTreasuryTagsViewModel(
    assoAPI: AssociationAPI,
    accountingCategoryAPI: AccountingCategoryAPI,
    navActions: NavigationActions
) : ViewModel() {
  private val _uiState = MutableStateFlow(ProfileTreasuryTagsUIState())
  val uiState: StateFlow<ProfileTreasuryTagsUIState> = _uiState

  init {
    accountingCategoryAPI.getCategories(
        CurrentUser.associationUid!!,
        { categoryList ->
          _uiState.value =
              _uiState.value.copy(treasuryTags = _uiState.value.treasuryTags + categoryList)
        },
        { Log.e("treasurytags", "Error loading tags") })
  }
}

data class ProfileTreasuryTagsUIState(
    val treasuryTags: List<AccountingCategory> = listOf(AccountingCategory("add", "Add a new tag"))
)
