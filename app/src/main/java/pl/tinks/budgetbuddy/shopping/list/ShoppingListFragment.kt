package pl.tinks.budgetbuddy.shopping.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
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
    private val adapter = ShoppingListAdapter()
    private lateinit var binding: FragmentShoppingListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var addItemEditText: TextInputEditText
    private lateinit var toolbar: MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val layoutManager = LinearLayoutManager(requireActivity())
        binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        addItemEditText = binding.editTextShoppingItem
        toolbar = binding.toolbarShoppingList
        recyclerView = binding.recyclerviewShoppingList

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context, layoutManager.orientation
            )
        )

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
                binding.recyclerviewShoppingList.smoothScrollToPosition(adapter.itemCount )
            }
        }
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
