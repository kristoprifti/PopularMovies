# PopularMovies
Project 1 and 2 of Google Associate Android Developer Nanodegree by Udacity

**Functionalities:**
- User is presented with a list of movies in a grid
- User can see details of a specific movie by clicking on the item in grid and redirected to Detail Activity
- In the detail activity screen user can mark a movie as his favorite or remove it from the favorites list (Favorite movies will be saved in an SQLite db in the users phone)
- User can filter movies in settings by Top Rated, Most popular or Favorite Movies
- Material Design techniques are applied throughout the app to provide a smooth and beautiful UI/UX for the user
- Fetched trailers from the database of TheMovieDB
- Fetched reviews from the database of TheMovieDB
- Implemented pagination to load more movies from the server each time the user reaches the bottom of recyclerview
- Implemented Database interaction for trailers and reviews
- Implemented Video Playback for trailers with Youtube Android API

**To-DO:**
- implement Master/Detail flow in the UI for tablets 

**Libraries Used:**
- android support libraries of android v25: Appcompat, Recyclerview, Preference, Cardview, Palette, Design
- OkHttp library for communication with the server
- Image loading and image caching library Picasso
- Field and method binding library ButterKnife
- Youtube Android API - library that plays trailer videos as a standalone player only if the user has Youtube App installed on his device
