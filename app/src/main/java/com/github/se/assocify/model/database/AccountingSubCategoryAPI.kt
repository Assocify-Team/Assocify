package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.AccountingSubCategory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
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

  private var subCategoryCache: List<AccountingSubCategory>? = null
  private var subCategoryCacheAssociationUID: String? = null

  /**
   * Get the subcategories of an association, but force an update of the cache
   *
   * @param associationUID the unique identifier of the association
   * @param onSuccess the callback to be called when the subcategories are retrieved
   * @param onFailure the callback to be called when the subcategories could not be retrieved
   */
  fun updateSubCategoryCache(
      associationUID: String,
      onSuccess: (List<AccountingSubCategory>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      val subCategories =
          db.from(collection)
              .select { filter { SupabaseAccountingSubCategory::associationUID eq associationUID } }
              .decodeList<SupabaseAccountingSubCategory>()
              .map { it.toAccountingSubCategory() }
      subCategoryCache = subCategories
      subCategoryCacheAssociationUID = associationUID
      onSuccess(subCategories)
    }
  }

  /**
   * Get the subcategories of an association
   *
   * @param associationUID the unique identifier of the category
   * @param onSuccess the callback to be called when the subcategories are retrieved
   * @param onFailure the callback to be called when the subcategories could not be retrieved
   */
  fun getSubCategories(
      associationUID: String,
      onSuccess: (List<AccountingSubCategory>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (subCategoryCacheAssociationUID == associationUID && subCategoryCache != null) {
      onSuccess(subCategoryCache!!)
    } else {
      updateSubCategoryCache(associationUID, onSuccess, onFailure)
    }
  }

  /**
   * Add a subcategory to a category
   *
   * @param associationUID the unique identifier of the association
   * @param subCategory the subcategory to add
   * @param onSuccess the callback to be called when the subcategory is added
   * @param onFailure the callback to be called when the subcategory could not be added
   */
  fun addSubCategory(
      associationUID: String,
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
                  categoryUID = subCategory.categoryUID,
                  associationUID = associationUID,
                  name = subCategory.name,
                  amount = subCategory.amount,
                  year = subCategory.year))

      if (subCategoryCacheAssociationUID == associationUID) {
        subCategoryCache = subCategoryCache?.plus(subCategory)
      }
      onSuccess()
    }
  }

  /**
   * Update a subcategory
   *
   * @param subCategory the subcategory to update
   * @param onSuccess the callback to be called when the subcategory is updated
   * @param onFailure the callback to be called when the subcategory could not be updated
   */
  fun updateSubCategory(
      subCategory: AccountingSubCategory,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.postgrest.from(collection).update({
        SupabaseAccountingSubCategory::name setTo subCategory.name
        SupabaseAccountingSubCategory::amount setTo subCategory.amount
        SupabaseAccountingSubCategory::year setTo subCategory.year
        SupabaseAccountingSubCategory::categoryUID setTo subCategory.categoryUID
      }) {
        filter { SupabaseAccountingSubCategory::uid eq subCategory.uid }
      }

      subCategoryCache =
          subCategoryCache?.map {
            if (it.uid == subCategory.uid) {
              subCategory
            } else {
              it
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

      subCategoryCache = subCategoryCache?.filter { it.uid != subCategory.uid }
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
    @SerialName("amount") val amount: Int,
    @SerialName("year") val year: Int
) {
  fun toAccountingSubCategory(): AccountingSubCategory {
    return AccountingSubCategory(
        uid = uid, name = name, amount = amount, categoryUID = categoryUID, year = year)
  }
}
