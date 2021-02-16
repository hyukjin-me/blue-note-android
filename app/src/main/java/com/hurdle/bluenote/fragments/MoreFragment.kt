package com.hurdle.bluenote.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hurdle.bluenote.R
import com.hurdle.bluenote.databinding.FragmentMoreBinding
import com.hurdle.bluenote.viewmodels.NoteViewModel


class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        val displayMetrics = resources.displayMetrics
        // 화면 크기 %로 설정
        val width = displayMetrics.widthPixels * 0.8
        val height = displayMetrics.heightPixels * 0.6

        val inflater = LayoutInflater.from(activity)
        val popupView = inflater.inflate(R.layout.popup_infomation, null)
        val popupWindow = PopupWindow(popupView, width.toInt(), height.toInt())

        val closeImageView = popupView.findViewById<ImageView>(R.id.more_close_image_view)
        val titleTextView = popupView.findViewById<TextView>(R.id.more_title_text_view)
        val contentTextView = popupView.findViewById<TextView>(R.id.more_contents_text_view)

        closeImageView.setOnClickListener {
            popupWindow.dismiss()
        }

        binding.moreOpensourceButton.setOnClickListener {
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
            titleTextView.text = getString(R.string.opensource_license)
            contentTextView.text = """
                    
                    MPAndroidChart 3.1.0 
                    (Apache-2.0)
                    
                    Retrofit2 2.9.0 
                    (Apache-2.0)
                    
                    Moshi 1.11.0 
                    (Apache-2.0)
                    
                    CircleImageView 3.0.0 
                    (Apache-2.0)
                """.trimIndent()
        }

        binding.moreVersionButton.setOnClickListener {
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
            titleTextView.text = getString(R.string.bluenote_version)

            try {
                val pInfo =
                    requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
                val version = pInfo.versionName
                contentTextView.text = """
                    App Version
                    $version
                """.trimIndent()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        // Email
        binding.moreSuggestionsButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            val address = getString(R.string.email_address)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            emailIntent.type = "message/rfc822"

            val emailMsg = getString(R.string.request_email)
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(emailMsg)
                .setPositiveButton(R.string.open) { _, _ ->
                    startActivity(emailIntent)
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                }
                .create()
            builder.show()
        }

        binding.moreKakaoButton.setOnClickListener {
            val url = "https://open.kakao.com/o/g0JkF5Lc"
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse(url)
            intent.data = uri

            val kakaoMsg = getString(R.string.kakaotalk_link_agree)
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(kakaoMsg)
                .setPositiveButton(R.string.open) { _, _ ->
                    startActivity(intent)
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                }
                .create()
            builder.show()
        }

        binding.moreTermofserviceButton.setOnClickListener {
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
            titleTextView.text = getString(R.string.terms_of_service)
            contentTextView.text = getString(R.string.privacy_information)
        }
    }
}