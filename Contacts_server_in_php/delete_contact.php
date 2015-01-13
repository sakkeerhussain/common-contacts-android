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

$id = $_GET['id'];

//echo $id;

//execute the SQL query and return records
$result = mysql_query("update contact_list set deleted = 1 where id='$id'");
if($result==1){
   echo 'success';
}else{
   echo 'failed';
}

?>