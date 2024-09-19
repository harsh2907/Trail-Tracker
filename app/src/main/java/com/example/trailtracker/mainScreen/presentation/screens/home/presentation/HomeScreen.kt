package com.example.trailtracker.mainScreen.presentation.screens.home.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.trailtracker.R
import com.example.trailtracker.mainScreen.domain.models.Run
import com.example.trailtracker.mainScreen.domain.models.User
import com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation.DeleteSessionDialog
import com.example.trailtracker.ui.theme.UiColors
import com.example.trailtracker.utils.SortType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    user: User,
    state: AllSessionsState,
    onSortTypeChanged: (SortType) -> Unit,
    navigateToSession: () -> Unit,
    onDeleteSession: (run: Run) -> Unit
) {
    var isSortMenuVisible by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var sessionToDelete: Run? by remember { mutableStateOf(null) }

    AnimatedVisibility(visible = showDeleteDialog) {
        DeleteSessionDialog(
            onCancel = {
                showDeleteDialog = false
                sessionToDelete = null
            },
            onDelete = {
                sessionToDelete?.let {
                    showDeleteDialog = false
                    onDeleteSession(it)
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = user.username.takeWhile { it != ' ' }) },
                actions = {
                    IconButton(onClick = {
                        isSortMenuVisible = true
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "sort"
                        )
                    }

                    DropdownMenu(
                        expanded = isSortMenuVisible,
                        onDismissRequest = { isSortMenuVisible = false }
                    ) {
                        SortType.entries.forEach { sortType ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = sortType.name.lowercase()
                                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                    )
                                },
                                onClick = {
                                    isSortMenuVisible = false
                                    onSortTypeChanged(sortType)
                                }
                            )
                        }
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = navigateToSession,
                containerColor = UiColors.primaryColor
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.running_person),
                    contentDescription = "run",
                    tint = Color.Black
                )
            }
        }
    ) { paddingValues ->

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                item {
                    if (state.runSessions.isEmpty()) {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No Sessions Yet",
                                style = MaterialTheme.typography.headlineMedium,
                            )

                            Text(
                                text = "Try doing some sessions and see your results here!",
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                }

                items(state.runSessions, key = { it.id }) { run ->
                    RunSessionCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .animateItemPlacement(),
                        run = run,
                        onLongClick = {
                            sessionToDelete = run
                            showDeleteDialog = true
                        },
                        onClick = {

                        }
                    )
                }
            }
        }
    }
}
