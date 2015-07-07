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
		<h1>Añadir organizaciones</h1>
		<a href="place.php">Añadir lugares</a> - <a href="event.php">Añadir eventos de fiestas</a> - <a href="gmevent.php">Añadir actividades de margolariak</a>
		<br/><br/>
		<h3>Ultimos 10:</h3>
		<?php
			//The file ../db-access.php is not here for security reasons.
			//It contains just the line
			//     $con = mysqli_connect(host, user, pass, database);
			include '../db-access.php';
			$res = mysqli_query($con, "SELECT * FROM people ORDER BY id DESC LIMIT 10;");
			echo "<table style='margin-left:15px;'>\n";
			echo "<tr><th style='border:1px solid black;'>Nombre</th><th style='border:1px solid black;'>Link</th></tr>\n";
			while ($row = mysqli_fetch_array($res)){
				echo "<tr><td style='border:1px solid black;'>$row[name]</td><td style='border:1px solid black;'>$row[link]</td></tr>\n";
			}
			echo "</table><br/></br/>\n<form method='get' action='/gm/insert/people.php'>\n";
		?>
		<h3>Añadir</h3>
		<p style='width:500px;margin-left:15px;'>Solo el nombre es obligatorio<br/><br/>
			El enlace sera preferentemente a la pagina oficial. Si no existiera, un blog, o Facebook, o cualquier otra red social servira. Como ultimo recurso, puede dejarse en blanco
		</p>
		<?php
			echo "<table style='margin-left:15px;'>\n";
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