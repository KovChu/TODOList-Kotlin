package com.demo.kuanyi.todolist_kotlin.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

/**
 * The class for detail item for DataBase
 * Created by kuanyi on 2016/5/6.
 */
@DatabaseTable(tableName = "detailItemTable")
class DetailItemTable {

    //the ID of the list item, it is auto-generated, so it cannot be set
    @DatabaseField(generatedId = true)
    var id: Int = 0


    //the title or description of the item
    @DatabaseField
    lateinit var title: String

    //the id of the list that the item belong to
    @DatabaseField
    var listId: Int = 0

    //whether the task is complete or not
    @DatabaseField
    var isComplete: Boolean = false

    constructor(title: String, listId: Int, isComplete: Boolean = false) : this() {
        this.title = title
        this.listId = listId
        this.isComplete = isComplete
    }

    constructor()
}
