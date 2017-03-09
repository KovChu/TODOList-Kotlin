package com.demo.kuanyi.todolist_kotlin.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

/**
 * The database table storing all the data for the TODOlist item
 * Created by kuanyi on 15/5/14.
 */

@DatabaseTable(tableName = "listItemTable")
class ListItemTable {

    //the ID of the list item, it is auto-generated, so it cannot be set
    @DatabaseField(generatedId = true)
    var id: Int = 0

    //the title or description of the item
    @DatabaseField
    lateinit var title: String

    //whether the task is complete or not
    @DatabaseField
    var isComplete: Boolean = false

    constructor(title: String, isComplete: Boolean) : this() {
        this.title = title
        this.isComplete = isComplete
    }

    constructor()
}
