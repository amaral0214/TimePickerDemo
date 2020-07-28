package com.example.myapplication.widgets

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.NumberPicker
import com.example.myapplication.R
import kotlinx.android.synthetic.main.view_v4_cico_time_picker.view.*
import java.util.*

class CICOTimePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

//    private var mHourSpinnerInput: EditText
//    private var mMinuteSpinnerInput: EditText
//    private var mAmPmSpinnerInput: EditText

    private val mAmPmStrings: Array<String>

    private val mTempCalendar: Calendar

    private var mIsAm: Boolean = false

    private val mLocale = context.getResources().getConfiguration().locale

    var mOnTimeChangedListener: OnTimeChangedListener? = null

    var mIsEnabled = true
        set(enabled) {
            mMinuteSpinner.isEnabled = enabled
            mHourSpinner.isEnabled = enabled
            mAmPmSpinner.isEnabled = enabled
            field = enabled
        }

    var mIs24Hour: Boolean = false
        set(is24Hour) {
            if (field == is24Hour) {
                return
            }
            // cache the current hour since spinner range changes and BEFORE changing mIs24Hour!!
            val currentHour = getHour()
            field = is24Hour
            updateHourControl()
            // set value after spinner range is updated
            setCurrentHour(currentHour, false)
//            updateMinuteControl()
            updateAmPmControl()
        }

    init {
        val view =
            LayoutInflater.from(context).inflate(R.layout.view_v4_cico_time_picker, this, true)
//        mHourSpinnerInput = mHourSpinner.findViewById(android.R.id.numberpicker_input)
//        mMinuteSpinnerInput = mMinuteSpinner.findViewById(android.R.id.numberpicker_input)
//        mAmPmSpinnerInput = mAmPmSpinner.findViewById(android.R.id.numberpicker_input)
        view.isSaveFromParentEnabled = false

        // hour
        mHourSpinner.setOnValueChangedListener { spinner, oldVal, newVal ->
            //            updateInputState()
            if (!mIs24Hour) {
                if (oldVal == HOURS_IN_HALF_DAY - 1 && newVal == HOURS_IN_HALF_DAY || oldVal == HOURS_IN_HALF_DAY && newVal == HOURS_IN_HALF_DAY - 1) {
                    mIsAm = !mIsAm
                    updateAmPmControl()
                }
            }
            onTimeChanged()
        }

        // minute
        mMinuteSpinner.minValue = 0
        mMinuteSpinner.maxValue = 59
        mMinuteSpinner.setOnLongPressUpdateInterval(100)
        mMinuteSpinner.setFormatter(TwoDigitFormatter())
        mMinuteSpinner.setOnValueChangedListener { spinner, oldVal, newVal ->
            //            updateInputState()
            if (mMinuteSpinner.displayedValues.size > 2) {
                val minValue = mMinuteSpinner.minValue
                val maxValue = mMinuteSpinner.maxValue
                if (oldVal == maxValue && newVal == minValue) {
                    val newHour = mHourSpinner.value + 1
                    if (!mIs24Hour && newHour == HOURS_IN_HALF_DAY) {
                        mIsAm = !mIsAm
                        updateAmPmControl()
                    }
                    mHourSpinner.value = newHour
                } else if (oldVal == minValue && newVal == maxValue) {
                    val newHour = mHourSpinner.value - 1
                    if (!mIs24Hour && newHour == HOURS_IN_HALF_DAY - 1) {
                        mIsAm = !mIsAm
                        updateAmPmControl()
                    }
                    mHourSpinner.value = newHour
                }
            }
            onTimeChanged()
        }

        // Get the localized am/pm strings and use them in the spinner.
        mAmPmStrings = getAmPmStrings(context)

        // am/pm
        mAmPmSpinner.minValue = 0
        mAmPmSpinner.maxValue = 1
        mAmPmSpinner.displayedValues = mAmPmStrings
        mAmPmSpinner.setOnValueChangedListener { picker, oldVal, newVal ->
            //                updateInputState()
            picker.requestFocus()
            mIsAm = !mIsAm
            updateAmPmControl()
            onTimeChanged()
        }

        if (isAmPmAtStart()) {
            // Move the am/pm view to the beginning
            timePickerLayout.removeView(mAmPmSpinner)
            timePickerLayout.addView(mAmPmSpinner, 0)
        }

        // update controls to initial state
        updateHourControl()
        updateMinuteControl()
        updateAmPmControl()

        // set to current time
        mTempCalendar = Calendar.getInstance(TimeZone.getDefault())
        setHour(mTempCalendar.get(Calendar.HOUR_OF_DAY))
        setMinute(mTempCalendar.get(Calendar.MINUTE))

        if (!isEnabled) {
            isEnabled = false
        }
    }

    private fun isAmPmAtStart(): Boolean {
        val bestDateTimePattern = DateFormat.getBestDateTimePattern(
            mLocale,
            "hm" /* skeleton */
        )

        return bestDateTimePattern.startsWith("a")
    }

    fun setDate(hour: Int, minute: Int) {
        setCurrentHour(hour, false)
        setCurrentMinute(minute, false)

        onTimeChanged()
    }

    fun setHour(hour: Int) {
        setCurrentHour(hour, true)
    }

    private fun setCurrentHour(hour: Int, notifyTimeChanged: Boolean) {
        var currentHour = hour
        // why was Integer used in the first place?
        if (currentHour == getHour()) {
            return
        }
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
            updateAmPmControl()
        }
        mHourSpinner.value = currentHour
        if (notifyTimeChanged) {
            onTimeChanged()
        }
    }

    fun getHour(): Int {
        val currentHour = mHourSpinner.value
        return if (mIs24Hour) {
            currentHour
        } else if (mIsAm) {
            currentHour % HOURS_IN_HALF_DAY
        } else {
            currentHour % HOURS_IN_HALF_DAY + HOURS_IN_HALF_DAY
        }
    }

    fun setMinute(minute: Int) {
        setCurrentMinute(minute, true)
    }

    private fun setCurrentMinute(minute: Int, notifyTimeChanged: Boolean) {
        if (minute == getMinute()) {
            return
        }
        mMinuteSpinner.value = minute
        if (notifyTimeChanged) {
            onTimeChanged()
        }
    }

    fun getMinute(): Int {
        return mMinuteSpinner.value * MINUTES_STEP_INTERVAL
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
            mIs24Hour = state.is24HourMode
            setHour(state.hour)
            setMinute(state.minute)
        }
    }

//    private fun updateInputState() {
//        // Make sure that if the user changes the value and the IME is active
//        // for one of the inputs if this widget, the IME is closed. If the user
//        // changed the value via the IME and there is a next input the IME will
//        // be shown, otherwise the user chose another means of changing the
//        // value and having the IME up makes no sense.
//
//        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//
//        if (inputMethodManager != null) {
//            if (inputMethodManager.isActive(mHourSpinnerInput)) {
//                mHourSpinnerInput.clearFocus()
//                inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
//            } else if (inputMethodManager.isActive(mMinuteSpinnerInput)) {
//                mMinuteSpinnerInput.clearFocus()
//                inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
//            } else if (inputMethodManager.isActive(mAmPmSpinnerInput)) {
//                mAmPmSpinnerInput.clearFocus()
//                inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
//            }
//        }
//    }

    private fun updateAmPmControl() {
        if (mIs24Hour) {
            mAmPmSpinner.visibility = View.GONE
        } else {
            val index = if (mIsAm) Calendar.AM else Calendar.PM
            mAmPmSpinner.value = index
            mAmPmSpinner.visibility = View.VISIBLE
        }
    }

    private fun updateHourControl() {
        if (mIs24Hour) {
            mHourSpinner.minValue = 0
            mHourSpinner.maxValue = 23
        } else {
            mHourSpinner.minValue = 1
            mHourSpinner.maxValue = 12
        }
    }

    private fun updateMinuteControl() {
        val minutesSize = 60 / MINUTES_STEP_INTERVAL
        mMinuteSpinner.minValue = 0
        mMinuteSpinner.maxValue = minutesSize - 1
        val minutesValues = arrayOfNulls<String>(minutesSize)
        for (i in 0 until minutesSize) {
            minutesValues[i] = (i * MINUTES_STEP_INTERVAL).toString()
        }
        mMinuteSpinner.displayedValues = minutesValues

//        if (mIs24Hour) {
//            mMinuteSpinnerInput.imeOptions = EditorInfo.IME_ACTION_DONE
//        } else {
//            mMinuteSpinnerInput.imeOptions = EditorInfo.IME_ACTION_NEXT
//        }
    }

    private fun onTimeChanged() {
        mOnTimeChangedListener?.onTimeChanged(
            this, getHour(), getMinute()
        )
    }

    fun getAmPmStrings(context: Context): Array<String> {
        val result = arrayOf("AM", "PM")
//        val result = arrayOfNulls<String>(2)
//        val d = LocaleData.get(context.resources.configuration.locale)
//        result[0] = if (d.amPm[0].length() > 4) d.narrowAm else d.amPm[0]
//        result[1] = if (d.amPm[1].length() > 4) d.narrowPm else d.amPm[1]
        return result
    }

    companion object {
        private const val HOURS_IN_HALF_DAY = 12
        private const val MINUTES_STEP_INTERVAL = 30
    }

    interface OnTimeChangedListener {
        fun onTimeChanged(view: View, hourOfDay: Int, minute: Int)
    }

    class TwoDigitFormatter : NumberPicker.Formatter {
        override fun format(value: Int): String {
            return String.format("%02d", value)
        }
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