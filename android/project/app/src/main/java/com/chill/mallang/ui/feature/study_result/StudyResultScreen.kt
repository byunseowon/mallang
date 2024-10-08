package com.chill.mallang.ui.feature.study_result

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chill.mallang.R
import com.chill.mallang.data.model.entity.StudyResultWord
import com.chill.mallang.ui.component.BackConfirmHandler
import com.chill.mallang.ui.component.LoadingDialog
import com.chill.mallang.ui.component.LongBlackButton
import com.chill.mallang.ui.feature.study.QuizBox
import com.chill.mallang.ui.feature.topbar.TopbarHandler
import com.chill.mallang.ui.theme.Gray3
import com.chill.mallang.ui.theme.Gray6
import com.chill.mallang.ui.theme.Green1
import com.chill.mallang.ui.theme.Green2
import com.chill.mallang.ui.theme.Sub1
import com.chill.mallang.ui.theme.Sub2
import com.chill.mallang.ui.theme.Typography

@Composable
fun StudyResultScreen(
    modifier: Modifier = Modifier,
    studyResultViewModel: StudyResultViewModel = hiltViewModel(),
    userAnswer: Int,
    popUpBackStack: () -> Unit = {},
) {
    val studyResultState by studyResultViewModel.studyResultState.collectAsStateWithLifecycle()

    // TopBar
    val (navController, setNavController) = remember { mutableStateOf<NavController?>(null) }
    val (isBackPressed, setBackPressed) = remember { mutableStateOf(false) }

    // context
    val context = LocalContext.current

    BackConfirmHandler(
        isBackPressed = isBackPressed,
        onConfirmMessage = stringResource(id = R.string.study_dialog_confirm_message),
        onConfirm = {
            setBackPressed(false)
            popUpBackStack()
        },
        onDismissMessage = stringResource(id = R.string.study_dialog_dismiss_message),
        onDismiss = {
            setBackPressed(false)
        },
        title = stringResource(id = R.string.confirm_dialog_default_message),
    )
    BackHandler(onBack = { setBackPressed(true) })

    TopbarHandler(
        isVisible = true,
        title = context.getString(R.string.study_quiz_result_title),
        onBack = { nav ->
            setBackPressed(true)
            setNavController(nav)
        },
    )

    when (studyResultState) {
        StudyResultState.Loading -> LoadingDialog()

        is StudyResultState.Success -> {
            StudyResultScreenContent(
                modifier = modifier,
                studyResultState = studyResultState as StudyResultState.Success,
                userAnswer = userAnswer,
                popUpBackStack = popUpBackStack,
            )
        }

        is StudyResultState.Error -> {
            // 에러 시 표시할 것
            // 에러났을 때 처리
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = context.getString(R.string.study_load_error_message),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun StudyResultScreenContent(
    modifier: Modifier,
    studyResultState: StudyResultState.Success,
    userAnswer: Int,
    popUpBackStack: () -> Unit = {},
) {
    val context = LocalContext.current
    var expandedItem by remember { mutableIntStateOf(-1) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = Color.White),
    ) {
        Column(
            modifier =
                modifier
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val title =
                if (studyResultState.result) {
                    context.getString(R.string.correct_message)
                } else {
                    context.getString(
                        R.string.wrong_message,
                    )
                }
            val titleColor =
                if (title == context.getString(R.string.correct_message)) Green1 else Sub1

            AnimatedTitle(title = title, titleColor = titleColor)

            if (!studyResultState.result) { // 오답일 때는 기존처럼 __ 빈칸 뚫린 거로
                QuizBox(
                    quizTitle = studyResultState.quizTitle,
                    quizScript = studyResultState.quizScript,
                )
            } else { // 정답일 때는 빈칸 없이
                QuizBoxWithUnderline(
                    systemMessage = studyResultState.quizTitle,
                    quizScript = studyResultState.quizScript,
                    underline = studyResultState.wordList[studyResultState.systemAnswer - 1].word,
                )
            }
            ResultAnswerList(
                studyResultState = studyResultState,
                userAnswer = userAnswer,
                fraction = 0.15f,
                expandedItem = expandedItem,
                onAnswerSelected = { selectedIndex ->
                    expandedItem = if (expandedItem == selectedIndex + 1) -1 else selectedIndex + 1
                },
            )
            Spacer(modifier = Modifier.weight(1f))
            LongBlackButton(onClick = {
                popUpBackStack()
            }, text = "완료")
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AnimatedTitle(
    title: String,
    titleColor: Color,
) {
    var isAnimated by remember { mutableStateOf(false) }
    val fontSize by animateFloatAsState(
        targetValue = if (isAnimated) 50f else 20f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
    )

    LaunchedEffect(true) {
        isAnimated = true
    }
    Text(
        modifier = Modifier.padding(vertical = 20.dp),
        text = title,
        style = Typography.headlineLarge,
        fontSize = fontSize.sp,
        color = titleColor,
    )
}

@Composable
fun ResultAnswerList(
    studyResultState: StudyResultState.Success,
    userAnswer: Int,
    expandedItem: Int = -1,
    fraction: Float,
    onAnswerSelected: (Int) -> Unit = { },
) {
    val size = studyResultState.wordList.size

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier =
            Modifier
                .padding(12.dp)
                .wrapContentSize(),
    ) {
        items(size) { index ->
            AnswerResultListItem(
                modifier = Modifier.fillParentMaxHeight(fraction),
                index = index,
                studyResultState = studyResultState,
                userAnswer = userAnswer,
                expandedItem = expandedItem,
                onItemClick = { selectedIndex ->
                    onAnswerSelected(selectedIndex)
                },
            )
        }
    }
}

@Composable
fun AnswerResultListItem(
    modifier: Modifier = Modifier,
    index: Int,
    userAnswer: Int,
    studyResultState: StudyResultState.Success,
    expandedItem: Int,
    onItemClick: (Int) -> Unit,
) {
    val isUserAnswer = userAnswer == index + 1
    val isSystemAnswer = studyResultState.systemAnswer == index + 1

    // 배경색
    val backgroundColor =
        when {
            isUserAnswer && isSystemAnswer -> Green2
            isUserAnswer && !isSystemAnswer -> Sub2
            else -> Color.White
        }

    // 번호, 테두리 색상
    val borderColor =
        when {
            isUserAnswer && isSystemAnswer -> Green1
            isUserAnswer && !isSystemAnswer -> Sub1
            else -> Gray6
        }

    // 뜻 확장 및 애니메이션 효과
    val expandTransition =
        updateTransition(targetState = expandedItem, label = "expandTransition")
    val expandedHeight by expandTransition.animateDp(
        label = "expandedHeight",
        transitionSpec = { tween(durationMillis = 200) },
    ) { expanded ->
        if (expanded == index + 1) 70.dp else 0.dp
    }

    val alpha by expandTransition.animateFloat(
        label = "alpha",
        transitionSpec = { tween(durationMillis = 200) },
    ) { expanded ->
        if (expanded == index + 1) 1f else 0f
    }

    Column {
        Box(
            modifier =
                modifier
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(8.dp),
                    ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    modifier
                        .fillMaxWidth()
                        .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
                        .border(2.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
                        .clickable { onItemClick(index) },
            ) {
                Box(
                    modifier =
                        Modifier
                            .background(
                                color = borderColor,
                                shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp),
                            ).fillMaxHeight()
                            .width(50.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${index + 1}",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = Typography.headlineLarge,
                    )
                }
                Spacer(modifier = Modifier.width(30.dp))
                Text(
                    text = studyResultState.wordList[index].word,
                    modifier = Modifier.weight(1f),
                    style = Typography.headlineLarge,
                )
                when (expandedItem == index + 1) {
                    false -> {
                        Icon(
                            modifier = Modifier.padding(10.dp),
                            painter = painterResource(id = R.drawable.ic_down),
                            contentDescription = null,
                        )
                    }

                    true -> {
                        Icon(
                            modifier = Modifier.padding(10.dp),
                            painter = painterResource(id = R.drawable.ic_up),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
        if (expandedItem == index + 1) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(expandedHeight)
                        .background(color = Color.Unspecified),
            ) {
                Text(
                    text = studyResultState.wordList[index].meaning,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .alpha(alpha),
                    style = Typography.displayMedium,
                    textAlign = TextAlign.Left,
                )
            }
        }
    }
}

@Composable
fun QuizBoxWithUnderline(
    systemMessage: String,
    quizScript: String,
    underline: String,
) {
    Box(
        modifier =
            Modifier
                .padding(12.dp)
                .border(width = 2.dp, color = Gray6, shape = RoundedCornerShape(10.dp))
                .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Q.",
                    style = Typography.headlineLarge,
                )
                Box(modifier = Modifier.width(10.dp))
                Text(
                    text = systemMessage,
                    style = Typography.headlineMedium,
                )
            }
            Box(modifier = Modifier.height(15.dp))
            Spacer(
                modifier =
                    Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(Gray3),
            )
            Box(modifier = Modifier.height(15.dp))
            Text(
                text =
                    buildAnnotatedString {
                        val parts = quizScript.split("__")
                        append(parts[0])
                        withStyle(
                            style =
                                SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                ),
                        ) {
                            append(underline)
                        }
                        append(parts[1])
                    },
                style = Typography.headlineSmall,
            )
        }
    }
}

@Preview
@Composable
fun ResultPreview() {
    StudyResultScreenContent(
        modifier = Modifier,
        studyResultState =
            StudyResultState.Success(
                quizTitle = "타이틀",
                quizScript = "스크립트요__빈칸",
                wordList =
                    arrayListOf(
                        StudyResultWord(word = "보기1", meaning = "뜻1"),
                        StudyResultWord(word = "보기1", meaning = "뜻1"),
                        StudyResultWord(word = "보기1", meaning = "뜻1"),
                        StudyResultWord(word = "보기1", meaning = "뜻1"),
                    ),
                result = true,
                systemAnswer = 1,
            ),
        userAnswer = 2,
    )
}
