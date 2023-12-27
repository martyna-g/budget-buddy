package pl.tinks.budgetbuddy.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import pl.tinks.budgetbuddy.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var upcomingPaymentsButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        upcomingPaymentsButton = binding.buttonUpcomingPayments
        upcomingPaymentsButton.setOnClickListener { navigateToPaymentList() }

        return binding.root
    }

    private fun navigateToPaymentList() {
        val action = HomeFragmentDirections.actionHomeFragmentToPaymentListFragment()
        findNavController().navigate(action)
    }
}
