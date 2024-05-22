package com.github.se.assocify.ui.screens.profile.treasuryTags

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.navigation.Destination
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.screens.profile.ProfileUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileTreasuryTagsViewModel(
    private val assoAPI: AssociationAPI,
    private val userAPI: UserAPI,
    private val accountingCategoryAPI: AccountingCategoryAPI,
    private val navActions: NavigationActions
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileTreasuryTagsUIState())
    val uiState: StateFlow<ProfileTreasuryTagsUIState> = _uiState

    init {
        accountingCategoryAPI.getCategories(
            CurrentUser.associationUid!!,
            { categoryList ->
                _uiState.value = _uiState.value.copy(treasuryTags = categoryList)
            },
            { Log.e("treasurytags", "Error loading tags") })
    }
}

data class ProfileTreasuryTagsUIState(
    val treasuryTags: List<AccountingCategory> = emptyList()
)
