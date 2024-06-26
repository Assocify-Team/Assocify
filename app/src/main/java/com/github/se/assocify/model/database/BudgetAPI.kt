package com.github.se.assocify.model.database

import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.BudgetItem
import com.github.se.assocify.model.entities.TVA
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class BudgetAPI(val db: SupabaseClient) : SupabaseApi() {

  private val collectionName = "budget_item"

  private var budgetCache: List<BudgetItem>? = null
  private var budgetCacheAssociationUID: String? = null

  init {
    CurrentUser.associationUid?.let { updateBudgetCache(it, {}, {}) }
  }

  /**
   * Get the budget of an association, but force an update of the cache
   *
   * @param associationUID the unique identifier of the association
   * @param onSuccess the callback to be called when the budget items are retrieved
   * @param onFailure the callback to be called when the budget items could not be retrieved
   */
  fun updateBudgetCache(
      associationUID: String,
      onSuccess: (List<BudgetItem>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      val response =
          db.from(collectionName)
              .select { filter { SupabaseBudgetItem::associationUID eq associationUID } }
              .decodeList<SupabaseBudgetItem>()
              .map { it.toBudgetItem() }
      budgetCache = response
      budgetCacheAssociationUID = associationUID
      onSuccess(response)
    }
  }

  /**
   * Get the budget of an association
   *
   * @param associationUID the unique identifier of the association
   * @param onSuccess the callback to be called when the budget items are retrieved
   * @param onFailure the callback to be called when the budget items could not be retrieved
   */
  fun getBudget(
      associationUID: String,
      onSuccess: (List<BudgetItem>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (budgetCacheAssociationUID == associationUID && budgetCache != null) {
      onSuccess(budgetCache!!)
      return
    }

    updateBudgetCache(associationUID, onSuccess, onFailure)
  }

  /**
   * Add the budget items of an association
   *
   * @param associationUID the unique identifier of the association
   */
  fun addBudgetItem(
      associationUID: String,
      budgetItem: BudgetItem,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from(collectionName).insert(SupabaseBudgetItem.fromBudgetItem(budgetItem, associationUID))

      // Update cache
      if (budgetCacheAssociationUID == associationUID) {
        budgetCache = budgetCache.orEmpty() + budgetItem
      }

      onSuccess()
    }
  }
  /**
   * Update a budget item
   *
   * @param associationUID the unique identifier of the association
   * @param budgetItem the budget item to update
   * @param onSuccess the callback to be called when the budget item is updated
   * @param onFailure the callback to be called when the budget item could not be updated
   */
  fun updateBudgetItem(
      associationUID: String,
      budgetItem: BudgetItem,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from(collectionName).update({
        BudgetAPI.SupabaseBudgetItem::name setTo budgetItem.nameItem
        BudgetAPI.SupabaseBudgetItem::description setTo budgetItem.description
        BudgetAPI.SupabaseBudgetItem::amount setTo budgetItem.amount
        BudgetAPI.SupabaseBudgetItem::year setTo budgetItem.year
        BudgetAPI.SupabaseBudgetItem::tva setTo budgetItem.tva.rate
        BudgetAPI.SupabaseBudgetItem::subcategoryUID setTo budgetItem.subcategoryUID
      }) {
        filter {
          BudgetAPI.SupabaseBudgetItem::associationUID eq associationUID
          SupabaseBudgetItem::uid eq budgetItem.uid
        }
      }

      // Update cache
      if (budgetCacheAssociationUID == associationUID) {
        budgetCache = budgetCache.orEmpty().map { if (it.uid == budgetItem.uid) budgetItem else it }
      }

      onSuccess()
    }
  }
  /**
   * Delete a budget item
   *
   * @param budgetItemUID the unique identifier of the budget item
   * @param onSuccess the callback to be called when the budget item is deleted
   * @param onFailure the callback to be called when the budget item could not be deleted
   */
  fun deleteBudgetItem(
      budgetItemUID: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from(collectionName).delete { filter { SupabaseBudgetItem::uid eq budgetItemUID } }

      // Update cache
      budgetCache = budgetCache.orEmpty().filter { it.uid != budgetItemUID }

      onSuccess()
    }
  }

  @Serializable
  data class SupabaseBudgetItem(
      @SerialName("uid") val uid: String,
      @SerialName("association_uid") val associationUID: String,
      @SerialName("name") val name: String,
      @SerialName("description") val description: String,
      @SerialName("amount") val amount: Int,
      @SerialName("year") val year: Int,
      @SerialName("tva") val tva: Float,
      @SerialName("subcategory_uid") val subcategoryUID: String
  ) {
    fun toBudgetItem(): BudgetItem {
      return BudgetItem(
          uid = uid,
          nameItem = name,
          amount = amount,
          description = description,
          year = year,
          subcategoryUID = subcategoryUID,
          tva = TVA.floatToTVA(tva))
    }

    companion object {
      fun fromBudgetItem(budgetItem: BudgetItem, associationUID: String) =
          SupabaseBudgetItem(
              uid = budgetItem.uid,
              associationUID = associationUID,
              name = budgetItem.nameItem,
              description = budgetItem.description,
              amount = budgetItem.amount,
              year = budgetItem.year,
              tva = budgetItem.tva.rate,
              subcategoryUID = budgetItem.subcategoryUID)
    }
  }
}
