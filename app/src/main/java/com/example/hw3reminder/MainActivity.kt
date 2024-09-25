package com.example.hw3reminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReminderApp()
        }
    }
}
@Composable
fun ReminderApp() {
    var reminderMessage by remember { mutableStateOf(TextFieldValue("")) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var reminders by remember { mutableStateOf(mutableListOf<String>()) }
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current // this should get the current time and date
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, hourOfDay: Int, minute: Int ->
            selectedTime = "$hourOfDay:$minute"
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                reminders.forEach { reminder ->
                    Text(text = reminder, modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                TextField(
                    value = reminderMessage,
                    onValueChange = { reminderMessage = it },
                    label = { Text("Enter reminder message") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { datePickerDialog.show() }) {
                        Text(text = "Select Date")
                    }
                    Button(onClick = { timePickerDialog.show() }) {
                        Text(text = "Select Time")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val currentCalendar = Calendar.getInstance()
                        val selectedCalendar = Calendar.getInstance().apply {
                            if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                                val (day, month, year) = selectedDate.split("/").map { it.toInt() }
                                val (hour, minute) = selectedTime.split(":").map { it.toInt() }
                                set(year, month - 1, day, hour, minute)
                            }
                        }

                        if (reminderMessage.text.isNotEmpty() &&
                            selectedDate.isNotEmpty() &&
                            selectedTime.isNotEmpty()
                        ) {
                            if (selectedCalendar.timeInMillis >= currentCalendar.timeInMillis) {
                                reminders.add("Reminder: ${reminderMessage.text}, Date: $selectedDate, Time: $selectedTime")
                                reminderMessage = TextFieldValue("")
                                selectedDate = ""
                                selectedTime = ""
                                snackbarMessage = "Reminder has been set"
                                showSnackbar = true
                            } else {
                                snackbarMessage = "Cannot set reminder, date or time has already passed"
                                showSnackbar = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Set Reminder")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {

}