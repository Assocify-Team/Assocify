package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.BalanceItem
import com.github.se.assocify.model.entities.Status
import com.github.se.assocify.model.entities.TVA
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class BalanceAPI(private val db: SupabaseClient) : SupabaseApi() {

  val collectionName = "balance_item"

  fun getBalance(
      associationUID: String,
      onSuccess: (List<BalanceItem>) -> Unit,
      onFailure: (Exception) -> Unit
  ): List<BalanceItem> {

    tryAsync(onFailure) {
      val response =
          db.from(collectionName)
              .select { filter { SupabaseBalanceItem::associationUID eq associationUID } }
              .decodeList<SupabaseBalanceItem>()
      onSuccess(response.map { it.toBalanceItem() })
    }

    return listOf()
  }

  fun addBalance(
      associationUID: String,
      categoryUID: String,
      receiptUID: String,
      balanceItem: BalanceItem,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from(collectionName)
          .insert(
              SupabaseBalanceItem.fromBalanceItem(
                  balanceItem, associationUID, receiptUID, categoryUID))
      onSuccess()
    }
  }

  fun updateBalance(
      associationUID: String,
      balanceItem: BalanceItem,
      receiptUID: String,
      categoryUID: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      db.from(collectionName)
          .update(
              SupabaseBalanceItem.fromBalanceItem(
                  balanceItem, associationUID, receiptUID, categoryUID))
      onSuccess()
    }
  }

  fun deleteBalance(balanceItemUID: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    tryAsync(onFailure) {
      db.from(collectionName).delete { filter { SupabaseBalanceItem::uid eq balanceItemUID } }
      onSuccess()
    }
  }
}

@Serializable
data class SupabaseBalanceItem(
    @SerialName("uid") val uid: String,
    @SerialName("name") val nameItem: String,
    @SerialName("association_uid") val associationUID: String,
    @SerialName("receipt_uid") val receiptUID: String,
    @SerialName("category_uid") val categoryUID: String,
    @SerialName("amount") val amount: Int, // unsigned: can be positive or negative
    @SerialName("tva") val tva: TVA,
    @SerialName("description") val description: String,
    @SerialName("date") val date: String,
    @SerialName("assignee") val assignee: String,
    @SerialName("status") val status: Status,
) {
  fun toBalanceItem(): BalanceItem {
    return BalanceItem(
        uid = uid,
        nameItem = nameItem,
        amount = amount,
        tva = tva,
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
        receiptUID: String,
        categoryUID: String
    ) =
        SupabaseBalanceItem(
            uid = balanceItem.uid,
            nameItem = balanceItem.nameItem,
            amount = balanceItem.amount,
            tva = balanceItem.tva,
            description = balanceItem.description,
            date = balanceItem.date.toString(),
            assignee = balanceItem.assignee,
            status = balanceItem.status,
            associationUID = associationUID,
            receiptUID = receiptUID,
            categoryUID = categoryUID)
  }
}
