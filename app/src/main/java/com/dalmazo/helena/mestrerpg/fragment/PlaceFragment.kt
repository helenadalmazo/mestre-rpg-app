package com.dalmazo.helena.mestrerpg.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dalmazo.helena.mestrerpg.R

class PlaceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return return inflater.inflate(R.layout.fragment_place, container, false)
    }
}