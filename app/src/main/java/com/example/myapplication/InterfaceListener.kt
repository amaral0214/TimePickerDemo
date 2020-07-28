package com.example.myapplication

import android.os.Bundle

interface OnFragmentInteractionListener{
    fun onFragmentInteraction(bundle: Bundle)
}

interface OnItemClickListener {
    fun onItemClicked(position: Int)
}
