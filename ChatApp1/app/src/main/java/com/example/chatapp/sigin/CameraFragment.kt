import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.chatapp.R
import com.example.chatapp.chatroom.ChatActivity
import com.example.chatapp.databinding.FragmentCameraBinding
import com.example.chatapp.sigin.SharedPreferencesHelper
import com.example.chatapp.sigin.SigninActivity
import java.io.ByteArrayOutputStream

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    private val PERMISSION_REQUEST_CODE = 123
    private val CAMERA_REQUEST_CODE = 124
    private val GALLERY_REQUEST_CODE = 125

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        if (sharedPreferencesHelper.isLoggedIn()) {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraCard.visibility = View.VISIBLE
        binding.profileSet.visibility = View.GONE
        binding.greenTick.visibility = View.GONE
        binding.textSuccessful.visibility = View.GONE

        binding.next.setOnClickListener {
            val userName = binding.userName.text.toString()
            if (userName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show()
            } else {
                val imageBitmap = binding.loadedImage.drawable?.toBitmap()
                profileSetup(userName, imageBitmap)
            }
        }

        binding.addPhoto.setOnClickListener {
            if (arePermissionsGranted()) {
                showImagePickerDialog()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun profileSetup(userName: String, imageBitmap: Bitmap?) {
        val sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userName", userName)
        editor.apply()

        if (imageBitmap != null) {
            saveImageToPreferences(imageBitmap, requireContext())
        }

        binding.profileSet.visibility = View.VISIBLE
        binding.greenTick.visibility = View.VISIBLE
        binding.textSuccessful.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.profileSet.visibility = View.GONE
            binding.greenTick.visibility = View.GONE
            binding.textSuccessful.visibility = View.GONE

            parentFragmentManager.beginTransaction()
                .remove(parentFragmentManager.findFragmentById(R.id.otp_fragment)!!)
                .commitAllowingStateLoss()

            val intent = Intent(requireContext(), com.example.chatapp.chatroom.ChatActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }, 1000)

        sharedPreferencesHelper.setLoggedIn(true)

    }

    private fun saveImageToPreferences(imageBitmap: Bitmap, context: Context) {
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val byteArray = baos.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            val sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("userImage", base64Image)
            editor.apply()
        Log.e("profilepic",base64Image)
    }

    private fun showImagePickerDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_image_picker, null)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val cameraIcon = dialogView.findViewById<View>(R.id.cameraContainer)
        val galleryIcon = dialogView.findViewById<View>(R.id.galleryContainer)

        cameraIcon.setOnClickListener {
            openCamera()
            dialog.dismiss()
        }

        galleryIcon.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                showImagePickerDialog()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    displayImage(imageBitmap)
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri: Uri? = data?.data
                    val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri!!)
                    val imageBitmap = BitmapFactory.decodeStream(inputStream)
                    displayImage(imageBitmap)
                }
            }
        }
    }

    private fun displayImage(imageBitmap: Bitmap) {
        binding.loadedImage.visibility = View.VISIBLE
        Glide.with(this)
            .load(imageBitmap)
            .transform(CenterCrop(), CircleCrop())
            .into(binding.loadedImage)
    }
}
