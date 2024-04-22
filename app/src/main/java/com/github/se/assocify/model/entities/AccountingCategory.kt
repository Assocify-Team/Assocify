package com.github.se.assocify.model.entities

/** Represents the category of an accounting item. */
// TODO: this should be in DB
// admin or presidence should be able to add new categories or modify it
enum class AccountingCategory {
  POLE,
  EVENT,
  FEES,
  COMMISSION
}
