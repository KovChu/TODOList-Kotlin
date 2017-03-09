package com.demo.kuanyi.todolist_kotlin.list

import android.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demo.kuanyi.todolist_kotlin.R
import com.demo.kuanyi.todolist_kotlin.Utils
import com.demo.kuanyi.todolist_kotlin.model.ListItemTable
import com.demo.kuanyi.todolist_kotlin.widget.AdapterCallback
import com.demo.kuanyi.todolist_kotlin.widget.ListViewHolder
import java.util.*

/**
 * A list adapter that takes in a list of ListItemTable and display it.
 * The adapter also holds the data, and does the modifications on the data
 * Created by kuanyi on 17/3/9.
 */
class ListAdapter(fragment: Fragment) : RecyclerView.Adapter<ListViewHolder>() {


    var mAllListItemTableList: ArrayList<ListItemTable> = ArrayList()

    //the list of the current display list, this will be the same with
    //mAllListItemTableList when the filter is not applied
    var mDisplayingItemTableList: ArrayList<ListItemTable> = ArrayList()

    //whether the list is filtered or not
    var isFiltered = false

    //the callback for communicating with fragment
    private var mAdapterCallback: AdapterCallback

    init {
        try {
            this.mAdapterCallback = fragment as AdapterCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("Fragment must implement AdapterCallback.")
        }
    }

    fun setItems(itemList : ArrayList<ListItemTable>) {
        mAllListItemTableList = itemList
        mDisplayingItemTableList = ArrayList<ListItemTable>()
        mDisplayingItemTableList.addAll(itemList)
    }

    /**
     * adding a new list item to the list
     * @param listItemTable the item to be added
     */
    fun addNewListItem(listItemTable: ListItemTable) {
        val position = mAllListItemTableList.size
        mAllListItemTableList.add(listItemTable)
        mDisplayingItemTableList.add(listItemTable)
        //notify the fragment that the size has been changed
        notifyAdapterSizeChange()
        notifyItemInserted(position)
    }

    //notify the fragment that the size of the adapter has changed
    private fun notifyAdapterSizeChange() {
        mAdapterCallback.onAdapterItemSizeChange(mAllListItemTableList.size)
    }

    // remove an item from the list
    private fun removeItem(itemTable: ListItemTable) {
        val position = mDisplayingItemTableList.indexOf(itemTable)
        //notify need to go first before the actual removing from list so the animation
        //can run correctly
        notifyItemRemoved(position)
        mAllListItemTableList.remove(itemTable)
        mDisplayingItemTableList.removeAt(position)
        //notify the fragment that the size has been changed
        notifyAdapterSizeChange()
    }

    /**
     * remove all items from the list
     */
    fun removeAllItems() {
        mAllListItemTableList.clear()
        mDisplayingItemTableList.clear()
        notifyDataSetChanged()
        notifyAdapterSizeChange()
    }

    /**
     * mark every item as complete or incomplete
     * @param complete flags for marking the data
     */
    fun markAllAsComplete(complete: Boolean) {
        for (itemTable in mAllListItemTableList) {
            itemTable.isComplete = complete
            //update the data in the database
            Utils.dataHelper.createOrUpdateListItem(itemTable)
        }
        filterData(false)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.list_item, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(listViewHolder: ListViewHolder, position: Int) {
        val itemTable = mDisplayingItemTableList[position]
        listViewHolder.textView.text = itemTable.title
        listViewHolder.cardView.setOnClickListener({
            //when the item is clicked, pass the item's id to the callback
            mAdapterCallback.onListItemClicked(itemTable.id)
        })
        listViewHolder.cardView.setOnLongClickListener({
            //when the item is long-clicked, remove the item
            removeItem(itemTable)
            Utils.dataHelper.removeListItem(itemTable.id)
            true
        })

        if (itemTable.isComplete) {
            //when the item is complete, display the check and change the background color
            listViewHolder.textView.setBackgroundResource(R.color.complete)
            listViewHolder.checkImageView.visibility = View.VISIBLE
        } else {
            //when the item is not complete, hide the check and change back the background color
            listViewHolder.textView.setBackgroundResource(R.color.transparent)
            listViewHolder.checkImageView.visibility = View.GONE
        }
    }

    override fun onViewRecycled(holder: ListViewHolder?) {
        super.onViewRecycled(holder)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    /**
     * Returns the total number of items in the data set held by the adapter.

     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return mDisplayingItemTableList.size
    }



    fun filterData(change: Boolean) {
        mDisplayingItemTableList = ArrayList<ListItemTable>()
        if (change) {
            //revert the isFilter flag
            isFiltered = !isFiltered
        }
        if (isFiltered) {
            mAllListItemTableList
                    .filterNot { it.isComplete }
                    .forEach {
                        //only add the incomplete item to the list
                        mDisplayingItemTableList.add(it)
                    }
        } else {
            //add all items to the list
            mDisplayingItemTableList.addAll(mAllListItemTableList)
        }
        notifyDataSetChanged()
    }
}
