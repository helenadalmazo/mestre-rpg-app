package com.dalmazo.helena.mestrerpg.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dalmazo.helena.mestrerpg.MonsterActivity
import com.dalmazo.helena.mestrerpg.R
import com.dalmazo.helena.mestrerpg.adapter.base.BaseRecyclerViewAdapter
import com.dalmazo.helena.mestrerpg.enum.RequestCode
import com.dalmazo.helena.mestrerpg.fragment.MonsterFragment
import com.dalmazo.helena.mestrerpg.model.Monster
import com.dalmazo.helena.mestrerpg.util.Extra

class MonsterRecyclerViewAdapter(monsterList: MutableList<Monster>,
                                 private val fragment: MonsterFragment)
    : BaseRecyclerViewAdapter<Monster>(monsterList) {

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return MonsterViewHolder(view, fragment)
    }

    class MonsterViewHolder: RecyclerView.ViewHolder, BaseRecyclerViewAdapter.Binder<Monster> {

        private val image: ImageView
        private val name: TextView
        private val fragment: MonsterFragment

        constructor(itemView: View, _fragment: MonsterFragment): super(itemView) {
            image = itemView.findViewById(R.id.npc_item_image) as ImageView
            name = itemView.findViewById(R.id.npc_item_name) as TextView
            fragment = _fragment
        }

        override fun bind(monster: Monster) {
            name.text = monster.name

            if (monster.image.isEmpty()) {
                image.setImageResource(R.drawable.no_image_available)
            } else {
                val bitmap = BitmapFactory.decodeByteArray(monster.image, 0, monster.image.size)
                image.setImageBitmap(bitmap)
            }

            itemView.setOnClickListener {
                val intent = Intent(fragment.activity, MonsterActivity::class.java).apply { putExtra(Extra.MONSTER_OBJECT, monster) }
                fragment.startActivityForResult(intent, RequestCode.MONSTER.value)
            }
        }
    }
}