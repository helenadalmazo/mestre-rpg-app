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

class NpcActivity : AppCompatActivity() {

    // padrão modo de edição = true, adicionar novo NPC
    var npcObject = Npc()

    var existsNPC = false
    var editMode = true

    lateinit var npcAction: Action

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

        npcAction = intent.getSerializableExtra(Extra.NPC_ACTION) as Action

        if (npcAction == Action.EDIT) {
            existsNPC = true
            editMode = false
            npcObject = intent.getSerializableExtra(Extra.NPC_OBJECT) as Npc

            editTextName.setText(npcObject.name, TextView.BufferType.EDITABLE)
            editTextCharacteristics.setText(npcObject.characteristics, TextView.BufferType.EDITABLE)
            editTextHistory.setText(npcObject.history, TextView.BufferType.EDITABLE)
            spinnerSex.setSelection(spinnerSexArrayAdapter.getPosition(npcObject.sex.toString()))
            spinnerRace.setSelection(spinnerRaceArrayAdapter.getPosition(npcObject.race.toString()))

            disableInput(editTextName)
            disableInput(editTextCharacteristics)
            disableInput(editTextHistory)
        }

//        val spinnerClass= findViewById<Spinner>(R.id.classs)
//        val spinnerClassArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Classs.values().map { classs -> classs.toString() })
//        spinnerClassArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerClass.adapter = spinnerClassArrayAdapter

//        findViewById<Button>(R.id.npc_cancel).setOnClickListener { view ->
//            finish()
//        }
    }

    private fun saveNpc() {
        if (!validateEditTextMandatory(listOf(editTextName, editTextCharacteristics, editTextHistory)) &&
            !validateSpinnerMandatory(listOf(spinnerSex, spinnerRace))) {
            return
        }

        val name = editTextName.text.toString()
        val characteristics = editTextCharacteristics.text.toString()
        val history = editTextHistory.text.toString()
        val sex = spinnerSex.selectedItem.toString()
        val race = spinnerRace.selectedItem.toString()

        npcObject = Npc(npcObject.id, name, characteristics, history, Sex.valueOf(sex), Race.valueOf(race))
//        npc.name = name
//        npc.characteristics = characteristics

        val intentToReturn = Intent().apply {
            putExtra(Extra.NPC_OBJECT, npcObject)
            putExtra(Extra.NPC_ACTION, npcAction)
        }
        setResult(Activity.RESULT_OK, intentToReturn);
        finish()
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

    private fun showAlertDialogDeleteNpc() {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Deletar NPC?")
        alertDialogBuilder.setMessage("Você tem certeza que deseja deletar o NPC?")

        alertDialogBuilder.setPositiveButton("Deletar") { _, _ -> deleteNpc() }
        alertDialogBuilder.setNegativeButton("Cancelar", null)

        alertDialogBuilder.create().show()
    }

    private fun validateSpinnerMandatory(spinners: List<Spinner>): Boolean {
        var valid = true
        spinners.forEach { spinner ->
            if (spinner.selectedItem != null) {
                (spinner.selectedView as TextView).error = "Campo obrigatório"
                valid = false
            }
        }
        return valid
    }

    private fun disableInput(textView: TextView) {
        textView.setCursorVisible(false)
        textView.setFocusableInTouchMode(false)
        textView.setEnabled(false)
    }

    private fun enableInput(textView: TextView) {
        textView.setCursorVisible(true)
        textView.setFocusableInTouchMode(true)
        textView.setEnabled(true)
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

        R.id.action_edit -> {
            editMode = true
            enableInput(editTextName)
            enableInput(editTextCharacteristics)
            enableInput(editTextHistory)
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
        if (existsNPC) {
            menu?.findItem(R.id.action_delete)?.setVisible(true)
        } else {
            menu?.findItem(R.id.action_delete)?.setVisible(false)
        }

        if (editMode) {
            menu?.findItem(R.id.action_edit)?.setVisible(false)
            menu?.findItem(R.id.action_save)?.setVisible(true)
        } else {
            menu?.findItem(R.id.action_edit)?.setVisible(true)
            menu?.findItem(R.id.action_save)?.setVisible(false)
        }
        return super.onPrepareOptionsMenu(menu)
    }
}
