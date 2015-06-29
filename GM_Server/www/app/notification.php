<?php
	/*
	 *
	 * This file returns notificatos to be delivered to the client app.
	 * 
	 * Only the notificatons that are still to be delivered are printed.
	 *
	 */

	//The file ../db-access.php is not here for security reasons.
	//It contains just the line
	//     $con = mysqli_connect(host, user, pass, database);
	include '../db-access.php';
	$res = mysqli_query($con, "SELECT * FROM `notification` WHERE time + INTERVAL duration MINUTE >= now();"); //TODO: Where clause
    while($row = mysqli_fetch_array($res)){
		echo "<notification>";
		echo "<id>$row[id]</id>";
		echo "<type>$row[type]</type>";
		echo "<title>$row[title]</title>";
		echo "<text>$row[text]</text>";
		echo "<gm>$row[gm]</gm>";
		echo "</notification>\n";
	}
	
?>
 
