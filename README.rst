=====================
Pokemon Search Engine
=====================

This is a JavaFX application that serves as a Pokemon search engine. It allows users to search for Pokemon either by their name or ID. The application retrieves information about the Pokemon from the PokeAPI, including the Pokemon's name, ID, image, and other relevant details.

Features
========
#. **Search:** Users can search for a Pokemon by entering its name or ID. The application will display the Pokemon's name, ID, image, and additional information.
#. **Pokemon Cards:** The application displays a maximum of 20 cards that the Pokemon is featured on. Users can navigate through these cards to view them all.
#. **Favorites:** Users can select certain cards as favorites and save them for later viewing. The application provides a "View" button that shows links to purchase the selected cards from two different sources.
#. **Save and Load:** Users can save their favorite cards to a database for future sessions. They can set a unique key to access their favorites and load them later.

APIs Used
=========
The application utilizes the following APIs:
#. **PokeAPI:** Used to retrieve basic information about the Pokemon, including their name, ID, image, and pokedex entries.
#. **PokeTCG API:** Used to fetch the cards that the Pokemon is featured on. The application displays a maximum of 20 cards for each Pokemon.
#. **Firebase Realtime Database:** Used to user information, including favorited Pokemon/Cards.

Firebase Integration
====================
Firebase is used to store and manage the user's favorite Pokemon cards. The application utilizes Firebase's Realtime Database for data storage. Users can save their favorite cards to the database by clicking the "Save" button. To access their favorites in future sessions, they need to enter the unique key they set and click the "Load" button.

What I Learned
==============
During the development of this project, I gained new knowledge and skills in the following areas:
#. **Firebase Realtime Database:** I learned how to utilize Firebase's Realtime Database to store and retrieve data without relying on the Firebase SDK. This was a new experience for me, as I had previously worked with Firestore but not the Realtime Database.
#. **JSON Parsing:** In this project, I learned how to properly parse JSON files. Instead of creating a custom JSON parser, I utilized the Gson library, which simplified the parsing process and improved reliability.
Overall, this project provided me with valuable hands-on experience in working with APIs, integrating Firebase for data storage, and enhancing my JSON parsing skills using Gson.

Installation
============
To run this JavaFX application, follow these steps:
#. Clone the repository: `https://github.com/krish-shar/cs1302-api`
#. Open the project in your prefered JavaIDE
#. Ensure you have the required dependancies, including the JavaFX library and Gson.
#. Build and run the project using your IDE's run configuration for JavaFX applications.

Or run the ``run.sh`` script

Thank you for reading, if you have any questions or concerns please reach out to 
Krishsharma2308@gmail.com
