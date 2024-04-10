package com.github.se.assocify.ui.screens.createAsso

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.se.assocify.model.database.AssociationAPI
import com.github.se.assocify.model.database.FirebaseApi
import com.github.se.assocify.model.entities.Association
import com.github.se.assocify.model.entities.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.Date

class CreateAssoViewmodel(/*association : Association // idk if there should be an argument, maybe db ?*/) : ViewModel() {
  private val _uiState = MutableStateFlow(Association())
  val uiState: StateFlow<Association> = _uiState

  val assoApi = AssociationAPI(db = FirebaseFirestore.getInstance())

  val date = getDateInstance().format(Date()) // current date ?
  var name: String by remember { mutableStateOf("") }

  val asso = Association(assoApi.getNewId(), "", "", date, "", listOf(), listOf())

}
