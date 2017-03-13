package com.demo.kuanyi.todolist_kotlin.detail

import android.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demo.kuanyi.todolist_kotlin.R
import com.demo.kuanyi.todolist_kotlin.Utils
import com.demo.kuanyi.todolist_kotlin.model.DetailItemTable
import com.demo.kuanyi.todolist_kotlin.widget.AdapterCallback
import com.demo.kuanyi.todolist_kotlin.widget.ListViewHolder
import java.util.*

/**
 * A list adapter that takes in a list of ListItemTable and display it.
 * The adapter also holds the data, and does the modifications on the data
 * Created by kuanyi on 15/5/14.
 */
class DetailAdapter(fragment: Fragment) : RecyclerView.Adapter<ListViewHolder>() {

    private var mAllListItemTableList: ArrayList<DetailItemTable> = ArrayList()

    //the list of the current display list, this will be the same with
    //mAllListItemTableList when the filter is not applied
    private var mDisplayingItemTableList: ArrayList<DetailItemTable> = ArrayList()

    //the callback for communicating with fragment
    private var mAdapterCallback: AdapterCallback

    //whether the list is filtered or not
    private var isFiltered = false


    init {
        try {
            this.mAdapterCallback = fragment as AdapterCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("Fragment must implement AdapterCallback.")
        }
    }

    fun setItems(itemList : ArrayList<DetailItemTable>) {
        mAllListItemTableList = itemList
        mDisplayingItemTableList = ArrayList<DetailItemTable>()
        mDisplayingItemTableList.addAll(itemList)
    }

    /**
     * adding a new list item to the list
     * @param detailItemTable the item to be added
     */
    fun addNewListItem(detailItemTable: DetailItemTable) {
        val position = mAllListItemTableList.size
        mAllListItemTableList.add(detailItemTable)
        mDisplayingItemTableList.add(detailItemTable)
        //notify the fragment that the size has been changed
        notifyAdapterSizeChange()
        notifyItemInserted(position)
    }

    //notify the fragment that the size of the adapter has changed
    private fun notifyAdapterSizeChange() {
        mAdapterCallback.onAdapterItemSizeChange(mDisplayingItemTableList.size)
    }

    // remove an item from the list
    private fun removeItem(itemTable: DetailItemTable) {
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

    // notify the item has been changed
    private fun changeItem(itemTable: DetailItemTable) {
        val position = mDisplayingItemTableList.indexOf(itemTable)
        notifyItemChanged(position)
    }

    /**
     * mark every item as complete or incomplete
     * @param complete flags for marking the data
     */
    fun markAllAsComplete(complete: Boolean) {
        for (itemTable in mAllListItemTableList) {
            itemTable.isComplete = complete
            //update the data in the database
            Utils.dataHelper.createOrUpdateDetailItem(itemTable)
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
            //when the item is clicked, revert its complete state.
            itemTable.isComplete = !itemTable.isComplete
            Utils.dataHelper.createOrUpdateDetailItem(itemTable)
            changeItem(itemTable)
        })
        listViewHolder.cardView.setOnLongClickListener({
            //when the item is long-clicked, remove the item
            removeItem(itemTable)
            Utils.dataHelper.removeListItem(itemTable.id)
            true
        })
        listViewHolder.checkImageView.visibility = View.VISIBLE
        listViewHolder.sizeText.visibility = View.GONE
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

    /**
     * Get the row id associated with the specified position in the list.

     * @param position The position of the item within the adapter's data set whose row id we want.
     * *
     * @return The id of the item at the specified position.
     */
    override fun getItemId(position: Int): Long {
        return mDisplayingItemTableList[position].id.toLong()
    }

    override fun getItemCount(): Int {
        return mDisplayingItemTableList.size
    }

    fun filterData(change: Boolean) {
        mDisplayingItemTableList = ArrayList<DetailItemTable>()
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
            mDisplayingItemTableList
                    .addAll(mAllListItemTableList)
        }
        notifyDataSetChanged()
    }
}
