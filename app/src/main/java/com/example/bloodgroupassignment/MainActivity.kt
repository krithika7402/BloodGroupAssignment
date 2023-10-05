package com.example.bloodgroupassignment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.insert
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bloodgroupassignment.ui.theme.BloodGroupAssignmentTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BloodGroupAssignmentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    insertData(context = applicationContext)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun insertData(context: Context) {
    val database = MyDatabase.getDatabase(context)
    val UserDao = database.myDao()
    var ins by remember {
        mutableStateOf(false)
    }
    var scope = rememberCoroutineScope()
    var bloodGroup by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var data by remember {
        mutableStateOf<List<User>?>(null)
    }

    if (ins) {
        scope.launch {
            try {
                UserDao.insert(
                    User(
                        bloodGroup = bloodGroup,
                        name = name,
                    )
                )
                ins = false
            } catch (
                ex: Exception
            ) {
                println(ex.message)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var expanded by remember { mutableStateOf(false) }
        var bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        Text("Insert, Retrieve, Delete Demo", fontWeight = FontWeight(900))
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = name, onValueChange = { name = it }, label = { Text(text = "Name") })

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = bloodGroup,
                onValueChange = {},
                label = { Text(text = "Blood Group") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                bloodGroups.forEach { group ->
                    DropdownMenuItem(text = { Text(text = group) }, onClick = {
                        bloodGroup = group
                        expanded = false
                    })

                }
            }
        }


        Button(onClick = { ins = !ins }) {
            Text(text = "Insert")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        var searchTerm by remember {
            mutableStateOf("")
        }
        var expanded_search by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded_search,
            onExpandedChange = { expanded_search = !expanded_search }) {
            TextField(
                value = searchTerm,
                onValueChange = {},
                label = { Text(text = "Blood Group") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded_search) },
                modifier = Modifier
                    .menuAnchor()
                    .padding(8.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded_search,
                onDismissRequest = { expanded_search = false }
            ) {
                bloodGroups.forEach { blood ->
                    DropdownMenuItem(text = { Text(text = blood) }, onClick = {
                        searchTerm = blood
                    })
                }

            }
        }

        Button(onClick = {
            scope.launch {
                try {
                    data = emptyList()
                    val userInfo = UserDao.get(searchTerm)
                    data = userInfo
                } catch (ex: Exception) {
                    println("Error while retrieving data")
                }
            }
        }) {
            Text(text = "Retrieve", Modifier.padding(4.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        if (!data.isNullOrEmpty()) {
            LazyColumn {
                items(data!!) {
                    Text(text = "Item Id: ${it.name} \nName: ${it.bloodGroup}\n")
                }
            }
        }
        var deleteName by remember {
            mutableStateOf("")
        }
        TextField(
            value = deleteName,
            onValueChange = { deleteName = it },
            label = { Text(text = "Name to delete") },
            modifier = Modifier.padding(8.dp)
        )

        Button(onClick = {
            scope.launch {
                try {
                    UserDao.delete(deleteName)
                    Toast.makeText(context, "Deleted records with name: $deleteName", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    println("Error while deleting data")
                    Toast.makeText(context, "Error while deleting data", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(text = "Delete", Modifier.padding(4.dp))
        }
    }
}