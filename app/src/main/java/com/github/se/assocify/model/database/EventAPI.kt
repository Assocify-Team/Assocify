package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Event
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import java.time.OffsetDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

        postgrest
            .from(collectionName)
            .insert(
                SupabaseEvent(
                    uid = event.uid,
                    name = event.name,
                    description = event.description,
                    startDate = event.startDate.toString(),
                    endDate = event.endDate.toString(),
                    guestsOrArtists = event.guestsOrArtists,
                    location = event.location))
        onSuccess(event.uid)
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
        val events = postgrest.from(collectionName).select().decodeList<SupabaseEvent>()
        onSuccess(events.map { it.toEvent() })
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
        val event =
            postgrest
                .from(collectionName)
                .select {
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
    updateEvent(
        uid = event.uid,
        name = event.name,
        description = event.description,
        startDate = event.startDate,
        endDate = event.endDate,
        guestsOrArtists = event.guestsOrArtists,
        location = event.location,
        onSuccess = onSuccess,
        onFailure = onFailure)
  }

  /**
   * Updates an event in the database
   *
   * @param uid the id of the event to update
   * @param name the name of the event
   * @param description the description of the event
   * @param startDate the start date of the event
   * @param endDate the end date of the event
   * @param guestsOrArtists the guests or artists of the event
   * @param location the location of the event
   * @param onSuccess called on success (by default does nothing)
   * @param onFailure called on failure
   */
  fun updateEvent(
      uid: String,
      name: String,
      description: String,
      startDate: OffsetDateTime,
      endDate: OffsetDateTime,
      guestsOrArtists: String,
      location: String,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit
  ) {
    scope.launch {
      try {
        postgrest.from(collectionName).update({
          SupabaseEvent::name setTo name
          SupabaseEvent::description setTo description
          SupabaseEvent::startDate setTo startDate.toString()
          SupabaseEvent::endDate setTo endDate.toString()
          SupabaseEvent::guestsOrArtists setTo guestsOrArtists
          SupabaseEvent::location setTo location
        }) {
          filter { Event::uid eq uid }
        }
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
      val uid: String,
      val name: String,
      val description: String,
      @SerialName("start_date") val startDate: String,
      @SerialName("end_date") val endDate: String,
      @SerialName("guests_or_artists") val guestsOrArtists: String,
      val location: String
  ) {
    fun toEvent() =
        Event(
            uid = uid,
            name = name,
            description = description,
            startDate = OffsetDateTime.parse(startDate),
            endDate = OffsetDateTime.parse(endDate),
            guestsOrArtists = guestsOrArtists,
            location = location)
  }
}
