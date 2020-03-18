package com.example.mooddiary

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntro2Fragment

class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Slide 1
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.intro_slide2_title),
                                                getString(R.string.intro_slide1_desc),
                                                R.drawable.app_icon2,
                                                resources.getColor(R.color.colorAccent)))
        //Slide 2
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.intro_slide2_title),
                                                getString(R.string.intro_slide2_title),
                                                R.drawable.book_icon,
                                                resources.getColor(R.color.colorPrimaryDark)))
        setBarColor(resources.getColor(R.color.colorPrimary))
        showSkipButton(false)
    }

    // Intro Tutorial Completion Handler
    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }
}