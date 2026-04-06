package com.example.flat_rent_app.presentation.screens.onboarding

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

@Immutable
data class OnboardingPalette(
    val accent: Color = Color(0xFF6650A4),
    val accentContainer: Color = Color(0xFFF3EEFF),
    val cardGray: Color = Color(0xFFF5F5F7),
    val label: Color = Color(0xFF6650A4),
    val border: Color = Color(0xFFE1D9F7),
    val textSecondary: Color = Color(0xFF6F6F7A),
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
    palette: OnboardingPalette = DefaultOnbPalette,
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
                shadowElevation = 10.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.ic_login_home),
                contentDescription = null,
                modifier = Modifier.size(84.dp)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(18.dp))

            Stepper(
                step = step,
                total = totalSteps,
                palette = palette
            )

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
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
    circleSize: Dp = 28.dp,
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
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
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
            .border(1.dp, palette.border, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (done) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
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
    palette: OnboardingPalette = DefaultOnbPalette,
    nextText: String = "Далее",
    backText: String = "Назад",
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (onBack != null) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.border),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(backText, fontWeight = FontWeight.SemiBold)
            }
        }

        Button(
            onClick = onNext,
            enabled = nextEnabled,
            modifier = Modifier
                .weight(if (onBack != null) 1f else 1f)
                .height(52.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            ),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Text(nextText, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
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
                    .clip(RoundedCornerShape(24.dp)),
                color = if (isSelected) palette.accentContainer else Color.White,
                contentColor = if (isSelected) palette.accent else MaterialTheme.colorScheme.onSurface,
                onClick = { onToggle(item) },
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) palette.accent else palette.border
                )
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
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
    palette: OnboardingPalette = DefaultOnbPalette,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = palette.cardGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
    palette: OnboardingPalette = DefaultOnbPalette,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = palette.cardGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(28.dp)),
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
                        tint = palette.textSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(Modifier.height(10.dp))
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
    palette: OnboardingPalette = DefaultOnbPalette,
    singleLine: Boolean = true,
    trailingDropdown: Boolean = false,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null,
    minLines: Int = 1,
) {
    val shape = RoundedCornerShape(28.dp)
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
fun OnbIconName() = Icon(Icons.Filled.WavingHand, contentDescription = null, tint = DefaultOnbPalette.label)

@Composable
fun OnbIconCity() = Icon(Icons.Filled.LocationOn, contentDescription = null, tint = DefaultOnbPalette.label)

@Composable
fun OnbIconEdu() = Icon(Icons.Filled.School, contentDescription = null, tint = DefaultOnbPalette.label)
