package com.demo.kuanyi.todolist_kotlin.widget

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.demo.kuanyi.todolist_kotlin.R


/**
 * The ViewHolder class holding the views for the list
 * Created by kuanyi on 15/5/14.
 */
class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val cardView: CardView = itemView as CardView
    val textView: TextView
    val checkImageView: ImageView

    init {
        //set the shadow radius
        cardView.radius = itemView.resources.getDimension(R.dimen.card_radius)

        textView = cardView.findViewById(R.id.list_item_title_textview) as TextView
        checkImageView = cardView.findViewById(R.id.list_item_check_img) as ImageView

    }
}
