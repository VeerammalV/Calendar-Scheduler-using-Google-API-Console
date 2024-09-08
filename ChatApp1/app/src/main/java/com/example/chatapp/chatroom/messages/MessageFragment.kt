package com.example.chatapp.chatroom.messages

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Contact
import com.example.chatapp.chatscreen.ConvoActivity
import com.example.chatapp.databinding.FragmentMessageBinding

class MessageFragment : Fragment() {

    private lateinit var binding: FragmentMessageBinding
    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter
    private val REQUEST_CONTACTS_PERMISSION = 1001
    private var contactsList = mutableListOf<Contact>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED) {
            displayContacts()
        } else {
            requestContactsPermission()
        }
    }

    private fun requestContactsPermission() {
        ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CONTACTS_PERMISSION
            )
    }

    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun displayContacts() {
        contactsList = mutableListOf<Contact>()
            val cursor = requireContext().contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null
            )
            cursor?.use {
                while (it.moveToNext()) {
                    val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val contact = Contact(name, number)
                    contactsList.add(contact)
//                    Log.e("Name",name)
//                    Log.e("Number",number)
                }
            }
        contactsAdapter = ContactsAdapter(contactsList, object : OnItemClickListener {
            override fun onItemClick(contact: Contact) {
                val intent = Intent(requireContext(), ConvoActivity::class.java)
                intent.putExtra("userName", contact.name)
                startActivity(intent)
            }
        })
        binding.recyclerView.adapter = contactsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


//    contactsAdapter = ContactsAdapter(contactsList)
//    binding.recyclerView.adapter = contactsAdapter
//    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayContacts()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    override fun onItemClick(contactName: String, object : OnItemClickListener) {
//        val intent = Intent(requireContext(), ConvoActivity::class.java)
//        intent.putExtra("contactName", contactName)
//        startActivity(intent)
//    }

//    val intent = Intent(requireContext(), ConvoActivity::class.java)
//    startActivity(intent)
//    requireActivity().finish()
//    }, 1000)

}