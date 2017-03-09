package com.demo.kuanyi.todolist_kotlin.widget

/**
 * This interface is used for communication between the fragment and the adapter
 * Created by kuanyi on 15/5/14.
 */
interface AdapterCallback {

    /**
     * receive notification when the size of the adapter has change
     * @param size the size of the adapter after the change
     */
    fun onAdapterItemSizeChange(size: Int)

    /**
     * notify the callback that an item has been clicked
     * @param itemId the id of the clicked item
     */
    fun onListItemClicked(itemId: Int)
}
