package com.cestco.apparchi

import android.content.res.Resources
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blankj.utilcode.util.AdaptScreenUtils
import com.wdy.voicelib.audioRecord.audioRecordUtils
import kotlinx.android.synthetic.main.activity_view_adapter.*

class ViewAdapterActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_adapter)


        val audioRecordUtils = audioRecordUtils.instance
            audioRecordUtils.creatAudioRecord(MediaRecorder.AudioSource.MIC,
            44100, AudioFormat.CHANNEL_IN_DEFAULT,AudioFormat.ENCODING_PCM_16BIT)

        mBtnStart.setOnClickListener{
           audioRecordUtils.start( "${this@ViewAdapterActivity.filesDir}/test.pcm" )
        }
        mBtnRelease.setOnClickListener{
            audioRecordUtils.release()
        }
    }

    override fun getResources(): Resources {
        return AdaptScreenUtils.adaptWidth(super.getResources(), 1080)
    }
}