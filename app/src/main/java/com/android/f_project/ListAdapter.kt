package com.android.f_project


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.f_project.datamodel.SceneModel

class ListAdapter(private val contentList: ArrayList<SceneModel>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_scene, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.text = contentList[position].time
        holder.content.text = contentList[position].content
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.time_field)
        val content: TextView = itemView.findViewById(R.id.text_field)
        val background: CardView = itemView.findViewById(R.id.card)
    }
}