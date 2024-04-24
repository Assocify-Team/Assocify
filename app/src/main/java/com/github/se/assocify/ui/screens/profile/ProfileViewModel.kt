package com.github.se.assocify.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.UserAPI

class ProfileViewModel(
    private val assoAPI: AssociationAPI,
    private val userAPI: UserAPI
) : ViewModel() {

}