package xyz.isekhon.drawabletuner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import xyz.isekhon.drawabletuner.data.model.DrawablePropertiesInRoom
import xyz.isekhon.drawabletuner.ui.screen.MainScreen
import xyz.isekhon.drawabletuner.ui.screen.XmlCodeViewScreen
import xyz.isekhon.drawabletuner.ui.theme.GradientDrawableTunerTheme
import xyz.isekhon.drawabletuner.ui.viewmodel.DrawableViewModel
import xyz.isekhon.drawabletuner.utils.ShapeXmlGenerator
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize XML generator
        ShapeXmlGenerator.init(this)
        
        enableEdgeToEdge()
        setContent {
            GradientDrawableTunerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GradientDrawableTunerApp()
                }
            }
        }
    }
}

@Composable
fun GradientDrawableTunerApp() {
    val navController = rememberNavController()
    val viewModel: DrawableViewModel = viewModel()
    val gson = Gson()
    
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToCodeView = { properties ->
                    val json = gson.toJson(properties)
                    val encoded = URLEncoder.encode(json, "UTF-8")
                    navController.navigate("codeView/$encoded")
                }
            )
        }
        
        composable(
            route = "codeView/{properties}",
            arguments = listOf(navArgument("properties") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 4 },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 4 },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("properties") ?: ""
            val json = URLDecoder.decode(encoded, "UTF-8")
            val properties = gson.fromJson(json, DrawablePropertiesInRoom::class.java)
            
            XmlCodeViewScreen(
                properties = properties,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
