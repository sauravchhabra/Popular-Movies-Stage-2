# Popular-Movies-Stage-2
Project for Android Developer Nanodegree at Udacity

Please enter your own API key in strings.xml to use the app. Visit https://www.themoviedb.org to get a new API key.


In this project the app connects to themovieDB API and parses the JSON received as a response.
The main screen of the app shows the poster to the user, on which the user can tap which then takes the user to DetailActivity screen.
The user can check the details in the DetailActivity of the movie poster that they have selected on the main screen. 
The app also works on small screen devices since it uses the ScrollView as the parent layout.
The app uses Room, ViewModel and LiveData to connect to the API and cache the results. Also the app saves the favourites list.
Implemented a favourite button so that the user can store the information in local database for that specific movie
The app also shows the trailers and reviews of the current movie selected from the list.
