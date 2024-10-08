package com.example.trailtracker.mainScreen.presentation.screens.runningSession.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.trailtracker.ui.theme.UiColors

@Composable
fun EndSessionDialog(
    isLoading: Boolean,
    onResumeSession: () -> Unit,
    onFinishSession: () -> Unit,
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.5f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Card(
                    modifier = Modifier.size(100.dp),
                    shape = ShapeDefaults.Medium,
                    colors = CardDefaults.elevatedCardColors(containerColor = UiColors.EerieBlack)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                AlertDialog(
                    title = {
                        Text(text = "Running is paused")
                    },
                    text = {
                        Text(text = "Do you want to resume or finish this session?")
                    },
                    containerColor = UiColors.EerieBlack,
                    onDismissRequest = {},
                    confirmButton = {
                        Button(
                            onClick = onResumeSession,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = UiColors.primaryColor
                            )
                        ) {
                            Text(
                                text = "Resume",
                                color = Color.Black
                            )
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = onFinishSession,
                        ) {
                            Text(
                                text = "Finish",
                                color = Color.White
                            )
                        }
                    },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                )
            }

        }
    }
}



@Composable
fun DeleteSessionDialog(
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.5f)
                ),
            contentAlignment = Alignment.Center
        ) {

            AlertDialog(
                title = {
                    Text(text = "Delete Session?")
                },
                text = {
                    Text(text = "Do you want to delete this session?")
                },
                containerColor = UiColors.EerieBlack,
                onDismissRequest = onCancel,
                confirmButton = {
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UiColors.primaryColor
                        )
                    ) {
                        Text(
                            text = "Delete",
                            color = Color.Black
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = onCancel,
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.White
                        )
                    }
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            )
        }
    }
}

@Composable
fun SignOutDialog(
    onCancel: () -> Unit,
    onSignOut: () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.5f)
                ),
            contentAlignment = Alignment.Center
        ) {

            AlertDialog(
                title = {
                    Text(text = "Sign out")
                },
                text = {
                    Text(text = "Do you want to sign out?")
                },
                containerColor = UiColors.EerieBlack,
                onDismissRequest = onCancel,
                confirmButton = {
                    Button(
                        onClick = onSignOut,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UiColors.primaryColor
                        )
                    ) {
                        Text(
                            text = "Sign Out",
                            color = Color.Black
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = onCancel,
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.White
                        )
                    }
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            )
        }
    }
}