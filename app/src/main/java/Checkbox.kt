package com.example.ri_routi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


enum class CheckBoxState {
    A, // #AEB0B6 테두리, 투명 배경 (기본 비활성)
    B, // #338AFF 테두리/배경, 중앙 체크마크 (선택됨)
    C, // #AEB0B6 테두리, #CCCDD1 배경
    D  // #CCCDD1 테두리/배경, 중앙 체크마크 (비활성 선택됨 등)
}

/**
 * [CustomCheckBox]
 * 조건(state)과 모양(isCircle)에 따라 스타일이 자동으로 적용되는 통합 체크박스
 */
@Composable
fun CustomCheckBox(
    state: CheckBoxState,
    isCircle: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val shape = if (isCircle) CircleShape else RoundedCornerShape(4.dp)


    val borderColor = when (state) {
        CheckBoxState.A -> Color(0xFFAEB0B6)
        CheckBoxState.B -> Color(0xFF338AFF)
        CheckBoxState.C -> Color(0xFFAEB0B6)
        CheckBoxState.D -> Color(0xFFCCCDD1)
    }

    val backgroundColor = when (state) {
        CheckBoxState.A -> Color.Transparent
        CheckBoxState.B -> Color(0xFF338AFF)
        CheckBoxState.C -> Color(0xFFCCCDD1)
        CheckBoxState.D -> Color(0xFFCCCDD1)
    }

    val hasCheckmark = when (state) {
        CheckBoxState.B, CheckBoxState.D -> true
        else -> false
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(16.dp)
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable { onClick() }
    ) {
        if (hasCheckmark) {
            Image(
                painter = painterResource(id = R.drawable.checkmark),
                contentDescription = "Checkmark",
                modifier = Modifier.size(10.dp)
            )
        }
    }
}


@Composable
fun CheckBoxVariant1(onClick: () -> Unit = {}) {
    CustomCheckBox(state = CheckBoxState.A, isCircle = false, onClick = onClick)
}


@Composable
fun CheckBoxVariant2(onClick: () -> Unit = {}) {
    CustomCheckBox(state = CheckBoxState.A, isCircle = true, onClick = onClick)
}


@Composable
fun CheckBoxVariant3(onClick: () -> Unit = {}) {
    CustomCheckBox(state = CheckBoxState.B, isCircle = false, onClick = onClick)
}

@Composable
fun CheckBoxVariant4(onClick: () -> Unit = {}) {
    CustomCheckBox(state = CheckBoxState.B, isCircle = true, onClick = onClick)
}

@Composable
fun CheckBoxVariant5(onClick: () -> Unit = {}) {
    CustomCheckBox(state = CheckBoxState.C, isCircle = false, onClick = onClick)
}


@Composable
fun CheckBoxVariant6(onClick: () -> Unit = {}) {
    CustomCheckBox(state = CheckBoxState.C, isCircle = true, onClick = onClick)
}


@Composable
fun CheckBoxVariant7(onClick: () -> Unit = {}) {
    CustomCheckBox(state = CheckBoxState.D, isCircle = false, onClick = onClick)
}


@Composable
fun CheckBoxVariant8(onClick: () -> Unit = {}) {
    CustomCheckBox(state = CheckBoxState.D, isCircle = true, onClick = onClick)
}


@Composable
fun CheckBoxShowcase() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(20.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CheckBoxVariant1()
            CheckBoxVariant2()
        }


        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CheckBoxVariant3()
            CheckBoxVariant4()
        }


        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CheckBoxVariant5()
            CheckBoxVariant6()
        }


        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CheckBoxVariant7()
            CheckBoxVariant8()
        }
    }
}

@Preview(
    showBackground = false,
    backgroundColor = 0xFFF2F3F5
)
@Composable
fun CheckBoxShowcasePreview() {
    CheckBoxShowcase()
}