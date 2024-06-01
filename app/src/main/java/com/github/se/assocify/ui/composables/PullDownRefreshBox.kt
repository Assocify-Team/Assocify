package com.github.se.assocify.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A container to make a scrollable content pullable to refresh.
 *
 * This box is meant to be used as a wrapper around a vertically scrolling element, such as a Column
 * or LazyColumn. It will allow the user to pull down on the content to refresh it. The box will
 * automatically show a loading indicator when the content is refreshing.
 *
 * The box will take up the entire size of its parent (typically a Scaffold), and will apply the
 * padding values to the content inside the box.
 *
 * NOTE: Pass the scaffold padding values to the box itself, NOT the content inside the box.
 *
 * @param refreshing Whether the content is currently refreshing.
 * @param onRefresh The callback to call when the user pulls down to refresh.
 * @param paddingValues The padding values to apply to the container.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullDownRefreshBox(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    paddingValues: PaddingValues? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
  val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh)

  Box(
      modifier =
          modifier
              .padding(paddingValues ?: PaddingValues(0.dp))
              .fillMaxSize()
              .pullRefresh(pullRefreshState)) {
        content()
        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
      }
}
