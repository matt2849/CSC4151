package com.spendsages.walletwatch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.spendsages.walletwatch.databinding.ActivityMainBinding

/* This is the "main" of the program and is also the primary activity of the app.
* This will immediately load upon app launch. */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var model : SharedViewModel

    override fun onDestroy() {
        super.onDestroy()
        /* Hide the keyboard. */
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.mainPager.windowToken, 0)
    }

    override fun onStop() {
        super.onStop()
        /* Hide the keyboard. */
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.mainPager.windowToken, 0)
    }

    override fun onPause() {
        super.onPause()
        /* Hide the keyboard. */
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.mainPager.windowToken, 0)
    }

    /* Overwritten function that performs tasks immediately upon app launch. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        /* Display the activity_main.xml layout. */
        setContentView(binding.root)

        /* Setup the fragment manager, which will load the three tabs and select Tab 1. */
        val fragmentAdapter = MainPagerAdapter(supportFragmentManager)
        binding.mainPager.adapter = fragmentAdapter
        binding.mainPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {}

            /* Listener that will force the numpad to open on Tab 1
            * and force the numpad to close when not on Tab 1. */
            override fun onPageScrollStateChanged(state: Int) {
                /* Wait until the Tab scrolling animation is done. */
                if (state == ViewPager.SCROLL_STATE_IDLE)
                {
                    /* Check if the current tab is not Tab 1. */
                    if (binding.mainPager.currentItem != 0)
                    {
                        /* Hide the keyboard. */
                        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                            .hideSoftInputFromWindow(binding.mainPager.windowToken, 0)
                    }
                    /* Otherwise, open the numpad. */
                    else {
                        showKeyboard(findViewById<me.abhinay.input.CurrencyEditText>(R.id.amountField))
                    }
                }
            }
        })

        binding.mainTabs.setupWithViewPager(binding.mainPager)

        /* Function that will open the Settings activity when the user taps the Settings button. */
        findViewById<ImageButton>(R.id.openSettingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }

        /* Setup the shared view model, so that all fragments can access the same live data. */
        val viewModelFactory = Injection.provideViewModelFactory(this)
        model = ViewModelProvider(this, viewModelFactory)[SharedViewModel::class.java]
        model.open(this)
    }

    /* Purpose: Force the focus on the given UI object and
    * then force the keyboard with the correct key layout to open.
    *
    * Parameters: view represents the UI object to set the focus on to.
    *
    * Returns: Nothing. */
    fun showKeyboard(view : View) {
        /* Set the focus on the UI object. */
        if (view.requestFocus()) {
            /* Open the keyboard that has the correct layout for the given UI textbox.
            * For example, if it is a numerical textbox, such as amountField,
            * it will open the numpad. */
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}