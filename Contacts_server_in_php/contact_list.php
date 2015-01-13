<?php
$username = "a2064679_root";
$password = "a2064679__root";
$hostname = "mysql9.000webhost.com"; 
$db_name  = "a2064679_list";

//connection to the database
$dbhandle = mysql_connect($hostname, $username, $password) 
  or die("Unable to connect to MySQL");

$selected = mysql_select_db($db_name,$dbhandle) 
  or die("Could not select examples");

//execute the SQL query and return records
$result = mysql_query("SELECT * FROM `contact_list` WHERE `deleted`=0 order by name asc");

//fetch tha data from the database
echo "[";

if($row = mysql_fetch_array($result)) {
   echo '{"id":"'.$row{'id'}.'", "visibility":"'.$row{'visibility'}.'", "name":"'.$row{'name'}.'", "phone_no":"'.$row{'phone_no'}.'", "image":"'.$row{'image'}.'"}';
}
while ($row = mysql_fetch_array($result)) {
   echo ',{"id":"'.$row{'id'}.'", "visibility":"'.$row{'visibility'}.'", "name":"'.$row{'name'}.'", "phone_no":"'.$row{'phone_no'}.'", "image":"'.$row{'image'}.'"}';
}

echo "] \n";
?>				