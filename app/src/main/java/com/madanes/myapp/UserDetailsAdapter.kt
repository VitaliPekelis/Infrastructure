package com.madanes.myapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserDetailsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    var data : List<UserDetailItemData> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType)
        {
            99 ->
            {
                UserAtrrViewHolder(
                        layoutInflater.inflate(R.layout.item_user_attribut, parent, false)
                )
            }

            else -> super.createViewHolder(parent, viewType)
        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return 99
    }

    override fun getItemCount(): Int
    {
        return data.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        when (holder)
        {
            is UserAtrrViewHolder ->
            {
                val item = data[holder.adapterPosition] as? UserDetailItemData
                onBindUserAttributeViewHolder(holder, item)
            }
        }
    }

    private fun onBindUserAttributeViewHolder(h: UserAtrrViewHolder, item: UserDetailItemData?)
    {
        item?.let {
            val text = "${it.title} ${it.value}"
            h.titleTextView.text = text
        }
    }

}


class UserAtrrViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
{
    val titleTextView = itemView.findViewById<TextView>(R.id.item_user_attr_tv)
}