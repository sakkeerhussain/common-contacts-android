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

if(isset($_GET['reg_id']) and isset($_GET['device_id']) and !empty($_GET['reg_id']) and !empty($_GET['device_id'])){

$regId = $_GET['reg_id'];
$device_id = $_GET['device_id'];
//echo $device_id."  dev id " .$regId."  \n";

   //checks is the device exists before
  $query = "SELECT * FROM `device_list` WHERE `device_id` = '$device_id'";
  $result = mysql_query($query);

  if($num_rows >= 1){


     //if device not registered before
     $query = "UPDATE `device_list` set `reg_id` = '$regId' where `device_id` = '$device_id' ;";
     $result = mysql_query($query);

     if($result>=1){
        echo 'success';
     }else{
        echo 'failed to update contact';
     } 

  }else{

     //if device not registered before
     $query = "INSERT INTO `device_list` (`device_id`, `reg_id`) VALUES ('$device_id', '$regId');";
     $result = mysql_query($query);

     if($result>=1){
        echo 'success';
     }else{
        echo 'failed to add contact';
     } 

  }
}else{
   echo 'failed to add contact';
}
?>		