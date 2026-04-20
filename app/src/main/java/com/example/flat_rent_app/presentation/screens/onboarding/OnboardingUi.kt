package com.example.flat_rent_app.presentation.screens.onboarding

import com.example.flat_rent_app.presentation.theme.Dimens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.flat_rent_app.R

private const val CHIP_FILL_FRACTION = 0.48f

@Immutable
data class OnboardingPalette(
    val accent: Color,
    val accentContainer: Color,
    val cardGray: Color,
    val label: Color,
    val border: Color,
    val textSecondary: Color,
)

@Composable
private fun rememberOnboardingPalette(): OnboardingPalette = OnboardingPalette(
    accent = MaterialTheme.colorScheme.primary,
    accentContainer = MaterialTheme.colorScheme.primaryContainer,
    cardGray = MaterialTheme.colorScheme.surfaceVariant,
    label = MaterialTheme.colorScheme.primary,
    border = MaterialTheme.colorScheme.outline.copy(alpha = 0.65f),
    textSecondary = MaterialTheme.colorScheme.onSurfaceVariant,
)

@Composable
fun OnboardingScaffold(
    step: Int,
    totalSteps: Int,
    title: String,
    footer: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    OnboardingScreen(
        step = step,
        totalSteps = totalSteps,
        title = title,
        content = { content() },
        bottomBar = footer
    )
}

@Composable
fun OnboardingFooter(
    onBack: (() -> Unit)? = null,
    onNext: () -> Unit,
    nextEnabled: Boolean,
    nextText: String = "Далее",
    backText: String = "Назад",
) {
    OnbBottomButtons(
        onBack = onBack,
        onNext = onNext,
        nextEnabled = nextEnabled,
        nextText = nextText,
        backText = backText
    )
}

sealed interface OnbIcon {
    data object Person : OnbIcon
    data object Location : OnbIcon
    data object School : OnbIcon
}

@Composable
fun OnbFieldLabel(label: String, icon: OnbIcon) {
    val leading: @Composable () -> Unit = when (icon) {
        OnbIcon.Person -> { { OnbIconName() } }
        OnbIcon.Location -> { { OnbIconCity() } }
        OnbIcon.School -> { { OnbIconEdu() } }
    }
    OnbLabeledField(label = label, leadingIcon = leading) { }
}

@Composable
fun OnbTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    trailingDropdown: Boolean = false,
    minLines: Int = 1,
) {
    OnbOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        singleLine = singleLine,
        trailingDropdown = trailingDropdown,
        minLines = minLines
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    step: Int,
    totalSteps: Int,
    title: String,
    modifier: Modifier = Modifier,
    palette: OnboardingPalette = rememberOnboardingPalette(),
    content: @Composable ColumnScope.() -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("") }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = Dimens.dp10
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = Dimens.dp24, vertical = Dimens.dp16)
                ) {
                    bottomBar()
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.dp24),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Dimens.dp8))

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_login_home),
                contentDescription = null,
                modifier = Modifier.size(Dimens.dp84)
            )

            Spacer(Modifier.height(Dimens.dp10))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(Dimens.dp18))

            Stepper(
                step = step,
                total = totalSteps,
                palette = palette
            )

            Spacer(Modifier.height(Dimens.dp24))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.dp24),
                verticalArrangement = Arrangement.spacedBy(Dimens.dp14),
                content = content
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(
        step = 1,
        totalSteps = 4,
        title = "Расскажи о себе",
        content = {},
        bottomBar = {}
    )
}

@Composable
private fun Stepper(
    step: Int,
    palette: OnboardingPalette,
    total: Int = 4,
    circleSize: Dp = Dimens.dp28,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..total).forEach { i ->
            val isDone = i < step
            val isActive = i == step
            StepCircle(
                index = i,
                done = isDone,
                active = isActive,
                size = circleSize,
                palette = palette
            )

            if (i != total) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(Dimens.dp4)
                        .clip(RoundedCornerShape(Dimens.dp999))
                        .background(if (i < step) palette.accent else palette.border)
                )
            }
        }
    }
}

@Composable
private fun StepCircle(
    index: Int,
    done: Boolean,
    active: Boolean,
    size: Dp,
    palette: OnboardingPalette,
) {
    val background = when {
        done || active -> palette.accent
        else -> palette.accentContainer
    }
    val contentColor = if (done || active) Color.White else palette.accent

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .border(Dimens.dp1, palette.border, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (done) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(Dimens.dp16)
            )
        } else {
            Text(
                text = index.toString(),
                color = contentColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun OnbBottomButtons(
    onBack: (() -> Unit)?,
    onNext: () -> Unit,
    nextEnabled: Boolean,
    palette: OnboardingPalette = rememberOnboardingPalette(),
    nextText: String = "Далее",
    backText: String = "Назад",
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.dp12)
    ) {
        if (onBack != null) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(Dimens.dp52),
                shape = RoundedCornerShape(Dimens.dp28),
                border = androidx.compose.foundation.BorderStroke(Dimens.dp1, palette.border),
                contentPadding = PaddingValues(horizontal = Dimens.dp18, vertical = Dimens.dp12)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(Dimens.dp18))
                Spacer(Modifier.width(Dimens.dp8))
                Text(backText, fontWeight = FontWeight.SemiBold)
            }
        }

        Button(
            onClick = onNext,
            enabled = nextEnabled,
            modifier = Modifier
                .weight(if (onBack != null) 1f else 1f)
                .height(Dimens.dp52),
            shape = RoundedCornerShape(Dimens.dp28),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            ),
            contentPadding = PaddingValues(horizontal = Dimens.dp18, vertical = Dimens.dp12)
        ) {
            Text(nextText, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(Dimens.dp8))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(Dimens.dp18))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipFlowRow(
    items: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    palette: OnboardingPalette = rememberOnboardingPalette(),
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.dp10),
        verticalArrangement = Arrangement.spacedBy(Dimens.dp10),
        maxItemsInEachRow = 2
    ) {
        items.forEach { item ->
            val isSelected = item in selected
            Surface(
                modifier = Modifier
                    .fillMaxWidth(CHIP_FILL_FRACTION)
                    .clip(RoundedCornerShape(Dimens.dp24)),
                color = if (isSelected) palette.accentContainer else MaterialTheme.colorScheme.surface,
                contentColor = if (isSelected) palette.accent else MaterialTheme.colorScheme.onSurface,
                onClick = { onToggle(item) },
                shape = RoundedCornerShape(Dimens.dp24),
                border = androidx.compose.foundation.BorderStroke(
                    Dimens.dp1,
                    if (isSelected) palette.accent else palette.border
                )
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(horizontal = Dimens.dp14, vertical = Dimens.dp12),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun AboutCardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    palette: OnboardingPalette = rememberOnboardingPalette(),
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.dp210),
        shape = RoundedCornerShape(Dimens.dp28),
        colors = CardDefaults.cardColors(containerColor = palette.cardGray),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.dp0)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.dp16)
        ) {
            if (value.isBlank()) {
                Text(
                    text = placeholder,
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Default
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun PhotoSlotCard(
    imageModel: Any?,
    title: String,
    countText: String,
    modifier: Modifier = Modifier,
    palette: OnboardingPalette = rememberOnboardingPalette(),
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.dp28),
        colors = CardDefaults.cardColors(containerColor = palette.cardGray),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.dp0)
    ) {
        Box(Modifier.fillMaxSize()) {
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(Dimens.dp28)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimens.dp14),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = null,
                        tint = palette.textSecondary,
                        modifier = Modifier.size(Dimens.dp26)
                    )
                    Spacer(Modifier.height(Dimens.dp10))
                    Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    if (countText.isNotBlank()) {
                        Text(
                            countText,
                            color = palette.textSecondary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnbLabeledField(
    label: String,
    palette: OnboardingPalette = rememberOnboardingPalette(),
    leadingIcon: @Composable (() -> Unit)? = null,
    field: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.dp6)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(Dimens.dp8))
            }
            Text(
                text = label,
                color = palette.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        field()
    }
}

@Composable
fun OnbOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    palette: OnboardingPalette = rememberOnboardingPalette(),
    singleLine: Boolean = true,
    trailingDropdown: Boolean = false,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    minLines: Int = 1,
) {
    val shape = RoundedCornerShape(Dimens.dp28)
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = palette.textSecondary) },
        singleLine = singleLine,
        readOnly = readOnly,
        minLines = minLines,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = palette.border,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

@Composable
fun OnbIconName() = Icon(Icons.Filled.WavingHand, contentDescription = null, tint = MaterialTheme.colorScheme.primary)

@Composable
fun OnbIconCity() = Icon(Icons.Filled.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)

@Composable
fun OnbIconEdu() = Icon(Icons.Filled.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
