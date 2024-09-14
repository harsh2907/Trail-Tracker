package com.example.trailtracker.mainScreen.presentation.screens.profile.presentation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.trailtracker.R
import com.example.trailtracker.mainScreen.domain.models.User
import com.example.trailtracker.ui.theme.UiColors

@Composable
fun ProfileScreen(
    currentUser: User,
    selectedImageUri: Uri?,
    totalDistance: Double,
    totalDuration: Long,
    averageSpeed: Double,
    totalCalories: Int,
    onProfileImageClicked:()->Unit
) {
    val profileSize = LocalConfiguration.current.screenWidthDp * 0.3
    val context = LocalContext.current


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest
                    .Builder(context)
                    .data(selectedImageUri ?: currentUser.profileImageUrl)
                    .build(),
                contentDescription = "profile image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(profileSize.dp)
                    .clip(CircleShape)
                    .background(UiColors.CardColorOffWhite)
                    .align(Alignment.Center)
                    .clickable { onProfileImageClicked() },
                placeholder = rememberAsyncImagePainter(
                    model = ImageRequest
                        .Builder(context)
                        .data(R.drawable.profile_placeholder)
                        .build()
                )
            )

        }

        Text(
            text = currentUser.username,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = currentUser.email,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileStatsCard(
            totalDistance = totalDistance,
            totalTime = totalDuration,
            avgSpeed = averageSpeed,
            totalCalories = totalCalories
        )

    }

}

@Composable
private fun ProfileStatsCard(
    totalDistance: Double,
    totalTime: Long,
    avgSpeed: Double,
    totalCalories: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .height(120.dp)
                    .weight(1f)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = UiColors.EerieBlack,
                    contentColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Total Distance"
                    )
                    Text(
                        text = "${String.format("%.1f", totalDistance)} km"
                    )
                }
            }

            Card(
                modifier = Modifier
                    .height(120.dp)
                    .weight(1f)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = UiColors.EerieBlack,
                    contentColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Total Time Spend")
                    Text(text = "$totalTime min")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .height(120.dp)
                    .weight(1f)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = UiColors.EerieBlack,
                    contentColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Average Speed")
                    Text(text = "${String.format("%.1f", avgSpeed)} km/h")
                }
            }

            Card(
                modifier = Modifier
                    .height(120.dp)
                    .weight(1f)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = UiColors.EerieBlack,
                    contentColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Calories Burned")
                    Text(text = "$totalCalories kcal")
                }
            }
        }
    }
}