package com.sample.sampleapp

import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Surface
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sample.sampleapp.room.Log
import com.sample.sampleapp.room.LogDatabase
import com.sample.sampleapp.ui.theme.SampleAppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

data class Tab(
    val text: String,
    @DrawableRes val image: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
        }
    }
}

@Composable
fun MainView() {
    val homeTab = Tab(text = "Home", image = R.drawable.home)
    val logsTab = Tab(text = "Logs", image = R.drawable.list)
    val tabs = listOf(homeTab, logsTab)
    val navController = rememberNavController()
    val sortBy = remember { mutableStateOf("Logs") }
    val snackbarHostState = remember { SnackbarHostState() }

    SampleAppTheme(darkTheme = false) {
        Surface (
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Scaffold(bottomBar = { NavigationBarView(tabs, navController) },
                snackbarHost = { SnackbarHost(snackbarHostState) } ) { padding ->
                NavHost(navController = navController, startDestination = homeTab.text) {
                    composable(homeTab.text) {
                        HomeView(snackbarHostState)
                    }
                    composable(logsTab.text) {
                        Column(modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Dropdown(listOf("Button Name", "Date")) { item ->
                                sortBy.value = item
                            }
                            LogView(sortBy.value)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationBarView(tabs: List<Tab>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
    }

    NavigationBar {
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tab.text)
                },
                icon = {
                    Image(
                        painter = painterResource(tab.image),
                        colorFilter = ColorFilter.tint(if(selectedTabIndex == index)
                            Color.Blue else Color.Black, blendMode = BlendMode.SrcIn),
                        contentDescription = tab.text
                    )
                },
                label = {
                    Text(tab.text, color = if(selectedTabIndex == index)
                        Color.Blue else Color.Black)
                })
        }
    }
}

@Composable
fun HomeView(snackbarHostState: SnackbarHostState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ButtonView("Button 1", Color.Red, snackbarHostState)
        ButtonView("Button 2", Color.Blue, snackbarHostState)
        ButtonView("Button 3", Color.Yellow, snackbarHostState)
        ButtonView("Button 4", Color.Green, snackbarHostState)
    }
}

@Composable
fun ButtonView(text: String, color: Color, snackbarHostState: SnackbarHostState) {
    val coroutineScope = rememberCoroutineScope()
    Button(onClick = {
                        LogDatabase.instance?.logDao()?.insertLog(Log(text))
                        coroutineScope.launch { // using the `coroutineScope` to `launch` showing the snackbar
                            // taking the `snackbarHostState` from the attached `scaffoldState`
                            snackbarHostState.showSnackbar(message = "$text clicked")
                        }
                     }, colors = ButtonDefaults.buttonColors(backgroundColor = color),
        modifier = Modifier
            .padding(40.dp)
            .fillMaxWidth()
            .size(width = 0.dp, height = 70.dp)) {
        Text(text = text, color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LogView(sortBy: String) {
    val logs = getLogs(sortBy == "Button Name")

    LazyColumn(
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items = logs!!, itemContent = {item: Log? ->
            item?.let {
                Text(text = "${it.buttonName} pressed")
                Text(text = formatCalendar(item.timestamp))
                Divider()
            }
        })
    }
}

@Composable
fun Dropdown(items: List<String>, action: (String) -> Unit) {
    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    val itemPosition = remember {
        mutableStateOf(1)
    }

    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.clickable {
                    isDropDownExpanded.value = true
                }
            ) {
                Text(text = "Sort by: ")
                Text(text = items[itemPosition.value])
                Image(
                    painter = painterResource(R.drawable.dropdown),
                    contentDescription = "DropDown Icon"
                )
            }
            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = {
                    isDropDownExpanded.value = false
                }) {
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(text = {
                        Text(text = item)
                    },
                        onClick = {
                            isDropDownExpanded.value = false
                            itemPosition.value = index
                            action(item)
                        })
                }
            }
        }
    }
}

private fun formatCalendar(date: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = date
    val format = SimpleDateFormat("MM/dd/yyyy hh:mm a")
    return format.format(calendar.time)
}

private fun getLogs(isSortedByButtonName: Boolean): List<Log?>? {
    if(isSortedByButtonName)
        return LogDatabase.instance?.logDao()?.getAllLogsByButtonName()
    return LogDatabase.instance?.logDao()?.getAllLogsByDate()
}

