<?php
	//The file db-access.php is not here for security reasons.
	//It contains just the line
	//     $con = mysqli_connect(host, user, pass, database);
	include '../../db-access.php';
	$user = mysqli_real_escape_string($con, $_GET['user']);
	$code = mysqli_real_escape_string($con, $_GET['code']);
	$lat = mysqli_real_escape_string($con, $_GET['lat']);
	$lon = mysqli_real_escape_string($con, $_GET['lon']);
	$manual = mysqli_real_escape_string($con, $_GET['manual']);
	$res_user = mysqli_query($con, "SELECT id FROM admin WHERE name = '$user' AND code = '$code' AND enabled = 1 ORDER BY id DESC;");
	if (mysqli_num_rows($res_user) == 0)
		echo "<status>fail</status>\n";
	else{
		$row_user = mysqli_fetch_array($res_user);
		if ($manual == "1"){
			$query = "INSERT INTO location (lat, lon, user, manual) VALUES ($lat, $lon, $row_user[id], 1);";
			mysqli_query($con, $query);
			echo "<status>sent</status>\n";
		}
		else{
			//Check if the las manual location is from the same user
			$res_manual = mysqli_query($con, "SELECT user FROM location WHERE manual = 1 ORDER BY time DESC LIMIT 1;"); //TODO: And only in the las 30 mins
			if (mysqli_num_rows($res_manual) == 0){
				//No locations yet in database, insert
				$query = "INSERT INTO location (lat, lon, user, manual) VALUES ($lat, $lon, $row_user[id], 0);";
				mysqli_query($con, $query);
				echo "<status>sent</status>\n";
			}
			else{
				$row_manual = mysqli_fetch_array($res_manual);
				if ($row_manual['user'] == $row_user['id']){
					$query = "INSERT INTO location (lat, lon, user, manual) VALUES ($lat, $lon, $row_user[id], 0);";
					mysqli_query($con, $query);
					echo "<status>sent</status>\n";
				}
				else{
					echo "<status>stop</status>\n";
				}
			}
		}
	}	
?>
 
