package com.example.chatapp.chatroom.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Contact
import com.example.chatapp.R

interface OnItemClickListener {
    fun onItemClick(contact: Contact)
}

class ContactsAdapter(private val contactsLists: List<Contact>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactTextView: TextView = itemView.findViewById(R.id.profile_name)
//        val contactImageView: ImageView = itemView.findViewById(R.id.profile_picture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_list_item, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return contactsLists.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentItem = contactsLists[position]
        holder.contactTextView.text = currentItem.name
//        Glide.with(holder.itemView)
//            .load(currentItem.profilePictureUri)
//            .placeholder(R.drawable.baseline_person_24)
//            .into(holder.contactImageView)
        holder.itemView.setOnClickListener {
            listener.onItemClick(currentItem)
        }
    }
}

