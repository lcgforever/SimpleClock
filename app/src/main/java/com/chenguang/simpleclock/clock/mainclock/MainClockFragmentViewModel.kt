package com.chenguang.simpleclock.clock.mainclock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chenguang.simpleclock.database.AlarmDao
import com.chenguang.simpleclock.database.toAlarmData
import com.chenguang.simpleclock.model.AlarmData
import com.chenguang.simpleclock.model.toAlarmEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [ViewModel] for [MainClockFragment] to provide alarm information
 */
class MainClockFragmentViewModel @Inject constructor(
    private val alarmDao: AlarmDao
) : ViewModel() {

    suspend fun loadAllAlarms(): List<AlarmData> {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            val currentTime = System.currentTimeMillis()
            val allAlarms = alarmDao.getAllAlarms().toMutableSet()
            val expiredAlarms =
                allAlarms.filter { it.timeMillis <= currentTime && it.repeatDays.isEmpty() }
            // First delete all expired alarms from database
            expiredAlarms.forEach {
                alarmDao.deleteAlarmById(it.id)
            }
            // Then return non expired alarms
            allAlarms.removeAll(expiredAlarms)
            allAlarms.map { it.toAlarmData() }.sortedByDescending { it.createTimestamp }
        }
    }

    fun deleteAlarmById(alarmId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.deleteAlarmById(alarmId)
        }
    }

    fun insertAlarm(alarmData: AlarmData) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.insertAlarms(alarmData.toAlarmEntity())
        }
    }

    fun updateAlarmEnableStatus(alarmId: Int, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.updateAlarmEnableStatus(alarmId, enabled)
        }
    }
}
