<?php
$connect = mysql_connect("localhost","root","@John1513");
mysql_selectdb("DEV_CHAT");

$id = $_GET[id];
$pass = $_GET[pass];
$nickname = $_GET[nickname];

$qry = "INSERT INTO MEMB(idMEMB,pwMEMB,nickMEMB) values('$id','$pass','$nickname')";
$result = mysql_query($qry);
if($result)
{
	echo "success!";
}
?>