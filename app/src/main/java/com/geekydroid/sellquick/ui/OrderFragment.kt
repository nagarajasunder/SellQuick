package com.geekydroid.sellquick.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.geekydroid.sellquick.R
import com.geekydroid.sellquick.adapters.CartFragmentAdapter
import com.geekydroid.sellquick.utils.listeners.itemRemoveListener
import com.geekydroid.sellquick.viewmodels.OrderFragmentViewModel
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OrderFragment : Fragment(R.layout.fragment_order), itemRemoveListener {

    private lateinit var emptyCartText: TextView
    private lateinit var lottieAnim: LottieAnimationView
    private lateinit var fragmentView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartFragmentAdapter
    private val viewModel: OrderFragmentViewModel by viewModels()
    private lateinit var fabCheckout: ExtendedFloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view
        setUI()

        viewModel.getOrderList().observe(viewLifecycleOwner) { response ->
            if (response.isNotEmpty()) {
                emptyCartText.visibility = View.GONE
                lottieAnim.visibility = View.GONE
                adapter.submitList(response)
            } else {
                adapter.submitList(listOf())
                fabCheckout.visibility = View.GONE
                lottieAnim.visibility = View.VISIBLE
                emptyCartText.visibility = View.VISIBLE
            }

        }

        fabCheckout.setOnClickListener {
            viewModel.checkout()
        }


    }

    private fun setUI() {
        fabCheckout = fragmentView.findViewById(R.id.fab_checkout)
        recyclerView = fragmentView.findViewById(R.id.recycle_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CartFragmentAdapter(this)
        recyclerView.adapter = adapter
        lottieAnim = fragmentView.findViewById(R.id.lottie_anim)
        emptyCartText = fragmentView.findViewById(R.id.empty_cart_text)
    }

    override fun onItemRemove(orderId: Int) {
        viewModel.removeOrder(orderId)
        "Item Removed from cart".showSnackBar()
    }

    private fun String.showSnackBar() {
        Snackbar.make(fragmentView, this, Snackbar.LENGTH_SHORT).show()
    }
}