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

$name = $_POST['name'];
$phone_no = $_POST['phone_no'];
$image = $_POST['image'];

//echo $name." - ". $phone_no;


///check for duplicate entry
$query = "SELECT * FROM `contact_list` WHERE `phone_no` = '$phone_no' AND `deleted` = 0 ";
$result = mysql_query($query);
$num_rows =  mysql_num_rows($result);

//echo "mysql_num_rows : ".$num_rows ;

if($num_rows >= 1){
  echo 'duplicate entry `'.$phone_no.'`';
}else{
  //execute the SQL query and return records
  $query = "INSERT INTO `contact_list` (`name`, `phone_no`, `image`) VALUES ('$name', '$phone_no', '$image');";
  $result = mysql_query($query);

  //echo $result;

  if($result==1){
     echo 'success';
  }else{
     echo 'failed to add contact';
  } 
}

?>		