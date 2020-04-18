package com.dalmazo.helena.mestrerpg

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.dalmazo.helena.mestrerpg.enum.Action
import com.dalmazo.helena.mestrerpg.enum.DisplayMode
import com.dalmazo.helena.mestrerpg.enum.RequestCode
import com.dalmazo.helena.mestrerpg.enum.model.Displacement
import com.dalmazo.helena.mestrerpg.enum.model.Size
import com.dalmazo.helena.mestrerpg.enum.model.Type
import com.dalmazo.helena.mestrerpg.model.Monster
import com.dalmazo.helena.mestrerpg.util.Extra
import com.dalmazo.helena.mestrerpg.util.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog

class MonsterActivity : AppCompatActivity() {

    private var monsterObject = Monster()

    private var displayMode = DisplayMode.EDIT

    private lateinit var imageViewMonster: ImageView
    private lateinit var editTextName: EditText
    private lateinit var editTextCharacteristics : EditText
    private lateinit var editTextHistory: EditText
    private lateinit var spinnerSize: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var spinnerDisplacement: Spinner

    private lateinit var addImageButton: TextView
    private lateinit var editImageButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monster)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addImageButton = findViewById(R.id.add_image)
        editImageButton = findViewById(R.id.edit_image)

        setupFields()

        if (intent.getSerializableExtra(Extra.MONSTER_OBJECT) != null) {
            monsterObject = intent.getSerializableExtra(Extra.MONSTER_OBJECT) as Monster
            setFieldsValue()
            changeToViewMode()
        } else {
            changeToEditMode()
        }
    }

    private fun setupFields() {
        imageViewMonster = findViewById(R.id.image)
        editTextName = findViewById(R.id.name)
        editTextCharacteristics = findViewById(R.id.characteristics)
        editTextHistory = findViewById(R.id.history)
        spinnerType = findViewById(R.id.type)
        spinnerSize = findViewById(R.id.size)
        spinnerDisplacement = findViewById(R.id.displacement)

        val spinnerTypeArrayAdapter = ArrayAdapter<Type>(this, android.R.layout.simple_spinner_item, Type.values())
        spinnerTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = spinnerTypeArrayAdapter

        val spinnerSizeArrayAdapter = ArrayAdapter<Size>(this, android.R.layout.simple_spinner_item, Size.values())
        spinnerSizeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSize.adapter = spinnerSizeArrayAdapter

        val spinnerDisplacementArrayAdapter = ArrayAdapter<Displacement>(this, android.R.layout.simple_spinner_item, Displacement.values())
        spinnerDisplacementArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDisplacement.adapter = spinnerDisplacementArrayAdapter
    }

    private fun setFieldsValue() {
        if (monsterObject.image.isEmpty()) {
            imageViewMonster.setImageDrawable(null)
        } else {
            val bitmap = BitmapFactory.decodeByteArray(monsterObject.image, 0, monsterObject.image.size)
            imageViewMonster.setImageBitmap(bitmap)
        }
        editTextName.setText(monsterObject.name, TextView.BufferType.EDITABLE)
        editTextCharacteristics.setText(monsterObject.characteristics, TextView.BufferType.EDITABLE)
        editTextHistory.setText(monsterObject.history, TextView.BufferType.EDITABLE)
        spinnerType.setSelection((spinnerType.adapter as ArrayAdapter<Type>).getPosition(monsterObject.type))
        spinnerSize.setSelection((spinnerSize.adapter as ArrayAdapter<Size>).getPosition(monsterObject.size))
        spinnerDisplacement.setSelection((spinnerDisplacement.adapter as ArrayAdapter<Displacement>).getPosition(monsterObject.displacement))
    }

    private fun save() {
        if (!validateEditTextsMandatory(listOf(editTextName, editTextCharacteristics, editTextHistory))) return

        val obj = buildObject()

        val action: Action = if (existsObject()) Action.UPDATE else Action.ADD

        var imageAction: Action? = null
        if (!obj.image.contentEquals(monsterObject.image)) {
            if (obj.image.isEmpty()) imageAction = Action.DELETE
            else imageAction = Action.UPDATE
        }

        val intentToReturn = Intent().apply {
            putExtra(Extra.MONSTER_OBJECT, obj)
            putExtra(Extra.MONSTER_ACTION, action)
            putExtra(Extra.MONSTER_IMAGE_ACTION, imageAction)
        }
        setResult(Activity.RESULT_OK, intentToReturn)
        finish()
    }

    private fun buildObject(): Monster {
        var image = byteArrayOf()
        if (imageViewMonster.drawable != null) {
            image = Utils.getByteArrayFromImageView(imageViewMonster)
        }

        val name = editTextName.text.toString()
        val characteristics = editTextCharacteristics.text.toString()
        val history = editTextHistory.text.toString()
        val size = spinnerSize.selectedItem as Size
        val type = spinnerType.selectedItem as Type
        val displacement = spinnerDisplacement.selectedItem as Displacement

        return Monster(monsterObject.id, image, name, characteristics, history, size, type, displacement)
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

    private fun delete() {
        val intentToReturn = Intent().apply {
            putExtra(Extra.MONSTER_OBJECT, monsterObject)
            putExtra(Extra.MONSTER_ACTION, Action.DELETE)
        }
        setResult(Activity.RESULT_OK, intentToReturn)
        finish()
    }

    private fun existsObject(): Boolean {
        return monsterObject.id != ""
    }

    private fun showAlertDialogDelete() {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Deletar Monstro?")
        alertDialogBuilder.setMessage("Você tem certeza que deseja deletar o Monstro?")

        alertDialogBuilder.setPositiveButton("Deletar") { _, _ -> delete() }
        alertDialogBuilder.setNegativeButton("Cancelar", null)

        alertDialogBuilder.create().show()
    }

    private fun showAlertDialogUndoObjectChanges() {
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
        displayMode = DisplayMode.EDIT

        editTextName.isEnabled = true
        editTextCharacteristics.isEnabled = true
        editTextHistory.isEnabled = true
        spinnerType.isEnabled = true
        spinnerSize.isEnabled = true
        spinnerDisplacement.isEnabled = true

        if (imageViewMonster.drawable == null) {
            addImageButton.visibility = View.VISIBLE
        } else {
            editImageButton.visibility = View.VISIBLE
        }
    }

    private fun changeToViewMode() {
        displayMode = DisplayMode.VIEW

        editTextName.isEnabled = false
        editTextCharacteristics.isEnabled = false
        editTextHistory.isEnabled = false
        spinnerType.isEnabled = false
        spinnerSize.isEnabled = false
        spinnerDisplacement.isEnabled = false

        addImageButton.visibility = View.GONE
        editImageButton.visibility = View.GONE
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

        if (imageViewMonster.drawable == null) view.findViewById<TextView>(R.id.remove_image).visibility = View.GONE

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

    private fun removeImage() {
        imageViewMonster.setImageDrawable(null)
        addImageButton.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == RequestCode.GALLERY_IMAGE.value) {
                imageViewMonster.setImageURI(data?.data)
            }
            else if (requestCode == RequestCode.CAMERA_IMAGE.value) {
                val image = data?.extras?.get("data") as Bitmap
                imageViewMonster.setImageBitmap(image)
            }

            addImageButton.visibility = View.GONE
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bar_actions_buttons, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_delete -> {
            showAlertDialogDelete()
            true
        }
        R.id.action_undo -> {
            if (monsterObject.equals(buildObject())) {
                changeToViewMode()
                invalidateOptionsMenu()
            } else {
                showAlertDialogUndoObjectChanges()
            }
            true
        }
        R.id.action_edit -> {
            changeToEditMode()
            invalidateOptionsMenu()
            true
        }
        R.id.action_save -> {
            save()
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
        if (existsObject()) {
            menu?.findItem(R.id.action_delete)?.isVisible = true

            if (displayMode == DisplayMode.EDIT) {
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
