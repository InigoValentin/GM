<!DOCTYPE html>
<html>
	<head>
		<meta content="text/html; charset=windows-1252" http-equiv="content-type"/>
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1">
		<title>People</title>
	</head>
	<body>
		<?php
			//The file db-access.php is not here for security reasons.
			//It contains just the line
			//     $con = mysqli_connect(host, user, pass, database);
			include '../db-access.php';
			$error = 0;
			$inserted = 0;
			for ($i = 0; $i < 10; $i ++){
				$name = $_GET['name_' . $i];
				$link = $_GET['link_' . $i];
				$query = "INSERT INTO people (name, link) VALUES ('$name', '$link');";
				if (strlen($name) > 0){
					mysqli_query($con, $query);
					echo "Entrada anadida: $name<br/>\n";
					$inserted ++;
				}
				else {
					echo "Error anadiendo: $name (Link: $link)<br/>\n;";
					$error ++;
				}
			}
			echo "<br/><br/><br/>$inserted nuevas entradas anadidas.<br/>\n$error errores.\n<br/><br/><form action='/gm/add/people.php'><input type='submit' value='Continuar anadiendo'/></form>";
		?>
	</body>
</html>