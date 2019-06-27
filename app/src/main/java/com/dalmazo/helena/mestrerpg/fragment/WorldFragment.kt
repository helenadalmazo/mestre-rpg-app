package com.dalmazo.helena.mestrerpg.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.dalmazo.helena.mestrerpg.WorldActivity
import com.dalmazo.helena.mestrerpg.R

class WorldFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val world = (activity as WorldActivity).world

        val view = inflater.inflate(R.layout.fragment_world, container, false)

        view.findViewById<EditText>(R.id.name).setText(world.name, TextView.BufferType.NORMAL)
        view.findViewById<EditText>(R.id.characteristics).setText(world.characteristics, TextView.BufferType.EDITABLE)
        view.findViewById<EditText>(R.id.history).setText(world.history, TextView.BufferType.EDITABLE)

        return view
    }
}