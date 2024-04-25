package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Event
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventAPI(private val db: SupabaseClient) : SupabaseApi() {

  private val scope = CoroutineScope(Dispatchers.Main)
  private val postgrest = db.postgrest
  private val collectionName = "event"

  /**
   * Creates an event in the database
   *
   * @param event the event to create
   * @param onSuccess called on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun addEvent(event: Event, onSuccess: (String) -> Unit = {}, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        val resp = postgrest.from(collectionName).insert(event).decodeAs<Event>().uid
        onSuccess(resp)
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Gets all events from the database
   *
   * @param onSuccess called on success with the list of events
   * @param onFailure called on failure
   */
  fun getEvents(onSuccess: (List<Event>) -> Unit, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        val events = postgrest.from(collectionName).select().decodeList<Event>()
        onSuccess(events)
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Gets an event from the database
   *
   * @param id the id of the event to get
   * @param onSuccess called on success with the event
   * @param onFailure called on failure
   */
  fun getEvent(id: String, onSuccess: (Event) -> Unit, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        val event = postgrest.from(collectionName).select { filter { Event::uid eq id } }
        onSuccess(event.decodeAs())
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Updates an event in the database
   *
   * @param event the event to update
   * @param onSuccess called on success with the updated event (by default does nothing)
   * @param onFailure called on failure
   */
  fun updateEvent(event: Event, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        postgrest.from(collectionName).update(event) { filter { Event::uid eq event.uid } }
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  /**
   * Deletes an event from the database
   *
   * @param id the id of the event to delete
   * @param onSuccess called on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun deleteEvent(id: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
    scope.launch {
      try {
        postgrest.from(collectionName).delete { filter { Event::uid eq id } }
        onSuccess()
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }
}
