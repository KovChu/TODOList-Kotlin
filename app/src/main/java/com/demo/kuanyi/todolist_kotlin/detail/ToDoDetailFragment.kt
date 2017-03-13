package com.demo.kuanyi.todolist_kotlin.detail

import android.app.AlertDialog
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import com.demo.kuanyi.todolist_kotlin.AbstractToDoFragment
import com.demo.kuanyi.todolist_kotlin.R
import com.demo.kuanyi.todolist_kotlin.Utils
import com.demo.kuanyi.todolist_kotlin.model.DetailItemTable
import com.demo.kuanyi.todolist_kotlin.model.ListItemTable
import com.demo.kuanyi.todolist_kotlin.widget.AdapterCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*

/**
 * The Detail List Fragment display the items of each todo list
 * Created by kuanyi on 2016/5/6.
 */
class ToDoDetailFragment(listItem : ListItemTable) : AbstractToDoFragment(), AdapterCallback {


    lateinit private var mItemAdapter: DetailAdapter

    private var isFilter = false

    private var mListItem = listItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                     savedInstanceState: Bundle?): View {
        val parentView = inflater.inflate(R.layout.fragment_list, container, false)
        return parentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mItemAdapter = DetailAdapter(this)
        recyclerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerview.adapter = mItemAdapter
        val runnable = Runnable {
            var existingList: List<DetailItemTable>? = Utils.dataHelper.queryForAllDetailItems(mListItem.id)
            if (existingList == null) {
                existingList = ArrayList<DetailItemTable>()
            }
            val message = Message()
            message.what = LOAD_LIST_DATA_COMPLETE
            message.obj = existingList
            mHandler.dispatchMessage(message)
        }
        activity.toolbar.title = mListItem.title
        Thread(runnable).start()
    }


    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == LOAD_LIST_DATA_COMPLETE) {
            val listItemTables = msg.obj as ArrayList<DetailItemTable>
            onAdapterItemSizeChange(listItemTables.size)
            mItemAdapter.setItems(listItemTables)
        }
        return false
    }

    //create the action bar items
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_mark_all_as_complete -> {
                //notify the adapter to mark all data as complete
                mItemAdapter.markAllAsComplete(true)
                return true
            }
            R.id.action_mark_all_as_incomplete -> {
                //notify the adapter to mark all data as incomplete
                mItemAdapter.markAllAsComplete(false)
                return true
            }
            R.id.action_remove_all -> {
                //notify the adapter to delete all items
                mItemAdapter.removeAllItems()
                //also clear all items in the database
                Utils.dataHelper.clearAllItem()
                displayHint()
                return true
            }
            R.id.action_filter -> {
                //change the icon by whether the filter is apply or not
                isFilter = !isFilter
                if (!isFilter) {
                    item.setIcon(R.drawable.uncheck)
                } else {
                    item.setIcon(R.drawable.check)
                }
                //notify the adapter to filter the data
                mItemAdapter.filterData(true)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAdapterItemSizeChange(size: Int) {
        if (size == 0) {
            displayHint()
        } else {
            dismissHint()
        }
    }
    /**
     * called when FAB has been clicked
     */
    override fun onFabClicked() {
        val alertDialog = AlertDialog.Builder(getActivity())
        alertDialog.setTitle(getString(R.string.add_new_item_title))
        alertDialog.setMessage(getString(R.string.add_new_item_description))
        val input = EditText(activity)
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        alertDialog.setView(input)
        alertDialog.setPositiveButton("OK") { dialogInterface, i ->
            //create a new list item and display
            val newDetailItemTable = DetailItemTable(input.text.toString(), mListItem.id)

            newDetailItemTable.title = input.text.toString()
            Utils.dataHelper.createOrUpdateDetailItem(newDetailItemTable)
        }
        alertDialog.show()
    }
    override fun onListItemClicked(item: ListItemTable) {
        //do not need to implement
    }

    // we need to display hint as a delay due to the remove item animation
    private fun displayHint() {
        val runnable = Runnable {
            emptyHintText.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
        mHandler.postDelayed(runnable, 500)
    }

    private fun dismissHint() {
        emptyHintText.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE
    }

    companion object {

        private val LOAD_LIST_DATA_COMPLETE = 0
    }
}
