package com.lumina.app_daymood.presentation.views.forms

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumina.app_daymood.ui.theme.BackgroundColor
import com.lumina.app_daymood.ui.theme.DisabledButton
import com.lumina.app_daymood.ui.theme.MainColor
import com.lumina.app_daymood.ui.theme.borderLines


private val QUESTIONS = listOf(
    "Normalmente dedico tiempo a pensar en mis emociones.",
    "Pienso que merece la pena prestar atención a mis emociones y estado de ánimo.",
    "Dejo que mis sentimientos afecten a mis pensamientos.",
    "Pienso en mi estado de ánimo constantemente.",
    "Casi siempre sé cómo me siento.",
    "Normalmente conozco mis sentimientos sobre las personas.",
    "A menudo me doy cuenta de mis sentimientos en diferentes situaciones.",
    "Puedo llegar a comprender mis sentimientos.",
    "Si doy demasiadas vueltas a las cosas, complicándolas, trato de calmarme.",
    "Me preocupo por tener un buen estado de ánimo."
)


/**
 * Vista del test TMMS-24 (versión 10 preguntas).
 * Dividida en dos secciones de 5 preguntas con animación de deslizamiento.
 *
 * @param onSubmit  Callback con el mapa pregunta-índice → respuesta (1-5)
 * @param onBack    Callback para retroceder desde la vista (opcional, ej: cerrarla)
 */
@Composable
fun TmmsTestView(
    onSubmit: (answers: Map<Int, Int>) -> Unit = {},
    onBack: () -> Unit = {}
) {
    // answers[questionIndex] = valor 1..5
    val answers = remember { mutableStateMapOf<Int, Int>() }
    var currentSection by remember { mutableStateOf(0) } // 0 = preguntas 1-5 | 1 = preguntas 6-10

    val sectionQuestions = if (currentSection == 0) QUESTIONS.take(5) else QUESTIONS.drop(5)
    val questionOffset = currentSection * 5  // 0 o 5

    val allCurrentAnswered = sectionQuestions.indices.all { answers.containsKey(it + questionOffset) }

    Scaffold(containerColor = BackgroundColor) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // ── Encabezado ─────────────────────────────────────────────────────
            Text(
                text = "¡Déjanos conocerte!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Este test está basado en el TMMS-24",
                fontSize = 13.sp,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(14.dp))

            // ── Escala de referencia ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "En la escala del 1 al 5",
                        fontSize = 13.sp,
                        color = Color(0xFF616161),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "1 - Muy desacuerdo  ·  5 - Muy de acuerdo",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Indicador de sección (dots) ─────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(2) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentSection) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index == currentSection) MainColor else DisabledButton)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Lista de preguntas (animada) ────────────────────────────────────
            AnimatedContent(
                targetState = currentSection,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInHorizontally { it * direction } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it * direction } + fadeOut())
                },
                label = "section_transition",
                modifier = Modifier.weight(1f)
            ) { section ->
                val questions = if (section == 0) QUESTIONS.take(5) else QUESTIONS.drop(5)
                val offset = section * 5

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(Modifier.height(4.dp))
                    questions.forEachIndexed { localIndex, question ->
                        val globalIndex = localIndex + offset
                        QuestionCard(
                            number = globalIndex + 1,
                            question = question,
                            selectedValue = answers[globalIndex],
                            onValueSelected = { value -> answers[globalIndex] = value }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            // ── Botones ─────────────────────────────────────────────────────────
            Spacer(Modifier.height(12.dp))

            if (currentSection == 0) {
                // Sección 1: solo botón "Siguiente"
                TmmsButton(
                    text = "Siguiente",
                    enabled = allCurrentAnswered,
                    onClick = { currentSection = 1 }
                )
            } else {
                // Sección 2: botones "Regresar" y "Enviar"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TmmsButton(
                        text = "Regresar",
                        enabled = true,
                        outlined = true,
                        modifier = Modifier.weight(1f),
                        onClick = { currentSection = 0 }
                    )
                    TmmsButton(
                        text = "Enviar",
                        enabled = allCurrentAnswered,
                        modifier = Modifier.weight(1f),
                        onClick = { onSubmit(answers.toMap()) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Card de pregunta ──────────────────────────────────────────────────────────

@Composable
private fun QuestionCard(
    number: Int,
    question: String,
    selectedValue: Int?,
    onValueSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        // Número y texto de la pregunta
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(MainColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$number",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = question,
                fontSize = 13.sp,
                color = Color(0xFF424242),
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(14.dp))

        // Opciones 1-5
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..5).forEach { value ->
                val isSelected = selectedValue == value
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MainColor else Color.Transparent)
                        .border(
                            width = if (isSelected) 0.dp else 1.5.dp,
                            color = if (isSelected) Color.Transparent else borderLines,
                            shape = CircleShape
                        )
                        .clickable { onValueSelected(value) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$value",
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color(0xFF9E9E9E)
                    )
                }
            }
        }
    }
}

// ─── Botón reutilizable ────────────────────────────────────────────────────────

@Composable
private fun TmmsButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    outlined: Boolean = false,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.height(52.dp),
            shape = RoundedCornerShape(28.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                // usamos el borde del tema pero con el color de la app
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MainColor,
                disabledContentColor = DisabledButton
            )
        ) {
            Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.height(52.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainColor,
                disabledContainerColor = DisabledButton,
                contentColor = Color.White,
                disabledContentColor = Color.White
            )
        ) {
            Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}