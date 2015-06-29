<?php
	/*
	 *
	 * This file inserts a sumbisson of an user of the master app.
	 *
	 */

	//The file ../../db-access.php is not here for security reasons.
	//It contains just the line
	//     $con = mysqli_connect(host, user, pass, database);
	include '../../db-access.php';
	$name = mysqli_real_escape_string($con, $_GET['name']);
	$phone = mysqli_real_escape_string($con, $_GET['phone']);
	$code = mysqli_real_escape_string($con, $_GET['code']);
	$query = "INSERT INTO admin (name, code, phone, enabled) VALUES('$name', '$code', '$phone', 0);";
	echo "<user>$name</user>\n";
	mysqli_query($con, $query);
	//echo $query;
	
?>
