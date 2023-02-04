# email_service
A small e-mail communication service that enables communication between multiple clients.


## How to run the email_service: 
 - If your IDE can't find dependencies/classes used in the code go to file -> "invalidate caches" and restart.
 - Before you run the program, in DbUsers.java, change the path variable to the path where your "database" folder is. 

1. To run the server side of the application, the main function is in ServerStart.java, run that to load the server.
2. To run the client, the main function is in ClientStart.java, run that to load the client. Beware that you have to input an username that is present in database/userList.txt


- If you try to run the client while the server is down you will get a "server is down" alert when you try to login. 
- If you are logged in the client and you shut down the server the client will still function, but you won't be able to send/receive any mails. 


## How it works: 

**Server Application:**

The server application is responsable of managing one or more client applications connected through a socket, managing information like the inbox and sent e-mails of each client, login credentials, and enstablishing the communication channel.

The server application also has a GUI in which a list view of all connected users and a list of logs for each relevant action performed by a client is shown.


**Client Application:**

The client application is associated to a specific account, it connects to the server through a socket connection by sending their login credentials. If the login was successful then the associated user inbox is received and through its graphical user inteface various functionalities are available: 
- sending an e-mail to one or more users
- forwarding an e-mail to one or more users
- replying to an e-mail
- replying to all receivers of an e-mail
- deleting an e-mail

The GUI has a button that turns on when new e-mails are found in the inbox, and by clicking it the inbox view is updated. 




