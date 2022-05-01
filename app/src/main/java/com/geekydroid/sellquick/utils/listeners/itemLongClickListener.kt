package com.geekydroid.sellquick.utils.listeners

import com.geekydroid.sellquickbackend.data.entity.Item

interface ItemLongClickListener {

    fun itemOnLongClick(item: Item)
}