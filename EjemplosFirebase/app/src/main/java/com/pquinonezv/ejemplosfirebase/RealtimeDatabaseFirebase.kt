package com.pquinonezv.ejemplosfirebase

import android.R.layout.simple_list_item_1
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.database.*
import com.pquinonezv.ejemplosfirebase.model.Artist

class RealtimeDatabaseFirebase : AppCompatActivity() {

    var databaseReference: DatabaseReference? = null
    val ARTIST_NODE : String = "Artists"

    var lstArtist: ListView? = null
    var artistsName : List<String>? = null
    var arrayAdapter : ArrayAdapter<String>? = null
    var lstArtistsModel : MutableList<Artist>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime_database_firebase)

        lstArtist = findViewById(R.id.lstArtist)

        lstArtistsModel = arrayListOf()

        artistsName = ArrayList<String>()
        arrayAdapter = ArrayAdapter(this, simple_list_item_1, artistsName as ArrayList<String>).also { arrayAdapter = it }
        lstArtist?.adapter = arrayAdapter

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        databaseReference = FirebaseDatabase.getInstance().getReference()
        databaseReference?.child(ARTIST_NODE)?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (artistsName as ArrayList<String>).clear()
                lstArtistsModel?.clear()
                if( snapshot.exists() ) {
                    for(item : DataSnapshot in snapshot.children) {
                        var artist : Artist = item.getValue(Artist::class.java) as Artist
                        Log.w(TAG, "Artist name: " + artist.name)
                        (artistsName as ArrayList<String>).add(artist.name)
                        lstArtistsModel?.add(artist)
                    }
                    arrayAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        lstArtist?.setOnItemLongClickListener(object: AdapterView.OnItemLongClickListener {
            override fun onItemLongClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ): Boolean {
                var id_artist : String? = lstArtistsModel?.get(position)?.id
                lstArtistsModel?.removeAt(position)

                if (id_artist != null) {
                    databaseReference?.child(ARTIST_NODE)?.child(id_artist)?.removeValue()
                }

                return true
            }

        })
    }

    fun createArtist(view: View) {
        var artist : Artist = Artist()
        artist.id = databaseReference?.push()?.key.toString()
        artist.name = "Garbage"
        artist.genre = "Rock"

        databaseReference?.child(ARTIST_NODE)?.child(artist.id)?.setValue(artist)
    }

}