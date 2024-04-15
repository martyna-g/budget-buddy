package pl.tinks.budgetbuddy.shopping.list

import android.os.Bundle
import android.view.ActionMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.tinks.budgetbuddy.R
import pl.tinks.budgetbuddy.databinding.FragmentShoppingListBinding
import pl.tinks.budgetbuddy.shopping.ShoppingItem
import pl.tinks.budgetbuddy.shopping.ShoppingListAdapter
import java.util.UUID

@AndroidEntryPoint
class ShoppingListFragment : Fragment() {

    private val viewModel: ShoppingListViewModel by viewModels()
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var binding: FragmentShoppingListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var addItemEditText: TextInputEditText
    private lateinit var toolbar: MaterialToolbar
    private var actionMode: ActionMode? = null

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().menuInflater.inflate(R.menu.shopping_list_action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, menuItem: MenuItem?): Boolean {
            when (menuItem?.itemId) {
                R.id.action_delete_selected -> {
                    for (item in adapter.selectedItems) {
                        viewModel.deleteShoppingItem(item)
                    }
                    addItemEditText.visibility = View.VISIBLE
                    mode?.finish()
                    return true
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        adapter = ShoppingListAdapter(
            ::toggleItemCollectedStatus,
            ::startActionMode,
            ::finishActionMode
        )

        addItemEditText = binding.editTextShoppingItem
        toolbar = binding.toolbarShoppingList
        recyclerView = binding.recyclerviewShoppingList

        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.adapter = adapter

        binding.buttonAddNewItem.setOnClickListener { addNewItem() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is ShoppingListUiState.Loading -> disableUserInteractions()
                        is ShoppingListUiState.Error -> showErrorDialog()
                        is ShoppingListUiState.Success -> {
                            enableUserInteractions()
                            adapter.submitList(uiState.data)
                        }
                    }
                }
            }
        }
    }

    private fun addNewItem() {
        val textInput = binding.editTextShoppingItem
        val itemName = textInput.text.toString()

        if (itemName.isNotBlank()) {
            viewModel.addShoppingItem(
                ShoppingItem(UUID.randomUUID(), itemName)
            )

            textInput.setText("")

            binding.recyclerviewShoppingList.post {
                binding.recyclerviewShoppingList.smoothScrollToPosition(adapter.itemCount)
            }
        }
    }

    private fun toggleItemCollectedStatus(shoppingItem: ShoppingItem) {
        viewModel.updateShoppingItem(
            shoppingItem.copy(
                isCollected = !shoppingItem.isCollected
            )
        )
    }

    private fun startActionMode() {
        addItemEditText.visibility = View.GONE
        actionMode = requireActivity().startActionMode(actionModeCallback)
    }

    private fun finishActionMode() {
        actionMode?.finish()
        addItemEditText.visibility = View.VISIBLE
    }


    private fun disableUserInteractions() {
        recyclerView.visibility = View.GONE
        toolbar.visibility = View.GONE
        addItemEditText.visibility = View.GONE
    }

    private fun enableUserInteractions() {
        recyclerView.visibility = View.VISIBLE
        toolbar.visibility = View.VISIBLE
        addItemEditText.visibility = View.VISIBLE
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireActivity()).setMessage(R.string.shopping_loading_error_message)
            .setPositiveButton(R.string.payments_loading_error_ok) { _, _ -> enableUserInteractions() }
            .show()
    }

}
