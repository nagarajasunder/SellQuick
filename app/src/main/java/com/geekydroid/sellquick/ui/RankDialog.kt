package com.geekydroid.sellquick.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.geekydroid.sellquick.R
import com.geekydroid.sellquickbackend.data.entity.Item
import com.google.android.material.chip.ChipGroup

private const val TAG = "RankDialog"

class RankDialog(
    private val item: Item,
    private val hourRank: Pair<Int, Int>,
    private val dayRank: Pair<Int, Int>
) : DialogFragment() {

    private lateinit var itemName: TextView
    private lateinit var itemPrice: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var totalOrders: TextView
    private lateinit var overallRank: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rank_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.rank_dialog, null)
        builder.setView(view)
        setUI(view)
        setData(hourRank, view)

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.hour_chip -> setData(hourRank, view)
                R.id.day_chip -> setData(dayRank, view)
            }
        }

        builder.setNegativeButton(
            "Close"
        ) { _, _ -> dialog?.dismiss() }
        return builder.create()
    }


    private fun setData(rankPair: Pair<Int, Int>, view: View) {
        if (rankPair.first != -1 && rankPair.second != -1) {
            overallRank.text =
                view.context.getString(R.string.overall_rank, rankPair.first.toString())
            totalOrders.text =
                view.context.getString(R.string.total_orders, rankPair.second.toString())
        } else {
            overallRank.text =
                view.context.getString(R.string.no_rank_available)
            totalOrders.text =
                view.context.getString(R.string.no_orders)
        }
    }

    private fun setUI(view: View) {
        itemName = view.findViewById(R.id.item_name)
        itemPrice = view.findViewById(R.id.item_price)
        chipGroup = view.findViewById(R.id.chip_group)
        totalOrders = view.findViewById(R.id.total_orders)
        overallRank = view.findViewById(R.id.rank)

        itemName.text = item.itemName
        itemPrice.text = item.itemPrice

    }

}