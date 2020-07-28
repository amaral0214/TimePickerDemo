package com.example.myapplication.fragments

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_text.*
import java.util.*

class TextFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Locale.setDefault(Locale.JAPANESE)
        textView.movementMethod = LinkMovementMethod.getInstance()

        val str = "<ul>\r\n<li>One-night stay in a guestroom</li>\r\n<li>Breakfast and dinner buffet for two adults (complimentary for two children younger than 12 years old)</li>\r\n<li>Complimentary access to the Gymboree World and the Nintendo Play Zone</li>\r\n<li>Gymboree Magformers Class (subject to additional charges )</li>\r\n<li>Complimentary access to the swimming pools (including the children’s pool), sauna and fitness center</li>\r\n</ul>\r\n"
        textView.text = Html.fromHtml(str).trim()
    }
}