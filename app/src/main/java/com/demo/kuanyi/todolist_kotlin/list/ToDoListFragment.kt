package com.demo.kuanyi.todolist_kotlin.list

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
import com.demo.kuanyi.todolist_kotlin.detail.ToDoDetailFragment
import com.demo.kuanyi.todolist_kotlin.model.ListItemTable
import com.demo.kuanyi.todolist_kotlin.widget.AdapterCallback
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class ToDoListFragment : AbstractToDoFragment(), AdapterCallback {

    lateinit private var mListAdapter: ListAdapter

    private var isFilter = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val parentView = inflater.inflate(R.layout.fragment_list, container, false)
        return parentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mListAdapter = ListAdapter(this)
        recyclerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerview.adapter = mListAdapter
        val runnable = Runnable {
            var existingList: List<ListItemTable>? = Utils.dataHelper.queryForAllListItems()
            if (existingList == null) {
                existingList = ArrayList<ListItemTable>()
            }
            val message = Message()
            message.what = LOAD_LIST_DATA_COMPLETE
            message.obj = existingList
            mHandler.dispatchMessage(message)
        }
        Thread(runnable).start()
    }

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
                mListAdapter.markAllAsComplete(true)
                return true
            }
            R.id.action_mark_all_as_incomplete -> {
                //notify the adapter to mark all data as incomplete
                mListAdapter.markAllAsComplete(false)
                return true
            }
            R.id.action_remove_all -> {

                val removeAllAlertDialog = AlertDialog.Builder(activity)
                removeAllAlertDialog.setTitle(getString(R.string.add_new_list_title))
                removeAllAlertDialog.setMessage(getString(R.string.add_new_list_description))
                removeAllAlertDialog.setPositiveButton("OK") { dialogInterface, i ->
                    //clear all the entries
                    //notify the adapter to delete all items
                    mListAdapter.removeAllItems()
                    //also clear all items in the database
                    Utils.dataHelper.clearAllItem()
                }
                removeAllAlertDialog.setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                removeAllAlertDialog.show()
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
                mListAdapter.filterData(true)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == LOAD_LIST_DATA_COMPLETE) {
            val listItemTables = msg.obj as ArrayList<ListItemTable>
            onAdapterItemSizeChange(listItemTables.size)
            mListAdapter.setItems(listItemTables)
        }
        return false
    }

    override fun onAdapterItemSizeChange(size: Int) {
        if (size == 0) {
            displayHint()
        } else {
            dismissHint()
        }
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
    /**
     * notify the callback that an item has been clicked
     * @param itemId the id of the clicked item
     */
    override fun onListItemClicked(itemId: Int) {

        fragmentManager.beginTransaction()
                .add(ToDoDetailFragment(itemId), "FRAGMENT_DETAIL")
                .commit()
    }

    /**
     * called when FAB has been clicked
     */
    override fun onFabClicked() {
        //create an alert dialog for user to input the text for the item
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setTitle(getString(R.string.add_new_list_title))
        alertDialog.setMessage(getString(R.string.add_new_list_description))
        val input = EditText(activity)
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        alertDialog.setView(input)
        alertDialog.setPositiveButton("OK") { dialogInterface, i ->
            //create a new list item and display
            val newListItemTable = ListItemTable()
            newListItemTable.title = input.text.toString()
            Utils.dataHelper.createOrUpdateListItem(newListItemTable)
            mListAdapter.addNewListItem(newListItemTable)
        }
        alertDialog.show()
    }

    companion object {
        private val LOAD_LIST_DATA_COMPLETE = 0
    }
}
