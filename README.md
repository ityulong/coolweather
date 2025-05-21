# CoolWeather App

CoolWeather is a simple Android application that provides users with real-time weather information for different locations across China. It allows users to choose a specific county and view its current weather conditions, including temperature and a weather description.

## Core Functionality

*   **Location Selection:** Users can navigate through a list of provinces, cities, and finally counties within China to select their desired location.
*   **Weather Display:** Shows current weather conditions for the selected county, including temperature ranges (e.g., min/max) and a textual description of the weather (e.g., "Sunny," "Cloudy").
*   **Manual Refresh:** Users can manually trigger an update to fetch the latest weather data for the currently selected location.
*   **Automatic Updates:** The app includes a background service designed to automatically refresh weather information periodically (Note: The service is initially disabled in the manifest and started by `WeatherActivity`).

## Application Architecture

The CoolWeather app is structured as follows:

*   **UI Layer:**
    *   `ChoseAreActivity`: Handles the user interface for selecting provinces, cities, and counties.
    *   `WeatherActivity`: Displays the weather information for the chosen county.
*   **Data Layer:**
    *   `CoolWeatherDB` (SQLite Database): Stores lists of provinces, cities, and counties fetched from the weather service. This allows for quicker access and offline browsing of area data.
    *   `SharedPreferences`: Used to store the most recent weather data for the selected city and other simple application settings or preferences.
*   **Network Layer:**
    *   `HttpUtil`: A utility class responsible for making HTTP GET requests to the external weather service (`weather.com.cn`).
    *   `HttpCallBackListener`: An interface used to handle asynchronous responses (successful data retrieval or errors) from the HTTP requests.
*   **Data Parsing:**
    *   `Utility`: Contains methods (`hanldeProvinceMessage`, `hanldeCityMessage`, `hanldeCountyMessage`, `handleWeatherResponse`) to parse the XML-based responses received from the weather service into meaningful data objects.
*   **Background Services:**
    *   `AutoWeatherService`: A service intended to run in the background to periodically update the weather information. It is started by `WeatherActivity`.
    *   `MyReceiver`: A broadcast receiver, likely intended to work with `AutoWeatherService`, perhaps to start the service on boot or based on network changes (though its current manifest declaration has it disabled).

## Key Technologies

*   **Android SDK:** The core framework used for building the application.
*   **Java:** The programming language used for development.
*   **SQLite:** Used for local database storage of area information.
*   **XML:** Used for defining layouts and parsing data from the weather service.

## Building and Running

This project is a standard Android application built using Gradle.

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select "Open an existing Android Studio project."
    *   Navigate to the cloned directory and select it.
3.  **Build and Run:**
    *   Allow Android Studio to sync and build the project.
    *   Connect an Android device or start an emulator.
    *   Click the "Run" button (or Shift+F10).

**Note:** The application fetches data from `http://www.weather.com.cn`. Ensure the device/emulator has internet connectivity. The weather API endpoints used in the code might be outdated or no longer accessible.
