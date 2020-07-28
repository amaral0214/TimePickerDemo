package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_time_picker.*

class TimePickerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        numberPicker.minValue = 0
        numberPicker.maxValue = 4
        numberPicker.displayedValues = arrayOf("00", "15", "30", "45", "60")

        timePicker.mIs24Hour = false

        wheelStyleTimePicker.initView(14 to 30, 16 to 0, 3 to 30)

        picker.setMax(20)
        picker.setMin(1)
    }
}