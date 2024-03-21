package pl.tinks.budgetbuddy.shopping.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.tinks.budgetbuddy.databinding.FragmentShoppingListBinding

class ShoppingListFragment : Fragment() {

    private lateinit var binding: FragmentShoppingListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentShoppingListBinding.inflate(inflater, container, false)

        return binding.root
    }
}
