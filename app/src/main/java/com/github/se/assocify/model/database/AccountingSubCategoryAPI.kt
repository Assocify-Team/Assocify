package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.AccountingSubCategory
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AccountingSubCategoryAPI(val db: SupabaseClient) : SupabaseApi() {
  val collection = "accounting_subcategory"

  fun getSubCategories(
      categoryUID: String,
      onSuccess: (List<AccountingSubCategory>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    tryAsync(onFailure) {
      val subCategories =
          db.postgrest
              .from(collection)
              .select { filter { SupabaseAccountingSubCategory::categoryUID eq categoryUID } }
              .decodeList<SupabaseAccountingSubCategory>()
      onSuccess(subCategories.map { it.toAccountingSubCategory() })
    }
  }

  fun addSubCategory(
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
                  name = subCategory.name,
                  amount = subCategory.amount))
      onSuccess()
    }
  }

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

@Serializable
data class SupabaseAccountingSubCategory(
    @SerialName("uid") val uid: String,
    @SerialName("category_uid") val categoryUID: String,
    @SerialName("name") val name: String,
    @SerialName("amount") val amount: Int
) {
  fun toAccountingSubCategory(): AccountingSubCategory {
    return AccountingSubCategory(uid = uid, name = name, amount = amount)
  }
}
