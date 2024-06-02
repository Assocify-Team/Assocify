package com.github.se.assocify.ui.screens.profile.treasuryTags

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.database.AccountingCategoryAPI
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.util.SnackbarSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileTreasuryTagsViewModel(
    private val accountingCategoryAPI: AccountingCategoryAPI,
    navActions: NavigationActions
) : ViewModel() {
  private val _uiState = MutableStateFlow(ProfileTreasuryTagsUIState())
  val uiState: StateFlow<ProfileTreasuryTagsUIState> = _uiState

  private val snackBarSystem = SnackbarSystem(_uiState.value.snackBarHostState)

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
        { _uiState.value = _uiState.value.copy(loading = false, error = "Error loading tags") })
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
    if (_uiState.value.nameError) return
    accountingCategoryAPI.deleteCategory(
        tag,
        {
          _uiState.value =
              _uiState.value.copy(treasuryTags = _uiState.value.treasuryTags.filter { tag != it })
        },
        { snackBarSystem.showSnackbar("Could not delete the tag") })
  }

  fun addTag(newTag: AccountingCategory) {
    if (_uiState.value.nameError) return
    accountingCategoryAPI.addCategory(
        associationUID = _uiState.value.assocId,
        newTag,
        {
          _uiState.value = _uiState.value.copy(treasuryTags = _uiState.value.treasuryTags + newTag)
          cancelPopUp()
        },
        {
          Log.e("TrasurySCreen", "does not work!!")
          cancelPopUp()
          snackBarSystem.showSnackbar("Could not add the tag")
        })
  }

  fun modifyTag(tag: AccountingCategory) {
    if (_uiState.value.nameError) return
    accountingCategoryAPI.updateCategory(
        _uiState.value.assocId,
        tag,
        {
          _uiState.value =
              _uiState.value.copy(
                  treasuryTags = _uiState.value.treasuryTags.filter { it.uid != tag.uid } + tag)
          cancelPopUp()
        },
        {
          Log.e("TrasurySCreen", "does not work!!")
          cancelPopUp()
          snackBarSystem.showSnackbar("Could not modify the tag")
        })
  }

  fun cancelPopUp() {
    _uiState.value =
        _uiState.value.copy(
            modify = false,
            creating = false,
            editedTag = null,
            displayedName = "",
            nameError = false)
  }

  fun checkNameError(name: String) {
    _uiState.value =
        _uiState.value.copy(
            nameError = name.isEmpty() || _uiState.value.treasuryTags.any { ac -> ac.name == name })
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
    val loading: Boolean = false,
    val snackBarError: String? = null,
    val nameError: Boolean = false,
    val snackBarHostState: SnackbarHostState = SnackbarHostState()
)
