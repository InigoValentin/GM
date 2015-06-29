<?php
	/*
	 *
	 * This file provides a web interface to easily add entries to the place table.
	 * 
	 * Please note that, along with the other files in this folder and the "insert"
	 * folder, ths file can cause security problems, so it must be removed from the
	 * server before publishing or distributng the app.
	 *
	 */
?>

<!DOCTYPE html>
<html>
	<head>
		<meta content="text/html; charset=windows-1252" http-equiv="content-type"/>
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1">
		<title>Lugares</title>
	</head>
	<body>
		<?php
			//The file ../db-access.php is not here for security reasons.
			//It contains just the line
			//     $con = mysqli_connect(host, user, pass, database);
			include '../db-access.php';
			$res = mysqli_query($con, "SELECT * FROM place;");
			echo "<table>\n";
			echo "<tr><th style='border:1px solid black;'>Nombre</th><th style='border:1px solid black;'>Direccion</th><th style='border:1px solid black;'>CP</th><th style='border:1px solid black;'>Latitud</th><th style='border:1px solid black;'>Longitud</th></tr>\n";
			while ($row = mysqli_fetch_array($res)){
				echo "<tr><td style='border:1px solid black;'>$row[name]</td><td style='border:1px solid black;'>$row[address]</td>\n";
				echo "<td style='border:1px solid black;'>$row[cp]</td><td style='border:1px solid black;'>$row[lat]</td><td style='border:1px solid black;'>$row[lon]</td></tr>\n";
			}
			echo "</table><br/></br/>\n<form method='get' action='/gm/insert/place.php'><table>\n";
			echo "<tr><th>Nombre</th><th>Direccion</th><th>CP</th><th>Latitud</th><th>Longitud</th></tr>\n";
			for ($i = 0; $i < 10; $i ++){
				echo "<tr><td><input type='text' name='name_$i' maxlength='60' style=\"width:180px;\"/></td>\n";
				echo "<td><input type='text' name='address_$i' maxlength='200' style=\"width:300px;\"/></td>\n";
				echo "<td><input type='text' name='cp_$i' maxlength='5' style=\"width:50px;\"/></td>\n";
				echo "<td><input type='text' name='lat_$i' maxlength='11' style=\"width:60px;\"/></td>\n";
				echo "<td><input type='text' name='lon_$i' maxlength='11' style=\"width:60px;\"/></td></tr>\n";
			}
			echo "</table>\n";
		?>
		<input type="submit" value="Guardar"/></form>
	</body>
</html>