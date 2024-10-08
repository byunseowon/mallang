package com.chill.mallang.ui.feature.map.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chill.mallang.R
import com.chill.mallang.ui.component.SpeechBubble
import com.chill.mallang.ui.theme.MallangTheme

@Composable
fun CharacterMessageBox(
    modifier: Modifier = Modifier,
    messageWithCharacter: MessageWithCharacter,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SpeechBubble(
            modifier = Modifier,
        ) {
            Text(
                messageWithCharacter.message,
                modifier =
                    Modifier
                        .padding(16.dp),
            )
        }
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = messageWithCharacter.character),
            contentDescription = "",
        )
    }
}

@Immutable
data class MessageWithCharacter(
    val message: String,
    val character: Int,
)

@Preview
@Composable
fun CharacterMessageBoxPreview() {
    MallangTheme {
        CharacterMessageBox(messageWithCharacter = MessageWithCharacter(message = "목적지에 도착했어요!", character = R.drawable.ic_logo))
    }
}
