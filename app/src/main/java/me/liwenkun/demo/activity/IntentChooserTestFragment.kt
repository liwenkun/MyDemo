package me.liwenkun.demo.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import me.liwenkun.demo.R
import me.liwenkun.demo.demoframework.DemoBaseFragment
import me.liwenkun.demo.libannotation.Demo


@Demo(title = "意图选择器")
class IntentChooserTestFragment : DemoBaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intent_chooser_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnIntentChooser = view.findViewById<Button>(R.id.btn_intent_chooser)
        btnIntentChooser.setOnClickListener {
            val mIntent = Intent(Intent.ACTION_PICK)
            mIntent.setType("image/*")
            Intent.createChooser(mIntent, "请选择想要打开的应用")
            startActivity(mIntent)
        }
    }
}
