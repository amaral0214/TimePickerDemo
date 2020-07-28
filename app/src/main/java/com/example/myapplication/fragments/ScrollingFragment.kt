package com.example.myapplication.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.example.myapplication.MainActivity.Companion.TARGET_FRAGMENT_NAME
import com.example.myapplication.adapters.ScrollingAdapter
import kotlinx.android.synthetic.main.fragment_scrolling.*

class ScrollingFragment : Fragment(), OnItemClickListener {
    var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scrolling, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        val list = resources.getStringArray(R.array.title_list)
        recyclerView.adapter = ScrollingAdapter(requireContext(), list.asList()).apply {
            listener = this@ScrollingFragment
        }
    }

    override fun onItemClicked(position: Int) {
        val target = when (position) {
            0 -> TextFragment::class.qualifiedName
            1 -> TimePickerFragment::class.qualifiedName
            2 -> SpinnerFragment::class.qualifiedName
            else -> null
        }
        listener?.onFragmentInteraction(bundleOf(TARGET_FRAGMENT_NAME to target))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnFragmentInteractionListener
    }
}