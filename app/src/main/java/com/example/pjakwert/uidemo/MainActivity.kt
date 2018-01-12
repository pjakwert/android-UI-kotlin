package com.example.pjakwert.uidemo

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    lateinit var mDrawerLayout : DrawerLayout
    //lateinit var mToolbar : Toolbar
    lateinit var mToggle : ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* --- if you want the toolbar on top of everything ---
        mToolbar = findViewById<Toolbar>(R.id.myActionBar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.mipmap.ic_launcher)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        */

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.openDrawer, R.string.closeDrawer)

        mDrawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()

        findViewById<NavigationView>(R.id.navigationLayout)?.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, MainFragment() as Fragment).commit()
        }
    }

    fun hideKeyboard() {
        val imManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imManager.hideSoftInputFromWindow( currentFocus.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        hideKeyboard()
        if (mToggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Snackbar.make(mDrawerLayout, item.title, Snackbar.LENGTH_SHORT).show()

        var fragment : Fragment?

        fragment = when (item.itemId) {
            R.id.itemHome -> MainFragment()
            R.id.itemRecycler -> RecyclerViewFragment()
            R.id.itemLogin -> LoginFragment()
            else -> return true // for unknown item
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
        mDrawerLayout.closeDrawer(GravityCompat.START)

        return true
    }
}


