# cs0320 Term Project

**Team Members:** 
* Ali Mir
* Hannah He
* Leandro Bengzon
* Matthew Alexander Sicat

**Project Idea:** 
Sesh provides users with the opportunity to create collaborative playlists in real-time. The application addresses the problem of disagreement over music choice in a group environment.  

**Mentor TA:** Rick Miyagi rick_miyagi@brown.edu

## Project Requirements

We plan to use Youtube's API to enable searching for songs, adding songs to a playlist and stream the audio. 

#### DJ Privileges
* Create a public/private party
* Search for songs to curate playlist
* Select and migrate requested songs to playlist
* Reorder playlist
* Invite others to become co-DJs
* Set request window (time frame to request songs) ★
* Set upvote/downvote window 
* Close the party
* Save playlist and allow it to be viewable in the future ★

#### Guest Privileges
* Join a party
* Search for songs
* Request songs for the DJ to play
* Upvote/downvote requested songs
* View saved playlists from prior parties attended ★

★ Indicates functionality added from user suggestion.
 
#### Storyboard of Sesh Usage:
* DJ creates a party and determines if it is public or private
* Guests join party (see thinking point 2 below)
* DJ determines a request and/or upvote/downvote window (if needed)
* DJ curates initial songs for playlist
* Guests request and upvote/downvote songs
* DJ closes request and/or upvote/downvote window (if needd)
* DJ migrates requested songs to playlist
* DJ can continue to add/delete/reorder playlist as long as party is open
* DJ closes party

#### Thinking Points
1. DJs should be able to determine whether a party is public or private. If we were able to set up a geoframe which allowed users to view and join parties happening nearby, a 'public party' option might be useful. However, if there is no way to reasonably limit the scope of public parties (i.e. the public party list would end up just being a list of every single public party happening anywhere at anytime), then a 'public party' option would not be realistic or user-friendly. 

2. A private party means that guests can only join given some sort permission. We are currently brainstorming on what the most user-friendly and hassle-free procedure this could follow. Some ideas include the DJ generating a random access code or generating their own password and somehow delivering them to guests, or potential guests requesting access to a party and a DJ granting them permission.

#### Current Limitations

YouTube Videos often play ads. If we are curating a playlist of videos and the user (specifically the DJ) does not have an ad blocker, the playlist may get interrupted by ads. Using an adblocker like UBlock can work around this problem externally.

## Project Specs and Mockup

Mockup link on myBalsamiq:

https://alimir.mybalsamiq.com/projects/sesh/prototype/Sesh%20Home?key=08c8839db9d3a3dd70066b7e29282a2c002fca9a


## Project Design Presentation
_A link to your design presentation/document will go here!_

## How to Build and Run
_A necessary part of any README!_
