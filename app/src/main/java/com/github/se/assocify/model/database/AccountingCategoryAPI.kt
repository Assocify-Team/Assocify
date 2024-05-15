package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.AccountingCategory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AccountingCategoryAPI(val db: SupabaseClient) : SupabaseApi() {

  private val collectionName = "accounting_category"

  private var categoryCache: List<AccountingCategory>? = null
  private var categoryCacheAssociationUID: String? = null

  /**
   * Get the categories of an association, but force an update of the cache
   *
   * @param associationUID the unique identifier of the association
   * @param onSuccess the callback to be called when the categories are retrieved
   * @param onFailure the callback to be called when the categories could not be retrieved
   */
  fun updateCategoryCache(
      associationUID: String,
      onSuccess: (List<AccountingCategory>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      val categories =
          db.from(collectionName)
              .select { filter { SupabaseAccountingCategory::associationUID eq associationUID } }
              .decodeList<SupabaseAccountingCategory>()
              .map { it.toAccountingCategory() }
      categoryCache = categories
      categoryCacheAssociationUID = associationUID
      onSuccess(categories)
    }
  }

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
    if (categoryCacheAssociationUID == associationUID && categoryCache != null) {
      categoryCache?.let { onSuccess(it) }
    } else {
      updateCategoryCache(associationUID, onSuccess, onFailure)
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
      db.from(collectionName)
          .insert(
              SupabaseAccountingCategory(
                  uid = category.uid, associationUID = associationUID, name = category.name))

      if (categoryCacheAssociationUID == associationUID) {
        categoryCache = categoryCache?.plus(category)
      }
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
      db.from(collectionName).update({ SupabaseAccountingCategory::name setTo category.name }) {
        filter {
          SupabaseAccountingCategory::uid eq category.uid
          SupabaseAccountingCategory::associationUID eq associationUID
        }
      }

      if (categoryCacheAssociationUID == associationUID) {
        categoryCache =
            categoryCache?.map {
              if (it.uid == category.uid) {
                category
              } else {
                it
              }
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
      db.from(collectionName).delete { filter { SupabaseAccountingCategory::uid eq category.uid } }

      categoryCache = categoryCache?.filter { it.uid != category.uid }

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
