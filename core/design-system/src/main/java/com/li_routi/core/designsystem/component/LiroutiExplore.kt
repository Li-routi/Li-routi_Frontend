package com.example.ri_routi

import com.li_routi.core.designsystem.R
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Font definition
val AstaSans = FontFamily.Default

// =============================================================================
// 1. BottomNavigationBar Components & Enums
// =============================================================================

enum class NavTab {
    HOME, GROUP_ROUTINE, CHALLENGE, MY
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onTabSelected: (NavTab) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(NavTab.HOME) }

    Box(
        modifier = modifier
            .size(width = 360.dp, height = 80.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(width = 360.dp, height = 48.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            NavItem(
                activeIconRes = R.drawable.home__active,
                defaultIconRes = R.drawable.home__default,
                label = "홈",
                isSelected = selectedTab == NavTab.HOME,
                onClick = {
                    selectedTab = NavTab.HOME
                    onTabSelected(NavTab.HOME)
                }
            )

            NavItem(
                activeIconRes = R.drawable.group__active,
                defaultIconRes = R.drawable.group__default,
                label = "그룹 루틴",
                isSelected = selectedTab == NavTab.GROUP_ROUTINE,
                onClick = {
                    selectedTab = NavTab.GROUP_ROUTINE
                    onTabSelected(NavTab.GROUP_ROUTINE)
                }
            )

            NavItem(
                activeIconRes = R.drawable.medal__active,
                defaultIconRes = R.drawable.medal__default,
                label = "챌린지",
                isSelected = selectedTab == NavTab.CHALLENGE,
                onClick = {
                    selectedTab = NavTab.CHALLENGE
                    onTabSelected(NavTab.CHALLENGE)
                }
            )

            NavItem(
                activeIconRes = R.drawable.ic_my__active,
                defaultIconRes = R.drawable.ic_my__default,
                label = "마이",
                isSelected = selectedTab == NavTab.MY,
                onClick = {
                    selectedTab = NavTab.MY
                    onTabSelected(NavTab.MY)
                }
            )
        }
    }
}

@Composable
private fun NavItem(
    @DrawableRes activeIconRes: Int,
    @DrawableRes defaultIconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFF296ECC)
    val defaultColor = Color(0xFF878A93)

    val currentIconRes = if (isSelected) activeIconRes else defaultIconRes
    val currentTextColor = if (isSelected) activeColor else defaultColor

    Box(
        modifier = Modifier
            .size(width = 90.dp, height = 48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = currentIconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )

            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .wrapContentSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = currentTextColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// =============================================================================
// 2. Header Components
// =============================================================================

@Composable
fun HeaderGnb(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(360.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .width(328.dp)
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "LI-ROUTI",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(173.dp))

            Image(
                painter = painterResource(id = R.drawable.add__alt),
                contentDescription = "Add",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Image(
                painter = painterResource(id = R.drawable.notification),
                contentDescription = "Notification",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HeaderNavigation(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(360.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .width(328.dp)
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.chevron__left),
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(100.5.dp))

            Text(
                text = "Navigation",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Close",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HeaderTitleWithAdd(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(360.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .width(328.dp)
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(20.dp))

            Spacer(modifier = Modifier.width(100.5.dp))

            Text(
                text = "Navigation",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.add__alt),
                contentDescription = "Add",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HeaderNavigationWithActions(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(360.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .width(328.dp)
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.chevron__left),
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(100.5.dp))

            Text(
                text = "Navigation",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(66.5.dp))

            Image(
                painter = painterResource(id = R.drawable.chat),
                contentDescription = "Chat",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Image(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Setting",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HeaderWithOverflow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(360.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .width(328.dp)
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.chevron__left),
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(288.dp))

            Image(
                painter = painterResource(id = R.drawable.overflow_menu__vertical),
                contentDescription = "More Options",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HeaderStoreSetting(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .width(360.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .width(328.dp)
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.chevron__left),
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "상점",
                fontFamily = AstaSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(137.dp))

            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 35.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFA8A8A8),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.diamond_orange),
                        contentDescription = "Orange Diamond",
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "450",
                        fontFamily = AstaSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(width = 57.dp, height = 30.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFA8A8A8),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.diamond_blue),
                        contentDescription = "Blue Diamond",
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "30",
                        fontFamily = AstaSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}



@Preview(showBackground = false, backgroundColor = 0xFFF5F5F8)
@Composable
fun AllComponentsVerticalPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        HeaderGnb()
        HeaderNavigation()
        HeaderTitleWithAdd()
        HeaderNavigationWithActions()
        HeaderWithOverflow()
        HeaderStoreSetting()
        BottomNavigationBar()
    }
}