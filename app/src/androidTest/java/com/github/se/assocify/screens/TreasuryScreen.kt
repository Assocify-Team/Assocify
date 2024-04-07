package com.github.se.assocify.ui.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**
 * This class represents the Treasury Screen and the elements it contains.
 */
class TreasuryScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<TreasuryScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("treasuryScreen") }) {

    val myReceiptsTab: KNode = onNode { hasTestTag("myReceiptsTab") }
    val budgetTab: KNode = onNode { hasTestTag("budgetTab") }
    val balanceTab: KNode = onNode { hasTestTag("balanceTab") }

    val createReceiptFab: KNode = onNode { hasTestTag("createReceipt") }

    val receiptList: KNode = onNode { hasTestTag("ReceiptList") }
    val receiptItemBox: KNode = onNode { hasTestTag("receiptItemBox") }
    val receiptDateText: KNode = onNode { hasTestTag("receiptDateText") }
    val receiptNameText: KNode = onNode { hasTestTag("receiptNameText") }
    val receiptDescriptionText: KNode = onNode { hasTestTag("receiptDescriptionText") }
    val receiptPriceText: KNode = onNode { hasTestTag("receiptPriceText") }
    val shoppingCartIcon: KNode = onNode { hasTestTag("shoppingCartIcon") }

    val accountIconButton: KNode = onNode { hasTestTag("accountIconButton") }
    val searchIconButton: KNode = onNode { hasTestTag("searchIconButton") }
}
