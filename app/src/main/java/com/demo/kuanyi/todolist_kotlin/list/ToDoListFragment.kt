package com.demo.kuanyi.todolist_kotlin.list

import android.app.AlertDialog
import android.app.FragmentTransaction
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    override fun onListItemClicked(item: ListItemTable) {

        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("TODO_DETAIL")
                .replace(R.id.content, ToDoDetailFragment(item), "TODO_DETAIL")
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

        val dialog = alertDialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        input.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = input.length() > 0

            }
        })
    }

    companion object {
        private val LOAD_LIST_DATA_COMPLETE = 0
    }
}
