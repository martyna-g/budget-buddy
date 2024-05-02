package pl.tinks.budgetbuddy.shopping.list

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.ActionMode
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var addItemButton: Button
    private lateinit var addItemLayout: TextInputLayout
    private var addItemLayoutHintColor: ColorStateList? = null
    private lateinit var toolbar: MaterialToolbar
    private var actionMode: ActionMode? = null

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            requireActivity().menuInflater.inflate(R.menu.shopping_list_action_menu, menu)
            disableTextInput()
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, menuItem: MenuItem?): Boolean {
            when (menuItem?.itemId) {
                R.id.action_delete_selected -> {
                    viewModel.deleteSelectedItems(adapter.selectedItems)
                    mode?.finish()
                    return true
                }

                R.id.action_delete_checked -> {
                    showConfirmationDialog(
                        R.string.delete_checked_items_message
                    ) {
                        viewModel.deleteCheckedItems()
                    }
                    mode?.finish()
                    return true
                }

                R.id.action_delete_unchecked -> {
                    showConfirmationDialog(
                        R.string.delete_unchecked_items_message
                    ) {
                        viewModel.deleteUncheckedItems()
                    }
                    mode?.finish()
                    return true
                }

                R.id.action_delete_all -> {
                    showConfirmationDialog(
                        R.string.delete_all_items_message
                    ) {
                        viewModel.deleteAllShoppingItems()
                    }
                    mode?.finish()
                    return true
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            adapter.selectedItems.clear()
            adapter.notifyDataSetChanged()
            enableTextInput()
            actionMode = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentShoppingListBinding.inflate(inflater, container, false)
        adapter = ShoppingListAdapter(
            ::toggleItemCollectedStatus, ::startActionMode, ::finishActionMode
        )

        addItemEditText = binding.editTextShoppingItem
        addItemButton = binding.buttonAddNewItem
        addItemLayout = binding.textInputLayoutAddShoppingItem
        toolbar = binding.toolbarShoppingList
        recyclerView = binding.recyclerviewShoppingList

        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.adapter = adapter

        binding.buttonAddNewItem.setOnClickListener { addNewItem() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        navController.addOnDestinationChangedListener { _, _, _ ->
            actionMode?.finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                topMargin = insets.top
                rightMargin = insets.right
            }

            WindowInsetsCompat.CONSUMED
        }

        addItemEditText.setOnEditorActionListener { _, actionId, keyEvent ->
            val actionDone = actionId == EditorInfo.IME_ACTION_DONE ||
                    (keyEvent?.action == KeyEvent.ACTION_DOWN
                            && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)

            if (actionDone) {
                addNewItem()
                true
            } else {
                false
            }
        }

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
                inBasket = !shoppingItem.inBasket
            )
        )
    }

    private fun startActionMode() {
        actionMode = requireActivity().startActionMode(actionModeCallback)
    }

    private fun finishActionMode() {
        actionMode?.finish()
    }


    private fun disableUserInteractions() {
        recyclerView.visibility = View.GONE
        toolbar.visibility = View.GONE
        addItemEditText.visibility = View.GONE
        addItemButton.visibility = View.GONE
    }

    private fun enableUserInteractions() {
        recyclerView.visibility = View.VISIBLE
        toolbar.visibility = View.VISIBLE
        addItemEditText.visibility = View.VISIBLE
        addItemButton.visibility = View.VISIBLE
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireActivity()).setMessage(R.string.shopping_loading_error_message)
            .setPositiveButton(R.string.payments_loading_error_ok) { _, _ ->
                enableUserInteractions()
            }.show()
    }

    private fun showConfirmationDialog(
        messageResId: Int, onConfirm: () -> Unit
    ) {
        AlertDialog.Builder(requireActivity()).setMessage(messageResId)
            .setPositiveButton(R.string.yes) { _, _ ->
                onConfirm()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun getColorFromAttr(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun disableTextInput() {
        val greyedOut = getColorFromAttr(requireContext(), R.attr.colorTextGreyedOut)
        addItemLayoutHintColor = addItemLayout.defaultHintTextColor

        addItemLayout.defaultHintTextColor = ColorStateList.valueOf(greyedOut)
        addItemEditText.isEnabled = false
        addItemButton.visibility = View.GONE
    }

    private fun enableTextInput() {

        if (addItemLayoutHintColor != null) {
            addItemLayout.defaultHintTextColor = addItemLayoutHintColor
        }

        addItemEditText.isEnabled = true
        addItemButton.visibility = View.VISIBLE
    }

}
