<?php
	/*
	 *
	 * This file helps building the database in the devices of the client apps.
	 * 
	 * To do so, it provides the current database version and the necessary
	 * queries to replicate the tables place, people and event.
	 *
	 */

	//The file ../db-access.php is not here for security reasons.
	//It contains just the line
	//     $con = mysqli_connect(host, user, pass, database);
	include '../db-access.php';
	$name = mysqli_real_escape_string($con, $_GET['name']);
	$code = mysqli_real_escape_string($con, $_GET['code']);
	$fg = mysqli_real_escape_string($con, $_GET['fg']);
	if ($fg == 'true')
		$fg = '1';
	else
		$fg = '0';
	if (strlen($code) > 0){
		$res = mysqli_query($con, "SELECT * FROM user WHERE code = '$code';");
		if (mysqli_num_rows($res) == 0){
			mysqli_query($con, "INSERT INTO user (name, code) VALUES ('$name', '$code');");
			$res = mysqli_query($con, "SELECT * FROM user WHERE code = '$code';");
			$row = mysqli_fetch_array($res);
		}
		else{
			$row = mysqli_fetch_array($res);
			if ($row['name'] != $name)
				mysqli_query($con, "UPDATE user SET name = '$name' WHERE code = '$code';");
		}
		mysqli_query($con, "INSERT INTO sync (user, foreground) VALUES ($row[id], $fg);");
	}
	$res = mysqli_query($con, "SELECT * FROM version;");
	$row = mysqli_fetch_array($res);
	echo "<version>$row[version]</version>\n";
	$res = mysqli_query($con, "SELECT * FROM place;");
    while($row = mysqli_fetch_array($res)){
		if ($row['name'] == '')
			$name = 'null';
		else
			$name = "'$row[name]'";
		if ($row['address'] == '')
			$address = 'null';
		else
			$address = "'$row[address]'";
		if ($row['cp'] == '')
			$cp = 'null';
		else
			$cp = "'$row[cp]'";
		if ($row['lat'] == '')
			$lat = 'null';
		else
			$lat = "$row[lat]";
		if ($row['lon'] == '')
			$lon = 'null';
		else
			$lon = "$row[lon]";
		echo "<query>INSERT INTO place VALUES ($row[id], $name, $address, $cp, $lat, $lon);</query>\n";
	}
	$res = mysqli_query($con, "SELECT * FROM people;");
    while($row = mysqli_fetch_array($res)){
		if ($row['name'] == '')
			$name = 'null';
		else
			$name = "'$row[name]'";
		if ($row['link'] == '')
			$link = 'null';
		else
			$link = "'$row[link]'";
		echo "<query>INSERT INTO people VALUES ($row[id], $name, $link);</query>\n";
	}
	$res = mysqli_query($con, "SELECT * FROM event;");
    while($row = mysqli_fetch_array($res)){
		if ($row['name'] == '')
			$name = 'null';
		else
			$name = "'$row[name]'";
		if ($row['description'] == '')
			$description = 'null';
		else
			$description = "'$row[description]'";
		if ($row['host'] == '')
			$host = 'null';
		else
			$host = "$row[host]";
		if ($row['place'] == '')
			$place = 'null';
		else
			$place = $row['place'];
		if ($row['start'] == '')
			$start = 'null';
		else
			$start = "'$row[start]'";
		if ($row['end'] == '')
			$end = 'null';
		else
			$end = "'$row[end]'";
		echo "<query>INSERT INTO event VALUES ($row[id], $row[schedule], $row[gm], $name, $description, $host, $place, $start, $end);</query>\n";
	}
?>
 
