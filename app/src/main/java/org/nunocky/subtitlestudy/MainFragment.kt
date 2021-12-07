package org.nunocky.subtitlestudy

import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.C.*
import org.nunocky.subtitlestudy.databinding.FragmentMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.SubtitleConfiguration
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import java.io.File
import com.google.android.exoplayer2.util.MimeTypes
import com.google.common.collect.ImmutableList


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // URI for movie
        // Movie file must be in raw resource or local storage
        val videoUri =
            Uri.parse("android.resource://${requireActivity().packageName}/" + R.raw.blueback)

        // URI for subtitle
        // subtitle file must be in local storage. If it is in raw resource or asset directory, it will fail.
        val subtitleUri = run {
            val file = File(requireActivity().dataDir, "subtitle.srt")
            Uri.parse(file.absolutePath)
        }

        player = loadMovie(videoUri, subtitleUri)
        // 字幕のスタイルを変更
        setupSubtitleStyle()

        binding.playerView.player = player
        player?.prepare()
        player?.play()
        binding.playerView.hideController()
    }

    override fun onPause() {
        super.onPause()
        player?.release()
        player = null
    }

    private fun loadMovie(videoUri : Uri, subtitleUri : Uri) : ExoPlayer {
        val pl = ExoPlayer.Builder(requireActivity()).build()

        val subtitle = SubtitleConfiguration.Builder(subtitleUri)
            .setMimeType(MimeTypes.APPLICATION_SUBRIP)
            .setSelectionFlags(SELECTION_FLAG_DEFAULT) // これでしか字幕が表示されない ...?
            .build()

        val mediaItem: MediaItem = MediaItem.Builder()
            .setUri(videoUri)
            .setSubtitleConfigurations(ImmutableList.of(subtitle))
            .build()

        pl.setMediaItem(mediaItem)

        return pl
    }

    private fun setupSubtitleStyle() {
        binding.playerView.subtitleView?.let { subtitleView ->
            subtitleView.setApplyEmbeddedFontSizes(false)
            subtitleView.setApplyEmbeddedStyles(false)
            subtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_PX, 64f)
            subtitleView.setBottomPaddingFraction(0.05f)

            val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

            // 透明な背景、白い文字に黒でアウトライン
            val style = CaptionStyleCompat(
                Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT,
                CaptionStyleCompat.EDGE_TYPE_OUTLINE, Color.BLACK, typeface
            )
//            val style = CaptionStyleCompat(
//                Color.WHITE, Color.GRAY, Color.TRANSPARENT,
//                CaptionStyleCompat.EDGE_TYPE_NONE, Color.TRANSPARENT, null
//            )
            subtitleView.setStyle(style)
        }
    }
}