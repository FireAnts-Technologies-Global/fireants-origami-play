package com.fireants.template.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.fireants.template.data.pref.AppSharedPref

class AppAudioController(
    context: Context,
    private val appSharedPref: AppSharedPref
) {
    private val appContext = context.applicationContext
    private val soundPool: SoundPool
    private var backgroundPlayer: MediaPlayer? = null
    private var clickSoundId = 0
    private var isClickSoundLoaded = false

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(MAX_SOUND_STREAMS)
            .setAudioAttributes(audioAttributes)
            .build()
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (sampleId == clickSoundId && status == LOAD_SUCCESS) {
                isClickSoundLoaded = true
            }
        }
        loadClickSound()
    }

    fun playBackgroundMusic() {
        if (!appSharedPref.isMusicEnabled) {
            pauseBackgroundMusic()
            return
        }

        val player = backgroundPlayer ?: createBackgroundPlayer() ?: return
        if (!player.isPlaying) {
            player.start()
        }
    }

    fun pauseBackgroundMusic() {
        backgroundPlayer?.takeIf { it.isPlaying }?.pause()
    }

    fun playClickSound() {
        if (!appSharedPref.isSoundFxEnabled || !isClickSoundLoaded || clickSoundId == 0) return
        soundPool.play(
            clickSoundId,
            SOUND_VOLUME,
            SOUND_VOLUME,
            SOUND_PRIORITY,
            NO_LOOP,
            NORMAL_RATE
        )
    }

    fun release() {
        backgroundPlayer?.release()
        backgroundPlayer = null
        soundPool.release()
    }

    private fun createBackgroundPlayer(): MediaPlayer? {
        val musicResId = rawResourceId(BACKGROUND_MUSIC_RES_NAME)
        if (musicResId == 0) return null

        return runCatching {
            MediaPlayer.create(appContext, musicResId)?.apply {
                isLooping = true
                setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
            }
        }.onFailure {
            Log.e(TAG, "Unable to create background music player", it)
        }.getOrNull().also {
            backgroundPlayer = it
        }
    }

    private fun loadClickSound() {
        val soundResId = rawResourceId(CLICK_SOUND_RES_NAME)
        if (soundResId == 0) return
        clickSoundId = soundPool.load(appContext, soundResId, SOUND_PRIORITY)
    }

    private fun rawResourceId(name: String): Int {
        return appContext.resources.getIdentifier(name, RAW_RESOURCE_TYPE, appContext.packageName)
    }

    companion object {
        private const val TAG = "AppAudioController"
        private const val BACKGROUND_MUSIC_RES_NAME = "music"
        private const val CLICK_SOUND_RES_NAME = "sound_fx"
        private const val RAW_RESOURCE_TYPE = "raw"
        private const val MAX_SOUND_STREAMS = 4
        private const val LOAD_SUCCESS = 0
        private const val SOUND_PRIORITY = 1
        private const val NO_LOOP = 0
        private const val NORMAL_RATE = 1f
        private const val MUSIC_VOLUME = 0.45f
        private const val SOUND_VOLUME = 1f
    }
}
