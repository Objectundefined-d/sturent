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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.tooling.preview.Preview

// Цвета приложения
private val AppBlue = Color(0xFF1A73E8)
private val AppBlack = Color(0xFF1C1C1E)
private val AppWhite = Color.White
private val AppGray = Color(0xFFF2F2F7)
private val AppLightBlue = Color(0xFFE8F0FE)

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
    emoji: String? = null,
    content: @Composable ColumnScope.() -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AppBlue, Color(0xFF0D47A1))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(top = 26.dp)
        ) {
            Stepper(step = step)
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
                        color = AppWhite,
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
            colors = CardDefaults.cardColors(containerColor = AppWhite),
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
@Preview(showBackground = true, showSystemUi = true)
fun OnboardingScreenPreview() {
    OnboardingScreen(
        step = 1,
        title = "Как тебя зовут?",
        content = {},
        bottomBar = {},
    )
}

@Composable
private fun Stepper(
    step: Int,
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
                size = circleSize
            )

            if (i != total) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(AppWhite.copy(alpha = 0.35f))
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
    size: Dp
) {
    val bg = when {
        done || active -> AppWhite
        else -> AppWhite.copy(alpha = 0.28f)
    }
    val borderColor = if (active) AppBlue else Color.Transparent

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bg)
            .border(width = 2.dp, color = borderColor, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            done -> Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = AppBlue,
                modifier = Modifier.size(18.dp)
            )
            else -> Text(
                text = index.toString(),
                color = if (active) AppBlue else AppWhite,
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
        )

        PillButton(
            text = nextText,
            iconRight = Icons.AutoMirrored.Filled.ArrowForward,
            enabled = nextEnabled,
            onClick = onNext,
        )
    }
}

@Composable
fun PillButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
            containerColor = AppBlue,
            disabledContainerColor = AppBlue.copy(alpha = 0.35f),
            contentColor = AppWhite,
            disabledContentColor = AppWhite.copy(alpha = 0.75f)
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 18.dp,
            vertical = 12.dp
        )
    ) {
        if (leading != null) leading()
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
                        color = AppBlue.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(999.dp)
                    ),
                color = if (isSelected) AppBlue else Color.Transparent,
                contentColor = if (isSelected) AppWhite else AppBlue,
                onClick = { onToggle(item) },
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
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
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = AppGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = AppBlack),
                cursorBrush = SolidColor(AppBlue),
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
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp)),
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
                    Text(title, color = AppBlack, fontWeight = FontWeight.SemiBold)
                    Text(
                        countText,
                        color = Color(0xFF666666),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun OnbLabeledField(
    label: String,
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
                color = AppBlue,
                style = MaterialTheme.typography.labelLarge
            )
        }
        field()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnbOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
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
                    tint = AppBlue
                )
            }
        } else null,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppBlue,
            unfocusedBorderColor = AppBlue.copy(alpha = 0.65f),
            cursorColor = AppBlue,
            focusedTextColor = AppBlack,
            unfocusedTextColor = AppBlack
        )
    )
}

@Composable
fun OnbIconName() = Icon(
    Icons.Filled.WavingHand,
    contentDescription = null,
    tint = AppBlue
)

@Composable
fun OnbIconCity() = Icon(
    Icons.Filled.LocationOn,
    contentDescription = null,
    tint = AppBlue
)

@Composable
fun OnbIconEdu() = Icon(
    Icons.Filled.School,
    contentDescription = null,
    tint = AppBlue
)