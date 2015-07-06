<?php
	/*
	 *
	 * This file reports the location of Gasteizko Margolariak.
	 * 
	 * It will only do so if the last location report is less than 25 minutes old.
	 *
	 */


	//The file ../db-access.php is not here for security reasons.
	//It contains just the line
	//     $con = mysqli_connect(host, user, pass, database);
	include '../db-access.php';
	$res_manual = mysqli_query($con, "SELECT admin.id AS uid FROM location, admin WHERE manual = 1 AND location.user = admin.id ORDER BY time DESC LIMIT 1;");
	if (mysqli_num_rows($res_manual) == 0){
		echo "<location>none</location>\n";
	}
	else{
		$row_manuals = mysqli_fetch_array($res_manual);
		$res = mysqli_query($con, "SELECT lat, lon, name FROM location, admin WHERE time > date_sub(now(), INTERVAL 15 MINUTE) AND location.user = $row_manuals[uid] AND manual = 0 AND location.user = admin.id ORDER BY time DESC LIMIT 1;");
		if (mysqli_num_rows($res) == 0){
			echo "<location>none</location>\n";
		}
		else{
			$row = mysqli_fetch_array($res);
			echo "<location>\n";
			echo "<lat>$row[lat]</lat>\n";
			echo "<lon>$row[lon]</lon>\n";
			echo "<user>$row[name]</user>\n";
			echo "</location>\n";
		}
	}