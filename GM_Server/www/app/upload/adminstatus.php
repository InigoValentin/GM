<?php
	//The file db-access.php is not here for security reasons.
	//It contains just the line
	//     $con = mysqli_connect(host, user, pass, database);
	include '../../db-access.php';
	$name = mysqli_real_escape_string($con, $_GET['name']);
	$code = mysqli_real_escape_string($con, $_GET['code']);
	$res = mysqli_query($con, "SELECT enabled FROM admin WHERE name = '$name' AND code = '$code' ORDER BY id DESC;");
	$row = mysqli_fetch_array($res);
	if (mysqli_num_rows($res) == 0)
		echo "<status>-1</status>\n";
	else
		echo "<status>$row[enabled]</status>\n";
	
	//echo $query;
	
?>
