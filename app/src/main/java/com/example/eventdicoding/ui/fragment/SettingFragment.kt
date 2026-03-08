package com.example.eventdicoding.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.eventdicoding.R
import com.example.eventdicoding.data.local.SettingPreferences
import com.example.eventdicoding.data.local.dataStore
import com.example.eventdicoding.vmodel.SettingViewModel
import com.example.eventdicoding.vmodel.SettingViewModelFactory
import com.example.eventdicoding.worker.DailyReminderWorker
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private lateinit var workManager: WorkManager
    private val periodicWorkName = "daily_reminder_event"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = view.findViewById<SwitchMaterial>(R.id.switchDarkMode)
        val switchReminder = view.findViewById<SwitchMaterial>(R.id.switchDailyReminder)

        val pref = SettingPreferences.getInstance(requireActivity().application.dataStore)
        val factory = SettingViewModelFactory(pref)
        val settingViewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            switchTheme.isChecked = isDarkModeActive
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        workManager = WorkManager.getInstance(requireContext())

        val prefs = requireActivity().getSharedPreferences("ReminderPref", 0)
        switchReminder.isChecked = prefs.getBoolean("isReminderActive", false)

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("isReminderActive", isChecked).apply()

            if (isChecked) {
                startPeriodicTask()
            } else {
                cancelPeriodicTask()
            }
        }
    }

    private fun startPeriodicTask() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            DailyReminderWorker::class.java,
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            periodicWorkName,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    private fun cancelPeriodicTask() {
        workManager.cancelUniqueWork(periodicWorkName)
    }
}