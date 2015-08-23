Interactive Whiteboard
======================

This is the Interactive Whiteboard on which I worked for approximately 9 months.

History
-------

In a CSC 425 Software Engineering class I took in the winter term of 2012, my class decided to create an interactive whiteboard as a group project. The term wasn't long enough though, so I decided to try creating it on my own. The Interactive Whiteboard was the first "large" program (1000+ lines of code) I had ever attempted. It is written in Java with over 10,000 lines of code (not counting comments and extra formatting lines), and I created everything using only the standard Java libraries that came with my Java SDK from oracle.com . There are three parts: client, server, and databse, and I programmed all of them. I worked on the program over the course of approximately nine months: ~May 2012 - January 2013.

Versions
--------
You may notice that I am up to version 4, and you may be wondering what happened in versions 1 and 2. Version 1 was my project from CSC 425 that was never finished since the term was over. Version 2 was my first attempt at creating the Interactive Whiteboard, which took place around March 2012 - May 2012. I soon scrapped the project as it was not well-engineered and fell apart. Version 3 was my first "working" version of the Interactive Whiteboard, but it proved buggy and unmaintainable, but the product functions and does not fall apart. 

Eventually, I realized it was time for me to move on from interactive whiteboards, so I went on to other interesting projects in Summer 2013. In July 2014, after reading *Don't Make Me Think! Revisited* by Steve Krug, I attempted to make a better user interface, which became IW Client 4, the client application for the fourth version of the interactive whiteboard. 

As of right now (August 2015), I am still interested in reworking the Interactive Whiteboard. I have gained much more software engineering experience and I am familiar with more maintainable networking practices. I have plans to give it a new name "Clip-Board" and am in the process of writing new requirements. The development may be a bit slow, though, as I have a lot going on, but I am sure I can find time for an additional project.  

Login and User Information
--------------------------

<dl>
  <dt>Username</dt>
    <dd>represents an account, unique for each user, required to access an account</dd>
  <dt>Password</dt>
    <dd>required to access an account</dd>
  <dt>Password Change</dt>
    <dd>allows a user to change his/her password</dd>
  <dt>Display Name</dt>
    <dd>the name of a user that is displayed online (usernames are not displayed)</dd>
  <dt>Display Name Change</dt>
    <dd>allows a user to change his/her display name. to perform this function, the server administrator must generate a name change code, and this prevents users from repeatedly changing their display name and confusing others</dd>
</dl>

Instant Messaging
-----------------

<dl>
  <dt>Friends List</dt>
    <dd>provide a user's username to add him/her to your friends list. then, you are able to see if s/he is online and send him/her private messages</dd>
  <dt>Pests List</dt>
    <dd>if there is someone in particular you wish to avoid, provide his/her username and then s/he will always see you as offline and be unable to send you private messages</dd>
  <dt>Private Messaging</dt>
    <dd>sends a message privately to someone on your friend's list. your friend does not have to be online. if your friend is not online, s/he will see it the next time s/he logs on</dd>
</dl>

Interactive Whiteboard
----------------------
<dl>
  <dt>Rooms</dt>
    <dd>every room has a unique whiteboard, audio chat, group text chat and user list.</dd>
  <dt>Room Creation, Modification</dt>
    <dd>each room has specific properties, such as its creation date and creator, which cannot be changed, or its name and if it is password protected, which can both be modified.
  <dt>Audio Chat</dt>
    <dd>allows you to speak to others as if you were physically in the same room with them. currently, it is lossy and echoes (I have not been able to find many online resources for computer audio).</dd>
  <dt>Whiteboard</dt>
    <dd>allows you to draw on a whiteboard and others can see what you have drawn. new users joining the room will also be able to see what you has been drawn previously. there is only one whiteboard in the room and only one person can draw on it at a time. if two people want to draw on it, they must "pass" it around (see user list description).</dd>
  <dt>Text Chat</dt>
    <dd>sends group text messages</dd>
  <dt>User List</dt>
    <dd>specifies user permissions. there are three ranks: administrator, moderator, normal user, represented by color. room creators are administrators and can nominate moderators. anyone with higher ranking can modify the privileges of people with lower ranking (for example, prevent someone from participating in audio chat, or take the whiteboard from him/her). the user list also allows you to "give" the whiteboard to someone else once you are done drawing on it.</dd>
    
Current Stage and Future Improvements
-------------------------------------

  This program is functional. The login and user information works fine. There remain a few problems with Friends List, Pests List and Private Messaging, and the code for the Room functionalities could be more efficient - especially for the whiteboard.
  I am especially proud that I addressed all the functionality I had planned, although there remain some bugs. In the future, I hope, with all the experience I have gained from this project, to rebuild an improved Interactive Whiteboard. Since January 2013, I have read much more on software engineering, gained greater practice with coding large programs, and experienced working in team environments. All of these will definitely help me, and I plan on recreating the Interactive Whiteboard this summer 2014, after my current projects (163, which is a card game, and studies in Computer Algebra) have been finished.
