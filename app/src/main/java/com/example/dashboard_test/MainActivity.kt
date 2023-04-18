package com.example.dashboard_test

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dashboard_test.Retrofit.retrofit
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private val smsList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        val inflater = layoutInflater
        val actionBarView = inflater.inflate(R.layout.action_bar_layout, null)

        // Set the custom layout as the ActionBar view
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            customView = actionBarView
        }

        // Find and set click listener for the action button
        val actionButton = actionBarView.findViewById<Button>(R.id.action_button)
        actionButton.setOnClickListener {
            // Perform the action to lead to the fragment
        }
        */


        val smsListView = findViewById<ListView>(R.id.sms_list_view)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)

        //ListView adapter
        adapter = ArrayAdapter(this, R.layout.list_item, smsList)
        smsListView.adapter = adapter

        //Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            readSmsMessages()
            swipeRefreshLayout.isRefreshing = false
        }

        // Check for READ_SMS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_SMS), 1)
        } else {
            readSmsMessages()
        }
    }

    private fun readSmsMessages() {
        // Read SMS messages, Telephony Parameters
        val cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
            null,
            null,
            Telephony.Sms.DATE + " DESC LIMIT 10" // Add a limit
        )


        val smsListView = findViewById<ListView>(R.id.sms_list_view)

        cursor?.let {
            smsList.clear()

            val messages = mutableListOf<messageData>()

            while (it.moveToNext()) {
                val address = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val date = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val formattedDate = SimpleDateFormat(
                    "MMMM dd, yyyy | HH:mm",
                    Locale.getDefault()
                ).format(Date(date))

                val message = messageData(address, body, formattedDate)
                messages.add(message)

                // Inflate custom layout for each item
                val listItemView = layoutInflater.inflate(R.layout.list_item, null)
                val addressTextView = listItemView.findViewById<TextView>(R.id.address_textview)
                val dateTextView = listItemView.findViewById<TextView>(R.id.date_textview)
                val bodyTextView = listItemView.findViewById<TextView>(R.id.body_textview)

                // Set values for the TextViews
                addressTextView.text = address
                dateTextView.text = formattedDate
                bodyTextView.text = body

                // Add the custom layout view to the ListView
                smsListView.addHeaderView(listItemView)
            }

            it.close()
            adapter.notifyDataSetChanged()

            val API = retrofit.create(API::class.java)
            val call = API.sendMessages(messages)
            call.enqueue(object : Callback<Response<ResponseBody>> {
                override fun onResponse(call: Call<Response<ResponseBody>>, response: Response<Response<ResponseBody>>) {
                    if (response.isSuccessful) {
                        val listItemView = layoutInflater.inflate(R.layout.list_item, null)
                        listItemView.setBackgroundResource(R.drawable.border_green)
                    } else {
                        val listItemView = layoutInflater.inflate(R.layout.list_item, null)
                        listItemView.setBackgroundResource(R.drawable.border_red)
                    }
                }
                override fun onFailure(call: Call<Response<ResponseBody>>, t: Throwable) {
                    val listItemView = layoutInflater.inflate(R.layout.list_item, null)
                    listItemView.setBackgroundResource(R.drawable.border_red)
                }
            })
        }
    }

}



