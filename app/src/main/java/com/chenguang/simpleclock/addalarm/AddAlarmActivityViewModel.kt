package com.chenguang.simpleclock.addalarm

import android.content.Context
import android.database.Cursor
import android.media.RingtoneManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.database.AlarmDao
import com.chenguang.simpleclock.model.AlarmData
import com.chenguang.simpleclock.model.AlarmSound
import com.chenguang.simpleclock.model.toAlarmEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [ViewModel] for [AddAlarmActivity] to provide alarm sound and other info
 */
class AddAlarmActivityViewModel @Inject constructor(
    private val alarmDao: AlarmDao
) : ViewModel() {

    suspend fun fetchAllAlarmSounds(context: Context): List<AlarmSound> {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            val alarmSoundList = mutableListOf<AlarmSound>()

            var alarmCursor: Cursor? = null
            try {
                val ringtoneManager = RingtoneManager(context)
                ringtoneManager.setType(RingtoneManager.TYPE_ALARM)
                alarmCursor = ringtoneManager.cursor
                while (!alarmCursor.isAfterLast && alarmCursor.moveToNext()) {
                    val position = alarmCursor.position
                    val uri = ringtoneManager.getRingtoneUri(position)
                    val ringtone = ringtoneManager.getRingtone(position)
                    alarmSoundList.add(
                        AlarmSound(
                            ringtone.getTitle(
                                context
                            ), uri
                        )
                    )
                }

                alarmSoundList.sortWith(compareBy(AlarmSound::name))
                // Add first item as no sound selection
                val noSoundOption = AlarmSound(
                    name = context.getString(R.string.no_sound_selection_text),
                    uri = null
                )
                alarmSoundList.add(0, noSoundOption)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                alarmCursor?.close()
            }

            alarmSoundList
        }
    }

    suspend fun insertAlarm(alarmData: AlarmData) {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            alarmDao.insertAlarms(alarmData.toAlarmEntity())
        }
    }
}
