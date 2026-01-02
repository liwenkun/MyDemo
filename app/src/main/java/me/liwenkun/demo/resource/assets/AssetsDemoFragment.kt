package me.liwenkun.demo.resource.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.liwenkun.demo.R
import me.liwenkun.demo.demoframework.DemoBaseFragment
import me.liwenkun.demo.libannotation.Demo

@Demo("Assets")
class AssetsDemoFragment : DemoBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_assets_demo, container, false)
    }


}