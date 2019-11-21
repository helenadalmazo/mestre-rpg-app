package com.dalmazo.helena.mestrerpg.repository.image

import com.dalmazo.helena.mestrerpg.model.Npc
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class NpcImageRepository {

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("npcs")

    public fun get(npc: Npc): Task<ByteArray> {
        return storageReference.child("${npc.id}.jpg").getBytes(1024 * 1024)
//        return FirebaseStorage.getInstance().reference.child("npcs").child("${npc.id}.jpg").getBytes(1024 * 1024)
//        return FirebaseStorage.getInstance().reference.child("npcs/${npc.id}.jpg").getBytes(1024 * 1024)
    }

    public fun save(npc: Npc, image: ByteArray): UploadTask {
        return storageReference.child("${npc.id}.jpg").putBytes(image)
    }

    public fun delete(npc: Npc): Task<Void>? {
        return storageReference.child("${npc.id}.jpg").delete()
    }
}