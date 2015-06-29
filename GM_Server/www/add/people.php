<?php
	/*
	 *
	 * This file provides a web interface to easily add entries to the people table.
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
		<title>Organizaciones</title>
	</head>
	<body>
		<?php
			//The file ../db-access.php is not here for security reasons.
			//It contains just the line
			//     $con = mysqli_connect(host, user, pass, database);
			include '../db-access.php';
			$res = mysqli_query($con, "SELECT * FROM people;");
			echo "<table>\n";
			echo "<tr><th style='border:1px solid black;'>Nombre</th><th style='border:1px solid black;'>Link</th></tr>\n";
			while ($row = mysqli_fetch_array($res)){
				echo "<tr><td style='border:1px solid black;'>$row[name]</td><td style='border:1px solid black;'>$row[link]</td></tr>\n";
			}
			echo "</table><br/></br/>\n<form method='get' action='/gm/insert/people.php'><table>\n";
			echo "<tr><th>Nombre</th><th>Link</th></tr>\n";
			for ($i = 0; $i < 10; $i ++){
				echo "<tr><td><input type='text' name='name_$i' maxlength='100' style=\"width:180px;\"/></td>\n";
				echo "<td><input type='text' name='link_$i' maxlength='500' style=\"width:400px;\"/></td></tr>\n";
			}
			echo "</table>\n";
		?>
		<input type="submit" value="Guardar"/></form>
	</body>
</html>