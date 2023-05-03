# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice looking HTML.

## Part 1: App Description

> Please provide a firendly description of your app, including the
> the primary functions available to users of the app. Be sure to
> describe exactly what APIs you are using and how they are connected
> in a meaningful way.

> **Also, include the GitHub `https` URL to your repository.**

This app is a Pokemon search engine.
It allows users to search for Pokémon by name or by ID.
It shows the user the Pokémon's name, ID, image, as well as some other information about the pokemon.
The app uses the PokeAPI to get the information about the Pokémon.
Aside from basic information, the app also shows a max of 20 cards that the Pokémon is featured on.
The users can tap through these cards and view all of them. They then can choose some to favorite and view them later.
Looking at what they have favorited, the user can click on the "View" button to see links to buy the card from two different sources.
If the user wants to save their favorites for another session, they can click the "Save" button. Which allows them to save their favorites to a database.
The user sets a unique key they decide which is what lets them access their favorites later.
They can load their favorites by entering the key they set and clicking the "Load" button.
This app uses the PokeAPI to get the basic information regarding the Pokemon as well as their pokedex entries.
It also uses the PokeTCG API to get the cards that the Pokémon is featured on.
And finally, the app uses Firebase to store the user's favorites.
<br /> This is the GitHub repository URL: https://github.com/krish-shar/cs1302-api 

## Part 2: New

> What is something new and/or exciting that you learned from working
> on this project?

Something new I learned from working on this project is how to use Firebase's Realtime Database.
Though I had used the Firestore database before, I had never used the Realtime Database.
I used it to store and retrieve data without using the Firebase SDK, which I had used before
in a previous project.
I also learned how to properly parse a JSON file. Before this, I had written a JSON parser, but it was not very good and was very finicky.
This time, I used the Gson library to parse the JSON file, which was much easier and more reliable.


## Part 3: Retrospect

> If you could start the project over from scratch, what do
> you think might do differently and why?

Something I would do differently is format my code a bit better. 
It would make my code more readable and easier to understand.
Instead of putting everything in one method, it would make my code considerably more modular as well, which helps with debugging and testing.