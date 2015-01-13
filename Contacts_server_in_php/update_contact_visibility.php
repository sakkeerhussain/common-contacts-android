<?php
include('database.php');

//connection to the database
$dbhandle = mysql_connect(hostname, username, password) 
  or die("Unable to connect to MySQL");

$selected = mysql_select_db(db_name,$dbhandle) 
  or die("Could not select examples");

$id = $_GET['id'];
$visibility = $_GET['visibility'];

//echo $id." - ". $visibility."<br/>";

//execute the SQL query and return records
$result = mysql_query("update contact_list set visibility = '$visibility' where id='$id'");
if($result==1){
   echo 'success';
}else{
   echo 'failed';
}

?>