package com.example.eventapi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tripgenie.R
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class EventItem(
    val name: String,
    val date: String,
    val venue: String,
    val imageUrl: String,
    val link: String
)

class EventsFragment : Fragment() {

    //  Replace with your Ticketmaster API key
    private val apiKey = "B2fuVCmAXBGRuuKlK6TYzZb35QIkY4tC"

    private val client = OkHttpClient()
    private lateinit var rvEvents: RecyclerView
    private lateinit var etCity: EditText
    private lateinit var btnSearch: MaterialButton
    private val eventList = mutableListOf<EventItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        etCity = view.findViewById(R.id.etCity)
        btnSearch = view.findViewById(R.id.btnSearchEvents)
        rvEvents = view.findViewById(R.id.rvEvents)
        rvEvents.layoutManager = LinearLayoutManager(requireContext())

        btnSearch.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a city name", Toast.LENGTH_SHORT).show()
            } else {
                fetchEvents(city)
            }
        }

        return view
    }

    private fun fetchEvents(city: String) {
        val url =
            "https://app.ticketmaster.com/discovery/v2/events.json?city=$city&apikey=$apiKey"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    val json = JSONObject(responseData)
                    val eventsArray = json.optJSONObject("_embedded")?.optJSONArray("events")

                    eventList.clear()
                    if (eventsArray != null && eventsArray.length() > 0) {
                        for (i in 0 until eventsArray.length()) {
                            val event = eventsArray.getJSONObject(i)
                            val name = event.optString("name", "Unnamed Event")
                            val date = event.optJSONObject("dates")
                                ?.optJSONObject("start")
                                ?.optString("localDate", "N/A")
                            val venue = event.optJSONObject("_embedded")
                                ?.optJSONArray("venues")
                                ?.optJSONObject(0)
                                ?.optString("name", "Unknown Venue") ?: "Unknown Venue"
                            val image = event.optJSONArray("images")
                                ?.optJSONObject(0)
                                ?.optString("url", "")
                            val link = event.optString("url", "")

                            eventList.add(EventItem(name, date ?: "N/A", venue, image ?: "", link))
                        }
                    }

                    withContext(Dispatchers.Main) {
                        if (eventList.isEmpty()) {
                            Toast.makeText(requireContext(), "No events found in $city", Toast.LENGTH_SHORT).show()
                        } else {
                            rvEvents.adapter = EventAdapter(eventList)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

class EventAdapter(private val events: List<EventItem>) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<android.widget.ImageView>(R.id.ivEventImage)
        val title = view.findViewById<android.widget.TextView>(R.id.tvEventTitle)
        val date = view.findViewById<android.widget.TextView>(R.id.tvEventDate)
        val venue = view.findViewById<android.widget.TextView>(R.id.tvEventVenue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = events[position]
        holder.title.text = item.name
        holder.date.text = "📅 ${item.date}"
        holder.venue.text = "📍 ${item.venue}"

        if (item.imageUrl.isNotEmpty()) {
            Picasso.get().load(item.imageUrl).into(holder.img)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = events.size
}
