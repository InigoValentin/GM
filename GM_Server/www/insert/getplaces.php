<?php

	/*
	 *
	 * This file list all the events in a sql readable format.
	 * 
	 * Please note that, along with the other files in this folder and the "add"
	 * folder, ths file can cause security problems, so it must be removed from the
	 * server before publishing or distributng the app.
	 *
	 */

	//The file ../db-access.php is not here for security reasons.
	//It contains just the line
	//     $con = mysqli_connect(host, user, pass, database);
	include '../db-access.php';
	mysqli_set_charset($con, 'utf8');
	$id = 294;
	$res = mysqli_query($con, "SELECT * FROM place ORDER BY id;");
	while ($row = mysqli_fetch_array($res)){
		
		echo "INSERT INTO place VALUES ($row[id], '$row[name]', '$row[address]', '$row[cp]', $row[lat], $row[lon]);\n";
		$id ++;
	}
?>