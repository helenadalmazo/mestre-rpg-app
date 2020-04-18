package com.dalmazo.helena.mestrerpg.repository.base

import com.dalmazo.helena.mestrerpg.model.base.BaseModel
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

open class BaseImageRepository<Model: BaseModel>(path: String) {

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child(path)

    public fun get(model: Model): Task<ByteArray> {
        return storageReference.child("${model.id}.jpg").getBytes(1024 * 1024)
    }

    public fun save(model: Model, image: ByteArray): UploadTask {
        return storageReference.child("${model.id}.jpg").putBytes(image)
    }

    public fun delete(model: Model): Task<Void>? {
        return storageReference.child("${model.id}.jpg").delete()
    }
}
