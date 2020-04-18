package com.dalmazo.helena.mestrerpg.repository.image

import com.dalmazo.helena.mestrerpg.model.Monster
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class MonsterImageRepository {

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("monsters")

    public fun get(monster: Monster): Task<ByteArray> {
        return storageReference.child("${monster.id}.jpg").getBytes(1024 * 1024)
    }

    public fun save(monster: Monster): UploadTask {
        return storageReference.child("${monster.id}.jpg").putBytes(monster.image)
    }

    public fun delete(monster: Monster): Task<Void>? {
        return storageReference.child("${monster.id}.jpg").delete()
    }
}