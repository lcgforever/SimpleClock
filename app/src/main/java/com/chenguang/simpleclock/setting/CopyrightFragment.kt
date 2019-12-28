package com.chenguang.simpleclock.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chenguang.simpleclock.R
import kotlinx.android.synthetic.main.fragment_copyright.copyright_fragment_web_view

/**
 * Fragment to display app copyright and open source license information
 */
class CopyrightFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_copyright, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        copyright_fragment_web_view.loadUrl("file:///android_res/raw/copyright.html")
    }
}
