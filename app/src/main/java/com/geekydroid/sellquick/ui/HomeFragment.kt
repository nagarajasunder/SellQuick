package com.geekydroid.sellquick.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
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

    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var emptySearchText: TextView
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
                if (it.isEmpty()) {
                    showAnims(true)
                } else {
                    showAnims(false)
                }

            }
        }

        viewModel.getOrderdata().observe(viewLifecycleOwner)
        { response ->
            if (response.isNotEmpty()) {
                viewModel.refreshRanks(response)
            }
        }


    }

    private fun showAnims(show: Boolean) {
        if (show) {
            lottieAnimationView.visibility = View.VISIBLE
            emptySearchText.visibility = View.VISIBLE
        } else {
            lottieAnimationView.visibility = View.GONE
            emptySearchText.visibility = View.GONE
        }
    }

    private fun setUI() {

        lottieAnimationView = fragmentView.findViewById(R.id.lottie_anim)
        emptySearchText = fragmentView.findViewById(R.id.empty_list_text)
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
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            /**
             * Called when the user submits the query. This could be due to a key press on the
             * keyboard or due to pressing a submit button.
             * The listener can override the standard behavior by returning true
             * to indicate that it has handled the submit request. Otherwise return false to
             * let the SearchView handle the submission by launching any associated intent.
             *
             * @param query the query text that is to be submitted
             *
             * @return true if the query has been handled by the listener, false to let the
             * SearchView perform the default action.
             */
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            /**
             * Called when the query text is changed by the user.
             *
             * @param newText the new content of the query text field.
             *
             * @return false if the SearchView should perform the default action of showing any
             * suggestions if available, true if the action was handled by the listener.
             */
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    viewModel.updateSearchText(newText)
                } else {
                    viewModel.updateSearchText("")
                }

                return true
            }

        })
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
        rankDialog = RankDialog(
            item = item,
            hourRank = ranks.first,
            dayRank = ranks.second,
            weekRank = ranks.third
        )
        val sm = requireActivity().supportFragmentManager
        rankDialog.show(sm, "rankdialog")
    }


}