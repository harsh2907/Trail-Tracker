package com.example.trailtracker.mainScreen.presentation.screens.home.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.trailtracker.R
import com.example.trailtracker.mainScreen.domain.models.User
import com.example.trailtracker.ui.theme.UiColors
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.SortType
import com.example.trailtracker.utils.formatTime
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    user: User,
    state: AllSessionsState,
    onSortTypeChanged: (SortType) -> Unit,
    navigateToSession: () -> Unit
) {
    var isSortMenuVisible by remember { mutableStateOf(false) }

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

                items(state.runSessions) { run ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .animateItemPlacement(),
                        shape = ShapeDefaults.Medium,
                        colors = CardDefaults.cardColors(
                            containerColor = UiColors.EerieBlack,
                            contentColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            if (run.imageUrl != null) {
                                AsyncImage(
                                    model = run.imageUrl,
                                    contentDescription = "map",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .aspectRatio(16 / 9f)
                                        .clip(ShapeDefaults.Medium)
                                )
                            }

                            RunningTimeSection(
                                modifier = Modifier.padding(vertical = 8.dp),
                                duration = run.sessionDuration.formatTime()
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(6.dp)
                            )

                            RunningDateSection(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                dateTime = Constants.convertEpochToFormattedDate(run.createdAt)
                            )

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Distance",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )

                                    Text(
                                        text = "${run.distanceCovered.roundToInt()} m",
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Avg. Speed",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )

                                    Text(
                                        text = "${run.averageSpeedInKPH.roundToInt()} km/h",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }


                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun RunningTimeSection(
    duration: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total Running Time",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RunningDateSection(
    dateTime: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = dateTime,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
