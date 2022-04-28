package nz.co.test.transactions.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import nz.co.test.transactions.R
import nz.co.test.transactions.data.services.Transaction
import nz.co.test.transactions.databinding.FragmentTransactionListBinding
import nz.co.test.transactions.ui.adapters.TransactionAdapter
import nz.co.test.transactions.ui.viewmodels.TransactionViewModel


@AndroidEntryPoint
class TransactionListFragment : Fragment() {

    private val viewModel by viewModels<TransactionViewModel>()
    private lateinit var binding : FragmentTransactionListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        //Labmda for clicking on transaction item
        val openTransactionDetails = { transaction: Transaction ->
            val navController = Navigation.findNavController(binding.root)
            val bundle = Bundle()
            bundle.putParcelable("transactionDetails", transaction)
            navController.navigate(R.id.actionShowTransactionDetails, bundle)
        }

        //Setting up a list
        val adapter = TransactionAdapter(openTransactionDetails)
        binding.transactionList.adapter = adapter

        //Processing states
        viewModel.transactionStateLiveData.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                binding.loading.visibility = View.VISIBLE
            }
            if (state.error.isNotBlank()) {
                binding.textError.text = state.error
                binding.loading.visibility = View.GONE
                binding.textError.visibility = View.VISIBLE
            }
            if (state.transactions.isNotEmpty()) {
                adapter.setItems(state.transactions)
                binding.loading.visibility = View.GONE
            }
        }

    }

    companion object {
        @JvmStatic fun newInstance() = TransactionListFragment()
    }
}