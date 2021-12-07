package org.nunocky.subtitlestudy

import android.app.Application
import java.io.File
import java.io.FileOutputStream

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        copyResources()
    }

    private fun copyResources() {
        resources.openRawResource(R.raw.subtitle).use { iStream->
            val destFile = File(dataDir, "subtitle.srt")
            FileOutputStream(destFile).use { oStream->
                val bytes = iStream.readBytes()
                oStream.write(bytes)
            }
        }
    }
}