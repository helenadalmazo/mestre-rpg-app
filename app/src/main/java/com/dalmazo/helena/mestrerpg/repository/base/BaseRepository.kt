package com.dalmazo.helena.mestrerpg.repository.base

import com.dalmazo.helena.mestrerpg.model.base.BaseModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

open class BaseRepository<Model: BaseModel>(worldId: String, collectionPath: String) {

    private val collectionReference: CollectionReference =
        FirebaseFirestore.getInstance().collection("worlds").document(worldId).collection(collectionPath)

    fun list(): Task<QuerySnapshot> {
        return collectionReference.get()
    }

    fun add(model: Model): Task<DocumentReference> {
        return collectionReference.add(model)
    }

    fun update(model: Model): Task<Void> {
        return collectionReference.document(model.id).set(model)
    }

    fun delete(model: Model): Task<Void> {
        return collectionReference.document(model.id).delete()
    }
}
