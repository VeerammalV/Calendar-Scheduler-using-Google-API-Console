package com.example.chatapp.chatscreen

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityConvoBinding
import com.example.chatapp.databinding.DialogAttachfilesBinding
import com.example.chatapp.databinding.DialogSendBinding
import com.example.chatapp.fonts.PTSansBold
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import android.Manifest
import android.app.Activity
import android.view.WindowManager
import android.widget.TimePicker
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.Locale


class ConvoActivity: AppCompatActivity() {

    private lateinit var binding: ActivityConvoBinding
    private lateinit var chatUserNameTextView: PTSansBold
    private lateinit var chatUserPicImageView: AppCompatImageView
    private var selectedHour = 0
    private var selectedMinute = 0
    private var startDateInMillis: Long = 0
    private var selectedDateTimeInMillis: Long = 0
    private lateinit var googleSignInClient: GoogleSignInClient
    private var googleSignInAccount: GoogleSignInAccount? = null
    private val CAMERA_REQUEST_CODE = 124
    private val GALLERY_REQUEST_CODE = 125
    private val PERMISSION_REQUEST_CODE = 123
    private val DOCUMENT_PICK_REQUEST_CODE = 1002


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityConvoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Username and profile picture
        chatUserNameTextView = findViewById(R.id.chat_username)
//        chatUserPicImageView = findViewById(R.id.chat_userpic)
//
        val userName = intent.getStringExtra("userName")
//        val profilePictureUri = intent.getStringExtra("profilePictureUri")
//
        Log.e("name", "$userName")
//        Log.e("pic", "$profilePictureUri")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        chatUserNameTextView.text = userName
//        profilePictureUri?.let {
//            Glide.with(this)
//                .load(profilePictureUri)
//                .placeholder(R.id.chat_userpic)
//                .into(chatUserPicImageView)
//        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            checkPermissions()
        }

        binding.attachments.setOnClickListener {
            attachFiles()
        }


        binding.camera.setOnClickListener {
            openCamera()
        }

        binding.send.setOnClickListener {
            var isInputEmpty = true
            if (binding.typeMessage.text.toString().trim().isEmpty()) {
                runOnUiThread {
                    binding.typeMessage.error = "Message cannot be empty"
                }
                isInputEmpty = true
            } else {
                isInputEmpty = false
                sendOrScheduleMessage()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
        private fun checkPermissions() {
            if (!permissionsGranted()) {
                requestPermissions()
            }
        }

        @RequiresApi(Build.VERSION_CODES.R)
        private fun requestPermissions() {
            val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun permissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                attachFiles()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun attachFiles() {
        val dialog = BottomSheetDialog(this)
        val dialogView = DialogAttachfilesBinding.inflate(layoutInflater)
        dialog.setContentView(dialogView.root)
        dialog.show()

        dialogView.documents.setOnClickListener {
            attachDocuments()
            dialog.dismiss()
        }
        dialogView.camera.setOnClickListener {
            openCamera()
            dialog.dismiss()
        }
        dialogView.photo.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }
    }

    private fun attachDocuments() {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(intent, DOCUMENT_PICK_REQUEST_CODE)
    }

    private fun openGallery() {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DOCUMENT_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedFileUri = data?.data
            //Code for sending the selected file
        }
    }


    // Send or Schedule Dialog
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendOrScheduleMessage() {
        val dialog = BottomSheetDialog(this)
        val dialogView = DialogSendBinding.inflate(layoutInflater)
        dialog.setContentView(dialogView.root)
        dialog.show()

        dialogView.textSendNow.setOnClickListener {
            sendMessageNow()
            dialog.dismiss()
        }
        dialogView.textScheduleLater.setOnClickListener {
            openCalendar()
            dialog.dismiss()
        }
    }

    // Send messages immediately
    private fun sendMessageNow() {
        TODO("Not yet implemented")
    }


    // Opens Android Calendar
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCalendar() {
        // Google Sign In
        initializeUserAccount()

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())


        val datePickerDialog = DatePickerDialog(
            this,  R.style.CustomDatePickerDialog,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance().apply{
                    set(selectedYear, selectedMonth, selectedDayOfMonth)
                }

//                val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
//                val selectedDateFormatted = "$selectedDayOfMonth/${selectedMonth+1}/$selectedYear"
//                Log.e("Selected Date:","Selected Date:$selectedDateFormatted")
//                val selectedDate = dateFormat.parse(selectedDateFormatted)
//                val selectedCalendar = Calendar.getInstance().apply {
//                    time = selectedDate
//                }
//                showTimePickerDialog(selectedCalendar)



                val today = Calendar.getInstance()
                if (selectedDate >= today) {
                    val selectedDateFormatted = "$selectedDayOfMonth/${selectedMonth+1}/$selectedYear"
                    Log.e("Selected Date:","Selected Date:$selectedDateFormatted")
                    startDateInMillis = selectedDate.timeInMillis
                    showTimePickerDialog()
                } else {
                    Toast.makeText(this, "Please select today's date or future date", Toast.LENGTH_SHORT).show()
                    openCalendar()
                }
            },
            year, month, dayOfMonth
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }


    // Opens Android Clock
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this, R.style.CustomTimePickerDialog,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }

                val today = Calendar.getInstance()
                if (startDateInMillis > 0 &&
                    (today.get(Calendar.YEAR) == selectedTime.get(Calendar.YEAR)) &&
                    (today.get(Calendar.DAY_OF_YEAR) == selectedTime.get(Calendar.DAY_OF_YEAR))
                ) {
                    selectedHour = hourOfDay
                    selectedMinute = minute
                    val selectedDateTime = Calendar.getInstance().apply {
                        timeInMillis = startDateInMillis
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                    }
                    selectedDateTimeInMillis = selectedDateTime.timeInMillis

                    Log.e("ConvoActivity", "Selected time: $selectedHour:$selectedMinute")

                    if (googleSignInAccount != null) {
                    fetchCalendarEvents(googleSignInAccount as GoogleSignInAccount)}

                } else {
                    Toast.makeText(this, "Selected time is in the past", Toast.LENGTH_SHORT).show()
                    showTimePickerDialog()
                }
            },
            currentHour,
            currentMinute,
            false
        )
        timePickerDialog.show()
    }


    // Login Google Account
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeUserAccount() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(CalendarScopes.CALENDAR))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (googleSignInAccount == null || googleSignInAccount?.isExpired == true){
            signIn()
        } else {
            Log.d("SignIn", "User account already initialized: $googleSignInAccount")
        }
    }

    // Inside handleSignInResult function
    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                Log.e("SignIn", "Signed in successfully")
                fetchCalendarEvents(account)
            } else {
                Log.e("SignIn", "User is not signed in")
            }        } catch(e: ApiException) {
            e.printStackTrace()
        }
    }


    // Login Dialog
    @RequiresApi(Build.VERSION_CODES.O)
    private val launcherLoginGoogle = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        } else {
            MaterialAlertDialogBuilder(this@ConvoActivity)
                .setTitle("Permission Required")
                .setMessage("Login with your Google Account")
                .setNegativeButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Login") { dialog, _ ->
                    dialog.dismiss()
                    signIn()
                }.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcherLoginGoogle.launch(signInIntent)
    }

    // Calendar Events
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchCalendarEvents(account: GoogleSignInAccount) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val transport = AndroidHttp.newCompatibleTransport()
                val jsonFactory: JacksonFactory = JacksonFactory.getDefaultInstance()
                val credential = GoogleAccountCredential.usingOAuth2(
                    this@ConvoActivity,
                    listOf(CalendarScopes.CALENDAR)
                )
                credential.selectedAccount = account.account

                val service = com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential )
                    .setApplicationName(getString(R.string.app_name))
                    .build()

                val event = com.google.api.services.calendar.model.Event()
                    .setSummary(binding.chatUsername.text.toString())
                    .setDescription(binding.typeMessage.text.toString())

//                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.getDefault())
                val startDateTime = DateTime(selectedDateTimeInMillis)
                val endDateTime = DateTime(selectedDateTimeInMillis)
                Log.e("time","$startDateTime, $endDateTime")

                event.start = EventDateTime().setDateTime(startDateTime)
                event.end = EventDateTime().setDateTime(endDateTime)

                Log.e("ConvoActivity", "Event Date & Time: $startDateTime")

                val createdEvent = service.events().insert("primary", event).execute()
                Log.e("Calendar Event", "Event created: ${createdEvent.htmlLink}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun convertToFormat(milliseconds: Long): DateTime {
        return DateTime(milliseconds)
    }

}

