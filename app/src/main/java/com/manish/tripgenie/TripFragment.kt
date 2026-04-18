package com.manish.tripgenie

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trip, container, false)

        // Initialize views
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

        setupRecyclerView()

        observeViewModel()

        generateButton.setOnClickListener {
            val place = placeInput.text.toString().trim()
            val days = daysInput.text.toString().trim()
            val budget = budgetInput.text.toString().trim()

            if (place.isEmpty() || days.isEmpty() || budget.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all mandatory fields", Toast.LENGTH_SHORT).show()
            } else if (!isNetworkAvailable()) {
                Toast.makeText(requireContext(), "No internet connection available", Toast.LENGTH_SHORT).show()
            } else {
                // Call the optimized ViewModel function
                viewModel.generateTripPlan(
                    destination = place,
                    days = days,
                    budget = budget,
                    style = "Moderate", // Example style
                    interests = "Sightseeing, Food" // Example interests
                )
            }
        }

        return view
    }

    private fun setupRecyclerView() {
        adapter = ItineraryAdapter()
        itineraryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        itineraryRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.itineraryList.observe(viewLifecycleOwner) { newList ->
            val hasData = newList.isNotEmpty()

            // Handle visibility
            emptyStateView.isVisible = !hasData
            resultHeaderCard.isVisible = hasData
            itineraryRecyclerView.isVisible = hasData

            if (hasData) {
                // First item is the summary header
                val summaryItem = newList.first()
                tripSummaryTitle.text = summaryItem.title
                tripSummaryDetails.text = summaryItem.details

                // The rest of the items are for the RecyclerView
                val itineraryItems = if (newList.size > 1) newList.subList(1, newList.size) else emptyList()
                adapter.submitList(itineraryItems)
            } else {
                adapter.submitList(emptyList()) // Clear the list when there's no data
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.isVisible = isLoading
            generateButton.isEnabled = !isLoading

            // Hide previous results while loading
            if (isLoading) {
                resultHeaderCard.isVisible = false
                itineraryRecyclerView.isVisible = false
                emptyStateView.isVisible = false // Hide empty state during load
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                // Show the empty state on error if the list is empty
                emptyStateView.isVisible = adapter.itemCount == 0
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
