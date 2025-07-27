package pl.tinks.budgetbuddy.shopping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.tinks.budgetbuddy.ErrorScreen
import pl.tinks.budgetbuddy.LoadingScreen
import pl.tinks.budgetbuddy.R
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel = hiltViewModel(),
    onErrorDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val state by viewModel.uiState.collectAsState(ShoppingListUiState.Loading)
    var selectionMode by rememberSaveable { mutableStateOf(false) }
    var selectedItems by rememberSaveable { mutableStateOf(setOf<UUID>()) }
    var newItem by rememberSaveable { mutableStateOf("") }

    fun addItemAndClear() {
        if (newItem.isNotBlank()) {
            viewModel.addShoppingItem(
                ShoppingItem(UUID.randomUUID(), newItem)
            )
            newItem = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                if (selectionMode) {
                    Text("${selectedItems.size} selected")
                } else {
                    Text(stringResource(R.string.shopping_list))
                }
            }, actions = {
                if (selectionMode) {
                    IconButton(onClick = {
                        viewModel.deleteItems(selectedItems)
                        selectedItems = emptySet()
                        selectionMode = false
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_selected)
                        )
                    }
                } else {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more_options)
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.dropdown_select_multiple)) },
                            onClick = {
                                selectionMode = true
                                expanded = false
                            })
                        DropdownMenuItem(text = { Text(stringResource(R.string.dropdown_delete_all)) },
                            onClick = {
                                viewModel.deleteAllShoppingItems()
                                expanded = false
                            })
                        DropdownMenuItem(text = { Text(stringResource(R.string.dropdown_delete_checked)) },
                            onClick = {
                                viewModel.deleteCheckedItems()
                                expanded = false
                            })
                        DropdownMenuItem(text = { Text(stringResource(R.string.dropdown_delete_unchecked)) },
                            onClick = {
                                viewModel.deleteUncheckedItems()
                                expanded = false
                            })
                    }
                }
            })
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(value = newItem,
                    onValueChange = { newItem = it },
                    label = { Text(stringResource(R.string.add_item)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addItemAndClear() })
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = { addItemAndClear() },
                    enabled = newItem.isNotBlank(),
                ) {
                    Icon(
                        Icons.Default.Add, contentDescription = stringResource(R.string.add_item)
                    )
                }
            }
            when (state) {
                is ShoppingListUiState.Success -> {
                    ShoppingListScreenContent(
                        items = (state as ShoppingListUiState.Success).data,
                        selectedItems = selectedItems,
                        onItemClick = { item ->
                            if (selectionMode) {
                                selectedItems = if (selectedItems.contains(item.id)) {
                                    selectedItems - item.id
                                } else {
                                    selectedItems + item.id
                                }
                                if (selectedItems.isEmpty()) selectionMode = false
                            } else {
                                viewModel.updateShoppingItem(item.copy(inBasket = !item.inBasket))
                            }
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                is ShoppingListUiState.Loading -> LoadingScreen(
                    modifier = modifier.padding(
                        innerPadding
                    )
                )

                is ShoppingListUiState.Error -> ErrorScreen(
                    onOk = onErrorDialogDismiss,
                    modifier = modifier.padding(innerPadding)
                )
            }
        }
    }
}
