<?php

$connect =mysql_connect("localhost","root","@John1513");
mysql_selectdb("DEV_CHAT");

$id = $_GET[id];

$qry = "SELECT * FROM FRIEND where idMEMB ='$id' ";
$q = mysql_query($qry);
while($e=mysql_fetch_assoc($q))
	$output[] =$e;

	print (json_encode($output));

?>

