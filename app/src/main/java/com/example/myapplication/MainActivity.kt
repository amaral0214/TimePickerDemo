package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Locale.setDefault(Locale.JAPANESE)
//        textView.movementMethod = LinkMovementMethod.getInstance()

//        val str = "<ul>\r\n<li>One-night stay in a guestroom</li>\r\n<li>Breakfast and dinner buffet for two adults (complimentary for two children younger than 12 years old)</li>\r\n<li>Complimentary access to the Gymboree World and the Nintendo Play Zone</li>\r\n<li>Gymboree Magformers Class (subject to additional charges )</li>\r\n<li>Complimentary access to the swimming pools (including the children’s pool), sauna and fitness center</li>\r\n</ul>\r\n"
//        textView.text = Html.fromHtml(str).trim()

        timePicker.mIs24Hour=false


//        numberPicker.minValue = 0
//        numberPicker.maxValue = 30
//        numberPicker.displayedValues = arrayOf("00","15","30","45","60")

        wheelStyleTimePicker.initView(14 to 30, 16 to 0, 3 to 30)

        picker.setMax(20)
        picker.setMin(1)
    }
}
