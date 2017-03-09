package com.demo.kuanyi.todolist_kotlin

import android.content.Context
import com.demo.kuanyi.todolist_kotlin.model.DataHelper

/**
 * The Utils class that holds the reference to a singleton DataHelper class
 * for easy retrieve for classes across the application.
 * Created by kuanyi on 15/5/14.
 */
object Utils {

    //we need DataHelper to be static to avoid multiple instances of the DB,
    //which will cause the data to be corrupted.
    lateinit var dataHelper: DataHelper

    fun initDataHelper(context: Context) {
        dataHelper = DataHelper(context)
    }

}
