package com.dalmazo.helena.mestrerpg

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dalmazo.helena.mestrerpg.enum.Action
import com.dalmazo.helena.mestrerpg.enum.Race
import com.dalmazo.helena.mestrerpg.enum.Sex
import com.dalmazo.helena.mestrerpg.model.Npc
import com.dalmazo.helena.mestrerpg.util.Extra
import com.dalmazo.helena.mestrerpg.util.Utils

class NpcActivity : AppCompatActivity() {

    var npcObject = Npc()
    var editMode = true

    lateinit var editTextName: EditText
    lateinit var editTextCharacteristics : EditText
    lateinit var editTextHistory: EditText
    lateinit var spinnerSex: Spinner
    lateinit var spinnerRace: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_npc)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextName = findViewById<EditText>(R.id.name)
        editTextCharacteristics = findViewById<EditText>(R.id.characteristics)
        editTextHistory = findViewById<EditText>(R.id.history)
        spinnerSex = findViewById<Spinner>(R.id.sex)
        spinnerRace = findViewById<Spinner>(R.id.race)

        val spinnerSexArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Sex.values().map { sex -> sex.toString() })
        spinnerSexArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSex.adapter = spinnerSexArrayAdapter

        val spinnerRaceArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Race.values().map { race -> race.toString() })
        spinnerRaceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRace.adapter = spinnerRaceArrayAdapter

        if (intent.getSerializableExtra(Extra.NPC_OBJECT) != null) {
            npcObject = intent.getSerializableExtra(Extra.NPC_OBJECT) as Npc

            setFieldsValue()
            changeToViewMode()
        }
    }

    private fun setFieldsValue() {
        editTextName.setText(npcObject.name, TextView.BufferType.EDITABLE)
        editTextCharacteristics.setText(npcObject.characteristics, TextView.BufferType.EDITABLE)
        editTextHistory.setText(npcObject.history, TextView.BufferType.EDITABLE)
        spinnerSex.setSelection((spinnerSex.adapter as ArrayAdapter<String>).getPosition(npcObject.sex.toString()))
        spinnerRace.setSelection((spinnerRace.adapter as ArrayAdapter<String>).getPosition(npcObject.race.toString()))
    }

    private fun saveNpc() {
        if (!validateEditTextMandatory(listOf(editTextName, editTextCharacteristics, editTextHistory))) {
            return
        }

        val action: Action = if (existsNpcObject()) Action.EDIT else Action.ADD
        val intentToReturn = Intent().apply {
            putExtra(Extra.NPC_OBJECT, buildNpcObject())
            putExtra(Extra.NPC_ACTION, action)
        }
        setResult(Activity.RESULT_OK, intentToReturn);
        finish()
    }

    private fun buildNpcObject(): Npc {
        val name = editTextName.text.toString()
        val characteristics = editTextCharacteristics.text.toString()
        val history = editTextHistory.text.toString()
        val sex = spinnerSex.selectedItem.toString()
        val race = spinnerRace.selectedItem.toString()
        return Npc(npcObject.id, name, characteristics, history, Sex.valueOf(sex), Race.valueOf(race))
    }

    private fun validateEditTextMandatory(editTexts: List<EditText>): Boolean {
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
    }

    private fun changeToViewMode() {
        editMode = false
        editTextName.isEnabled = false
        editTextCharacteristics.isEnabled = false
        editTextHistory.isEnabled = false
        spinnerSex.isEnabled = false
        spinnerRace.isEnabled = false
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
