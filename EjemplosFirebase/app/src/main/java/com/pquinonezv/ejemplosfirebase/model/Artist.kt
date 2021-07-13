package com.pquinonezv.ejemplosfirebase.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Artist (
    var id: String = "",
    var name : String = "",
    var genre: String = ""

)