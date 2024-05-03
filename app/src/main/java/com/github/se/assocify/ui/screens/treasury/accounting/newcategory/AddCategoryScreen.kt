package com.github.se.assocify.ui.screens.treasury.accounting.newcategory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.AccountingCategory
import com.github.se.assocify.navigation.NavigationActions
import com.github.se.assocify.ui.composables.BackButton

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
          AccountingCategory("Global"),
          AccountingCategory("Pole"),
          AccountingCategory("Events"),
          AccountingCategory("Commission"),
          AccountingCategory("Fees"))
  var subCategoryTitle by remember { mutableStateOf("") }
  var selectedSubCategory by remember { mutableStateOf(categoryList.first().name) }
  var selectedValue by remember { mutableIntStateOf(0) }
  var newCategoryName by remember { mutableStateOf("") }
  var showAddCategoryDialog by remember { mutableStateOf(false) }

  // Show dialog to add category
  AddCategoryDialog(
      showDialog = showAddCategoryDialog,
      onDismissRequest = { showAddCategoryDialog = false },
      onAddSubCategoryClick = { /* TODO: Add sub category */},
      newCategoryName = newCategoryName,
      onNewCategoryNameChange = { newCategoryName = it })

  Scaffold(
      modifier = Modifier.testTag("addAccountingSubCategoryScreen"),
      topBar = {
        TopAppBar(
            title = {
              Text(modifier = Modifier.testTag("subCategoryTitle"), text = "New Sub Category")
            },
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
              right = 0.dp)) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

              // Name field
              OutlinedTextField(
                  modifier = Modifier.testTag("categoryNameField").fillMaxWidth(),
                  value = subCategoryTitle,
                  onValueChange = { subCategoryTitle = it },
                  label = { Text("Category name") },
                  supportingText = { /* TODO: Error management */})
              // Value field
              OutlinedTextField(
                  modifier = Modifier.testTag("valueField").fillMaxWidth(),
                  value = selectedValue.toString(),
                  onValueChange = { selectedValue = it.toIntOrNull() ?: 0 },
                  label = { Text("Value") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  supportingText = { /* TODO: Error management */})

              // Accounting category dropdown
              var expanded by remember { mutableStateOf(false) }
              ExposedDropdownMenuBox(
                  expanded = expanded,
                  onExpandedChange = { expanded = !expanded },
                  modifier = Modifier.testTag("categoryDropdown")) {
                    TextField(
                        value = selectedSubCategory,
                        onValueChange = {},
                        label = { Text("Category") },
                        trailingIcon = {
                          ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.textFieldColors())
                    ExposedDropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false }) {
                          categoryList.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = { selectedSubCategory = category.name })
                          }
                        }
                  }

              // Add category button
              OutlinedButton(
                  modifier = Modifier.testTag("addSubCategoryButton").fillMaxWidth(),
                  onClick = { showAddCategoryDialog = true },
                  content = {
                    Icon(Icons.Outlined.Edit, contentDescription = "Add sub category")
                    Text(" Add sub category")
                  })
              Spacer(modifier = Modifier.height(16.dp))

              Column {
                Button(
                    modifier = Modifier.testTag("createButton").fillMaxWidth(),
                    onClick = { /* TODO */},
                    content = { Text("Create") })
              }

              Spacer(modifier = Modifier.weight(1.0f))
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onAddSubCategoryClick: () -> Unit,
    newCategoryName: String,
    onNewCategoryNameChange: (String) -> Unit
) {
  if (showDialog) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
      Surface(
          modifier = Modifier.wrapContentWidth().wrapContentHeight(),
          shape = MaterialTheme.shapes.large,
          tonalElevation = AlertDialogDefaults.TonalElevation) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(modifier = Modifier.testTag("categoryTitle"), text = "New Category")
              OutlinedTextField(
                  modifier = Modifier.testTag("newCategoryFieldPopup").fillMaxWidth(),
                  value = newCategoryName,
                  onValueChange = onNewCategoryNameChange,
                  label = { Text("Category name") },
                  supportingText = { /* TODO: Error management */})
              Spacer(modifier = Modifier.height(24.dp))
              TextButton(
                  onClick = onAddSubCategoryClick, modifier = Modifier.align(Alignment.End)) {
                    Text("Add Sub Category")
                  }
            }
          }
    }
  }
}
