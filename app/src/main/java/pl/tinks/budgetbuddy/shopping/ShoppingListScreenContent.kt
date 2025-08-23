package pl.tinks.budgetbuddy.shopping

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.tinks.budgetbuddy.ErrorScreen
import pl.tinks.budgetbuddy.LoadingScreen
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.ui.theme.BudgetBuddyTheme
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreenContent(
    state: ShoppingListUiState,
    onAddClick: (String) -> Unit,
    onUpdateItem: (ShoppingItem) -> Unit,
    onDeleteItems: (Set<UUID>) -> Unit,
    onDeleteAll: () -> Unit,
    onDeleteChecked: () -> Unit,
    onDeleteUnchecked: () -> Unit,
    onErrorDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var selectionMode by rememberSaveable { mutableStateOf(false) }
    var selectedItems by rememberSaveable { mutableStateOf(setOf<UUID>()) }
    var newItem by rememberSaveable { mutableStateOf("") }

    fun addItemAndClear() {
        if (newItem.isNotBlank()) {
            onAddClick(newItem)
            newItem = ""
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
        topBar = {
            TopAppBar(title = {
                if (selectionMode) {
                    Text("${selectedItems.size} selected")
                } else {
                    Text(
                        text = stringResource(R.string.shopping_list),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }, actions = {
                if (selectionMode) {
                    IconButton(onClick = {
                        onDeleteItems(selectedItems)
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
                                onDeleteAll()
                                expanded = false
                            })
                        DropdownMenuItem(text = { Text(stringResource(R.string.dropdown_delete_checked)) },
                            onClick = {
                                onDeleteChecked()
                                expanded = false
                            })
                        DropdownMenuItem(text = { Text(stringResource(R.string.dropdown_delete_unchecked)) },
                            onClick = {
                                onDeleteUnchecked()
                                expanded = false
                            })
                    }
                }
            })
        },
    ) { innerPadding ->
        Column(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null
            ) {
                focusManager.clearFocus()
            }) {
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
                    ShoppingListContent(
                        items = state.data, selectedItems = selectedItems, onItemClick = { item ->
                            if (selectionMode) {
                                selectedItems = if (selectedItems.contains(item.id)) {
                                    selectedItems - item.id
                                } else {
                                    selectedItems + item.id
                                }
                                if (selectedItems.isEmpty()) selectionMode = false
                            } else {
                                onUpdateItem(item.copy(inBasket = !item.inBasket))
                            }
                            focusManager.clearFocus()
                        }, modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                is ShoppingListUiState.Loading -> LoadingScreen(
                    modifier = modifier.padding(
                        innerPadding
                    )
                )

                is ShoppingListUiState.Error -> ErrorScreen(
                    onOk = onErrorDialogDismiss, modifier = modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
fun ShoppingListScreenContentPreview() {
    BudgetBuddyTheme {
        ShoppingListScreenContent(state = ShoppingListUiState.Success(
            listOf(
                ShoppingItem(
                    UUID.randomUUID(), "Random Item"
                )
            )
        ),
            onAddClick = {},
            onUpdateItem = {},
            onDeleteItems = {},
            onDeleteAll = {},
            onDeleteChecked = {},
            onDeleteUnchecked = {},
            onErrorDialogDismiss = {})
    }
}
