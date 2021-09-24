package video.report.mediaplayer.preference.delegates

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * An [Int] delegate that is backed by [SharedPreferences].
 */
private class LongPreferenceDelegate(
    private val name: String,
    private val defaultValue: Long,
    private val preferences: SharedPreferences
) : ReadWriteProperty<Any, Long> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Long =
        preferences.getLong(name, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.edit().putLong(name, value).apply()
    }

}

/**
 * Creates a [Boolean] from [SharedPreferences] with the provide arguments.
 */
fun SharedPreferences.longPreference(
    name: String,
    defaultValue: Long
): ReadWriteProperty<Any, Long> = LongPreferenceDelegate(name, defaultValue, this)
