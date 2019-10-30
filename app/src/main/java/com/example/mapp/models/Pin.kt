package com.example.mapp.models

import com.google.android.gms.maps.model.LatLng

/*
//class Pin(var pinID: Int = -1, var name: String = "Name not given", var GPSlocation: LatLng = LatLng(-34.0, 151.0),
         // var description: String = "Description not given") */
class Pin(var pinID: Int = -1, var name: String = "Name not given",
          var description: String = "Description not given", var latitude: Double = -34.0, var longitude: Double  = 151.0, var icon: String = "", var mapid: String = "", var dbPinId: String = "")
{

    fun pinGPSLocation() : LatLng {
        return LatLng(latitude, longitude)
    }

}