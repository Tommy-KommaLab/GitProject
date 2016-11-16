<?php
$connect = mysql_connect("localhost","root","1234");
mysql_selectdb("chatting");

$id = $_GET[id];
$pass = $_GET[pass];
$nickname = $_GET[nickname];

$qry = "INSERT INTO member(id,pass,nickname) values('$id','$pass','$nickname')";
$result = mysql_query($qry);
if($result)
{
	echo "success!";
}
?>