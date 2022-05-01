package com.geekydroid.sellquick.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geekydroid.sellquick.R
import com.geekydroid.sellquick.adapters.HomeFragmentAdapter
import com.geekydroid.sellquick.utils.listeners.ItemLongClickListener
import com.geekydroid.sellquick.utils.listeners.ItemOnClickListener
import com.geekydroid.sellquick.viewmodels.HomeFragmentViewModel
import com.geekydroid.sellquickbackend.data.entity.Item
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), ItemOnClickListener, ItemLongClickListener {

    private lateinit var fragmentView: View
    private val viewModel: HomeFragmentViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeFragmentAdapter
    private lateinit var rankDialog: RankDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view
        setHasOptionsMenu(true)
        setUI()
        viewModel.getItems().observe(viewLifecycleOwner)
        { response ->
            response?.let {
                adapter.submitList(it)
            }
        }

        viewModel.getOrderdata().observe(viewLifecycleOwner)
        { response ->
            if (response.isNotEmpty()) {
                viewModel.refreshRanks(response)
            }
        }


    }

    private fun setUI() {

        recyclerView = fragmentView.findViewById(R.id.recycle_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HomeFragmentAdapter(this, this)
        recyclerView.adapter = adapter

    }

    override fun addToCart(item: Item) {
        val response = viewModel.addItem(item)
        if (response) {
            showSnackBar("${item.itemName} added to Cart")
        } else {
            showSnackBar("${item.itemName} already added to cart")
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(fragmentView, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cart -> navigateToCart()
        }

        return true
    }

    private fun navigateToCart() {
        val cartItems = viewModel.getCartItems()
        viewModel.emptyCart()
        val action =
            HomeFragmentDirections.actionHomeFragmentToOrderFragment(cartItems)
        findNavController().navigate(action)
    }

    override fun itemOnLongClick(item: Item) {
        val ranks = viewModel.getItemRank(item.itemId)
        rankDialog = RankDialog(item = item, hourRank = ranks.first, dayRank = ranks.second)
        val sm = requireActivity().supportFragmentManager
        rankDialog.show(sm, "rankdialog")
    }


}