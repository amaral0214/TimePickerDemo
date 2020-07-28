package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myapplication.fragments.ScrollingFragment
import com.example.myapplication.fragments.SpinnerFragment
import com.example.myapplication.fragments.TextFragment
import com.example.myapplication.fragments.TimePickerFragment

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addFragment(SpinnerFragment(), null, false)
    }

    inline fun <reified F : Fragment> replaceFragmentBKTX(tag: String?, addToBackStack: Boolean, args: Bundle?) {
        supportFragmentManager.commit {
            replace<F>(R.id.frameLayout, tag, args)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }

    inline fun <reified F : Fragment> addFragmentBKTX(tag: String?, addToBackStack: Boolean, args: Bundle?) {
        supportFragmentManager.commit {
            add<F>(R.id.frameLayout, tag, args)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }

    fun replaceFragment(fragment: Fragment, tag: String?, addToBackStack: Boolean) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
            commit()
        }
    }

    fun addFragment(fragment: Fragment, tag: String?, addToBackStack: Boolean) {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frameLayout, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
            commit()
        }
    }

    override fun onFragmentInteraction(bundle: Bundle) {
        when (bundle[TARGET_FRAGMENT_NAME]) {
            TextFragment::class.qualifiedName -> TextFragment()
            TimePickerFragment::class.qualifiedName -> TimePickerFragment()
            SpinnerFragment::class.qualifiedName -> SpinnerFragment()
            else -> null
        }?.let {
            replaceFragment(it, null, true)
        }
    }

    companion object {
        const val TARGET_FRAGMENT_NAME = "TARGET_FRAGMENT_NAME"
    }
}
