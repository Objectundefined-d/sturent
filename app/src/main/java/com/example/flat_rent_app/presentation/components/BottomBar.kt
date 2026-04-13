package com.example.flat_rent_app.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flat_rent_app.util.BottomTabs

@Composable
fun AppBottomBar(
    selected: BottomTabs,
    onHome: () -> Unit,
    onChats: () -> Unit,
    onProfile: () -> Unit,
    onFavorites: () -> Unit
) {
    NavigationBar(
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selected == BottomTabs.HOME,
            onClick = onHome,
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )

        NavigationBarItem(
            selected = selected == BottomTabs.FAVORITES,
            onClick = onFavorites,
            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )

        NavigationBarItem(
            selected = selected == BottomTabs.CHATS,
            onClick = onChats,
            icon = { Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null) },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )
        NavigationBarItem(
            selected = selected == BottomTabs.PROFILE,
            onClick = onProfile,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    AppBottomBar(
        selected = BottomTabs.CHATS,
        onHome = {},
        onChats = {},
        onProfile= {},
        onFavorites = {}
    )
}
