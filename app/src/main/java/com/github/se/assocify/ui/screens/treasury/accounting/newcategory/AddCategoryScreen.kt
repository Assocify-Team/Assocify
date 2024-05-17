package com.github.se.assocify.ui.screens.treasury.accounting.newcategory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.BackButton
import com.github.se.assocify.ui.composables.DropdownOption
import com.github.se.assocify.ui.composables.DropdownWithSetOptions
import com.github.se.assocify.ui.util.DateUtil
import java.util.UUID

// Scaffold padding values
val HorizontalPadding = 20.dp
val VerticalPadding = 40.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountingSubCategory(
    navActions: NavigationActions,
) {

  // Temporary here while we don't have the viewmodel
  val categoryList =
      listOf(
          AccountingCategory("", "Global"),
          AccountingCategory("", "Pole"),
          AccountingCategory("", "Events"),
          AccountingCategory("", "Commission"),
          AccountingCategory("", "Fees"))
  var subCategoryTitle by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(categoryList[0].name) }

  var selectedValue by remember { mutableIntStateOf(0) }
  var newCategoryName by remember { mutableStateOf("") }
  var showAddCategoryDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
  // Show dialog to add category
  AddCategoryDialog(
      showDialog = showAddCategoryDialog,
      onDismissRequest = { showAddCategoryDialog = false },
      onAddSubCategoryClick = { /* TODO: Add sub category */},
      newTagName = newCategoryName,
      onNewTagChange = { newCategoryName = it })

  Scaffold(
      modifier = Modifier.testTag("addAccountingCategoryScreen"),
      topBar = {
        TopAppBar(
            title = { Text(modifier = Modifier.testTag("categoryTitle"), text = "New Category") },
            navigationIcon = {
              BackButton(
                  contentDescription = "Cancel",
                  onClick = { navActions.back() },
                  modifier = Modifier.testTag("cancelButton"))
            })
      },
      contentWindowInsets =
          WindowInsets(
              top = VerticalPadding,
              left = HorizontalPadding,
              bottom = VerticalPadding,
              right = HorizontalPadding)) { paddingValues ->
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

              // Name field
              OutlinedTextField(
                  modifier = Modifier
                      .testTag("categoryNameField")
                      .fillMaxWidth(),
                  value = subCategoryTitle,
                  singleLine = true,
                  onValueChange = { subCategoryTitle = it },
                  label = { Text("Category name") },
                  supportingText = { /* TODO: Error management */})

            // Accounting category dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .testTag("categoryDropdown")
                    .padding(8.dp)) {
                OutlinedTextField(
                    value = selectedCategory,
                    singleLine = true,
                    onValueChange = {},
                    label = { Text("Tag") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    readOnly = true,
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .clickable { expanded = !expanded })
                ExposedDropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }) {
                    categoryList.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category.name
                                expanded = false
                            })
                    }
                }
            }

              Spacer(modifier = Modifier.height(16.dp))

              Column {
                Button(
                    modifier = Modifier
                        .testTag("createButton")
                        .fillMaxWidth(),
                    onClick = { /* TODO */},
                    content = { Text("Create") })
              }

              Spacer(modifier = Modifier.weight(1.0f))
            }
      }
}


@Composable
fun AddCategoryPopUp() {
    // Temporary here while we don't have the viewmodel
    val categoryList =
        listOf(
            AccountingCategory("", "Global"),
            AccountingCategory("", "Pole"),
            AccountingCategory("", "Events"),
            AccountingCategory("", "Commission"),
            AccountingCategory("", "Fees"))

    val dropdownOptionsCategory = categoryList.map { category ->
        DropdownOption(
            name = category.name,
            uid = category.uid
        )
    }

    val yearList = DateUtil.getYearList().reversed()
    val dropdownOptionsYear = yearList.map { year ->
        DropdownOption(
            name = year,
            uid = UUID.randomUUID().toString()
        )
    }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var year by remember { mutableStateOf(yearList[0]) }
    var selectedCategory by remember { mutableStateOf(categoryList[0].name) }
    var categoryUid by remember { mutableStateOf("") }
    var expandedCat by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }

    val horizontalPadding = 16.dp
    val verticalSpacing = 8.dp

    Dialog(onDismissRequest = { showAddCategoryDialog = false }) {
        Card(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .testTag("newSubCategoryDialog"),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(verticalSpacing)
            ) {
                // Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = verticalSpacing),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("New category", style = MaterialTheme.typography.titleLarge)
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close dialog",
                        modifier = Modifier
                            .clickable { showAddCategoryDialog = false }
                            .testTag("newSubCategoryCancelButton")
                    )
                }

                // Name field
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("categoryNameField"),
                    value = name,
                    singleLine = true,
                    onValueChange = { name = it },
                    label = { Text("Category name") }
                )

                // Accounting category dropdown
                DropdownWithSetOptions(
                    options = dropdownOptionsCategory,
                    selectedOption = DropdownOption(
                        selectedCategory,
                        categoryList.find { it.name == selectedCategory }?.uid ?: ""
                    ),
                    opened = expandedCat,
                    onOpenedChange = { expandedCat = it },
                    onSelectOption = { option ->
                        categoryUid = option.uid
                        selectedCategory = option.name
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("categoryDropdown"),
                    label = "Tag"
                )

                // Year dropdown
                DropdownWithSetOptions(
                    options = dropdownOptionsYear,
                    selectedOption = DropdownOption(
                        year,
                        dropdownOptionsYear.find { it.name == year }?.uid ?: ""
                    ),
                    opened = expandedYear,
                    onOpenedChange = { expandedYear = it },
                    onSelectOption = { option ->
                        year = option.name
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("yearDropdown"),
                    label = "Year"
                )
            }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.Absolute.Right
                ) {
                    // Create button
                    TextButton(
                        onClick = {
                            val newSubCategory = AccountingSubCategory(
                                UUID.randomUUID().toString(),
                                categoryUid,
                                name,
                                0,
                                year.toInt()
                            )
                            /*TODO: create new subcategory*/
                            showAddCategoryDialog = false
                        },
                        modifier = Modifier.testTag("newSubCategoryCreateButton"),
                    ) {
                        Text("Create")
                    }
            }


        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onAddSubCategoryClick: () -> Unit,
    newTagName: String,
    onNewTagChange: (String) -> Unit
) {
  if (showDialog) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
      Surface(
          modifier = Modifier
              .wrapContentWidth()
              .wrapContentHeight(),
          shape = MaterialTheme.shapes.large,
          tonalElevation = AlertDialogDefaults.TonalElevation) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(modifier = Modifier.testTag("tagName"), text = "New Tag")
              OutlinedTextField(
                  modifier = Modifier
                      .testTag("newTagFieldPopup")
                      .fillMaxWidth(),
                  value = newTagName,
                  singleLine = true,
                  onValueChange = onNewTagChange,
                  label = { Text("Tag name") },
                  supportingText = { /* TODO: Error management */})
              Spacer(modifier = Modifier.height(24.dp))
              TextButton(
                  onClick = onAddSubCategoryClick, modifier = Modifier.align(Alignment.End)) {
                    Text("Create new tag")
                  }
            }
          }
    }
  }
}
