package id.ac.umn.myapplication

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    NavHost(navController, startDestination = if (authViewModel.getCurrentUser() != null) "home" else "login") {
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("signup") { SignupScreen(navController, authViewModel) }
        composable("home") { HomeScreen(navController, authViewModel) }
    }
}