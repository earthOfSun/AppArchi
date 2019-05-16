package com.wdy.voicelib.audioRecord

import android.media.AudioRecord
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.wdy.voicelib.Constant
import java.io.File

class audioRecordUtils {

    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = audioRecordUtils()
    }

    private var minBufferSize: Int = 0
    private  var audioRecord: AudioRecord ?= null
    private lateinit var parseArr: ByteArray
    private var state = Constant.UNINIT
    private var audioFile: String? = null
    /**
     * @param audioSource 音频源
     * @param sampleRate sample rate、采样率
     * @param channel channel、声道
     * @param audioFormat Audio data format、音频格式
     */
    fun creatAudioRecord(
        audioSource: Int,
        sampleRateInHz: Int,
        channelConfig: Int,
        audioFormat: Int
    ) {
        if (!PermissionUtils.isGranted(PermissionConstants.STORAGE,PermissionConstants.MICROPHONE)){
            ToastUtils.showShort("缺少必要权限")
            return
        }
        minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
        audioRecord = AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, minBufferSize)
//        if (minBufferSize != 0 && audioRecord != null && audioRecord.state == AudioRecord.STATE_INITIALIZED) {
//            parseArr = ByteArray(minBufferSize)
//        }else ToastUtils.showShort("init AudioRecord failure")
    }

    fun start(audioPath: String) {
        LogUtils.e(audioPath,audioRecord,minBufferSize)
        if (audioRecord != null && audioRecord!!.state == AudioRecord.STATE_INITIALIZED) {
            LogUtils.e("615541115")
            audioRecord!!.startRecording()
            state = Constant.RECORDING
            audioFile = audioPath
            Thread(AudioRunnable()).start()
        }
    }

    fun stop() {
        if (audioRecord != null && audioRecord!!.state == AudioRecord.RECORDSTATE_RECORDING){
            audioRecord!!.stop()
            state = Constant.STOPING
        }
    }

    fun release() {
        if (audioRecord != null && audioRecord!!.state == AudioRecord.RECORDSTATE_RECORDING){
            audioRecord!!.release()

        }
    }

    inner class AudioRunnable : Runnable {
        override fun run() {
            if (FileUtils.isFileExists(audioFile))
                FileUtils.delete(audioFile)
            val file = File(audioFile)
            val buffs = ByteArray(minBufferSize)
            while (audioRecord!!.state == AudioRecord.RECORDSTATE_RECORDING) {
                var len = audioRecord!!.read(buffs, 0, minBufferSize)
                LogUtils.e("5565")
                if (len > 0) {
                    file.writeBytes(buffs)
                }
            }

        }

    }
}