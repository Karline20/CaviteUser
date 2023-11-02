package coding.legaspi.caviteuser.presentation.auth.profilecreation

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coding.legaspi.caviteuser.R
import coding.legaspi.caviteuser.data.model.profile.Profile
import coding.legaspi.caviteuser.databinding.ActivityProfileCreationBinding
import coding.legaspi.caviteuser.presentation.di.Injector
import coding.legaspi.caviteuser.presentation.home.HomeActivity
import coding.legaspi.caviteuser.presentation.terms.TermsActivity
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModel
import coding.legaspi.caviteuser.presentation.viewmodel.EventViewModelFactory
import coding.legaspi.caviteuser.utils.DialogHelper
import coding.legaspi.caviteuser.utils.DialogHelperFactory
import coding.legaspi.caviteuser.utils.FirebaseManager
import coding.legaspi.caviteuser.utils.VibrateView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class ProfileCreation : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    @Inject
    lateinit var factory: EventViewModelFactory
    private lateinit var loginViewModel: EventViewModel
    private lateinit var binding: ActivityProfileCreationBinding
    private lateinit var dialogHelper: DialogHelper
    val requiredPermissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    var isStorageImagePermitted = false
    var isCameraAccessPermitted = false

    val TAG = "Permission"

    lateinit var selectedImageUri: Uri
    lateinit var uriForCamera: Uri

    val data = listOf("Female", "Male")
    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var address: String
    private lateinit var age: String
    private lateinit var userid: String
    private lateinit var selectedOption: String
    private var timestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileCreationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        (application as Injector).createEventsSubComponent().inject(this)
        loginViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)
        dialogHelper = DialogHelperFactory.create(this)
        userid = intent.getStringExtra("userid").toString()

        binding.progressBar.visibility = View.GONE
        binding.saveBtn.setOnClickListener(this)
        binding.imgProfile.setOnClickListener(this)
        binding.etFirst.onFocusChangeListener = this
        binding.etlast.onFocusChangeListener = this
        binding.etaddress.onFocusChangeListener = this
        binding.etAge.onFocusChangeListener = this


        listentToSpinner()
    }

    override fun onStart() {
        super.onStart()
        if (!isStorageImagePermitted){
            requestPermissionStorageImages()
        }
    }

    override fun onClick(view: View?) {
        if(view != null){
            when(view.id){
                R.id.saveBtn -> {
                    submitForm()
                }
                R.id.imgProfile ->{
                    listenToCamera()
                }
            }
        }
    }

    private fun submitForm() {
        binding.progressBar.visibility = VISIBLE
        timestamp = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date(timestamp)
        val formattedDate = sdf.format(date)
        if (validate()){
            val responseLiveData = loginViewModel.postProfile(Profile(address, age, formattedDate, firstname,selectedOption, lastname,  timestamp.toString(), userid))
            responseLiveData.observe(this, Observer {
                if (it!=null){
                    Log.d("ProfileCreation", "$it")
                    if (it.body()?.userid!=null){
                        FirebaseManager().saveImageToFirebase(this, it.body()?.userid.toString(), selectedImageUri){
                            if (it){
                                val intent = Intent(this, TermsActivity::class.java)
                                intent.putExtra("userid", userid)
                                startActivity(intent)
                                binding.progressBar.visibility = View.GONE
                                finish()
                            }else{
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(this, "Check you internet connection!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }else{
                    binding.progressBar.visibility = View.GONE
                }
            })
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when (view.id) {
                R.id.etFirst -> {
                    if (hasFocus) {
                        if (binding.etFirstTil.isCounterEnabled) {
                            binding.etFirstTil.isCounterEnabled = false
                        }
                    } else {
                        validateName()
                    }
                }

                R.id.etlast -> {
                    if (hasFocus) {
                        if (binding.etlastTil.isCounterEnabled) {
                            binding.etlastTil.isCounterEnabled = false
                        }
                    } else {
                        validateLast()
                    }
                }
                R.id.etaddress -> {
                    if (hasFocus) {
                        if (binding.etaddressTil.isCounterEnabled) {
                            binding.etaddressTil.isCounterEnabled = false
                        }
                    } else {
                        validateAddress()
                    }
                }

                R.id.etAge -> {
                    if (hasFocus) {
                        if (binding.etAgeTil.isCounterEnabled) {
                            binding.etAgeTil.isCounterEnabled = false
                        }
                    } else {
                        validateAge()
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        if(event == KeyEvent.KEYCODE_ENTER && keyEvent!!.action == KeyEvent.ACTION_UP){
            submitForm()
        }
        return false
    }

    private fun listenToCamera() {
        binding.imgProfile.setOnClickListener {
            if (isCameraAccessPermitted){
                openCameraOrGallery()
            }else{
                requestPermissionCameraAccess()
            }
        }
    }

    private fun listentToSpinner() {

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter

        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedOption = data[position]
                Log.d("AddEventView", "$selectedOption")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("AddEventView", "0")
            }

        }
    }

    private fun validate(): Boolean{
        var isValid = true
        if (!validateName(shouldVibrate = false)) isValid = false
        if (!validateLast(shouldVibrate = false)) isValid = false
        if (!validateAddress(shouldVibrate = false)) isValid = false
        if (!validateAge(shouldVibrate = false)) isValid = false
        if (!isValid) VibrateView.vibrate(this, binding.rrlLogin)
        return isValid
    }

    private fun validateName(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var errorMessage: String? = null
        firstname = binding.etFirst.text.toString()
        if (firstname.isEmpty()) {
            errorMessage = "First name is required!"
        }
        if (errorMessage != null && shouldUpdateView) {
            binding.etFirstTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(this@ProfileCreation, this)
            }
        }
        return errorMessage == null
    }

    private fun validateLast(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var errorMessage: String? = null
        lastname = binding.etlast.text.toString()
        if (lastname.isEmpty()) {
            errorMessage = "Last name is required!"
        }
        if (errorMessage != null && shouldUpdateView) {
            binding.etlastTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(this@ProfileCreation, this)
            }
        }
        return errorMessage == null
    }

    private fun validateAddress(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var errorMessage: String? = null
        address = binding.etaddress.text.toString()
        if (address.isEmpty()) {
            errorMessage = "Address is required!"
        }
        if (errorMessage != null && shouldUpdateView) {
            binding.etaddressTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(this@ProfileCreation, this)
            }
        }
        return errorMessage == null
    }

    private fun validateAge(shouldUpdateView: Boolean = true, shouldVibrate: Boolean = true): Boolean {
        var errorMessage: String? = null
        age = binding.etAge.text.toString()
        if (age.isEmpty()) {
            errorMessage = "Age is required!"
        }
        if (errorMessage != null && shouldUpdateView) {
            binding.etAgeTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrate) VibrateView.vibrate(this@ProfileCreation, this)
            }
        }
        return errorMessage == null
    }



    fun requestPermissionStorageImages() {
        if (ContextCompat.checkSelfPermission(this, requiredPermissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "${requiredPermissions[0]} Granted")
            isStorageImagePermitted = true
            requestPermissionCameraAccess()
        } else {
            requestPermissionLauncherStorageImages.launch(requiredPermissions[0])
        }
    }

    private val requestPermissionLauncherStorageImages: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "${requiredPermissions[0]} Granted")
                isStorageImagePermitted = true
                requestPermissionCameraAccess()
            } else {
                Log.d(TAG, "${requiredPermissions[0]} Not Granted")
                isStorageImagePermitted = false
            }
        }

    private fun requestPermissionCameraAccess() {
        if (ContextCompat.checkSelfPermission(this, requiredPermissions[1]) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "${requiredPermissions[1]} Granted")
            isCameraAccessPermitted = true
        } else {
            requestPermissionLauncherCameraAccess.launch(requiredPermissions[1])
        }
    }

    private val requestPermissionLauncherCameraAccess: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "${requiredPermissions[1]} Granted")
            } else {
                Log.d(TAG, "${requiredPermissions[1]} Not Granted")
                isCameraAccessPermitted = false
            }
        }


    private val launcherForCamera: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = uriForCamera
                binding.imgProfile.setImageURI(uriForCamera)
            }
        }

    fun openCameraOrGallery() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
            .setItems(arrayOf("Open Camera", "Open Gallery")) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Legaspi")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Captured by Legaspi")
        uriForCamera = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForCamera)
        launcherForCamera.launch(cameraIntent)
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        pickImageActivityResultLauncher.launch(intent)
    }

    fun getPathFromURI(contentUri: Uri?): String? {
        var res: String? = null

        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentUri?.let { contentResolver.query(it, proj, null, null, null) }
        cursor?.use {
            if (it.moveToFirst()) {
                val column_index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                res = it.getString(column_index)
            }
        }

        return res
    }

    val pickImageActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selected: Uri? = data?.data

            val path = getPathFromURI(selected)
            if (path != null) {
                val file = File(path)
                val newSelectedImageUri = Uri.fromFile(file)
                selectedImageUri = newSelectedImageUri
                binding.imgProfile.setImageURI(newSelectedImageUri)
            }
        }
    }
}