package com.geekydroid.sellquick.viewmodels

import androidx.lifecycle.*
import com.geekydroid.sellquick.utils.TimeUtils
import com.geekydroid.sellquickbackend.data.entity.Item
import com.geekydroid.sellquickbackend.data.entity.ItemRankWrapper
import com.geekydroid.sellquickbackend.data.entity.Order
import com.geekydroid.sellquickbackend.data.entity.OrderStatus
import com.geekydroid.sellquickbackend.repository.ItemRepository
import com.geekydroid.sellquickbackend.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.set


@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    repository: ItemRepository,
    private val orderRepository: OrderRepository
) :
    ViewModel() {


    private val orderData: LiveData<List<ItemRankWrapper>> =
        orderRepository.getOrdersForRank()


    private val itemRankForDay: HashMap<Int, Pair<Int, Int>> = hashMapOf()
    private val itemRankForHour: HashMap<Int, Pair<Int, Int>> = hashMapOf()
    private val itemRankForWeek: HashMap<Int, Pair<Int, Int>> = hashMapOf()


    fun getOrderdata() = orderData


    fun refreshRanks(
        list: List<ItemRankWrapper>
    ) {
        val orderForHourList =
            list.filter { (it.orderedOn >= TimeUtils.getPastOneHour() && it.orderedOn <= System.currentTimeMillis()) }

        val orderForDayList = list.filter {
            it.orderedOn >= TimeUtils.getStartTimeOfTheDay() && it.orderedOn <= TimeUtils.getEndTimeOfTheDay()
        }

        val orderForWeekList = list.filter {
            it.orderedOn >= TimeUtils.getPastOnWeek() && it.orderedOn <= System.currentTimeMillis()
        }



        if (orderForHourList.isNotEmpty()) {
            assignRanks(orderForHourList, itemRankForHour)
        }
        if (orderForDayList.isNotEmpty()) {
            assignRanks(orderForDayList, itemRankForDay)
        }
        if (orderForWeekList.isNotEmpty()) {
            assignRanks(orderForWeekList, itemRankForWeek)
        }


    }

    private fun assignRanks(list: List<ItemRankWrapper>, map: HashMap<Int, Pair<Int, Int>>) {
        map.clear()
        var rank = 1
        var maxSales = list[0].totalOrders
        map[list[0].itemId] = Pair(1, list[0].totalOrders)
        for (i in 1 until list.size) {
            if (list[i].totalOrders < maxSales) {
                map[list[i].itemId] = Pair(++rank, list[i].totalOrders)
                maxSales = list[i].totalOrders
            } else {
                map[list[i].itemId] = Pair(rank, list[i].totalOrders)
            }
        }

    }

    private var cartList: MutableList<Int> = mutableListOf()

    private val searchText: MutableLiveData<String> = MutableLiveData("")

    private var items: LiveData<List<Item>> = Transformations.switchMap(searchText) {
        repository.getAllItems(it)
    }

    fun getSearchText() = searchText
    fun updateSearchText(text: String) {
        searchText.postValue(text)
    }


    fun getItems() = items

    fun addItem(item: Item): Boolean {
        return if (cartList.contains(item.itemId)) {
            false
        } else {
            val order = Order(
                itemId = item.itemId, orderedOn = System.currentTimeMillis(),
                orderStatus = OrderStatus.INCART
            )
            cartList.add(item.itemId)
            insertOrder(order)
            true
        }
    }

    private fun insertOrder(order: Order) {
        viewModelScope.launch {
            orderRepository.insertOrder(order)
        }
    }


    fun getCartItems(): IntArray {

        return cartList.toIntArray()
    }

    fun emptyCart() {
        cartList.clear()
    }

    fun getItemRank(itemId: Int): Triple<Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>> {

        return Triple(
            itemRankForHour.getOrDefault(
                itemId, Pair(-1, -1)
            ),
            itemRankForDay.getOrDefault(itemId, Pair(-1, -1)),
            itemRankForWeek.getOrDefault(itemId, Pair(-1, -1))

        )

    }

}