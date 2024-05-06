package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.AccountingCategory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AccountingCategoriesAPI(val db: SupabaseClient) : SupabaseApi() {

  private val collection_name_category = "accounting_category"

  /**
   * Get the categories of an association
   *
   * @param associationUID the unique identifier of the association
   * @param onSuccess the callback to be called when the categories are retrieved
   * @param onFailure the callback to be called when the categories could not be retrieved
   * @return the list of categories
   */
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

  /**
   * Add a category
   *
   * @param associationUID the unique identifier of the association
   * @param category the category to add
   * @param onSuccess the callback to be called when the category is added
   * @param onFailure the callback to be called when the category could not be added
   */
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

  /**
   * Update a category
   *
   * @param associationUID the unique identifier of the association
   * @param category the category to update
   * @param onSuccess the callback to be called when the category is updated
   * @param onFailure the callback to be called when the category could not be updated
   */
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

  /**
   * Delete a category in cascade
   *
   * @param category the category to delete
   * @param onSuccess the callback to be called when the category is deleted
   * @param onFailure the callback to be called when the category could not be deleted
   */
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

/** A representation of a category in the database */
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
