package com.example.tripgenie

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class TripFragment : Fragment() {

    private lateinit var placeInput: EditText
    private lateinit var daysInput: EditText
    private lateinit var budgetInput: EditText
    private lateinit var travelersInput: EditText
    private lateinit var generateButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateView: View
    private lateinit var itineraryRecyclerView: RecyclerView
    private lateinit var resultHeaderCard: MaterialCardView
    private lateinit var tripSummaryTitle: TextView
    private lateinit var tripSummaryDetails: TextView
    private lateinit var adapter: ItineraryAdapter
    
    private val viewModel: TripViewModel by viewModels()
    private val itineraryList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip, container, false)

        placeInput = view.findViewById(R.id.placeInput)
        daysInput = view.findViewById(R.id.daysInput)
        budgetInput = view.findViewById(R.id.budgetInput)
        travelersInput = view.findViewById(R.id.travelersInput)
        generateButton = view.findViewById(R.id.generateButton)
        progressBar = view.findViewById(R.id.progressBar)
        emptyStateView = view.findViewById(R.id.emptyStateView)
        itineraryRecyclerView = view.findViewById(R.id.itineraryRecyclerView)
        resultHeaderCard = view.findViewById(R.id.resultHeaderCard)
        tripSummaryTitle = view.findViewById(R.id.tripSummaryTitle)
        tripSummaryDetails = view.findViewById(R.id.tripSummaryDetails)

        adapter = ItineraryAdapter(itineraryList)
        itineraryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itineraryRecyclerView.adapter = adapter

        observeViewModel()

        generateButton.setOnClickListener {
            val place = placeInput.text.toString().trim()
            val days = daysInput.text.toString().trim()
            val travelers = travelersInput.text.toString().trim()
            val budget = budgetInput.text.toString().trim()

            if (place.isEmpty() || days.isEmpty() || budget.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill mandatory fields", Toast.LENGTH_SHORT).show()
            } else if (!isNetworkAvailable()) {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            } else {
                updateSummaryHeader(place, days, budget)
                viewModel.generateTripPlan(
                    destination = place,
                    days = days,
                    budget = budget,
                    style = "Moderate",
                    interests = "Sightseeing, Food"
                )
            }
        }

        return view
    }

    private fun updateSummaryHeader(place: String, days: String, budget: String) {
        tripSummaryTitle.text = "Trip to $place"
        tripSummaryDetails.text = "$days Days | Moderate Style | Budget: ₹$budget"
    }

    private fun observeViewModel() {
        viewModel.itineraryList.observe(viewLifecycleOwner) { newList ->
            itineraryList.clear()
            itineraryList.addAll(newList)
            adapter.notifyDataSetChanged()
            
            val hasData = newList.isNotEmpty()
            itineraryRecyclerView.visibility = if (hasData) View.VISIBLE else View.GONE
            resultHeaderCard.visibility = if (hasData) View.VISIBLE else View.GONE
            emptyStateView.visibility = if (hasData) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            generateButton.isEnabled = !isLoading
            if (isLoading) {
                emptyStateView.visibility = View.GONE
                resultHeaderCard.visibility = View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
