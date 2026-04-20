package com.example.flat_rent_app.presentation.screens.mainscreen

import com.example.flat_rent_app.presentation.theme.TextSizes

import com.example.flat_rent_app.presentation.theme.Dimens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
        verticalArrangement = Arrangement.spacedBy(Dimens.dp16),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.dp32)
            .padding(bottom = Dimens.dp48, top = Dimens.dp16)
    ) {
        Text(
            text = stringResource(R.string.match_title),
            fontSize = TextSizes.sp36,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.scale(scale.value)
        )

        Text(
            text = stringResource(R.string.match_subtitle),
            fontSize = TextSizes.sp15,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(Dimens.dp8))

        Button(
            onClick = onSendMessage,
            modifier = Modifier.fillMaxWidth().height(Dimens.dp52),
            shape = RoundedCornerShape(Dimens.dp16),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.match_write),
                fontSize = TextSizes.sp16,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(Dimens.dp52),
            shape = RoundedCornerShape(Dimens.dp16),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = stringResource(R.string.match_continue),
                fontSize = TextSizes.sp16
            )
        }
    }
}
