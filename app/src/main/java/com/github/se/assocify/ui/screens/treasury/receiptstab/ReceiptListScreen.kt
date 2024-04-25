package com.github.se.assocify.ui.screens.treasury.receiptstab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.assocify.model.entities.Receipt
import com.github.se.assocify.ui.util.DateUtil
import com.github.se.assocify.ui.util.PriceUtil

/** My Receipts UI page */
@Composable
fun ReceiptListScreen(viewModel: ReceiptListViewModel) {
  // Good practice to re-collect it as the page changes
  val viewmodelState by viewModel.uiState.collectAsState()

  LazyColumn(
      modifier = Modifier.testTag("ReceiptList"),
      verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
      horizontalAlignment = Alignment.CenterHorizontally) {
        // Header for the user receipts
        item {
          Text(
              text = "My Receipts",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp))
          HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
        }

        if (viewmodelState.userReceipts.isNotEmpty()) {
          // First list of receipts
          viewmodelState.userReceipts.forEach { receipt ->
            item {
              ReceiptItem(receipt, viewModel)
              HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
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
        if (viewmodelState.allReceipts.isNotEmpty()) {
          // Header for the global receipts
          item {
            Text(
                text = "All Receipts",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp))
            HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
          }
          // Second list of receipts
          viewmodelState.allReceipts.forEach { receipt ->
            item {
              ReceiptItem(receipt, viewModel)
              HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp))
            }
          }
        }
      }
}

/** Receipt item from the list in My Receipts page */
@Composable
private fun ReceiptItem(receipt: Receipt, viewModel: ReceiptListViewModel) {
  Box(
      modifier =
          Modifier.fillMaxWidth().padding(6.dp).height(70.dp).testTag("receiptItemBox").clickable {
            viewModel.onReceiptClick(receipt)
          }) {
        Column(modifier = Modifier.padding(start = 20.dp)) {
          Text(
              text = DateUtil.toString(receipt.date),
              modifier = Modifier.padding(top = 6.dp).testTag("receiptDateText"),
              style =
                  TextStyle(
                      fontSize = 12.sp,
                      lineHeight = 16.sp,
                      color = MaterialTheme.colorScheme.secondary,
                      letterSpacing = 0.5.sp,
                  ))
          Text(
              text = receipt.title,
              modifier = Modifier.testTag("receiptNameText"),
              style =
                  TextStyle(
                      fontSize = 16.sp,
                      lineHeight = 24.sp,
                      letterSpacing = 0.sp,
                  ))
          Text(
              text = receipt.description,
              modifier = Modifier.testTag("receiptDescriptionText"),
              style =
                  TextStyle(
                      fontSize = 12.sp,
                      lineHeight = 24.sp,
                      color = MaterialTheme.colorScheme.secondary,
                      letterSpacing = 0.sp,
                  ))
        }

        Row(
            modifier =
                Modifier.align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 8.dp)
                    .testTag("receiptPriceAndIconRow"),
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = PriceUtil.fromCents(receipt.cents),
                  modifier = Modifier.testTag("receiptPriceText"),
                  style =
                      TextStyle(
                          fontSize = 14.sp,
                          lineHeight = 24.sp,
                          color = MaterialTheme.colorScheme.secondary,
                          letterSpacing = 0.sp,
                      ))
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                  modifier = Modifier.size(20.dp).testTag("shoppingCartIcon"),
                  imageVector = Icons.Filled.ShoppingCart,
                  contentDescription = "Arrow icon",
              )
            }
      }
}
