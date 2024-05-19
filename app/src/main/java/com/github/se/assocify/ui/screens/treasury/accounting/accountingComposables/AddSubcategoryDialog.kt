package com.github.se.assocify.ui.screens.treasury.accounting.accountingComposables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.assocify.ui.composables.DropdownOption
import com.github.se.assocify.ui.composables.DropdownWithSetOptions
import com.github.se.assocify.ui.screens.treasury.accounting.AccountingViewModel
import com.github.se.assocify.ui.util.DateUtil
import java.util.UUID

/** The dialog to add a new category */
@Composable
fun AddSubcategoryDialog(viewModel: AccountingViewModel) {

  val state by viewModel.uiState.collectAsState()

  val dropdownOptionsCategory =
      state.categoryList.map { category ->
        DropdownOption(name = category.name, uid = category.uid)
      }

  val yearList = DateUtil.getYearList().reversed()
  val dropdownOptionsYear =
      yearList.map { year -> DropdownOption(name = year, uid = UUID.randomUUID().toString()) }

  var expandedCat by remember { mutableStateOf(false) }
  var expandedYear by remember { mutableStateOf(false) }

  val horizontalPadding = 16.dp
  val verticalSpacing = 8.dp
  if (state.showNewSubcategoryDialog) {
    Dialog(onDismissRequest = { viewModel.hideNewSubcategoryDialog() }) {
      Card(
          modifier =
              Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
                  .testTag("addAccountingCategoryScreen"),
          shape = RoundedCornerShape(16.dp),
      ) {
        Column(
            modifier =
                Modifier.verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding, vertical = verticalSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(verticalSpacing)) {
              // Title
              Row(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(vertical = verticalSpacing)
                          .testTag("categoryTitle"),
                  horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text("New category", style = MaterialTheme.typography.titleLarge)
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close dialog",
                    modifier =
                        Modifier.clickable { viewModel.hideNewSubcategoryDialog() }
                            .testTag("cancelButton"))
              }

              // Name field
              OutlinedTextField(
                  modifier = Modifier.fillMaxWidth().testTag("categoryNameField"),
                  value = state.newSubcategoryTitle,
                  singleLine = true,
                  onValueChange = { viewModel.setNewSubcategoryTitle(it) },
                  label = { Text("Category name") },
                  isError = state.newSubcategoryTitleError != null,
                  supportingText = { Text(state.newSubcategoryTitleError ?: "") })

              // Accounting category dropdown
              DropdownWithSetOptions(
                  options = dropdownOptionsCategory,
                  selectedOption =
                      if (state.newSubcategoryCategory != null) {
                        DropdownOption(
                            state.newSubcategoryCategory!!.name, state.newSubcategoryCategory!!.uid)
                      } else {
                        DropdownOption("Select Tag", "-")
                      },
                  opened = expandedCat,
                  onOpenedChange = { expandedCat = it },
                  onSelectOption = { option ->
                    viewModel.setNewSubcategoryCategory(
                        state.categoryList.find { it.uid == option.uid })
                  },
                  modifier = Modifier.fillMaxWidth().testTag("categoryDropdown"),
                  label = "Tag",
                  errorMessage = state.newSubcategoryCategoryError ?: "")

              // Year dropdown
              DropdownWithSetOptions(
                  options = dropdownOptionsYear,
                  selectedOption =
                      DropdownOption(state.newSubcategoryYear, state.newSubcategoryYear),
                  opened = expandedYear,
                  onOpenedChange = { expandedYear = it },
                  onSelectOption = { option -> viewModel.setNewSubcategoryYear(option.name) },
                  modifier = Modifier.fillMaxWidth().testTag("yearDropdown"),
                  label = "Year")
            }

        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Absolute.Right) {
              // Create button
              TextButton(
                  onClick = { viewModel.createNewSubcategory() },
                  modifier = Modifier.testTag("createButton"),
              ) {
                Text("Create")
              }
            }
      }
    }
  }
}
