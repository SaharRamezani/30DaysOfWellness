package com.example.a30daysofwellness

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
fun MainScreen(viewModel: MainViewModel, activity: ComponentActivity) {
    var showDialog by remember { mutableStateOf(false) }
    val cards by viewModel.cardList.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        if (showDialog) {
            AddCardDialog(
                onDismiss = { showDialog = false },
                onConfirm = { title, description, imageUri ->
                    viewModel.addCard(title, description, imageUri)
                    showDialog = false
                },
                activity = activity
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = padding
        ) {
            items(cards) { card ->
                WellnessCard(card)
            }
        }
    }
}

@Composable
fun WellnessCard(card: CardModel) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Day ${card.day}")
            card.imageUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }
            }
            Text(text = card.title)

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = card.description)
            }
        }
    }
}

@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Uri?) -> Unit,
    activity: Activity
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Card") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Pick Image")
                }
                imageUri?.let {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.height(120.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Pass a default imageRes if image is not set
                val imageRes = R.drawable.ic_launcher_background
                onConfirm(title, description, imageUri)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
