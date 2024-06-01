package com.github.se.assocify.ui.screens.profile.treasuryTags

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.navigation.NavigationActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileTreasuryTagsViewModel(
    private val accountingCategoryAPI: AccountingCategoryAPI,
    navActions: NavigationActions
) : ViewModel() {
  private val _uiState = MutableStateFlow(ProfileTreasuryTagsUIState())
  val uiState: StateFlow<ProfileTreasuryTagsUIState> = _uiState

  init {
    fetchCategories()
  }

  fun fetchCategories() {
    _uiState.value = _uiState.value.copy(loading = true)
    accountingCategoryAPI.getCategories(
        CurrentUser.associationUid!!,
        { categoryList ->
          _uiState.value = _uiState.value.copy(treasuryTags = categoryList)
          _uiState.value = _uiState.value.copy(loading = false, error = null)
        },
        { _uiState.value = _uiState.value.copy(loading = false, error = "Error loading receipts") })
  }

  fun modifying(modifying: Boolean, editedTag: AccountingCategory) {
    _uiState.value =
        _uiState.value.copy(
            modify = modifying, displayedName = "Editing tag", editedTag = editedTag)
  }

  fun creating(modifying: Boolean) {
    _uiState.value = _uiState.value.copy(creating = modifying, displayedName = "Creating tag")
  }

  fun deleteTag(tag: AccountingCategory) {
    accountingCategoryAPI.deleteCategory(
        tag,
        {
          _uiState.value =
              _uiState.value.copy(treasuryTags = _uiState.value.treasuryTags.filter { tag != it })
        },
        {})
  }

  fun addTag(newTag: AccountingCategory) {
    accountingCategoryAPI.addCategory(
        associationUID = _uiState.value.assocId,
        newTag,
        {
          _uiState.value = _uiState.value.copy(treasuryTags = _uiState.value.treasuryTags + newTag)
        },
        {})
  }

  fun modifyTag(tag: AccountingCategory) {
    accountingCategoryAPI.updateCategory(
        _uiState.value.assocId,
        tag,
        {
          _uiState.value =
              _uiState.value.copy(
                  treasuryTags = _uiState.value.treasuryTags.filter { it.uid != tag.uid } + tag)
        },
        {})
  }

  fun cancelPopUp() {
    _uiState.value =
        _uiState.value.copy(modify = false, creating = false, editedTag = null, displayedName = "")
  }
}

data class ProfileTreasuryTagsUIState(
    val assocId: String = CurrentUser.associationUid!!,
    val treasuryTags: List<AccountingCategory> = emptyList(),
    val modify: Boolean = false,
    val creating: Boolean = false,
    val editedTag: AccountingCategory? = null,
    val displayedName: String = "",
    val error: String? = null,
    val loading: Boolean = false
)
