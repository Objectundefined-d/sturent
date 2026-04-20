package com.example.flat_rent_app.presentation.screens.mainscreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flat_rent_app.R

@Composable
fun MatchScreen(
    onContinue: () -> Unit,
    onSendMessage: () -> Unit
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 48.dp, top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.match_title),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.scale(scale.value)
        )

        Text(
            text = stringResource(R.string.match_subtitle),
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onSendMessage,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text(
                text = stringResource(R.string.match_write),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text(
                text = stringResource(R.string.match_continue),
                fontSize = 16.sp
            )
        }
    }
}
