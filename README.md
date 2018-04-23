# tictactoe

An interactive tic-tac-toe game written in clojurescript, based on Timothy Pratley's screencast: http://www.youtube.com/watch?v=pIiOgTwjbes

## Overview

This is a fairly unpolished follow-along with a clojurescript tutorial, focusing on the language and dev environment. Uses the `reagent` library for page rendering and state management. Uses `figwheel` for a hot-reload dev env, preserving state between reloads. (This feature would be GREAT for larger projects based on, say, sequential forms being filled out.)

## Future features

- The tutorial gives a minimal implementation of the computer's 'AI' - it just makes random available moves until someone wins. An obvious next step is to put together a simple rule-based opponent AI given that tic-tac-toe has a very small space of potential games.

- Put together build system and upload the 'production' version to a github page for public enjoyment.

- Improve page layout and polish. Is it feasible to use, say, Twitter Bootstrap from inside a clojurescript/reagent project?