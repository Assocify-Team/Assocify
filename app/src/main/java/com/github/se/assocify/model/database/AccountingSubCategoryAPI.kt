package com.github.se.assocify.model.database

import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.AccountingSubCategory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * API to interact with the accounting subcategories in the database
 *
 * @property db the Supabase client
 */
class AccountingSubCategoryAPI(val db: SupabaseClient) : SupabaseApi() {
  private val collection = "accounting_subcategory"

  /**
   * Get the subcategories of an association
   *
   * @param categoryUID the unique identifier of the category
   * @param onSuccess the callback to be called when the subcategories are retrieved
   * @param onFailure the callback to be called when the subcategories could not be retrieved
   */
  fun getSubCategories(
      associationUID: String,
      onSuccess: (List<AccountingSubCategory>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      val subCategories =
          db.postgrest
              .from(collection)
              .select { filter { SupabaseAccountingSubCategory::associationUID eq associationUID } }
              .decodeList<SupabaseAccountingSubCategory>()
      onSuccess(subCategories.map { it.toAccountingSubCategory() })
    }
  }

  /**
   * Add a subcategory to a category
   *
   * @param associationUID the unique identifier of the association
   * @param categoryUID the unique identifier of the category
   * @param subCategory the subcategory to add
   * @param onSuccess the callback to be called when the subcategory is added
   * @param onFailure the callback to be called when the subcategory could not be added
   */
  fun addSubCategory(
      associationUID: String,
      categoryUID: String,
      subCategory: AccountingSubCategory,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.postgrest
          .from(collection)
          .insert(
              SupabaseAccountingSubCategory(
                  uid = subCategory.uid,
                  categoryUID = categoryUID,
                  associationUID = associationUID,
                  name = subCategory.name,
                  amount = subCategory.amount

                  ))
      onSuccess()
    }
  }

  /**
   * Update a subcategory
   *
   * @param categoryUID the unique identifier of the category
   * @param subCategory the subcategory to update
   * @param onSuccess the callback to be called when the subcategory is updated
   * @param onFailure the callback to be called when the subcategory could not be updated
   */
  fun updateSubCategory(
      categoryUID: String,
      subCategory: AccountingSubCategory,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.postgrest.from(collection).update({
        SupabaseAccountingSubCategory::name setTo subCategory.name
        SupabaseAccountingSubCategory::amount setTo subCategory.amount
      }) {
        filter {
          SupabaseAccountingSubCategory::uid eq subCategory.uid
          SupabaseAccountingSubCategory::categoryUID eq categoryUID
        }
      }
      onSuccess()
    }
  }

  /**
   * Delete a subcategory
   *
   * @param subCategory the subcategory to delete
   * @param onSuccess the callback to be called when the subcategory is deleted
   * @param onFailure the callback to be called when the subcategory could not be deleted
   */
  fun deleteSubCategory(
      subCategory: AccountingSubCategory,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.postgrest.from(collection).delete {
        filter { SupabaseAccountingSubCategory::uid eq subCategory.uid }
      }
      onSuccess()
    }
  }
}

/** A subcategory of an accounting category representation in the database */
@Serializable
data class SupabaseAccountingSubCategory(
    @SerialName("uid") val uid: String,
    @SerialName("category_uid") val categoryUID: String,
    @SerialName("association_uid") val associationUID: String,
    @SerialName("name") val name: String,
    @SerialName("amount") val amount: Int
) {
  fun toAccountingSubCategory(): AccountingSubCategory {
    return AccountingSubCategory(uid = uid, name = name, amount = amount, categoryUID = categoryUID)
  }
}
