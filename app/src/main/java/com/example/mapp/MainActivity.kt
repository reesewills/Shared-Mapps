package com.example.mapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //toolbar setup
        setSupportActionBar(findViewById(R.id.main_toolbar))

        supportActionBar?.title = "Discover"



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.discover_menu,menu)

        //associate searchable config with the searchview
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.app_bar_search)?.actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo((componentName)))
        }


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        if (item?.itemId == R.id.app_bar_search) {
            //do something to display search bar in the UI

            //maybe do something to hide the search bar if it's already there

            return true
        }


        return super.onOptionsItemSelected(item)
    }


    }
