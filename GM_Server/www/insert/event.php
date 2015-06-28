<!DOCTYPE html>
<html>
	<head>
		<meta content="text/html; charset=windows-1252" http-equiv="content-type"/>
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1">
		<title>Lugares</title>
	</head>
	<body>
		<?php
			//The file db-access.php is not here for security reasons.
			//It contains just the line
			//     $con = mysqli_connect(host, user, pass, database);
			include '../db-access.php';
			$error = 0;
			$inserted = 0;
			$date = $_GET['date'];
			for ($i = 0; $i < 10; $i ++){
				$name = $_GET['name_' . $i];
				$description = $_GET['description_' . $i];
				$start_h = $_GET['start_h_' . $i];
				$start_m = $_GET['start_m_' . $i];
				$start = "str_to_date('" . $date . " " . $start_h . ":" . $start_m . "', '%Y-%m-%d %H:%i:%s')";
				$end_h = $_GET['end_h_' . $i];
				$end_m = $_GET['end_m_' . $i];
				if (strlen($end_h) == 0 || strlen($end_m) == 0)
					$end = "null";
				else
					$end = "str_to_date('" . $date . " " . $end_h . ":" . $end_m . "', '%Y-%m-%d %H:%i:%s')";
				$place = $_GET['place_' . $i];
				$host = $_GET['host_' . $i];
				if (strlen($host) == 0)
					$host = "null";
				$query = "INSERT INTO event (gm, schedule, name, description, start, end, place, host) VALUES (0, 1, '$name', '$description', $start, $end, $place, $host);";
				//if (strlen($name) > 0 && $address != "" && $cp != "" && $lat != "" && $lon != ""){
				//	mysqli_query($con, $query);
				//	echo "Entrada anadida: $name<br/>\n";
				//	$inserted ++;
				//}
				//else if (strlen($name) > 0 && ($address == "" || $cp == "" || $lat == "" || $lon == "")){
				//	echo "Error anadiendo: $name (Direccion: $address, CP: $cp, GPS: $lat, $lon)<br/>\n;";
				//	$error ++;
				//}
				if (strlen($name) > 0){
					echo $query;
					mysqli_query($con, $query);
					echo "Entrada anadida: $name<br/>\n";
					$inserted ++;
					//TODO: insert
				}
			}
			echo "<br/><br/><br/>$inserted nuevas entradas anadidas.<br/>\n$error errores.\n<br/><br/><form action='/gm/add/event.php'><input type='submit' value='Continuar anadiendo'/></form>";
		?>
	</body>
</html>