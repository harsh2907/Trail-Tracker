package com.example.trailtracker.mainScreen.presentation.screens.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLazyColumn(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    indicatorColor: Color = Color.White,
    onRefresh:  () -> Unit, // Make onRefresh a suspend function
    lazyListState: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val currentOnRefresh by rememberUpdatedState(onRefresh)

    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = onRefresh) {
        if (isRefreshing) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content
            )
        }
    }

/*    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection),
        contentAlignment = Alignment.Center
    ) {
        if (isRefreshing) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content
            )
        }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                pullToRefreshState.startRefresh()
            } else {
                pullToRefreshState.endRefresh()
            }
        }

        LaunchedEffect(pullToRefreshState.isRefreshing) {
            if (pullToRefreshState.isRefreshing) {
                currentOnRefresh()
                delay(1000)
                pullToRefreshState.endRefresh() // End the refresh after the refresh operation
            }
        }

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            containerColor = indicatorColor
        )
    }*/
}
