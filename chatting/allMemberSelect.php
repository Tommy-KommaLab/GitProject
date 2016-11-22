<?php

mysql_connect("localhost","root","@John1513");

mysql_select_db("DEV_CHAT");

$q= mysql_query("SELECT * FROM MEMB");
while($e=mysql_fetch_assoc($q))
	$output[] =$e;

print (json_encode($output));

?>
