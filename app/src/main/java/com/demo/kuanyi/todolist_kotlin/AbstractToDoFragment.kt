package com.demo.kuanyi.todolist_kotlin

import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import com.demo.kuanyi.todolist_kotlin.widget.OnFabClickCallback

/**
 * The abstract class that contains the share methods of communications and actions
 * for the fragments that extends it
 * Created by kuanyi on 15/5/14.
 */
abstract class AbstractToDoFragment : Fragment(), Handler.Callback, OnFabClickCallback {

    lateinit var mHandler: Handler

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //create a Handler runs on UI thread for making changes with UI
        mHandler = Handler(this)
    }

}
