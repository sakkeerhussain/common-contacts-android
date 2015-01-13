<?php
include('database.php');

//connecting to database
$dbhandle = mysql_connect(hostname, username, password) 
  or die("Unable to connect to MySQL");

$selected = mysql_select_db(db_name,$dbhandle) 
  or die("Could not select examples");

//reading values
$id = $_POST['id'];
$name = $_POST['name'];
$phone_no = $_POST['phone_no'];
$image = $_POST['image'];
$visibility = $_POST['visibility'];

//for checking
//echo $id." - ". $name ." - ". $phone_no ." - ". $visibility." - ". $image ."<br/>";

//updating
if($image == ""){
    $result = mysql_query("UPDATE `contact_list` SET `name` = '$name', `phone_no` = '$phone_no', `visibility` = '$visibility' where `id`='$id'");
}else{
    $result = mysql_query("UPDATE `contact_list` SET `name` = '$name', `phone_no` = '$phone_no', `image` = '$image', `visibility` = '$visibility' where `id`='$id'");
}

//producing result
if($result==1){
   echo 'success';
}else{
   echo 'failed';
}

?>