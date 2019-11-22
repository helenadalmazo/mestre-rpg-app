package com.dalmazo.helena.mestrerpg

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dalmazo.helena.mestrerpg.enum.Action
import com.dalmazo.helena.mestrerpg.enum.Race
import com.dalmazo.helena.mestrerpg.enum.Sex
import com.dalmazo.helena.mestrerpg.model.Npc
import com.dalmazo.helena.mestrerpg.util.Extra
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.dalmazo.helena.mestrerpg.enum.RequestCode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class NpcActivity : AppCompatActivity() {

    private var npcObject = Npc()
    private var npcImage: Bitmap? = null

    private var editMode = true
    private var imageChanged = false

    private lateinit var imageViewNpc: ImageView
    private lateinit var editTextName: EditText
    private lateinit var editTextCharacteristics : EditText
    private lateinit var editTextHistory: EditText
    private lateinit var spinnerSex: Spinner
    private lateinit var spinnerRace: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_npc)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageViewNpc = findViewById<ImageView>(R.id.image)
        editTextName = findViewById<EditText>(R.id.name)
        editTextCharacteristics = findViewById<EditText>(R.id.characteristics)
        editTextHistory = findViewById<EditText>(R.id.history)
        spinnerSex = findViewById<Spinner>(R.id.sex)
        spinnerRace = findViewById<Spinner>(R.id.race)

        val spinnerSexArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Sex.values().map { sex -> sex.value })
        spinnerSexArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSex.adapter = spinnerSexArrayAdapter

        val spinnerRaceArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Race.values().map { race -> race.value })
        spinnerRaceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRace.adapter = spinnerRaceArrayAdapter

        if (intent.getSerializableExtra(Extra.NPC_OBJECT) != null) {
            npcObject = intent.getSerializableExtra(Extra.NPC_OBJECT) as Npc

            FirebaseStorage.getInstance().reference
                .child("npcs/${npcObject.id}.jpg").getBytes(1024*1024)
                .addOnSuccessListener { bytes ->
                    npcImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imageViewNpc.setImageBitmap(npcImage)
                }

            setFieldsValue()
            changeToViewMode()
        }
    }

    private fun setFieldsValue() {
        imageViewNpc.setImageBitmap(npcImage)
        editTextName.setText(npcObject.name, TextView.BufferType.EDITABLE)
        editTextCharacteristics.setText(npcObject.characteristics, TextView.BufferType.EDITABLE)
        editTextHistory.setText(npcObject.history, TextView.BufferType.EDITABLE)
        spinnerSex.setSelection((spinnerSex.adapter as ArrayAdapter<String>).getPosition(npcObject.sex?.value))
        spinnerRace.setSelection((spinnerRace.adapter as ArrayAdapter<String>).getPosition(npcObject.race?.value))
    }

    private fun saveNpc() {
        if (!validateEditTextsMandatory(listOf(editTextName, editTextCharacteristics, editTextHistory))) return

        val action: Action = if (existsNpcObject()) Action.EDIT else Action.ADD
        val intentToReturn = Intent().apply {
            putExtra(Extra.NPC_OBJECT, buildNpcObject())
            putExtra(Extra.NPC_ACTION, action)
            if (imageChanged) {
                if (imageViewNpc.drawable != null) {
                    val npcImageBitmap = (imageViewNpc.drawable as BitmapDrawable).bitmap

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    npcImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

                    putExtra(Extra.NPC_IMAGE_ACTION, Action.EDIT)
                    putExtra(Extra.NPC_IMAGE, byteArrayOutputStream.toByteArray())
                } else {
                    putExtra(Extra.NPC_IMAGE_ACTION, Action.DELETE)
                }
            }
        }
        setResult(Activity.RESULT_OK, intentToReturn);
        finish()
    }

    private fun buildNpcObject(): Npc {
        val name = editTextName.text.toString()
        val characteristics = editTextCharacteristics.text.toString()
        val history = editTextHistory.text.toString()
        val sex = Sex.get(spinnerSex.selectedItem.toString())
        val race = Race.get(spinnerRace.selectedItem.toString())
        return Npc(npcObject.id, name, characteristics, history, sex, race)
    }

    private fun validateEditTextsMandatory(editTexts: List<EditText>): Boolean {
        var valid = true
        editTexts.forEach { editText ->
            if (editText.length() < 1) {
                editText.error = "Campo obrigatório"
                valid = false
            }
        }
        return valid
    }

    private fun deleteNpc() {
        val intentToReturn = Intent().apply {
            putExtra(Extra.NPC_OBJECT, npcObject)
            putExtra(Extra.NPC_ACTION, Action.DELETE)
        }
        setResult(Activity.RESULT_OK, intentToReturn);
        finish()
    }

    private fun existsNpcObject(): Boolean {
        return npcObject.id != ""
    }

    private fun showAlertDialogDeleteNpc() {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Deletar NPC?")
        alertDialogBuilder.setMessage("Você tem certeza que deseja deletar o NPC?")

        alertDialogBuilder.setPositiveButton("Deletar") { _, _ -> deleteNpc() }
        alertDialogBuilder.setNegativeButton("Cancelar", null)

        alertDialogBuilder.create().show()
    }

    private fun showAlertDialogUndoNpcChanges() {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Desfazer alterações?")
        alertDialogBuilder.setMessage("Você tem certeza que deseja desfazer as alterações?")

        alertDialogBuilder.setPositiveButton("Sim") { _, _ ->
            setFieldsValue()
            changeToViewMode()
            invalidateOptionsMenu()
        }
        alertDialogBuilder.setNegativeButton("Cancelar", null)

        alertDialogBuilder.create().show()
    }

    private fun changeToEditMode() {
        editMode = true
        editTextName.isEnabled = true
        editTextCharacteristics.isEnabled = true
        editTextHistory.isEnabled = true
        spinnerSex.isEnabled = true
        spinnerRace.isEnabled = true
        findViewById<ImageButton>(R.id.edit_image).visibility = View.VISIBLE
        if (npcImage == null) findViewById<TextView>(R.id.add_image).visibility = View.VISIBLE
    }

    private fun changeToViewMode() {
        editMode = false
        editTextName.isEnabled = false
        editTextCharacteristics.isEnabled = false
        editTextHistory.isEnabled = false
        spinnerSex.isEnabled = false
        spinnerRace.isEnabled = false
        findViewById<ImageButton>(R.id.edit_image).visibility = View.GONE
        findViewById<TextView>(R.id.add_image).visibility = View.GONE
    }

    fun showBottomSheetDialogEditImage(view: View) {
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_edit_image, null)

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(view)

        view.findViewById<TextView>(R.id.take_image_from_camera).setOnClickListener {
            bottomSheetDialog.dismiss()
            takeImageFromCamera()
        }
        view.findViewById<TextView>(R.id.choose_image_from_gallery).setOnClickListener {
            bottomSheetDialog.dismiss()
            chooseImageFromGallery()
        }
        view.findViewById<TextView>(R.id.remove_image).setOnClickListener {
            bottomSheetDialog.dismiss()
            removeImage()
        }
        if (npcImage == null) view.findViewById<TextView>(R.id.remove_image).visibility = View.GONE

        bottomSheetDialog.show()
    }

    private fun chooseImageFromGallery() {
        startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RequestCode.GALLERY_IMAGE.value)
    }

    private fun takeImageFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takeCameraImageIntent ->
            takeCameraImageIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeCameraImageIntent, RequestCode.CAMERA_IMAGE.value)
            }
        }
    }

    fun removeImage() {
        imageChanged = true
        imageViewNpc.setImageDrawable(null)
        findViewById<TextView>(R.id.add_image).visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == RequestCode.GALLERY_IMAGE.value) {
                imageViewNpc.setImageURI(data?.data)
            }
            else if (requestCode == RequestCode.CAMERA_IMAGE.value) {
                val image = data?.extras?.get("data") as Bitmap
                imageViewNpc.setImageBitmap(image)
            }

            imageChanged = true
            findViewById<TextView>(R.id.add_image).visibility = View.GONE
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bar_actions_buttons, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_delete -> {
            showAlertDialogDeleteNpc()
            true
        }
        R.id.action_undo -> {
            if (npcObject.equals(buildNpcObject())) {
                changeToViewMode()
                invalidateOptionsMenu()
            } else {
                showAlertDialogUndoNpcChanges()
            }
            true
        }
        R.id.action_edit -> {
            changeToEditMode()
            invalidateOptionsMenu()
            true
        }
        R.id.action_save -> {
            saveNpc()
            true
        }
        android.R.id.home -> {
            setResult(Activity.RESULT_CANCELED, Intent());
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (existsNpcObject()) {
            menu?.findItem(R.id.action_delete)?.isVisible = true

            if (editMode) {
                menu?.findItem(R.id.action_undo)?.isVisible = true
                menu?.findItem(R.id.action_edit)?.isVisible = false
                menu?.findItem(R.id.action_save)?.isVisible = true
            } else {
                menu?.findItem(R.id.action_undo)?.isVisible = false
                menu?.findItem(R.id.action_edit)?.isVisible = true
                menu?.findItem(R.id.action_save)?.isVisible = false
            }

        } else {
            menu?.findItem(R.id.action_delete)?.isVisible = false
            menu?.findItem(R.id.action_undo)?.isVisible = false
            menu?.findItem(R.id.action_edit)?.isVisible = false
            menu?.findItem(R.id.action_save)?.isVisible = true
        }

        return super.onPrepareOptionsMenu(menu)
    }
}
