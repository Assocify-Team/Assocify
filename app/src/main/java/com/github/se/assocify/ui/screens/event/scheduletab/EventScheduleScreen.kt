package com.github.se.assocify.ui.screens.event.scheduletab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.se.assocify.model.entities.Task
import com.github.se.assocify.ui.composables.CenteredCircularIndicator
import com.github.se.assocify.ui.composables.DatePickerWithDialog
import com.github.se.assocify.ui.composables.ErrorMessage
import com.github.se.assocify.ui.util.DateTimeUtil
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.max

/** A screen that displays the schedule of a staffer of an event. */
@Composable
fun EventScheduleScreen(
    viewModel: EventScheduleViewModel,
) {
  val state by viewModel.uiState.collectAsState()

  val hourHeight = 60.dp

  // Scroll to 8:00 on page load
  val scrollStartHour = 8
  val initialScroll = with(LocalDensity.current) { hourHeight.toPx() }.toInt() * scrollStartHour
  val scrollState = rememberScrollState(initialScroll)

  if (state.loading) {
    CenteredCircularIndicator()
    return
  }

  if (state.error != null) {
    ErrorMessage(errorMessage = state.error) { viewModel.fetchTasks() }
    return
  }

  Column {
    DateSwitcher(viewModel)
    Row(modifier = Modifier.verticalScroll(scrollState)) {
      ScheduleSidebar(hourHeight = hourHeight)
      ScheduleContent(hourHeight = hourHeight, tasks = state.currentDayTasks, viewModel = viewModel)
    }
  }
}

/* ------------------- Composables ------------------- */

/**
 * Layout displaying the schedule of a staffer of an event.
 *
 * @param hourHeight The height of an hour in the schedule.
 * @param hourNum The number of hours to display in the schedule.
 * @param tasks The tasks to display in the schedule.
 */
@Composable
fun ScheduleContent(
    hourHeight: Dp,
    hourNum: Int = 24,
    tasks: List<OverlapTask>,
    viewModel: EventScheduleViewModel
) {
  val divColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
  Layout(
      content = {
        tasks.forEach { overlapTask ->
          val task = overlapTask.task

          // startOffset determines the y position of the task
          val startTime = DateTimeUtil.toLocalTime(task.startTime)
          val startHour = startTime.hour
          val startMinute = startTime.minute
          val startOffset = hourHeight * (startHour + startMinute / 60f)

          // duration determines the duration of the task (in hours), with a minimum of 30 minutes
          val duration = max((task.duration.toDouble() / 60), 0.5)

          // the number of lines to display in the task (2 if the duration is >= 1h)
          val lines = if (duration > 1) 2 else 1

          Box(
              modifier =
                  Modifier.height((duration * hourHeight.value).dp)
                      .padding(horizontal = 4.dp, vertical = 2.dp)
                      .offset(y = startOffset)
                      .clickable { viewModel.openTask(task.uid) }) {
                ScheduleTask(task, lines)
              }
        }
      },
      modifier =
          Modifier.padding(top = 11.dp)
              .fillMaxWidth()
              .height((hourNum * hourHeight.value).dp)
              // Draw horizontal lines for hours behind the schedule
              .drawBehind {
                repeat(hourNum) {
                  drawLine(
                      divColor,
                      start = Offset(0f, it * hourHeight.toPx()),
                      end = Offset(size.width, it * hourHeight.toPx()),
                      strokeWidth = 1.dp.toPx())
                }
              }) { measurables, constraints ->
        val placeables =
            measurables.zip(tasks).map { (measurable, task) ->
              val width = constraints.maxWidth / task.overlaps
              val height =
                  measurable.minIntrinsicHeight(0) // The height of the task is already determined
              measurable.measure(
                  constraints.copy(
                      minHeight = height, maxHeight = height, minWidth = width, maxWidth = width))
            }
        layout(constraints.maxWidth, constraints.maxHeight) {
          placeables.zip(tasks).forEach { (placeable, overlapTask) ->
            val xOffset = constraints.maxWidth / overlapTask.overlaps * overlapTask.order
            placeable.place(
                xOffset, 0) // All tasks are placed at (0, 0) because they are already offset
          }
        }
      }
}

/**
 * A task in the schedule
 *
 * @param task The task to display
 */
@Composable
fun ScheduleTask(task: Task, lines: Int) {
  Column(
      modifier =
          Modifier.fillMaxSize()
              .clipToBounds()
              .background(
                  color = MaterialTheme.colorScheme.primaryContainer,
                  shape = RoundedCornerShape(4.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)) {
        Text(
            task.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium)
        if (lines > 1) {
          Text(
              task.category,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              style = MaterialTheme.typography.bodySmall)
          // TODO: Add more task details ?
        }
      }
}

/** The current date, with buttons to switch to the previous and next date. */
@Composable
fun DateSwitcher(viewModel: EventScheduleViewModel) {
  val state by viewModel.uiState.collectAsState()
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { viewModel.previousDate() }) {
          Icon(Icons.AutoMirrored.Filled.ArrowLeft, contentDescription = "Previous date")
        }
        DatePickerWithDialog(
            value = state.dateText,
            onDateSelect = { viewModel.changeDate(it) },
            textFieldFormat = false)
        IconButton(onClick = { viewModel.nextDate() }) {
          Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "Next date")
        }
      }
}

/** Sidebar displaying the hours of the schedule */
@Composable
fun ScheduleSidebar(
    hourHeight: Dp,
    minHour: Int = 0,
    maxHour: Int = 24,
) {
  val numHours = maxHour - minHour
  Column {
    repeat(numHours) { i ->
      Box(modifier = Modifier.height(hourHeight)) {
        Text(
            text = LocalTime.of(minHour + i, 0).format(DateTimeFormatter.ofPattern("HH:mm")),
            modifier = Modifier.fillMaxHeight().padding(4.dp),
            style = MaterialTheme.typography.bodySmall)
      }
    }
  }
}
