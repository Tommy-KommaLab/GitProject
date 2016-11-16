<?php

mysql_connect("localhost","root","1234");

mysql_select_db("chatting");

$q= mysql_query("SELECT * FROM member ");
while($e=mysql_fetch_assoc($q))
	$output[] =$e;

print (json_encode($output));

?>
