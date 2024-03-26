package com.github.se.assocify.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode


class SelectAssociationScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SelectAssociationScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("SelectAssociationScreen") }) {
        val searchOrganization: KNode = child { hasTestTag("SearchOrganization") }
        val registeredList: KNode = child { hasTestTag("RegisteredList") }
        val createOrgaButton: KNode = child { hasTestTag("CreateNewOrganizationButton") }

    }

class DisplayOrganizationScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<DisplayOrganizationScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("DisplayOrganizationScreen") }) {
    val organizationName: KNode = child { hasTestTag("OrganizationName") }
    val organizationIcon: KNode = child { hasTestTag("OrganizationIcon") }
    }