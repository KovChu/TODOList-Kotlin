package com.demo.kuanyi.todolist_kotlin.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

import java.sql.SQLException

/**
 * The custom OrmLiteSqliteOpenHelper to initiate the database and holds reference
 * to the daos for actions.
 * Created by kuanyi on 15/5/14.
 */
class DBHelper(context: Context) : OrmLiteSqliteOpenHelper(context, DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION) {
    internal var listItemTable: Dao<ListItemTable, Int> = getDao<Dao<ListItemTable, Int>, ListItemTable>(ListItemTable::class.java)

    internal var detailItemTable: Dao<DetailItemTable, Int> = getDao<Dao<DetailItemTable, Int>, DetailItemTable>(DetailItemTable::class.java)

    /**
     * What to do when your database needs to be created. Usually this entails creating the tables and loading any
     * initial data.
     *
     * **NOTE:** You should use the connectionSource argument that is passed into this method call or the one
     * returned by getConnectionSource(). If you use your own, a recursive call or other unexpected results may result.
     *

     * @param database         Database being created.
     * *
     * @param connectionSource
     */
    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        try {
            TableUtils.createTable(connectionSource, ListItemTable::class.java)
            TableUtils.createTable(connectionSource, DetailItemTable::class.java)
        } catch (e: SQLException) {
            Log.e(DBHelper::class.java.name, "Create Fail", e)
            e.printStackTrace()
        }

    }

    /**
     * What to do when your database needs to be updated. This could mean careful migration of old data to new data.
     * Maybe adding or deleting database columns, etc..
     *
     *
     *
     *
     * **NOTE:** You should use the connectionSource argument that is passed into this method call or the one
     * returned by getConnectionSource(). If you use your own, a recursive call or other unexpected results may result.
     *

     * @param database         Database being upgraded.
     * *
     * @param connectionSource To use get connections to the database to be updated.
     * *
     * @param oldVersion       The version of the current database so we can know what to do to the database.
     * *
     * @param newVersion
     */
    override fun onUpgrade(database: SQLiteDatabase,
                           connectionSource: ConnectionSource,
                           oldVersion: Int, newVersion: Int) {

    }


    override fun close() {
        super.close()
    }

    fun clearListItemTable() {
        try {
            TableUtils.clearTable(getConnectionSource(), ListItemTable::class.java)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    fun clearDetailItemTable() {
        try {
            TableUtils.clearTable(getConnectionSource(), DetailItemTable::class.java)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

    companion object {
        private val DATABASE_NAME = "todolist.db"
        private val DATABASE_VERSION = 1
    }
}
