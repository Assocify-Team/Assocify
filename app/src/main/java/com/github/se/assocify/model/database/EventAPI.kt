package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Event
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
        val event = postgrest.from(collectionName).select {
          filter { Event::uid eq id }
          limit(1)
          single()
        }
          .decodeAs<SupabaseEvent>()
        onSuccess(event.toEvent())
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

  @Serializable
  private data class SupabaseEvent(
      val uid: String? = null,
      val name: String,
      val description: String,
      @SerialName("start_date") val startDate: String,
      @SerialName("end_date") val endDate: String,
      @SerialName("guest_or_artists") val guestsOrArtists: String,
      val location: String
  ){
    fun toEvent() = Event(
        uid = uid ?: "",
        name = name,
        description = description,
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        guestsOrArtists = guestsOrArtists,
        location = location
    )
  }
}
