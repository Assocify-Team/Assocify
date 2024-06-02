package com.github.se.assocify.ui.screens.treasury.receiptstab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.model.entities.RoleType
import com.github.se.assocify.ui.composables.CenteredCircularIndicator
import com.github.se.assocify.ui.composables.ErrorMessage
import com.github.se.assocify.ui.composables.PullDownRefreshBox
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.PriceUtil

/** My Receipts UI page */
@Composable
fun ReceiptListScreen(viewModel: ReceiptListViewModel) {
  // Good practice to re-collect it as the page changes
  val viewmodelState by viewModel.uiState.collectAsState()

  if (viewmodelState.loading) {
    CenteredCircularIndicator()
    return
  }

  if (viewmodelState.error != null) {
    ErrorMessage(errorMessage = viewmodelState.error) { viewModel.updateReceipts() }
    return
  }

  PullDownRefreshBox(
      refreshing = viewmodelState.refresh, onRefresh = { viewModel.refreshReceipts() }) {
        LazyColumn(
            modifier = Modifier.testTag("ReceiptList").fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Header for the user receipts
              item {
                Text(
                    text = "My Receipts",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
                HorizontalDivider()
              }

              if (viewmodelState.userReceipts.isNotEmpty()) {
                // First list of receipts
                viewmodelState.userReceipts.forEach { receipt ->
                  item {
                    ReceiptItem(receipt, viewModel)
                    HorizontalDivider()
                  }
                }
              } else {
                // Placeholder for empty list
                item {
                  Text(
                      text = "No receipts found. You can create one!",
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.padding(20.dp))
                }
              }

              // Global receipts only appear if the user has the permission,
              // which is handled in the viewmodel whatsoever
              if (viewmodelState.allReceipts.isNotEmpty() &&
                  (viewmodelState.userCurrentRole.type == RoleType.TREASURY ||
                          viewmodelState.userCurrentRole.type == RoleType.PRESIDENCY)) {
                // Header for the global receipts
                item {
                  Text(
                      text = "All Receipts",
                      style = MaterialTheme.typography.titleMedium,
                      modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
                  HorizontalDivider()
                }
                // Second list of receipts
                viewmodelState.allReceipts.forEach { receipt ->
                  item {
                    ReceiptItem(receipt, viewModel)
                    HorizontalDivider()
                  }
                }
              }

              item { Spacer(modifier = Modifier.height(80.dp)) }
            }
      }
}

/** Receipt item from the list in Receipts page */
@Composable
private fun ReceiptItem(receipt: Receipt, viewModel: ReceiptListViewModel) {
  ListItem(
      modifier = Modifier.clickable { viewModel.onReceiptClick(receipt) }.fillMaxWidth(),
      headlineContent = {
        Text(modifier = Modifier.testTag("receiptNameText"), text = receipt.title)
      },
      overlineContent = {
        Text(
            modifier = Modifier.testTag("receiptDateText"),
            text = DateUtil.formatDate(receipt.date))
      },
      supportingContent = {
        Text(
            modifier = Modifier.testTag("receiptDescriptionText"),
            text = receipt.description.ifEmpty { "-" },
            maxLines = 1,
        )
      },
      trailingContent = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("receiptPriceAndIconRow")) {
              Text(
                  text = PriceUtil.fromCents(receipt.cents),
                  modifier = Modifier.testTag("receiptPriceText"),
                  style = MaterialTheme.typography.bodyMedium)
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                  modifier = Modifier.testTag("statusIcon").size(30.dp),
                  imageVector = receipt.status.getIcon(),
                  contentDescription = "status icon")
            }
      },
  )
}
