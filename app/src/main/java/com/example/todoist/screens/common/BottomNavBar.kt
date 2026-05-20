package com.example.todoist.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.todoist.screens.theme.PurpleLight

enum class BottomNavTab { HOME, CALENDAR, ADD, TASKS, PROFILE }

@Composable
fun BottomNavBar(
    activeTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(PurpleLight)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavItem(
                icon = Icons.Default.Home,
                tab = BottomNavTab.HOME,
                activeTab = activeTab,
                onClick = onTabSelected,
            )
            NavItem(
                icon = Icons.Default.CalendarMonth,
                tab = BottomNavTab.CALENDAR,
                activeTab = activeTab,
                onClick = onTabSelected,
            )
            AddNavItem(onClick = { onTabSelected(BottomNavTab.ADD) })
            NavItem(
                icon = Icons.Default.Description,
                tab = BottomNavTab.TASKS,
                activeTab = activeTab,
                onClick = onTabSelected,
            )
            NavItem(
                icon = Icons.Default.Person,
                tab = BottomNavTab.PROFILE,
                activeTab = activeTab,
                onClick = onTabSelected,
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    tab: BottomNavTab,
    activeTab: BottomNavTab,
    onClick: (BottomNavTab) -> Unit,
) {
    val isActive = tab == activeTab
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick(tab) },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.name,
            tint = if (isActive) Color.White else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun AddNavItem(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
    }
}
