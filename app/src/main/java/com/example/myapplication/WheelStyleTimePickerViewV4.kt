package com.example.myapplication

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateFormat
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.contrarywind.view.WheelView
import kotlinx.android.synthetic.main.view_v4_wheel_style_time_picker.view.*
import java.text.DateFormatSymbols
import java.util.*

class WheelStyleTimePickerViewV4 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var mIsAm: Boolean = false
    private var maxTime: Pair<Int, Int> = getRoundDownTime(23 to 59) // max time can reach
    private var minTime: Pair<Int, Int> = 0 to 0 // min time can reach
    private var lastTime: Pair<Int, Int> = -1 to 0 // last selected time, init with an invalid value
    private val mLocale = Locale.getDefault()

    private val minutesSize = 60 / MINUTES_STEP_INTERVAL
    private val displayedMinutes by lazy {
        mutableListOf<Int>().apply {
            for (i in 0 until minutesSize) {
                add(i * MINUTES_STEP_INTERVAL)
            }
        }
    }
    private val displayedAmPm by lazy {
        DateFormatSymbols.getInstance(mLocale).amPmStrings.toList()
    }
    private val displayedDays = listOf("Today")

    var mOnTimeChangedListener: OnTimeChangedListener? = null

    var mIsEnabled = true
        set(enabled) {
            mMinuteSpinner.isEnabled = enabled
            mHourSpinner.isEnabled = enabled
            mAmPmSpinner.isEnabled = enabled
            field = enabled
        }

    private var mIs24Hour: Boolean = false
    private var oldHourVal: Int = 0
    private var oldMinuteVal: Int = 0

    private val dayAdapter = CustomWheelAdapterV4(displayedDays)
    private val hourAdapter = CustomWheelAdapterV4(emptyList<Int>())
    private val minuteAdapter = CustomWheelAdapterV4(emptyList<Int>())
    private val amPMAdapter = CustomWheelAdapterV4(emptyList<String>())

    init {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.view_v4_wheel_style_time_picker, this, true)
        view.isSaveFromParentEnabled = false

        setWheelViewStyle(mDaySpinner)
        setWheelViewStyle(mHourSpinner)
        setWheelViewStyle(mMinuteSpinner)
        setWheelViewStyle(mAmPmSpinner)

        // hour
        mHourSpinner.setOnItemSelectedListener {
            Log.d("byq","mHourSpinner.setOnItemSelectedListener")
            var currentTime = getDate()
            if (currentTime < minTime || currentTime > maxTime) {
                // Sliding wheel may cause time out of range, so handle it.
                currentTime = if (isClockwiseMoved(lastTime, currentTime)) {
                    minTime
                } else {
                    maxTime
                }
                setCurrentHour(currentTime.first)
            }
            setCurrentMinute(currentTime.second)
            updateAmPm()
            onTimeChanged()
        }

        mHourSpinner.onWheelScrolledListener = object : CustomWheelViewV4.OnWheelScrolledListener {
            override fun onWheelScrolled(oldVal: Int, newVal: Int, dy: Float) {
                Log.d("byq", "$oldVal $newVal")
                val index = hourAdapter.indexOf(HOURS_IN_HALF_DAY)
                if (!mIs24Hour) {
                    if (dy > 0 && (index in (oldVal + 1)..newVal || newVal < oldVal && index !in (newVal + 1)..oldVal)
                        || dy < 0 && (index in (newVal + 1)..oldVal || oldVal < newVal && index !in (oldVal + 1)..newVal)) {
                        mIsAm = !mIsAm
                        updateAmPm()
                    }
                }
            }
        }

        // minute
        mMinuteSpinner.setOnItemSelectedListener {
            Log.d("byq","mMinuteSpinner.setOnItemSelectedListener")
            updateAmPm()
            onTimeChanged()
        }

        mMinuteSpinner.onWheelScrolledListener =
            object : CustomWheelViewV4.OnWheelScrolledListener {
                override fun onWheelScrolled(oldVal: Int, newVal: Int, dy: Float) {
                    if (mMinuteSpinner.isLoop) {

                    }
                }
            }

        // am/pm
        mAmPmSpinner.setOnItemSelectedListener {
            Log.d("byq","mAmPmSpinner.setOnItemSelectedListener")
            mIsAm = !mIsAm
            setCurrentMinute(getMinute())
            onTimeChanged()
        }

        if (isAmPmAtStart()) {
            // Move the am/pm view to the beginning
            timePickerLayout.removeView(mAmPmSpinner)
            timePickerLayout.addView(mAmPmSpinner, 1)
        }

        // initial state
        mDaySpinner.setCyclic(dayAdapter.itemsCount >= ITEM_VISIBLE_COUNT)
        mDaySpinner.adapter = dayAdapter

        // set to current time
        val tempCalendar = Calendar.getInstance(TimeZone.getDefault())
        setCurrentHour(tempCalendar.get(Calendar.HOUR_OF_DAY))
        setCurrentMinute(tempCalendar.get(Calendar.MINUTE))
        updateAmPm()

        if (!isEnabled) {
            mIsEnabled = false
        }
    }

    private fun setWheelViewStyle(wheelView: WheelView) {
//        val typeface = Typeface.createFromAsset(context.assets, context.getString(R.string.OPENSANS_SEMIBOLD))
//        wheelView.setTypeface(typeface)
        wheelView.setItemsVisibleCount(ITEM_VISIBLE_COUNT)
    }

    private fun isAmPmAtStart(): Boolean {
        val bestDateTimePattern = DateFormat.getBestDateTimePattern(
            mLocale,
            "hm" /* skeleton */
        )

        return bestDateTimePattern.startsWith("a")
    }

    fun initView(
        defaultTime: Pair<Int, Int>? = null,
        maxTime: Pair<Int, Int>? = null,
        minTime: Pair<Int, Int>? = null,
        is24Hour: Boolean = false
    ) {
        if (maxTime != null && maxTime < this.maxTime && maxTime >= this.minTime) {
            this.maxTime = getRoundDownTime(maxTime)
        }
        if (minTime != null && minTime <= this.maxTime && minTime > this.minTime) {
            this.minTime = getRoundUpTime(minTime)
        }
        mIs24Hour = is24Hour

        if (defaultTime == null || defaultTime <= this.minTime) {
            setDate(this.minTime.first, this.minTime.second)
        } else if (defaultTime >= this.maxTime) {
            setDate(this.maxTime.first, this.maxTime.second)
        } else {
            val time = getRoundUpTime(defaultTime)
            setDate(time.first, time.second)
        }
    }

    fun setDate(hour: Int, minute: Int) {
        if (hour to minute <= maxTime && hour to minute >= minTime) {
            setCurrentHour(hour)
            setCurrentMinute(minute)
            updateAmPm()
            onTimeChanged()
        }
    }

    fun getDate() = getHour() to getMinute()

    private fun setCurrentHour(hour: Int) {
        updateHourControl()
        var currentHour = hour
        if (!mIs24Hour) {
            // convert [0,23] ordinal to wall clock display
            if (currentHour >= HOURS_IN_HALF_DAY) {
                mIsAm = false
                if (currentHour > HOURS_IN_HALF_DAY) {
                    currentHour -= HOURS_IN_HALF_DAY
                }
            } else {
                mIsAm = true
                if (currentHour == 0) {
                    currentHour = HOURS_IN_HALF_DAY
                }
            }
        }
        mHourSpinner.currentItem = hourAdapter.indexOf(currentHour)
        Log.d("byq", "setCurrentHour $hour + ${hourAdapter.displayedValues}")
    }

    fun getHour(): Int {
        val currentHour = hourAdapter.getItem(mHourSpinner.currentItem)?:-1
        return when {
            mIs24Hour -> {
                currentHour
            }
            mIsAm -> {
                currentHour % HOURS_IN_HALF_DAY
            }
            else -> {
                currentHour % HOURS_IN_HALF_DAY + HOURS_IN_HALF_DAY
            }
        }
    }

    private fun setCurrentMinute(minute: Int) {
        updateMinuteControl()
        mMinuteSpinner.currentItem = minuteAdapter.indexOf(minute)
        Log.d("byq", "setCurrentMinute $minute + ${minuteAdapter.displayedValues}")
    }

    fun getMinute(): Int {
        return minuteAdapter.getItem(mMinuteSpinner.currentItem)?:-1
    }

    fun set24HourMode(is24Hour: Boolean) {
        if (mIs24Hour == is24Hour) {
            return
        }
        // cache the current hour since spinner range changes and BEFORE changing mIs24Hour!!
        val currentHour = getHour()
        mIs24Hour = is24Hour
        // set value after spinner range is updated
        setCurrentHour(currentHour)
        updateAmPm()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return superState?.let {
            SavedState(it, getHour(), getMinute(), mIs24Hour)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            setCurrentHour(state.hour)
            setCurrentMinute(state.minute)
            updateAmPm()
        }
    }

    private fun updateAmPm() {
        if (mIs24Hour) {
            mAmPmSpinner.visibility = View.GONE
        } else {
            updateAmPmControl()
            val displayedAmPm = if (mIsAm) displayedAmPm[0] else displayedAmPm[1]
            mAmPmSpinner.currentItem = amPMAdapter.indexOf(displayedAmPm)
            mAmPmSpinner.visibility = View.VISIBLE
            Log.d("byq", "updateAmPm $displayedAmPm + ${amPMAdapter.displayedValues}")
        }
    }

    private fun updateHourControl() {
        val newDisplayedValues: List<Int>
        if (mIs24Hour) {
            newDisplayedValues = (minTime.first..maxTime.first).toList()
        } else {
            if (maxTime.first - minTime.first > 10) {
                // range >= 12, have all number 1..12 included
                newDisplayedValues = (1..12).toList()
            } else if (minTime.first > HOURS_IN_HALF_DAY) {
                // [13, 23]
                val minValue = minTime.first - HOURS_IN_HALF_DAY
                val maxValue = maxTime.first - HOURS_IN_HALF_DAY
                newDisplayedValues = (minValue..maxValue).toList()
            } else if (minTime.first == 0) {
                // [0, n] (0 <= n < 11)
                val hoursValues = mutableListOf<Int>().apply {
                    add(12)
                    addAll(1..maxTime.first)
                }
                newDisplayedValues = hoursValues
            } else if (minTime.first == 1 || minTime.first == 2 || maxTime.first <= HOURS_IN_HALF_DAY) {
                // [1, 11] or [2, 12] or [3, 12]
                newDisplayedValues = (minTime.first..maxTime.first).toList()
            } else {
                // [m, n] (3 <= m <= 12, 12 < n < 23, range < 12)
                val hoursValues = mutableListOf<Int>().apply {
                    for (i in minTime.first..12) {
                        add(i)
                    }
                    for (i in 13..maxTime.first) {
                        add(i - HOURS_IN_HALF_DAY)
                    }
                }
                newDisplayedValues = hoursValues
            }
        }
        if (hourAdapter.itemsCount == newDisplayedValues.size && hourAdapter.getItem(0) == newDisplayedValues[0]) return
        hourAdapter.displayedValues = newDisplayedValues
        mHourSpinner.setCyclic(hourAdapter.itemsCount >= ITEM_VISIBLE_COUNT)
        mHourSpinner.adapter = hourAdapter
    }

    private fun updateMinuteControl() {
        val hour = getHour()
        val newDisplayedValues = displayedMinutes.filter {
            val time = hour to it
            time >= minTime && time <= maxTime
        }
        if (minuteAdapter.itemsCount == newDisplayedValues.size && minuteAdapter.getItem(0) == newDisplayedValues[0]) return
        minuteAdapter.displayedValues = newDisplayedValues
        mMinuteSpinner.setCyclic(minuteAdapter.itemsCount >= ITEM_VISIBLE_COUNT)
        mMinuteSpinner.adapter = minuteAdapter
    }

    private fun updateAmPmControl() {
        // check opposite time optional or not, such as current is 2:30 pm then will check 2:30 am.
        if (!mIs24Hour) {
            val newDisplayedValues: List<String>
            val currentTime = getDate()
            val time = if (mIsAm) {
                currentTime.first + HOURS_IN_HALF_DAY
            } else {
                currentTime.first - HOURS_IN_HALF_DAY
            } to currentTime.second
            newDisplayedValues = if (time >= minTime && time <= maxTime) {
                displayedAmPm
            } else if (mIsAm) {
                listOf(displayedAmPm[0])
            } else {
                listOf(displayedAmPm[1])
            }
            if (amPMAdapter.itemsCount == newDisplayedValues.size && amPMAdapter.getItem(0) == newDisplayedValues[0]) return
            amPMAdapter.displayedValues = newDisplayedValues
            mAmPmSpinner.setCyclic(amPMAdapter.itemsCount >= ITEM_VISIBLE_COUNT)
            mAmPmSpinner.adapter = amPMAdapter
        }
    }

    private fun onTimeChanged() {
        lastTime = getDate()
        Log.d("byq", "onTimeChanged ${lastTime.first}:${lastTime.second}")
        mOnTimeChangedListener?.onTimeChanged(this, lastTime.first, lastTime.second)
    }

    // define operator to compare time values(<hour,minute>)
    private operator fun Pair<Int, Int>.compareTo(param: Pair<Int, Int>): Int {
        return first * 60 + second - param.first * 60 - param.second
    }

    private fun isClockwiseMoved(from: Pair<Int, Int>, to: Pair<Int, Int>): Boolean {
        val divider = ((from.first + 24) * 60 + from.second - to.first * 60 - to.second) % 1440
        val divider2 = ((to.first + 24) * 60 + to.second - from.first * 60 - from.second) % 1440
        return divider > divider2
    }

    private fun getRoundDownTime(param: Pair<Int, Int>): Pair<Int, Int> {
        return param.first to param.second / MINUTES_STEP_INTERVAL * MINUTES_STEP_INTERVAL
    }

    private fun getRoundUpTime(param: Pair<Int, Int>): Pair<Int, Int> {
        var hour = param.first
        var minute = param.second / MINUTES_STEP_INTERVAL * MINUTES_STEP_INTERVAL
        if (param.second % MINUTES_STEP_INTERVAL > 0) {
            minute += MINUTES_STEP_INTERVAL
        }
        if (minute >= 60) {
            hour++
            minute = 0
        }
        return hour to minute
    }

    companion object {
        private const val HOURS_IN_HALF_DAY = 12
        private const val MINUTES_STEP_INTERVAL = 30 // 1..60
        private const val ITEM_VISIBLE_COUNT = 7
    }

    interface OnTimeChangedListener {
        fun onTimeChanged(view: View, hourOfDay: Int, minute: Int)
    }

    class SavedState : BaseSavedState {
        val hour: Int
        val minute: Int
        val is24HourMode: Boolean

        constructor(
            superState: Parcelable, hour: Int, minute: Int, is24HourMode: Boolean
        ) : super(superState) {
            this.hour = hour
            this.minute = minute
            this.is24HourMode = is24HourMode
        }

        private constructor(parcel: Parcel) : super(parcel) {
            hour = parcel.readInt()
            minute = parcel.readInt()
            is24HourMode = parcel.readInt() == 1
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(hour)
            dest.writeInt(minute)
            dest.writeInt(if (is24HourMode) 1 else 0)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}