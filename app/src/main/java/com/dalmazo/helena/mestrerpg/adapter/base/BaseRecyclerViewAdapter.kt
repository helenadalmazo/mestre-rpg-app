package com.dalmazo.helena.mestrerpg.adapter.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.dalmazo.helena.mestrerpg.model.base.BaseModel

abstract class BaseRecyclerViewAdapter<Model: BaseModel>: RecyclerView.Adapter<RecyclerView.ViewHolder>, Filterable {

    private val list: MutableList<Model>
    private val originalList: MutableList<Model>

    constructor(_list: MutableList<Model>) {
        list = _list
        originalList = _list.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return getViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val obj = list[position]
        (holder as Binder<Model>).bind(obj)
    }

    abstract fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder

    interface Binder<in Model> {
        fun bind(item: Model)
    }

    override fun getItemCount() = list.size

    override fun getFilter() = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            list.clear()

            if (constraint.isNullOrBlank()) {
                list.addAll(originalList)
            } else {
//                val filteredNpcs = originalList.filter { it.name.toLowerCase().contains(constraint.toString().toLowerCase()) }
                for (currentNpc in originalList) {
                    if (currentNpc.name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        list.add(currentNpc)
                    }
                }
            }

            filterResults.values = list
            filterResults.count = list.count()

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    fun add(model: Model) {
        originalList.add(model)
        list.add(model)
        val position = getPosition(model) // get position after add model
        notifyItemInserted(position)
    }

    fun update(model: Model) {
        val position = getPosition(model)
        originalList.set(position, model)
        list.set(position, model)
        notifyItemChanged(position)
    }

    fun remove(model: Model) {
        val position = getPosition(model)
        originalList.remove(model)
        list.remove(model)
        notifyItemRemoved(position)
    }

    private fun getPosition(model: Model): Int {
        return originalList.indexOf(originalList.filter { it.id == model.id }.first())
    }
}