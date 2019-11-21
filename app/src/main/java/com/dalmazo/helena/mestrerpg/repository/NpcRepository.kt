package com.dalmazo.helena.mestrerpg.repository

import com.dalmazo.helena.mestrerpg.model.Npc
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class NpcRepository {
    var collectionReference: CollectionReference

    constructor(worldId: String) {
        this.collectionReference = FirebaseFirestore.getInstance().collection("worlds").document(worldId).collection("npcs")
    }

    fun list(): Task<QuerySnapshot> {
        return collectionReference.get()
    }

    fun add(npc: Npc): Task<DocumentReference> {
        return collectionReference.add(npc)
    }

    fun update(npc: Npc): Task<Void> {
        return collectionReference.document(npc.id).set(npc)
    }

    fun delete(npc: Npc): Task<Void> {
        return collectionReference.document(npc.id).delete()
    }
}