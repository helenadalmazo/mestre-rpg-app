package com.dalmazo.helena.mestrerpg

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dalmazo.helena.mestrerpg.enum.Race
import com.dalmazo.helena.mestrerpg.enum.Sex
import com.dalmazo.helena.mestrerpg.model.Npc

class NpcActivity : AppCompatActivity() {

    val NPC_EXTRA = "NPC_EXTRA"
    val NPC_ACTION_EXTRA = "NPC_ACTION_EXTRA"
    val ADD_NPC_EXTRA = "ADD_NPC_EXTRA"
    val EDIT_NPC_EXTRA = "EDIT_NPC_EXTRA"
    val DELETE_NPC_EXTRA = "DELETE_NPC_EXTRA"

    var existsNPC = false
    var editMode = true

    lateinit var npc: Npc

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

        if (intent.extras?.getSerializable(NPC_EXTRA) != null) {
            npc = intent.extras.getSerializable(NPC_EXTRA) as Npc
            existsNPC = true
            editMode = false

            editTextName.setText(npc.name, TextView.BufferType.EDITABLE)
            editTextCharacteristics.setText(npc.characteristics, TextView.BufferType.EDITABLE)
            editTextHistory.setText(npc.history, TextView.BufferType.EDITABLE)
            spinnerSex.setSelection(spinnerSexArrayAdapter.getPosition(npc.sex.toString()))
            spinnerRace.setSelection(spinnerRaceArrayAdapter.getPosition(npc.race.toString()))

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

        npc = Npc(Math.random().toLong(),name, characteristics, history, Sex.valueOf(sex), Race.valueOf(race))

        val intentToReturn = Intent().apply {
            putExtra(NPC_EXTRA, npc)
            if (existsNPC) {
                putExtra(NPC_ACTION_EXTRA, EDIT_NPC_EXTRA)
            } else {
                putExtra(NPC_ACTION_EXTRA, ADD_NPC_EXTRA)
            }
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
            putExtra(NPC_EXTRA, npc)
            putExtra(NPC_ACTION_EXTRA, DELETE_NPC_EXTRA)
        }
        this@NpcActivity.setResult(Activity.RESULT_OK, intentToReturn);
        this@NpcActivity.finish()
    }

    private fun showDialogDeleteNpc() {

        val dialogDeleteBuilder = AlertDialog.Builder(this)

        dialogDeleteBuilder.setTitle("Deletar NPC?")
        dialogDeleteBuilder.setMessage("Você tme certeza que deseja deletar o NPC?")


        dialogDeleteBuilder.setPositiveButton("DELETAR"){dialog, which ->
            deleteNpc()
        }

        dialogDeleteBuilder.setNegativeButton("Cancelar"){dialog,which ->

        }

        val dialog: AlertDialog = dialogDeleteBuilder.create()

        dialog.show()
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
            showDialogDeleteNpc()
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
