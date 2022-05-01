package com.geekydroid.sellquick.utils.listeners

import com.geekydroid.sellquickbackend.data.entity.Item

interface ItemOnClickListener {

    fun addToCart(item: Item)

}