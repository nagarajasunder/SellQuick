package com.geekydroid.sellquick.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.sellquickbackend.data.entity.CartWrapper
import com.geekydroid.sellquickbackend.data.entity.OrderStatus
import com.geekydroid.sellquickbackend.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderFragmentViewModel @Inject constructor(private val repository: OrderRepository) :
    ViewModel() {

    private val orderList: LiveData<List<CartWrapper>> = repository.getAllOrders()

    fun getOrderList() = orderList
    fun checkout() {
        val cartList = getOrderList().value
        if (cartList != null && cartList.isNotEmpty()) {
            viewModelScope.launch {
                repository.checkout(cartList.map { it.orderId }, OrderStatus.PURCHASED.toString())
            }
        }
    }

    fun removeOrder(orderId: Int) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
        }
    }


}
