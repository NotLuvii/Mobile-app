package com.example.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import java.io.ByteArrayOutputStream
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import jxl.Workbook
import jxl.write.WritableWorkbook
import jxl.write.WritableSheet
import jxl.write.Label
import java.io.IOException
import android.os.Environment
import android.view.MenuItem
import java.text.SimpleDateFormat
import androidx.appcompat.widget.PopupMenu
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val imagesList = mutableListOf<Uri>()
    private lateinit var photoURI: Uri
    private var selectedButtonText: String? = null
    val selectedColor = Color.parseColor("#FCD066")
    val unselectedColor = Color.parseColor("#DEDEDE")
    private var selectedCrButton: Button? = null
    var selectedCrText = ""
    var userEmail: String? = null
    var userName: String? = null

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 2
    private val REQUEST_STORAGE_PERMISSION = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("com.example.app", MODE_PRIVATE)

        // Check if email is configured

        setContentView(R.layout.activity_main)
        userEmail = intent.getStringExtra("USER_EMAIL")
        userName = intent.getStringExtra("USER_NAME")

        sharedPreferences = getSharedPreferences("com.example.app", MODE_PRIVATE)

        println("Selected Color: $selectedColor")
        println("Selected Color: $unselectedColor")

        val salesRepText = findViewById<TextView>(R.id.salesRepText)

        salesRepText.setText(userName)

        val clientEditText = findViewById<EditText>(R.id.clientEditText)
        val partEditText = findViewById<EditText>(R.id.partEditText)

        val materialEditText = findViewById<EditText>(R.id.materialEditText)

        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)

        val digitizationButton = findViewById<Button>(R.id.digitizationButton)
        val changeMaterialButton = findViewById<Button>(R.id.changeMaterialButton)
        val repeatOrderButton = findViewById<Button>(R.id.repeatOrderButton)
        val drawingButton = findViewById<Button>(R.id.drawingButton)
        val stpButton = findViewById<Button>(R.id.stpButton)
        val stlButton = findViewById<Button>(R.id.stlButton)
        val othEditText = findViewById<EditText>(R.id.othEditText)
        val machiningButton = findViewById<Button>(R.id.machiningButton)
        val balancingButton = findViewById<Button>(R.id.balancingButton)
        val cr1Button = findViewById<Button>(R.id.cr1Button)
        val cr2Button = findViewById<Button>(R.id.cr2Button)
        val cr3Button = findViewById<Button>(R.id.cr3Button)
        val assemblyButton = findViewById<Button>(R.id.assemblyButton)

        val clearButton = findViewById<Button>(R.id.clearButton)

        val photoButton = findViewById<Button>(R.id.photoButton)
        val submitButton = findViewById<Button>(R.id.submitButton)


        val singleSelectButtons = listOf(cr1Button, cr2Button, cr3Button)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }

        // Check if the write external storage permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
        }


        singleSelectButtons.forEach { button ->
            button.setBackgroundColor(unselectedColor)
            button.setOnClickListener {
                if (selectedCrButton == button) {
                    // If the selected button is pressed again, unselect it
                    updateButtonState(button, false)
                    selectedCrButton = null
                    selectedCrText = ""
                } else {
                    // Select the new button and unselect the previous one
                    selectedCrButton?.let { updateButtonState(it, false) }
                    selectedCrButton = button
                    selectedCrText = button.text.toString() // Store the selected button text
                    updateButtonState(button, true)
                }
            }
        }


        val categories = resources.getStringArray(R.array.category_options)
        val cadFiles = resources.getStringArray(R.array.cad_file_options)
        val materials = resources.getStringArray(R.array.material_options)
        val tolerances = resources.getStringArray(R.array.tolerances_options)
        val coatingMaterials = resources.getStringArray(R.array.coating_material_options)
        val criticalities = resources.getStringArray(R.array.criticality_options)

        val multipleSelectButtons = listOf(
            digitizationButton, changeMaterialButton, repeatOrderButton,
            drawingButton, stpButton, stlButton,
            machiningButton, balancingButton, assemblyButton
        )

        multipleSelectButtons.forEach { button ->
            button.setBackgroundColor(unselectedColor)
            button.setOnClickListener {
                button.isSelected = !button.isSelected
                updateButtonState(button, button.isSelected)
            }
        }

        clearButton.setOnClickListener {
            clearFields()
        }

        photoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        submitButton.setOnClickListener {
                saveFormData()
        }

        val menuButton: Button = findViewById(R.id.menuButton)
        menuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // Handle Home action
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.menu_configure_email -> {
                    // Handle Configure Email action
                    val intent = Intent(this, LoginActivityNoPref::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow in your app.
                } else {
                    // Permission denied. Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
                }
                return
            }
            REQUEST_STORAGE_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow in your app.
                } else {
                    // Permission denied. Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Storage permission is required to save pictures", Toast.LENGTH_SHORT).show()
                }
                return
            }
            // Add other 'when' lines to check for other permissions this app might request.
        }
    }

    private fun updateButtonState(button: Button, isSelected: Boolean) {
        if(isSelected) {
            button.setBackgroundColor(selectedColor)
            println("ButtonColor " +  "Button ${button.text} background color: $selectedColor")
        } else {
            button.setBackgroundColor(unselectedColor)
            println("ButtonColor" + " Button ${button.text} background color: $unselectedColor")
        }
        button.isSelected = isSelected
    }

    private fun updateButtonStates(selectedButton: Button) {
        val buttons = listOf(findViewById<Button>(R.id.cr1Button), findViewById<Button>(R.id.cr2Button), findViewById<Button>(R.id.cr3Button))

        buttons.forEach { button ->
            button.isSelected = button == selectedButton
            button.setBackgroundColor(if (button.isSelected) selectedColor else unselectedColor)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                val photoFile: File? = createImageFile()
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(this,
                        "com.example.app.fileprovider",
                        it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imagesList.add(photoURI)
        }
    }

    private fun clearFields()
    {
        findViewById<EditText>(R.id.clientEditText).setText("")
        findViewById<EditText>(R.id.partEditText).setText("")
        findViewById<EditText>(R.id.materialEditText).setText("")
        findViewById<EditText>(R.id.annualDemandEditText).setText("")
        findViewById<EditText>(R.id.balancingEditText).setText("")
        findViewById<EditText>(R.id.othEditText).setText("")
        findViewById<EditText>(R.id.additionalTestingEditText).setText("")
        findViewById<EditText>(R.id.operatingConditionsEditText).setText("")
        findViewById<EditText>(R.id.mediumEditText).setText("")
        findViewById<EditText>(R.id.coatingEditText).setText("")
        findViewById<EditText>(R.id.deliveryScopeEditText).setText("")

        val buttonsToUnselect = listOf<Button>(
            findViewById(R.id.digitizationButton),
            findViewById(R.id.changeMaterialButton),
            findViewById(R.id.repeatOrderButton),
            findViewById(R.id.drawingButton),
            findViewById(R.id.stpButton),
            findViewById(R.id.stlButton),
            findViewById(R.id.machiningButton),
            findViewById(R.id.balancingButton),
            findViewById(R.id.assemblyButton),
            findViewById(R.id.cr1Button),
            findViewById(R.id.cr2Button),
            findViewById(R.id.cr3Button)
        )

        // Unselect all buttons
        buttonsToUnselect.forEach { button ->
            updateButtonState(button, false)
        }

        // Reset the selectedCrButton and selectedCrText
        selectedCrButton = null
        selectedCrText = ""


    }

    private fun showUnansweredQuestionsDialog() {
        val unansweredQuestions = StringBuilder("The following questions are unanswered:\n")

        if (findViewById<EditText>(R.id.clientEditText).text.isEmpty()) {
            unansweredQuestions.append("- Client Name\n")
        }
        if (findViewById<EditText>(R.id.partEditText).text.isEmpty()) {
            unansweredQuestions.append("- Part Name\n")
        }
        if (findViewById<EditText>(R.id.emailEditText).text.isEmpty()) {
            unansweredQuestions.append("- Email\n")
        }



        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(unansweredQuestions.toString())
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                saveFormData()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Unanswered Questions")
        alert.show()
    }

    private fun saveFormData() {
        // Gather input data
        val email: String = userEmail ?: ""
        println(email)
        val clientName = findViewById<EditText>(R.id.clientEditText).text.toString()
        val partName = findViewById<EditText>(R.id.partEditText).text.toString()
        val materialName = findViewById<EditText>(R.id.materialEditText).text.toString()
        val description = findViewById<EditText>(R.id.descriptionEditText).text.toString()
        var digitization = ""
        var materialChange = ""
        var repeatOrder = ""
        var drawings = ""
        var stp = ""
        var stl = ""
        var oth = ""
        var machining = ""
        var balancing = ""

        var assembly = ""

        var annualDemand = ""
        var operatingConditions = ""
        var medium = ""
        var coating = ""
        var additionalTesting = ""
        var delivery = ""

        if(findViewById<EditText>(R.id.annualDemandEditText).text.isEmpty()){annualDemand = "Not Entered"} else {annualDemand = findViewById<EditText>(R.id.annualDemandEditText).text.toString()}
        if(findViewById<EditText>(R.id.operatingConditionsEditText).text.isEmpty()){operatingConditions = "Not Entered"} else {operatingConditions = findViewById<EditText>(R.id.operatingConditionsEditText).text.toString()}
        if(findViewById<EditText>(R.id.mediumEditText).text.isEmpty()){medium = "Not Entered"} else {medium = findViewById<EditText>(R.id.mediumEditText).text.toString()}
        if(findViewById<EditText>(R.id.coatingEditText).text.isEmpty()){coating = "Not Entered"} else {coating = findViewById<EditText>(R.id.coatingEditText).text.toString()}
        if(findViewById<EditText>(R.id.additionalTestingEditText).text.isEmpty()){additionalTesting = "Not Entered"} else {additionalTesting = findViewById<EditText>(R.id.additionalTestingEditText).text.toString()}
        if(findViewById<EditText>(R.id.deliveryScopeEditText).text.isEmpty()){delivery = "Not Entered"} else {delivery = findViewById<EditText>(R.id.deliveryScopeEditText).text.toString()}
        if(findViewById<EditText>(R.id.balancingEditText).text.isEmpty()){balancing = "Not Entered"} else {balancing = findViewById<EditText>(R.id.balancingEditText).text.toString()}
        if(findViewById<EditText>(R.id.othEditText).text.isEmpty()){balancing = "Not Entered"} else {balancing = findViewById<EditText>(R.id.othEditText).text.toString()}

        if(findViewById<Button>(R.id.digitizationButton).isSelected) {digitization = "Yes"}else{digitization = "No"}
        if(findViewById<Button>(R.id.changeMaterialButton).isSelected) {materialChange = "Yes"}else{materialChange  = "No"}
        if(findViewById<Button>(R.id.repeatOrderButton).isSelected) {repeatOrder = "Yes"}else{repeatOrder  = "No"}
        if(findViewById<Button>(R.id.drawingButton).isSelected) {drawings = "Yes"}else{drawings  = "No"}

        if(findViewById<Button>(R.id.stpButton).isSelected) {stp = "Yes"}else{stp  = "No"}
        if(findViewById<Button>(R.id.stlButton).isSelected) {stl = "Yes"}else{stl  = "No"}

        if(findViewById<Button>(R.id.machiningButton).isSelected) {machining = "Yes"}else{machining  = "No"}
        if(findViewById<Button>(R.id.balancingButton).isSelected) {balancing = "Yes"}else{balancing  = "No"}
        if(findViewById<Button>(R.id.assemblyButton).isSelected) {assembly = "Yes"}else{assembly  = "No"}
        // Create email body
        val emailBody = """
            Client Name: $clientName
            Part Name: $partName
            Material Name: $materialName

            Description: $description
            Digitization: $digitization
            Material Change: $materialChange
            Repeat Order: $repeatOrder
            2D Drawings: $drawings

            STP: $stp
            STL: $stl
            Others: $oth

            Machining: $machining
            Balancing: $balancing
            Criticality: $selectedCrText
            Assembly: $assembly

            Annual Demand: $annualDemand
            Operating Conditions: $operatingConditions
            Medium: $medium
            Coating: $coating
            Additional Testings: $additionalTesting
            Delivery Scope: $delivery

        """.trimIndent()

        // Send email
        sendEmail(email, "$clientName - $partName", emailBody, imagesList)
    }

    private fun buildString(initialCapacity: Int = 16, builderAction: StringBuilder.() -> Unit): String {
        val sb = StringBuilder(initialCapacity)
        sb.builderAction()
        return sb.toString()
    }

    private fun StringBuilder.appendIfNotEmpty(label: String, value: String) {
        if (value.isNotEmpty()) {
            append("$label: $value\n")
        }
    }

    private fun sendEmail(to: String, subject: String, body: String, imageUris: List<Uri>) {
        if(findViewById<EditText>(R.id.clientEditText).text.toString().isNotEmpty() &&
           findViewById<EditText>(R.id.partEditText).text.toString().isNotEmpty() &&
           findViewById<EditText>(R.id.materialEditText).text.toString().isNotEmpty())
        {
            val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(imageUris))
            }
            try {
                startActivity(Intent.createChooser(emailIntent, "Send email..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(this@MainActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            if(findViewById<EditText>(R.id.clientEditText).text.toString().isEmpty())
            {
                findViewById<EditText>(R.id.clientEditText).error = "Client name is required"
            }
            if(findViewById<EditText>(R.id.partEditText).text.toString().isEmpty())
            {
                findViewById<EditText>(R.id.partEditText).error = "Part name is required"
            }
            if(findViewById<EditText>(R.id.materialEditText).text.toString().isEmpty())
            {
                findViewById<EditText>(R.id.materialEditText).error = "Material name is required"
            }
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
