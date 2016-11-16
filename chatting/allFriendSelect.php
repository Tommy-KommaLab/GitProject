<?php

$connect =mysql_connect("localhost","root","1234");
mysql_selectdb("chatting");

$id = $_GET[id];

$qry = "SELECT * FROM friend where id ='$id' ";
$q = mysql_query($qry);
while($e=mysql_fetch_assoc($q))
	$output[] =$e;

	print (json_encode($output));

?>

