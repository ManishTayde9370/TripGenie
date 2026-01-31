# TripGenie – Smart Travel Companion App

TripGenie is an Android-based smart travel application designed to make trip planning **simpler, smarter, and safer**. The app combines **AI-powered trip planning**, **real-time flight and hotel comparison**, **local events discovery**, **maps integration**, and **safety awareness** into a single platform.

This project is developed as an academic and practical implementation of modern Android development concepts using real-world APIs.

---

## 🚀 Key Features

### 🤖 AI Trip Planner
- Users enter destination, travel dates, and preferences
- AI generates a **day-wise itinerary** including sightseeing, food suggestions, and travel tips
- Focus on automation and personalization

### ✈️ Flight Price Comparison
- Compares **near real-time flight prices** from multiple airlines
- Supports **Indian and international tourist cities**
- Prices displayed in **Indian Rupees (₹)**
- Users search using **city names** (not IATA codes)
- Flights sorted by **cheapest**, **fastest**, and **best value**
- Tap on a flight to view **detailed flight information**

### 🏨 Hotel Search & Comparison
- Search hotels by **city and selected dates**
- View **price per night**, **total cost**, and **availability**
- Hotel details include:
  - Hotel name & full address
  - Room type
  - Meal inclusion (breakfast included or not)
  - Cancellation details (if available)
- Hotel and room images shown when available (fallback placeholders used otherwise)

### 🗺️ Map Integration
- Interactive map view for:
  - Hotels
  - Events
  - Tourist locations
- Users can visually explore locations using map markers
- Enhances location-based travel planning

### 🎉 Events Discovery
- Discover local events such as cultural programs, expos, and conferences
- Supports **Indian local events** and international listings
- Events shown with date, venue, and location

### 🛡️ Safety Module
- Displays basic safety awareness for selected cities
- Designed for future integration with official safety data sources

### 👤 User Profile & Session Management
- Login & signup with validation
- Personalized greetings on home screen
- Session handled locally
- Logout clears session securely

---

## 🧱 Tech Stack

| Category | Technology |
|-------|-----------|
| Platform | Android (Kotlin) |
| UI | XML / Material Design |
| Architecture | Fragment-based navigation |
| Networking | OkHttp |
| Async | Kotlin Coroutines |
| AI | Google Gemini API |
| Flights & Hotels | Amadeus APIs |
| Events | PredictHQ / Ticketmaster |
| Maps | Google Maps / OpenStreetMap |
| Storage | SharedPreferences |

---

## 🧠 Key Design Decisions

- City names are used in UI instead of technical codes for better user experience
- Prices are localized to INR for Indian users
- Fallback handling for missing data (events, images)
- Clean separation of API, business logic, and UI layers

---

## 📌 Limitations
- Prices are **indicative** and subject to change
- Booking and payment are **not implemented**
- Some APIs have free-tier limitations
- Hotel images may not be available for all properties

---

## 🔮 Future Enhancements
- Firebase authentication
- Save trips & favorites
- Push notifications for events and deals
- Offline itinerary access
- Navigation and directions
- Advanced safety alerts

---

## 🎓 Academic Note

This project is developed for educational purposes and demonstrates:
- Real-world API integration
- Clean Android architecture
- User-centric design
- Practical problem-solving in travel technology

---

## 🏁 Conclusion

TripGenie is a comprehensive smart travel companion that demonstrates how AI, travel APIs, and modern Android development can be combined to deliver a rich, user-friendly travel planning experience.

