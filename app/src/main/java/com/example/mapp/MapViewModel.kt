package com.example.mapp

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.example.mapp.models.Map
import com.example.mapp.models.Pin
import com.example.mapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore



class MapViewModel (application: Application): AndroidViewModel(application) {
    private var _tracksList: MutableLiveData<ArrayList<Map>> = MutableLiveData()
    private var _dbTracksList: MutableLiveData<ArrayList<com.example.mapp.models.Map>> = MutableLiveData()
    lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private var currUser: MutableLiveData<Map> = MutableLiveData()

    /*fun addFollowedMap(mapID: String) {
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        var currentUserId: String = ""
        var currentUser: FirebaseUser = mAuth.getCurrentUser()!!
        var currentUsername: String = ""
        var currentFollowedMaps: ArrayList<String> = arrayListOf<String>()
        db.collection("users").get().addOnSuccessListener { userResult ->
            for (document in userResult) {
                if(document.data.get("email") as String == currentUser.email){
                    currentUserId = document.id
                    currentUsername = document.data.get("username") as String
                    for (followedMap in document.data.get("followedMaps") as Array<String>){
                        currentFollowedMaps.add(followedMap)
                    }
                }
            }
        }
        currentFollowedMaps.add(mapID)
        var newUser: User = User(currentUser.getEmail()!!, currentFollowedMaps.toArray() as Array<String>, currentUsername)
        db.collection("users").document(currentUserId).set(newUser)
    }*/

    fun deletePin(pinId: Int, mapID: String){
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        db.collection("pins").whereEqualTo("mapid", mapID).get().addOnSuccessListener { userResult ->
            println("why won't it check")

            for (document in userResult){
                println(pinId)
                println(document.data.get("pinID") as Long)
                if(pinId.toLong() == (document.data.get("pinID") as Long)){
                    db.collection("pins").document(document.id).delete()
                }
            }
        }
        /*db.collection("maps").get().addOnSuccessListener { userResult ->
            for (document in userResult) {
                if(document.id == mapID){
                    var newPins = (document.data.get("pins") as ArrayList<Pin>)
                    for(pin in newPins){
                        if (pin.pinID == pinId){
                            newPins.remove(pin)
                        }
                    }
                    db.collection("maps").document(document.id).update("pins", newPins)
                    //document.update("pins", newPins)
                }
                println("printing document")
            }
        }*/
    }

    fun updatePin(pinId: Int, mapID: String, pin: Pin){
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        if(pinId == -1){
            db.collection("pins").get().addOnSuccessListener { userResult ->
                pin.pinID = userResult.size()
                pin.mapid = mapID
                db.collection("pins").document().set(pin)
            }
        }
        else{
            db.collection("pins").whereEqualTo("mapid", mapID).get().addOnSuccessListener { userResult ->
                for(document in userResult){
                    if(pinId.toLong() == document.data.get("pinID") as Long){
                        pin.pinID = pinId
                        pin.mapid = mapID
                        db.collection("pins").document(document.id).update(
                            "description", pin.description,
                            "icon", pin.icon,
                            "name", pin.name
                        )

                        //db.collection("pins").document(document.id).set(pin)
                    }
                }

            }
        }





        /*db.collection("maps").get().addOnSuccessListener { userResult ->
            for (document in userResult) {
                if(document.id == mapID){
                    if(pinId == -1){
                        var newPins = (document.data.get("pins") as ArrayList<Pin>)
                        pin.pinID = newPins.size
                        newPins.add(pin)
                        println("checking document here")
                        println(db.collection("maps").document(document.id))
                        println(pin.pinID)
                        println(pin.name)
                        db.collection("pins")
                        //db.collection("maps").document(document.id).update("pins", newPins)
                    }
                    else{
                        var newPins = (document.data.get("pins") as ArrayList<Pin>)
                        for(i in 0..newPins.size){
                            newPins.set(i, pin)
                        }
                        db.collection("maps").document(document.id).update("pins", newPins)
                    }

                    //document.update("pins", newPins)
                }
            }
        }*/
    }

    fun getFollowedMaps(): MutableLiveData<ArrayList<Map>> {
        loadFollowedMaps()
        return _dbTracksList
    }

    fun loadFollowedMaps(): MutableLiveData<ArrayList<Map>> {
        var maps = ArrayList<Map>()
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        var currentUser: FirebaseUser = mAuth.getCurrentUser()!!
        var currentUsername: String = ""
        var currentFollowedMaps: ArrayList<String> = arrayListOf<String>()
        db.collection("users").get().addOnSuccessListener { userResult ->
            for (document in userResult) {
                if(document.data.get("email") as String == currentUser.email){
                    currentUsername = document.data.get("username") as String
                    for (followedMap in document.data.get("followedMaps") as ArrayList<String>){
                        currentFollowedMaps.add(followedMap)
                    }
                }
            }
        }
        db.collection("maps").get().addOnSuccessListener { userResult ->
            for (document in userResult) {
                println("printing document")
                var newMap = Map(document.data.get("name") as String, (document.data.get("creatorName") as String).toString(), (document.data.get("creatorID") as String).toString(), (document.get("locationTag") as String).toString())
                if(currentFollowedMaps.contains(document.id)){
                    maps.add(newMap)
                }
            }
            _dbTracksList.value = maps
        }

        return _dbTracksList
    }

    fun getSavedMaps(): MutableLiveData<ArrayList<Map>> {
        loadSavedMaps()
        return _dbTracksList
    }

    fun loadSavedMaps(): MutableLiveData<ArrayList<Map>> {
        var maps = ArrayList<Map>()
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        var currentUser: FirebaseUser = mAuth.getCurrentUser()!!
        var currentUsername: String = ""
        db.collection("users").get().addOnSuccessListener { userResult ->
            for (document in userResult) {
                println("CHECKING FOR A USERNAME")
                if(document.data.get("email") as String == currentUser.email){
                    currentUsername = document.data.get("username") as String
                    println(currentUsername)
                }
            }
        }
        db.collection("maps").get().addOnSuccessListener { userResult ->
            for (document in userResult) {
                println("printing document")
                var newMap = Map(document.data.get("name") as String, (document.data.get("creatorName") as String).toString(), (document.data.get("creatorID") as String).toString(), (document.get("locationTag") as String).toString())
                if(newMap.creatorName == currentUsername){
                    maps.add(newMap)
                    println("new map")
                    println(newMap)
                }
            }
            _dbTracksList.value = maps
        }
        return _dbTracksList
    }

    /*fun getNewMaps(): MutableLiveData<ArrayList<Map>> {
        var trackDatar: ArrayList<Map> = arrayListOf<Map>()
        //val newTrack1 = Map("first track", 0, 2, 0)
        //val newTrack2 = Map("second track", 2, 0, 0)
        //trackDatar.add(newTrack1)
        //trackDatar.add(newTrack2)
        _tracksList.value = trackDatar
        return _tracksList
    }*/

}