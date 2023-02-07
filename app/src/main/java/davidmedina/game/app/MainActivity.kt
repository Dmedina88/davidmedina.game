@file:OptIn(ExperimentalMaterial3Api::class)

package davidmedina.game.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import davidmedina.game.app.ui.theme.DavidmedinagameTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContent {
            val navController = rememberNavController()
            val currentBackStackEntry = navController.currentBackStackEntryAsState()
           val currentDest = currentBackStackEntry.value?.destination?.route.toString()
            DavidmedinagameTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = currentDest)
                            },
                            navigationIcon =
                            {
                                if (currentDest != Routes.HOME.name && currentDest != Routes.REGISTER.name) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(Icons.Filled.ArrowBack, "backIcon")
                                    }
                                } else {
                                    Spacer(Modifier)
                                }
                            })
                            }

                    ) { innerPadding ->


                    NavGraph(navController = navController, innerPadding = innerPadding)
                }
            }
        }

    }

}

