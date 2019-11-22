package com.dalmazo.helena.mestrerpg.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dalmazo.helena.mestrerpg.NpcActivity
import com.dalmazo.helena.mestrerpg.R
import com.dalmazo.helena.mestrerpg.enum.RequestCode
import com.dalmazo.helena.mestrerpg.fragment.NpcFragment
import com.dalmazo.helena.mestrerpg.model.Npc
import com.dalmazo.helena.mestrerpg.repository.image.NpcImageRepository
import com.dalmazo.helena.mestrerpg.util.Extra
import com.google.firebase.storage.FirebaseStorage

class NpcAdapter(private val fragment: NpcFragment, private val npcList: MutableList<Npc>) : RecyclerView.Adapter<NpcAdapter.NpcViewHolder>(), Filterable {

    private val originalNpcList: MutableList<Npc> = npcList.toMutableList()

    class NpcViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById(R.id.npc_item_image) as ImageView
        val name = itemView.findViewById(R.id.npc_item_name) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NpcViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.npc_list_item, parent, false) as View
        return NpcViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NpcViewHolder, position: Int) {
        val npc = npcList[position]

        holder.name.text = npc.name

        holder.image.setImageResource(R.drawable.no_image_available)
        NpcImageRepository().get(npc).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.image.setImageBitmap(bitmap)
        }

        holder.itemView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(fragment.activity, NpcActivity::class.java).apply { putExtra(Extra.NPC_OBJECT, npc) }
                fragment.startActivityForResult(intent, RequestCode.NPC.value)
            }}
        )
    }

    override fun getItemCount() = npcList.size

    override fun getFilter() = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            npcList.clear()

            if (constraint.isNullOrBlank()) {
                npcList.addAll(originalNpcList)
            } else {
//                val filteredNpcs = originalNpcList.filter { it.name.toLowerCase().contains(constraint.toString().toLowerCase()) }
                for (currentNpc in originalNpcList) {
                    if (currentNpc.name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        npcList.add(currentNpc)
                    }
                }
            }

            filterResults.values = npcList
            filterResults.count = npcList.count()

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    fun add(npc: Npc) {
        originalNpcList.add(npc)
        npcList.add(npc)
        val position = getPositiion(npc) // get position after add npc
        notifyItemInserted(position)
    }

    fun update(npc: Npc) {
        val position = getPositiion(npc)
        originalNpcList.set(position, npc)
        npcList.set(position, npc)
        notifyItemChanged(position)
    }

    fun remove(npc: Npc) {
        val position = getPositiion(npc)
        originalNpcList.remove(npc)
        npcList.remove(npc)
        notifyItemRemoved(position)
    }

    private fun getPositiion(npc: Npc): Int {
        return originalNpcList.indexOf(originalNpcList.filter { it.id == npc.id }.first())
    }
}