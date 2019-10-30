package com.example.mapp

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.media.session.MediaButtonReceiver.handleIntent
import android.support.v7.app.AppCompatActivity;
import android.util.Log


import kotlinx.android.synthetic.main.activity_search_results.*

class SearchResults : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        setSupportActionBar(toolbar)
        handleIntent(intent)
        Log.d("SearchResults", "on create")


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)

    }

    private fun handleIntent(intent: Intent) {
        val query = intent.getStringExtra(SearchManager.QUERY)
        Log.d("SearchResults", query)
        //user query to search my data some how
    }


}
