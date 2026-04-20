package com.example.flat_rent_app.presentation.screens.startscreen

import com.example.flat_rent_app.presentation.theme.Dimens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.flat_rent_app.R
import android.content.res.Configuration
import androidx.compose.material3.Scaffold
import androidx.compose.ui.tooling.preview.Preview
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme

@Composable
fun WelcomeScreen(
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    Scaffold { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = Dimens.dp28)
                .padding(top = Dimens.dp130),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_welcome_logo),
                contentDescription = null,
                modifier = Modifier.size(Dimens.dp140)
            )

            Spacer(Modifier.height(Dimens.dp20))

            Text(
                text = stringResource(R.string.welcome_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(Dimens.dp10))

            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.dp52),
                shape = RoundedCornerShape(Dimens.dp28)
            ) {
                Text(stringResource(R.string.action_register))
            }

            Spacer(Modifier.height(Dimens.dp12))

            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.dp52),
                shape = RoundedCornerShape(Dimens.dp28)
            ) {
                Text(stringResource(R.string.action_login))
            }

            Spacer(Modifier.height(Dimens.dp48))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun WelcomeScreenPreviewLight() {
    FlatrentappTheme {
        WelcomeScreen(onRegister = {}, onLogin = {})
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WelcomeScreenPreviewDark() {
    FlatrentappTheme {
        WelcomeScreen(onRegister = {}, onLogin = {})
    }
}
