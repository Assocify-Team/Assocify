package com.github.se.assocify.model.database

import com.github.se.assocify.model.entities.Event
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventAPI(private val db : SupabaseClient) : SupabaseApi() {

    private val scope = CoroutineScope(Dispatchers.Main)
    private val postgrest = db.postgrest
    private val collectionName = "events"

    /**
     * Creates an event in the database
     *
     * @param event the event to create
     * @param onSuccess called on success (by default does nothing)
     * @param onFailure called on failure
     */
    suspend fun createEvent(event: Event, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
        scope.launch {
            try {
                postgrest.from(collectionName).insert(event)
                onSuccess()
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
    suspend fun getEvents(onSuccess: (List<Event>) -> Unit, onFailure: (Exception) -> Unit) {
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
    suspend fun getEvent(id: Int, onSuccess: (Event) -> Unit, onFailure: (Exception) -> Unit) {
        scope.launch {
            try {
                val event = postgrest.from(collectionName).select(columns = Columns.ALL) {
                    filter {
                        Event::uid eq id
                    }
                }
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
    suspend fun updateEvent(event: Event, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
        scope.launch {
            try {
                val updatedEvent = postgrest.from(collectionName)
                    .update(event) {
                        filter {
                            Event::uid eq event.uid
                        }
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
    suspend fun deleteEvent(id: Int, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit) {
        scope.launch {
            try {
                val isSuccess = postgrest.from(collectionName).delete {
                    filter {
                        Event::uid eq id
                    }
                }
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}




