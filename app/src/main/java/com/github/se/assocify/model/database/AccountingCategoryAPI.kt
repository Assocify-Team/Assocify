package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.AccountingCategory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AccountingCategoriesAPI(val db: SupabaseClient) : SupabaseApi() {

  val collection_name_category = "accounting_category"

  fun getCategories(
      associationUID: String,
      onSuccess: (List<AccountingCategory>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      val categories =
          db.postgrest
              .from(collection_name_category)
              .select { filter { SupabaseAccountingCategory::associationUID eq associationUID } }
              .decodeList<SupabaseAccountingCategory>()
      onSuccess(categories.map { it.toAccountingCategory() })
    }
  }

  fun addCategory(
      associationUID: String,
      category: AccountingCategory,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.postgrest
          .from(collection_name_category)
          .insert(
              SupabaseAccountingCategory(
                  uid = category.uid, associationUID = associationUID, name = category.name))
      onSuccess()
    }
  }

  fun updateCategory(
      associationUID: String,
      category: AccountingCategory,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.postgrest.from(collection_name_category).update({
        SupabaseAccountingCategory::name setTo category.name
      }) {
        filter {
          SupabaseAccountingCategory::uid eq category.uid
          SupabaseAccountingCategory::associationUID eq associationUID
        }
      }
      onSuccess()
    }
  }

  fun deleteCategory(
      category: AccountingCategory,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.postgrest.from(collection_name_category).delete {
        filter { SupabaseAccountingCategory::uid eq category.uid }
      }
      onSuccess()
    }
  }
}

@Serializable
data class SupabaseAccountingCategory(
    @SerialName("uid") val uid: String,
    @SerialName("association_uid") val associationUID: String,
    @SerialName("name") val name: String
) {
  fun toAccountingCategory(): AccountingCategory {
    return AccountingCategory(uid = uid, name = name)
  }
}
