package me.liwenkun.demo.customview

import android.graphics.Outline
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import me.liwenkun.demo.R
import me.liwenkun.demo.databinding.FragmentCustomViewBinding
import me.liwenkun.demo.demoframework.DemoBaseFragment
import me.liwenkun.demo.libannotation.Demo
import me.liwenkun.demo.utils.px


@Demo(title = "自定义小组件集合")
class CustomViewFragment : DemoBaseFragment() {

    private lateinit var binding: FragmentCustomViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentCustomViewBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager: ViewPager = binding.viewPager
        val indicator: Indicator = binding.indicator
        viewPager.adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return 14
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val textView = TextView(context)
                textView.gravity = Gravity.CENTER
                // noinspection all
                textView.text = "仿照搜狗输入法指示器$position"
                container.addView(textView)
                return textView
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }
        }
        indicator.setUpWithPager(viewPager)

        val ivOutline = view.findViewById<ImageView>(R.id.iv_outline)
        val seekBar: SeekBar = view.findViewById(R.id.sb_elevation)
        ivOutline.clipToOutline = true
        ivOutline.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                if (view != null) {
                    outline?.setRoundRect(0, 0, view.width, view.height, px(10).toFloat())
                }
            }
        }
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                ivOutline.elevation = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val clipLayout: ClipLayout = view.findViewById(R.id.clip_layout)
        val sbClip = view.findViewById<SeekBar>(R.id.sb_clip)
        sbClip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                clipLayout.setClip(1 - progress.toFloat() / seekBar!!.max)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

    }


}