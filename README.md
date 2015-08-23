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

Running the Whiteboard
--------

###Version 3
It can be a bit difficult to get Version 3 running because it involves starting a database server and a whiteboard server. You must also have ports 9000, and 9994 through 10010 free, or else the servers cannot start. The user interface may also feel a bit unintuitive. Please bear with these shortcomings, as this was my first major project.

Once you've made sure the ports are available, you should start the database server first. You should run "Database Server v3.jar" in the "Interactive Whiteboard 3 Database" folder. A user interface will appear, and you should click "Start". If everything works correctly, you should get a message, "The server was successfully started." You can close out the program log if you wish.

When the Database Server is started, you should then start the whiteboard server. You should run "Whiteboard Server v3.jar" in the "Interactive Whiteboard 3 Server" folder. A user interface will appear asking you for the Database Server's location. Hopefully, you are running the Database Server on the same machine, and you can just click "Load Server". If that's not the case, enter the correct IP Address of the Database Server. The port is configured to be 9000 for the Database Server, and there's no need to change that. Once the Whiteboard Server is loaded, you will see a big "Start" button and you should click that. If everything works correctly, you should get a message, "The server was successfully started." You can close out the program log if you wish.

Once both servers are up and running, you can run multiple clients to connect to it. In the following steps, we will go through getting two clients to draw on the whiteboard of a room. 

First, we will get one client connected to a room. Run one instance of "Whiteboard Client v3.jar" in the "Interactive Whiteboard 3 Client" folder. Hopefully, your Whiteboard Server will also be running on the same computer, so you should be okay using localhost as the IP and 9999 is automatically the Whiteboard Server's port. You should use mjchao as the username, and change the password to abcdefg (it is not abcdefg by default). Your login should succeed and you will be taken to a blank page. In the menu, select "Room->Join a Room" and you will be taken to a new window with 1 room to join. There is 1 room because the database comes with 1 room in its datafiles. Click "View" and notice that it says "Password Protection: ON". In the "Join Password" field, type "qaz", which is the password for the room and then click "Join". The room should appear.

Now, we will get a second client connected to the same room. Run another instance of "Whiteboard Client v3.jar". This time, log in with username "mjchao2" and password "abcefg". Follow the same sequence of steps to join the room. Now, you should have two users in one room, and if you want, you could join with a third user (e.g. username = "person1", password = "abcdefg" - Look through all the datafiles in the "Interactive Whiteboard 3 Database" to find out all registered users).



###Version 4
Version 4 consists of just the client user interface. It has fake data and one room with 3 whiteboard activities. Inside the "IW Client 4" folder, simply run "Client 4.jar" and you will see the login interface. The password does not matter, so just click "Login". You can navigate to different features with the menu. The same features from version 3 are present: account settings, one-to-one messaging, and group collaboration.

The notable improvement in version 4 is the room user interface. Go to Collaboration->Browse Rooms and you will see a table with various rooms to join. Since the server is not implemented, all the rooms are the same. Join any room you wish and you will be taken to the room user interface. You may wish to maximize this window, and you will see three whiteboard activities. 

You can drag activities around by pressing the cursor over the title (a hand icon should appear). The whiteboard should let you draw on it, erase parts of it, and undo/redo actions. Additional activities could be added by extending the Activity class. The idea is that users will be able to add as many activities as they'd like, rearrange them however they want on the room, and collaborate.

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
