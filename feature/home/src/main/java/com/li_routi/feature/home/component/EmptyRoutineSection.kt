package com.li_routi.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 등록된 루틴이 하나도 없을 때 표시하는 empty 상태 영역 (Figma `comp/empty`, node `2187:25270`).
 */
@Composable
fun EmptyRoutineSection(modifier: Modifier = Modifier, message: String = "아직 루틴이 없어요!") {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.warning),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = LiroutiTheme.typography.body2LongMedium,
            color = LiroutiTheme.colors.labelInfo,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyRoutineSectionPreview() {
    LiroutiFrontendTheme {
        EmptyRoutineSection()
    }
}
