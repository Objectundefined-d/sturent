package com.example.flat_rent_app.presentation.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.tooling.preview.Preview

@Immutable
data class OnboardingPalette(
    val topOrange: Color = Color(0xFFFFB74D),
    val bottomOrange: Color = Color(0xFFFF3D00),
    val accentOrange: Color = Color(0xFFFF6D00),
    val lightOrange: Color = Color(0x33FF6D00),
    val chipSelected: Color = Color(0xFF9FC5FF),
    val chipSelectedText: Color = Color(0xFF0B3D91),
    val labelBlue: Color = Color(0xFF1E88E5),
    val cardGray: Color = Color(0xFFE8E8E8),
)

private val DefaultOnbPalette = OnboardingPalette()

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
        title = title,
        emoji = null,
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
@Composable
fun OnboardingScreen(
    step: Int,
    title: String,
    modifier: Modifier = Modifier,
    palette: OnboardingPalette = DefaultOnbPalette,
    emoji: String? = null,
    content: @Composable ColumnScope.() -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(palette.topOrange, palette.bottomOrange))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(top = 26.dp)
        ) {
            Stepper(step = step, palette = palette)
            Spacer(Modifier.height(22.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildString {
                        append(title)
                        if (!emoji.isNullOrBlank()) {
                            append(" ")
                            append(emoji)
                        }
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 38.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 190.dp),
            shape = RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
                    .padding(top = 22.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    content = content
                )

                bottomBar()
            }
        }
    }
}


@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
fun OnboardingScreenPreview() {
    OnboardingScreen(
        step = 1,
        title = "",
        modifier = Modifier,
        palette = DefaultOnbPalette,
        emoji = null,
        content = {},
        bottomBar = {},
    )
}

@Composable
private fun Stepper(
    step: Int,
    palette: OnboardingPalette,
    total: Int = 4,
    circleSize: Dp = 32.dp,
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
                        .height(2.dp)
                        .background(Color.White.copy(alpha = 0.35f))
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
    val bg = when {
        done || active -> Color.White
        else -> Color.White.copy(alpha = 0.28f)
    }
    val border = when {
        active -> palette.accentOrange
        else -> Color.Transparent
    }
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bg)
            .border(width = 2.dp, color = border, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            done -> Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = palette.accentOrange,
                modifier = Modifier.size(18.dp)
            )

            else -> Text(
                text = index.toString(),
                color = if (active) palette.accentOrange else Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OnbBottomButtons(
    onBack: (() -> Unit)?,
    onNext: () -> Unit,
    nextEnabled: Boolean,
    palette: OnboardingPalette = DefaultOnbPalette,
    nextText: String = "Далее",
    backText: String = "Назад",
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PillButton(
            text = backText,
            iconLeft = Icons.AutoMirrored.Filled.ArrowBack,
            enabled = onBack != null,
            onClick = { onBack?.invoke() },
            palette = palette
        )

        PillButton(
            text = nextText,
            iconRight = Icons.AutoMirrored.Filled.ArrowForward,
            enabled = nextEnabled,
            onClick = onNext,
            palette = palette
        )
    }
}

@Composable
fun PillButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    palette: OnboardingPalette = DefaultOnbPalette,
    iconLeft: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconRight: androidx.compose.ui.graphics.vector.ImageVector? = null,
    leading: (@Composable () -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.accentOrange,
            disabledContainerColor = palette.accentOrange.copy(alpha = 0.35f),
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.75f)
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 18.dp,
            vertical = 12.dp
        )
    ) {
        if (leading != null) {
            leading()
        }
        if (iconLeft != null) {
            Icon(iconLeft, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.Bold)
        if (iconRight != null) {
            Spacer(Modifier.width(8.dp))
            Icon(iconRight, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipFlowRow(
    items: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    palette: OnboardingPalette = DefaultOnbPalette,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        maxItemsInEachRow = 2
    ) {
        items.forEach { item ->
            val isSelected = item in selected
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.48f)
                    .clip(RoundedCornerShape(999.dp))
                    .border(
                        width = 1.5.dp,
                        color = palette.accentOrange.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(999.dp)
                    )
                    .background(if (isSelected) palette.chipSelected else Color.Transparent)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isSelected) palette.chipSelected else Color.Transparent)
                    .then(Modifier),
                color = if (isSelected) palette.chipSelected else Color.Transparent,
                contentColor = if (isSelected) palette.chipSelectedText else palette.accentOrange,
                onClick = { onToggle(item) },
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    text = item,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
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
    palette: OnboardingPalette = DefaultOnbPalette,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = palette.cardGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(Modifier.fillMaxSize().padding(16.dp)) {
            if (value.isBlank()) {
                Text(
                    text = placeholder,
                    color = Color(0xFF6B6B6B),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1B1B1B)),
                cursorBrush = SolidColor(palette.accentOrange),
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
    palette: OnboardingPalette = DefaultOnbPalette,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = palette.cardGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = null,
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(title, color = Color(0xFF555555), fontWeight = FontWeight.SemiBold)
                    Text(countText, color = Color(0xFF777777), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun OnbLabeledField(
    label: String,
    palette: OnboardingPalette = DefaultOnbPalette,
    leadingIcon: @Composable (() -> Unit)? = null,
    field: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = label,
                color = palette.labelBlue,
                style = MaterialTheme.typography.labelLarge
            )
        }
        field()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnbOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    palette: OnboardingPalette = DefaultOnbPalette,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    trailingDropdown: Boolean = false,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    minLines: Int = 1,
) {
    val shape = RoundedCornerShape(18.dp)
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier
                    .clip(shape)
                    .background(Color.Transparent)
                else Modifier
            ),
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = singleLine,
        readOnly = readOnly,
        minLines = minLines,
        trailingIcon = if (trailingDropdown) {
            {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = palette.accentOrange
                )
            }
        } else null,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = palette.accentOrange,
            unfocusedBorderColor = palette.accentOrange.copy(alpha = 0.65f),
            cursorColor = palette.accentOrange,
            focusedTextColor = Color(0xFF1B1B1B),
            unfocusedTextColor = Color(0xFF1B1B1B)
        )
    )
}

@Composable
fun OnbIconName() = Icon(Icons.Filled.WavingHand, contentDescription = null, tint = DefaultOnbPalette.labelBlue)

@Composable
fun OnbIconCity() = Icon(Icons.Filled.LocationOn, contentDescription = null, tint = DefaultOnbPalette.labelBlue)

@Composable
fun OnbIconEdu() = Icon(Icons.Filled.School, contentDescription = null, tint = DefaultOnbPalette.labelBlue)
