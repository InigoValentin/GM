Gasteizko Margolariak Android App
===

An Android app intended for members of <a href="http://margolariak.com/">Gasteizko Margolariak</a> in <a href="http://www.vitoria-gasteiz.org/">Vitoria-Gasteiz</a>, Spain.

During the regional festival, it can help them find activities around the town, both hosted by Gasteizko Margolariak and other partes, as well as sugest them things to to when they are near places of interest.




The repository is a subset of three projects

* **GM** Is the main Android app, the one that will be published or distributed for the final user. It shows the festival schedule, suggest nearby activities, receives notifications, and can find the current location of Gasteizko Margolariak.

* **GM_Master** Should not be published or freely distributed. It is intended for a few members of Gasteizko Margolariak, and can report theire location so the users of the main app can see it. Also, it can send custom notifications to the users of the main app.

* **GM_server** Contains files to build a web server that wll act as a data provider for the main app, and as a bridge between the master and the main app.
