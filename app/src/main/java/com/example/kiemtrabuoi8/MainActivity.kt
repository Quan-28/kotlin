package com.example.kiemtrabuoi8

import android.media.MediaPlayer
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kiemtrabuoi8.R.drawable.*
import java.io.IOException

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var timeStart: TextView
    private lateinit var tvTieuDe: TextView
    private lateinit var tvNgheSi: TextView
    private lateinit var timeEnd: TextView
    private lateinit var seekBarTime: SeekBar
    private lateinit var btnPause: Button
    private lateinit var musicPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val song: Song? = intent.getParcelableExtra("Song")
        timeStart = findViewById(R.id.timeStart)
        timeEnd = findViewById(R.id.timeEnd)
        seekBarTime = findViewById(R.id.viewTime)
        btnPause = findViewById(R.id.btnPause)
        tvTieuDe = findViewById(R.id.tvTieuDe)
        tvNgheSi = findViewById(R.id.tvNgheSi)

        tvTieuDe.text = song?.title
        tvNgheSi.text = song?.artist

        musicPlayer = MediaPlayer()
        try {
            val songPath = song?.path
            if (songPath != null && !songPath.isEmpty()) {
                musicPlayer.setDataSource(songPath)
                musicPlayer.prepare()
            } else {
                Toast.makeText(this, "Error: Song path is null or empty", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }


        musicPlayer.isLooping = true
        musicPlayer.seekTo(0)

        val duration = millisecondsToString(musicPlayer.duration)
        timeEnd.text = duration
        btnPause.setOnClickListener(this@MainActivity)

        seekBarTime.max = musicPlayer.duration
        seekBarTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicPlayer.seekTo(progress)
                    seekBar?.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        updateSeekBar()

    }

    private fun updateSeekBar() {
        Thread {
            while (true) {
                if (musicPlayer.isPlaying) {
                    try {
                        val current = musicPlayer.currentPosition.toDouble()
                        val elapsedTime = millisecondsToString(current.toInt())

                        runOnUiThread {
                            timeStart.text = elapsedTime
                            seekBarTime.progress = current.toInt()
                        }

                        Thread.sleep(1000)
                    } catch (_: InterruptedException) {
                    }
                }
            }
        }.start()
    }

    private fun millisecondsToString(time: Int): String {
        val minutes = time / 1000 / 60
        val second = time / 1000 % 60
        return String.format("%02d:%02d", minutes, second)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == R.id.btnPause) {
                if (musicPlayer.isPlaying) {
                    musicPlayer.pause()
                    btnPause.setBackgroundResource(playbutton)
                } else {
                    musicPlayer.start()
                    btnPause.setBackgroundResource(pausebutton)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (musicPlayer.isPlaying) {
                musicPlayer.stop()
            }
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (musicPlayer.isPlaying) {
            musicPlayer.stop()
        }
        musicPlayer.release()
    }
}
