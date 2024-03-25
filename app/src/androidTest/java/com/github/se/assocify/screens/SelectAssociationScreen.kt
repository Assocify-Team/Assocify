package com.github.se.assocify.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode


class SelectAssociationScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SelectAssociationScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("SelectAssociationScreen") }) {
        // Search Bar Tag
        val searchOrganization: KNode = child { hasTestTag("SearchOrganization") }
        val registeredList: KNode = child { hasTestTag("RegisteredList") }

    }