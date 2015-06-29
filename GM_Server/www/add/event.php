<?php
	/*
	 *
	 * This file provides a web interface to easily add entries to the event table.
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
		<title>Programa de fiestas</title>
	</head>
	<body>
		<?php
			//The file ../db-access.php is not here for security reasons.
			//It contains just the line
			//     $con = mysqli_connect(host, user, pass, database);
			include '../db-access.php';
			$res = mysqli_query($con, "SELECT event.name AS name, start, end, place.name AS place FROM event, place WHERE schedule = 1 AND event.place = place.id ORDER BY start;");
			echo "<table>\n";
			echo "<tr><th style='border:1px solid black;'>Nombre</th><th style='border:1px solid black;'>Empieza</th><th style='border:1px solid black;'>Termina</th><th style='border:1px solid black;'>Lugar</th></tr>\n";
			while ($row = mysqli_fetch_array($res)){
				echo "<tr><td style='border:1px solid black;'>$row[name]</td><td style='border:1px solid black;'>$row[start]</td>\n";
				echo "<td style='border:1px solid black;'>$row[end]</td><td style='border:1px solid black;'>$row[place]</td></tr>\n";
			}
			echo "</table><br/></br/>\n<form method='get' action='/gm/insert/event.php'>\n";
			echo "<select name='date'>\n";
			echo "<option value='2015-07-25'>25 julio</option>\n";
			echo "<option value='2015-08-04'>4 agosto</option>\n";
			echo "<option value='2015-08-05'>5 agosto</option>\n";
			echo "<option value='2015-08-06'>6 agosto</option>\n";
			echo "<option value='2015-08-07'>7 agosto</option>\n";
			echo "<option value='2015-08-08'>8 agosto</option>\n";
			echo "<option value='2015-08-09'>9 agosto</option>\n";
			echo "</select>\n";
			echo "<table>\n";
			echo "<tr><th>Nombre</th><th>Description</th><th>Empieza</th><th>Termina</th><th>Lugar</th><th>Organiza</th></tr>\n";
			for ($i = 0; $i < 10; $i ++){
				echo "<tr><td style='border:1px solid black;'><input type='text' name='name_$i' maxlength='60' style=\"width:180px;\"/></td>\n";
				echo "<td style='border:1px solid black;'><textarea name='description_$i' rows='4' columns='50'/></textarea></td>\n";
				echo "<td style='border:1px solid black;'><input type='text' name='start_h_$i' maxlength='2' style=\"width:30px;\"/>:<input type='text' name='start_m_$i' maxlength='2' style=\"width:30px;\"/></td>\n";
				echo "<td style='border:1px solid black;'><input type='text' name='end_h_$i' maxlength='2' style=\"width:30px;\"/>:<input type='text' name='end_m_$i' maxlength='2' style=\"width:30px;\"/></td>\n";
				echo "<td style='border:1px solid black;'><select name='place_$i'>\n";
				echo "<option value=''>-</option>\n";
				$res_place = mysqli_query($con, "SELECT * FROM place ORDER BY name;");
				while ($row_place = mysqli_fetch_array($res_place)){
					echo "<option value='$row_place[id]'>$row_place[name]</option>\n";
				}
				echo "</select></td>\n";
				echo "<td style='border:1px solid black;'><select name='host_$i'>\n";
				echo "<option value=''>-</option>\n";
				$res_people = mysqli_query($con, "SELECT * FROM people ORDER BY name;");
				while ($row_people = mysqli_fetch_array($res_people)){
					echo "<option value='$row_people[id]'>$row_people[name]</option>\n";
				}
				echo "</select></td></td></tr>\n";
			}
			echo "</table>\n";
		?>
		<input type="submit" value="Guardar"/></form>
	</body>
</html>