package com.github.se.assocify.ui.screens.treasury.accounting.newcategory

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.model.entities.AccountingSubCategory
import com.github.se.assocify.ui.composables.DropdownOption
import com.github.se.assocify.ui.composables.DropdownWithSetOptions
import com.github.se.assocify.ui.util.DateUtil
import java.util.UUID

/** The pop up to add a new category */
@Composable
fun AddCategoryPopUp(show: Boolean) {
  // Temporary here while we don't have the viewmodel
  val categoryList =
      listOf(
          AccountingCategory("", "Global"),
          AccountingCategory("", "Pole"),
          AccountingCategory("", "Events"),
          AccountingCategory("", "Commission"),
          AccountingCategory("", "Fees"))

  val dropdownOptionsCategory =
      categoryList.map { category -> DropdownOption(name = category.name, uid = category.uid) }

  val yearList = DateUtil.getYearList().reversed()
  val dropdownOptionsYear =
      yearList.map { year -> DropdownOption(name = year, uid = UUID.randomUUID().toString()) }
  var showAddCategoryDialog by remember { mutableStateOf(show) }
  var name by remember { mutableStateOf("") }
  var year by remember { mutableStateOf(yearList[0]) }
  var selectedCategory by remember { mutableStateOf(categoryList[0].name) }
  var categoryUid by remember { mutableStateOf("") }
  var expandedCat by remember { mutableStateOf(false) }
  var expandedYear by remember { mutableStateOf(false) }

  val horizontalPadding = 16.dp
  val verticalSpacing = 8.dp
  if (showAddCategoryDialog) {
    Dialog(onDismissRequest = { showAddCategoryDialog = false }) {
      Card(
          modifier =
              Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
                  .testTag("addAccountingCategoryScreen"),
          shape = RoundedCornerShape(16.dp),
      ) {
        Column(
            modifier =
                Modifier.verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding),
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
                        Modifier.clickable { showAddCategoryDialog = false }
                            .testTag("cancelButton"))
              }

              // Name field
              OutlinedTextField(
                  modifier = Modifier.fillMaxWidth().testTag("categoryNameField"),
                  value = name,
                  singleLine = true,
                  onValueChange = { name = it },
                  label = { Text("Category name") })

              // Accounting category dropdown
              DropdownWithSetOptions(
                  options = dropdownOptionsCategory,
                  selectedOption =
                      DropdownOption(
                          selectedCategory,
                          categoryList.find { it.name == selectedCategory }?.uid ?: ""),
                  opened = expandedCat,
                  onOpenedChange = { expandedCat = it },
                  onSelectOption = { option ->
                    categoryUid = option.uid
                    selectedCategory = option.name
                  },
                  modifier = Modifier.fillMaxWidth().testTag("categoryDropdown"),
                  label = "Tag")

              // Year dropdown
              DropdownWithSetOptions(
                  options = dropdownOptionsYear,
                  selectedOption =
                      DropdownOption(year, dropdownOptionsYear.find { it.name == year }?.uid ?: ""),
                  opened = expandedYear,
                  onOpenedChange = { expandedYear = it },
                  onSelectOption = { option -> year = option.name },
                  modifier = Modifier.fillMaxWidth().testTag("yearDropdown"),
                  label = "Year")
            }

        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Absolute.Right) {
              // Create button
              TextButton(
                  onClick = {
                    val newSubCategory =
                        AccountingSubCategory(
                            UUID.randomUUID().toString(), categoryUid, name, 0, year.toInt())
                    /*TODO: create new subcategory*/
                    showAddCategoryDialog = false
                  },
                  modifier = Modifier.testTag("createButton"),
              ) {
                Text("Create")
              }
            }
      }
    }
  }
}
