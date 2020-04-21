package com.dalmazo.helena.mestrerpg.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dalmazo.helena.mestrerpg.NpcActivity
import com.dalmazo.helena.mestrerpg.R
import com.dalmazo.helena.mestrerpg.adapter.base.BaseRecyclerViewAdapter
import com.dalmazo.helena.mestrerpg.enum.RequestCode
import com.dalmazo.helena.mestrerpg.fragment.NpcFragment
import com.dalmazo.helena.mestrerpg.model.Npc
import com.dalmazo.helena.mestrerpg.util.Extra

class NpcRecyclerViewAdapter(npcList: MutableList<Npc>,
                             private val fragment: NpcFragment)
    : BaseRecyclerViewAdapter<Npc>(npcList) {

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return NpcViewHolder(view, fragment)
    }

    class NpcViewHolder: RecyclerView.ViewHolder, BaseRecyclerViewAdapter.Binder<Npc> {

        private val image: ImageView
        private val name: TextView
        private val fragment: NpcFragment

        constructor(itemView: View, _fragment: NpcFragment) : super(itemView) {
            image = itemView.findViewById(R.id.npc_item_image) as ImageView
            name = itemView.findViewById(R.id.npc_item_name) as TextView
            fragment = _fragment
        }

        override fun bind(npc: Npc) {
            name.text = npc.name

            if (npc.image.isEmpty()) {
                image.setImageResource(R.drawable.no_image_available)
            } else {
                val bitmap = BitmapFactory.decodeByteArray(npc.image, 0, npc.image.size)
                image.setImageBitmap(bitmap)
            }

            itemView.setOnClickListener {
                val intent = Intent(fragment.activity, NpcActivity::class.java).apply { putExtra(Extra.NPC_OBJECT, npc) }
                fragment.startActivityForResult(intent, RequestCode.NPC.value)
            }
        }
    }
}