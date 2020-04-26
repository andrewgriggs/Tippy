package edu.stanford.griggsad.tippy

import android.animation.ArgbEvaluator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

private const val IMAGE_POOR_URL =
    "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/google/241/face-vomiting_1f92e.png"
private const val IMAGE_ACCEPTABLE_URL =
    "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/google/241/slightly-frowning-face_1f641.png"
private const val IMAGE_GOOD_URL =
    "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/google/241/slightly-smiling-face_1f642.png"
private const val IMAGE_GREAT_URL =
    "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/google/241/ok-hand_1f44c.png"
private const val IMAGE_AMAZING_URL =
    "https://images.emojiterra.com/google/android-10/128px/1f4af.png"

private var split = 1

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //var split = 1
        button1.setOnClickListener{
            split = 1
            button1.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorButtonClicked))
            button2.setBackgroundTintList(null)
            button3.setBackgroundTintList(null)
            button4.setBackgroundTintList(null)
            computeTipAndTotal()
        }
        button2.setOnClickListener{
            split = 2
            button1.setBackgroundTintList(null)
            button2.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorButtonClicked))
            button3.setBackgroundTintList(null)
            button4.setBackgroundTintList(null)
            computeTipAndTotal()
        }
        button3.setOnClickListener{
            split = 3
            button1.setBackgroundTintList(null)
            button2.setBackgroundTintList(null)
            button3.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorButtonClicked))
            button4.setBackgroundTintList(null)
            computeTipAndTotal()
        }
        button4.setOnClickListener{
            split = 4
            button1.setBackgroundTintList(null)
            button2.setBackgroundTintList(null)
            button3.setBackgroundTintList(null)
            button4.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorButtonClicked))
            computeTipAndTotal()
        }

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercent.text = "$progress%"
                updateTipDescription(progress)
                computeTipAndTotal()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?){}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        etBase.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription : String
        when (tipPercent) {
            in 0..9 -> tipDescription = "Poor"
            in 10..14 -> tipDescription = "Acceptable"
            in 15..19 -> tipDescription = "Good"
            in 20..24 -> tipDescription = "Great"
            else -> tipDescription = "Amazing"
        }
        tvTipDescription.text = tipDescription
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.colorWorstTip),
            ContextCompat.getColor(this, R.color.colorBestTip)
        ) as Int
        tvTipDescription.setTextColor(color)
        when (tipPercent) {
            in 0..9 -> Glide.with(this).load(IMAGE_POOR_URL).into(tipDescriptionImage)
            in 10..14 -> Glide.with(this).load(IMAGE_ACCEPTABLE_URL).into(tipDescriptionImage)
            in 15..19 -> Glide.with(this).load(IMAGE_GOOD_URL).into(tipDescriptionImage)
            in 20..24 -> Glide.with(this).load(IMAGE_GREAT_URL).into(tipDescriptionImage)
            else -> Glide.with(this).load(IMAGE_AMAZING_URL).into(tipDescriptionImage)
        }
    }

    private fun computeTipAndTotal() {
        // Get the value of the base and tip percent
        if (etBase.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            tvSplitAmount.text = ""
            return
        }
        val baseAmount = etBase.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        val splitAmt = totalAmount / split
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
        tvSplitAmount.text = "%.2f".format(splitAmt)
    }

}
