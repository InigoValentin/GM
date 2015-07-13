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
	$id = 1;
	$res = mysqli_query($con, "SELECT * FROM event ORDER BY start");
	while ($row = mysqli_fetch_array($res)){
		if ($row['host'] == null)
			$host = "null";
		else
			$host = "$row[host]";
		if ($row['end'] == null)
			$end = "null";
		else
			$end = "str_to_date('$row[end]', '%Y-%m-%d %H:%i:%s')";
		echo "INSERT INTO event VALUES ($id, $row[schedule], $row[gm], '$row[name]', '$row[description]', $host, $row[place], str_to_date('$row[start]', '%Y-%m-%d %H:%i:%s'), $end);\n";
		$id ++;
	}
?>