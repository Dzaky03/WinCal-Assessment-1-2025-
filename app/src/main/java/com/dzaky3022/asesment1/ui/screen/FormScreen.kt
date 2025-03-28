package com.dzaky3022.asesment1.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dzaky3022.asesment1.R
import com.dzaky3022.asesment1.navigate
import com.dzaky3022.asesment1.navigation.Screen
import com.dzaky3022.asesment1.shareData
import com.dzaky3022.asesment1.ui.component.CustomInput
import com.dzaky3022.asesment1.ui.theme.Background
import com.dzaky3022.asesment1.ui.theme.BackgroundDark
import com.dzaky3022.asesment1.ui.theme.Danger
import com.dzaky3022.asesment1.ui.theme.IconBackgroundGray
import kotlin.math.roundToInt

enum class ActivityLevel(val label: String, val value: Double) {
    Low("Sedentary", 35.0),
    Medium("Light Exercise", 40.0),
    High("Heavy Exercise", 45.0);
}

enum class TempUnit(val value: String, val symbol: String) {
    Celsius("Celsius", "°C"),
    Fahrenheit("Fahrenheit", "°F"),
    Kelvin("Kelvin", "K");
}

enum class WeightUnit(val value: String, val symbol: String) {
    Kilogram("Kilogram", "kg"),
    Pound("Pound", "lbs"),
}

enum class Gender(val value: String) {
    Male("Male"),
    Female("Female"),
}

enum class Direction {
    Horizontal,
    Vertical,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    var weight by remember { mutableStateOf("") }
    var weightUnit by remember { mutableStateOf(WeightUnit.Kilogram) }
    var temp by remember { mutableStateOf("") }
    var tempUnit by remember { mutableStateOf(TempUnit.Celsius) }
    var activityLevel by remember { mutableStateOf(ActivityLevel.Low) }
    var gender by remember { mutableStateOf(Gender.Male) }
    var expanded by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value
    Scaffold(
        modifier = modifier,
        containerColor = Background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(IconBackgroundGray),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back Button",
                                tint = Color.Unspecified
                            )
                        }
                    }
                },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Add New Nutrition",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { expanded = !expanded }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = Color.Unspecified
                        )
                    }
                    MoreMenu(
                        expanded = expanded,
                        onDismiss = {
                            expanded = false
                        },
                        onAdd = {
                            navigate(navController, Screen.Form.route)
                        },
                        onShare = {
                            shareData(context, context.getString(R.string.share_template))
                        },
                    );
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(vertical = 24.dp, horizontal = 16.dp)
                .fillMaxSize()
        ) {
            RadioButtonGroup(
                label = "Activity Level",
                options = ActivityLevel.entries,
                selectedOption = activityLevel,
                onOptionSelected = {
                    activityLevel = it
                }
            )
            CustomInput(
                isRequired = true,
                label = "Room Temperature",
                hint = "Enter your Room Temperature",
                initialValue = temp,
                onChange = { temp = it },
                suffixIcon = {
                    Text(tempUnit.symbol, color = Color.Gray)
                }
            )
            Spacer(Modifier.height(10.dp))
            RadioButtonGroup(
                label = "Temperature Unit",
                options = TempUnit.entries,
                selectedOption = tempUnit,
                onOptionSelected = {
                    tempUnit = it
                }
            )
            Spacer(Modifier.height(8.dp))
            CustomInput(
                isRequired = true,
                label = "Weight",
                hint = "Enter your Weight",
                initialValue = weight,
                onChange = { weight = it },
                suffixIcon = {
                    Text(weightUnit.symbol, color = Color.Gray)
                }
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButtonGroup(
                    label = "Gender",
                    direction = Direction.Vertical,
                    options = Gender.entries,
                    selectedOption = gender,
                    onOptionSelected = {
                        gender = it
                    }
                )
                Spacer(Modifier.width(24.dp))
                RadioButtonGroup(
                    label = "Weight Unit",
                    direction = Direction.Vertical,
                    options = WeightUnit.entries,
                    selectedOption = weightUnit,
                    onOptionSelected = {
                        weightUnit = it
                    }
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    enabled = weight.isNotEmpty() && temp.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        result =
                            "Result: ${
                                calculateWaterIntake(
                                    weight.toDouble(),
                                    temp.toDouble(),
                                    gender,
                                    tempUnit,
                                    weightUnit,
                                    activityLevel
                                ).roundToInt()
                            }"
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isPressed) Color.White else BackgroundDark,
                    ),
                    interactionSource = interactionSource,
                    border = BorderStroke(1.dp, BackgroundDark)
                ) {
                    Text(
                        "Calculate",
                        fontSize = 16.sp,
                        color = if (isPressed) BackgroundDark else Color.White,
                        modifier = Modifier.padding(vertical = 4.dp),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            if (result.isNotEmpty())
                Text(
                    text = "$result ml",
                    fontSize = 16.sp,
                    color = BackgroundDark,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
        }
    }
}


@Composable
private fun <T> RadioButtonGroup(
    label: String,
    direction: Direction = Direction.Horizontal,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "*", color = Danger, fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (direction == Direction.Horizontal)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == selectedOption),
                                onClick = { onOptionSelected(option) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { onOptionSelected(option) },
                            colors = RadioButtonDefaults.colors(selectedColor = BackgroundDark)
                        )
                        Text(
                            text = option.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        else
            options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected = (option == selectedOption),
                            onClick = { onOptionSelected(option) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = { onOptionSelected(option) },
                        colors = RadioButtonDefaults.colors(selectedColor = BackgroundDark)
                    )
                    Text(
                        text = option.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
    }
}


private fun calculateWaterIntake(
    weight: Double,
    temp: Double,
    gender: Gender,
    tempUnit: TempUnit,
    weightUnit: WeightUnit,
    activityLevel: ActivityLevel,
): Double {
    return calculateWeight(weight, weightUnit) * activityLevel.value * (1 + calculateClimate(
        temp,
        tempUnit
    )) * (1 + calculateGender(gender))
}

private fun calculateWeight(weight: Double, unit: WeightUnit): Double {
    return when (unit) {
        WeightUnit.Pound -> weight / 2.205
        else -> weight
    }
}

private fun calculateClimate(temp: Double, unit: TempUnit): Double {
    val celsiusTemp = when (unit) {
        TempUnit.Fahrenheit -> (temp - 32) / 1.8
        TempUnit.Kelvin -> temp - 273.15
        else -> temp
    }
    return when {
        celsiusTemp < 15.0 -> -(5 / 100.0)
        celsiusTemp in 15.0..30.0 -> 0.0
        else -> 10 / 100.0
    }
}

fun calculateGender(gender: Gender): Double {
    return when (gender) {
        Gender.Male -> 10 / 100.0
        Gender.Female -> 0.0
    }
}