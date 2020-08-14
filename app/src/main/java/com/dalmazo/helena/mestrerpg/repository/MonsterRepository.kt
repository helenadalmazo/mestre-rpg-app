package com.dalmazo.helena.mestrerpg.repository

import com.dalmazo.helena.mestrerpg.model.Monster
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MonsterRepository {
    var collectionReference: CollectionReference

    constructor(worldId: String) {
        this.collectionReference = FirebaseFirestore.getInstance().collection("worlds").document(worldId).collection("monsters")
    }

    fun list(): Task<QuerySnapshot> {
        return collectionReference.get()
    }

    fun add(monster: Monster): Task<DocumentReference> {
        return collectionReference.add(monster)
    }

    fun update(monster: Monster): Task<Void> {
        return collectionReference.document(monster.id).set(monster)
    }

    fun delete(monster: Monster): Task<Void> {
        return collectionReference.document(monster.id).delete()
    }
}