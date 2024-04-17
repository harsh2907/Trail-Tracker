package com.example.trailtracker.onboardingDetails.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trailtracker.ui.theme.TrailTrackerTheme
import com.example.trailtracker.ui.theme.UiColors

@Composable
fun OnboardingDetailsScreen(
    onContinue: (name: String, weight: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val isButtonEnable by remember {
        derivedStateOf {
            name.isNotBlank() && name.length > 3 && weight.isNotBlank() && weight.toInt() > 20
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = "Please enter your name and weight",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))


            TextField(
                value = name, onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Enter your name")
                },
                shape = ShapeDefaults.Medium,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = UiColors.EerieBlack,
                    focusedContainerColor = UiColors.EerieBlack,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = UiColors.primaryColor
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                })
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = weight, onValueChange = { weight = it.take(3) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Enter your weight in kg")
                },
                shape = ShapeDefaults.Medium,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = UiColors.EerieBlack,
                    focusedContainerColor = UiColors.EerieBlack,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = UiColors.primaryColor
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                })
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(UiColors.EerieBlack),
            contentAlignment = Alignment.TopEnd
        ) {
            TextButton(
                enabled = isButtonEnable,
                onClick = {
                    onContinue(name, weight)
                }) {
                Text(text = "Continue")
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnBoardingDetailsPreview() {
    TrailTrackerTheme {
        OnboardingDetailsScreen(
            onContinue = { a, b -> }
        )
    }
}