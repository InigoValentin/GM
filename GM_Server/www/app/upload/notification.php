<?php
	//The file db-access.php is not here for security reasons.
	//It contains just the line
	//     $con = mysqli_connect(host, user, pass, database);
	include '../../db-access.php';
	$user = mysqli_real_escape_string($con, $_GET['user']);
	$code = mysqli_real_escape_string($con, $_GET['code']);
	$title = mysqli_real_escape_string($con, $_GET['title']);
	$text = mysqli_real_escape_string($con, $_GET['text']);
	$duration = mysqli_real_escape_string($con, $_GET['duration']);
	$gm = mysqli_real_escape_string($con, $_GET['gm']);
	$type = mysqli_real_escape_string($con, $_GET['type']);
	$res_user = mysqli_query($con, "SELECT id FROM admin WHERE name = '$user' AND code = '$code' AND enabled = 1 ORDER BY id DESC;");
	if (mysqli_num_rows($res_user) == 0)
		echo "<status>-2</status>\n";
	else{
		if ($title == "" || $text == "")
			echo "<status>-1</status>\n";
		else{
			//Everything ok, do something
			$row_user = mysqli_fetch_array($res_user);
			$query = "INSERT INTO notification (user, duration, type, title, text, gm) VALUES ($row_user[id], $duration, '$type', '$title', '$text', gm);";
			mysqli_query($con, $query);
			echo "<status>ok</status>\n";
		}
	}
	$row_user = mysqli_fetch_array($res_user);
	//echo $query;
	
?>
 
