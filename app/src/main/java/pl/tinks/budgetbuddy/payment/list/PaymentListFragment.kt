package pl.tinks.budgetbuddy.payment.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import pl.tinks.budgetbuddy.databinding.FragmentPaymentListBinding
import pl.tinks.budgetbuddy.payment.PaymentListAdapter
import javax.inject.Inject

@AndroidEntryPoint
class PaymentListFragment : Fragment() {

    private val viewModel: PaymentListViewModel by viewModels()
    private lateinit var binding: FragmentPaymentListBinding
    private lateinit var recyclerView: RecyclerView
    @Inject lateinit var adapter: PaymentListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentPaymentListBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerviewPaymentList

        val layoutManager = LinearLayoutManager(requireActivity())
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, layoutManager.orientation)

        recyclerView.also {
            it.adapter = adapter
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)
        }

        return binding.root
    }

}
