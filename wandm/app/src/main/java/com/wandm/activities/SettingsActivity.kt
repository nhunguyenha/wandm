package com.wandm.activities

import android.app.WallpaperManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.Toolbar
import android.view.WindowManager
import com.ms_square.etsyblur.BlurringView
import com.wandm.R
import com.wandm.utils.PreferencesUtils
import com.wandm.utils.PreferencesUtils.PREFS_RATE
import com.wandm.utils.PreferencesUtils.PREFS_REPORT
import com.wandm.utils.PreferencesUtils.PREFS_TEXT_SIZE
import com.wandm.utils.PreferencesUtils.PREFS_THEME
import com.wandm.utils.PreferencesUtils.PREFS_VERSION
import com.wandm.utils.Utils
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : PreferenceActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private val TAG = "SettingsActivity"

        private var delegate: AppCompatDelegate? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getDelegate()?.installViewFactory()
        getDelegate()?.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbarSettings)
        addPreferencesFromResource(R.xml.preferences)
        setupWindows()
        blurringView.blurConfig(Utils.getBlurViewConfig())
        setupViews()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        getDelegate()?.onPostCreate(savedInstanceState);
    }


    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        getDelegate()?.setContentView(layoutResID);
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        setBlurBackground(background, blurringView)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onPostResume() {
        super.onPostResume()
        getDelegate()?.onPostResume()
    }

    override fun onStop() {
        super.onStop()
        getDelegate()?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        getDelegate()?.onDestroy()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        when (p1) {
            PREFS_THEME -> {
                if (PreferencesUtils.getLightTheme()) {
                    findPreference(PREFS_THEME).summary = resources.getString(R.string.light_theme)
                    Utils.applyLightTheme(this)
                } else {
                    findPreference(PREFS_THEME).summary = resources.getString(R.string.dark_theme)
                    Utils.applyLightTheme(this)
                }

                recreate()
            }

            PREFS_TEXT_SIZE -> {
            }
        }
    }


    private fun setSupportActionBar(toolbar: Toolbar) {
        getDelegate()?.setSupportActionBar(toolbar)
    }

    private fun getDelegate(): AppCompatDelegate? {
        if (delegate == null) {
            delegate = AppCompatDelegate.create(this, null)
        }
        return delegate
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun setupViews() {
        val isLightTheme = PreferencesUtils.getLightTheme()
        if (isLightTheme) {
            findPreference(PREFS_THEME).summary = resources.getString(R.string.light_theme)
            findPreference(PREFS_RATE).icon = resources.getDrawable(R.drawable.ic_rate_light)
            findPreference(PREFS_REPORT).icon = resources.getDrawable(R.drawable.ic_report_light)
            findPreference(PREFS_VERSION).icon = resources.getDrawable(R.drawable.ic_info_light)
            findPreference(PREFS_THEME).icon = resources.getDrawable(R.drawable.ic_theme_light)
            findPreference(PREFS_TEXT_SIZE).icon = resources.getDrawable(R.drawable.ic_text_size_light)
        } else {
            findPreference(PREFS_THEME).summary = resources.getString(R.string.dark_theme)
            findPreference(PREFS_RATE).icon = resources.getDrawable(R.drawable.ic_rate_dark)
            findPreference(PREFS_REPORT).icon = resources.getDrawable(R.drawable.ic_report_dark)
            findPreference(PREFS_VERSION).icon = resources.getDrawable(R.drawable.ic_info_dark)
            findPreference(PREFS_THEME).icon = resources.getDrawable(R.drawable.ic_theme_dark)
            findPreference(PREFS_TEXT_SIZE).icon = resources.getDrawable(R.drawable.ic_text_size_dark)
        }
    }


    /**
     * Set blur background for this Activity
     *
     * @param imageView
     * @param blurringView
     */
    protected fun setBlurBackground(imageView: AppCompatImageView, blurringView: BlurringView) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        val wallpaperDrawable = wallpaperManager.drawable
        imageView.background = wallpaperDrawable
        blurringView.blurredView(imageView)
    }

    private fun setupWindows() {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}