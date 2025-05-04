package id.ac.umn.myapplication

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController, authViewModel: AuthViewModel) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var snackbarMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            authViewModel.uploadProfilePicture(it) { success, message ->
                snackbarMessage = if (success) {
                    "Profile picture updated!"
                } else {
                    message ?: "Upload failed."
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome, ${authViewModel.getCurrentUser()?.email}")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            imagePickerLauncher.launch("image/*")
        }) {
            Text("Upload/Change Profile Picture")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            authViewModel.deleteProfilePicture { success, message ->
                snackbarMessage = if (success) {
                    "Profile picture deleted!"
                } else {
                    message ?: "Delete failed."
                }
            }
        }) {
            Text("Delete Profile Picture")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            authViewModel.signOut()
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }) {
            Text("Logout")
        }

        if (snackbarMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(snackbarMessage, color = MaterialTheme.colorScheme.primary)
        }
    }
}