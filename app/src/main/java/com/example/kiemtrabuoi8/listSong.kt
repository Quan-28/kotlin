package com.example.kiemtrabuoi8

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class listSong : AppCompatActivity() {
    private val requestPermission = 123
    private lateinit var songArrayList: ArrayList<Song>
    private lateinit var rvSongs: RecyclerView
    private lateinit var songsAdapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_song)
        supportActionBar?.hide()

        rvSongs = findViewById(R.id.rvSongs)
        val layoutManager = LinearLayoutManager(this)
        rvSongs.layoutManager = layoutManager

        songArrayList = ArrayList()

        songsAdapter = SongAdapter(this, songArrayList)
        rvSongs.adapter = songsAdapter

        rvSongs.setPadding(0, 0, 0, 0)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 999)
        } else {
            getSongs()
        }



        songsAdapter.setOnItemClickListener(object : SongAdapter.OnItemClickListener {
            override fun onItemClick(song: Song) {
                val openMusicPlayer = Intent(this@listSong, MainActivity::class.java)
                openMusicPlayer.putExtra("song", song)
                startActivity(openMusicPlayer)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 999) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getSongs()
            }
        }
    }


    private fun getSongs() {
        val contentResolver = contentResolver
        val songUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor: Cursor? = contentResolver.query(songUri, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst()) {
            val indexTitle: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val indexArtist: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val indexData: Int = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            do {
                val title: String = songCursor.getString(indexTitle) ?: ""
                val artist: String = songCursor.getString(indexArtist) ?: ""
                val path: String = songCursor.getString(indexData) ?: ""
                songArrayList.add(Song(title, artist, path))
            } while (songCursor.moveToNext())
        }

        songsAdapter.notifyDataSetChanged()
    }
}
