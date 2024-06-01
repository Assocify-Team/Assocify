package com.github.se.assocify.model.database

import com.github.se.assocify.model.CurrentUser
import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class BalanceAPI(private val db: SupabaseClient) : SupabaseApi() {

  private val collectionName = "balance_item"

  private var balanceCache: List<BalanceItem>? = null
  private var balanceCacheAssociationUID: String? = null

  init {
    CurrentUser.associationUid?.let { updateBalanceCache(it, {}, {}) }
  }

  /**
   * Get the balance of an association, but force an update of the cache
   *
   * @param associationUID the unique identifier of the association
   * @param onSuccess the callback to be called when the budget items are retrieved
   * @param onFailure the callback to be called when the budget items could not be retrieved
   */
  fun updateBalanceCache(
      associationUID: String,
      onSuccess: (List<BalanceItem>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      val response =
          db.from(collectionName)
              .select { filter { SupabaseBalanceItem::associationUID eq associationUID } }
              .decodeList<SupabaseBalanceItem>()
              .map { it.toBalanceItem() }
      balanceCache = response
      balanceCacheAssociationUID = associationUID
      onSuccess(response)
    }
  }

  fun getBalance(
      associationUID: String,
      onSuccess: (List<BalanceItem>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (balanceCacheAssociationUID == associationUID && balanceCache != null) {
      onSuccess(balanceCache!!)
    } else {
      updateBalanceCache(associationUID, onSuccess, onFailure)
    }
  }

  fun addBalance(
      associationUID: String,
      categoryUID: String,
      receiptUID: String?,
      balanceItem: BalanceItem,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from(collectionName)
          .insert(
              SupabaseBalanceItem.fromBalanceItem(
                  balanceItem, associationUID, receiptUID, categoryUID))
      if (balanceCacheAssociationUID == associationUID) {
        balanceCache = balanceCache?.plus(balanceItem)
      }
      onSuccess()
    }
  }

  fun updateBalance(
      associationUID: String,
      balanceItem: BalanceItem,
      receiptUID: String?,
      categoryUID: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from(collectionName).update(
          SupabaseBalanceItem.fromBalanceItem(
              balanceItem, associationUID, receiptUID, categoryUID)) {
            filter {
              BalanceAPI.SupabaseBalanceItem::associationUID eq associationUID
              SupabaseBalanceItem::uid eq balanceItem.uid
            }
          }
      if (balanceCacheAssociationUID == associationUID) {
        balanceCache = balanceCache?.map { if (it.uid == balanceItem.uid) balanceItem else it }
      }
      onSuccess()
    }
  }

  fun deleteBalance(balanceItemUID: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      db.from(collectionName).delete { filter { SupabaseBalanceItem::uid eq balanceItemUID } }
      balanceCache = balanceCache?.filter { it.uid != balanceItemUID }
      onSuccess()
    }
  }

  @Serializable
  private data class SupabaseBalanceItem(
      @SerialName("uid") val uid: String,
      @SerialName("name") val nameItem: String,
      @SerialName("association_uid") val associationUID: String,
      @SerialName("receipt_uid") val receiptUID: String?,
      @SerialName("subcategory_uid") val subcategoryUID: String,
      @SerialName("amount") val amount: Int, // unsigned: can be positive or negative
      @SerialName("tva") val tva: Float,
      @SerialName("description") val description: String,
      @SerialName("date") val date: String,
      @SerialName("assignee") val assignee: String,
      @SerialName("status") val status: Status,
  ) {
    fun toBalanceItem(): BalanceItem {
      return BalanceItem(
          uid = uid,
          nameItem = nameItem,
          subcategoryUID = subcategoryUID,
          receiptUID = receiptUID,
          amount = amount,
          tva = TVA.floatToTVA(tva),
          description = description,
          date = LocalDate.parse(date),
          assignee = assignee,
          status = status,
      )
    }

    companion object {
      fun fromBalanceItem(
          balanceItem: BalanceItem,
          associationUID: String,
          receiptUID: String?,
          categoryUID: String
      ) =
          SupabaseBalanceItem(
              uid = balanceItem.uid,
              nameItem = balanceItem.nameItem,
              amount = balanceItem.amount,
              tva = balanceItem.tva.rate,
              description = balanceItem.description,
              date = balanceItem.date.toString(),
              assignee = balanceItem.assignee,
              status = balanceItem.status,
              associationUID = associationUID,
              receiptUID = receiptUID,
              subcategoryUID = categoryUID)
    }
  }
}
