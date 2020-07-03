package com.example.myapplication

import com.contrarywind.adapter.WheelAdapter

class NumericWheelAdapter(private val displayedValues: Array<Int>) : WheelAdapter<Int> {

    fun update() {

    }

    override fun indexOf(o: Int?): Int {
        return displayedValues.find { it == o } ?: -1
    }

    override fun getItemsCount(): Int {
        return displayedValues.size
    }

    override fun getItem(index: Int): Int {
        return displayedValues.getOrNull(index) ?: 0
    }
}

class CustomWheelAdapterV4<T>(var displayedValues: List<T>) : WheelAdapter<T> {
    override fun indexOf(o: T): Int {
        return displayedValues.indexOf(o)
    }

    override fun getItemsCount(): Int {
        return displayedValues.size
    }

    override fun getItem(index: Int): T? {
        return displayedValues.getOrNull(index)
    }
}