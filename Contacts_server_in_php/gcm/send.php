<?php
    include('../database.php');

    $apiKey = "AIzaSyD00Kbaq3ztkjoyC0e5CADhEieEEQ5MRkI";


    //connection to the database
    $dbhandle = mysql_connect(hostname, username, password) 
      or die("Unable to connect to MySQL");

    $selected = mysql_select_db(db_name,$dbhandle) 
      or die("Could not select database");

    //execute the SQL query and return records
    $result = mysql_query("SELECT * FROM `device_list` ");


    if(isset($_GET['device_id']) and !empty($_GET['device_id'])){
        $device_exept = $_GET['device_id'];
    }else{
        $device_exept ="";
    }

    // Message to be sent
    if(isset($_GET['message']) and !empty($_GET['message'])){
        $message = $_GET['message'];
    }else{
        $message ="";
    }

    //fetch tha data from the database
    $registrationIDs = array();
    while ($row = mysql_fetch_array($result)) {
       $reg_id = $row{'reg_id'};
       if($row{'device_id'} != $device_exept){
           $registrationIDs[]=$reg_id;
       }
    }


    

    // Set POST variables
    $url = 'https://android.googleapis.com/gcm/send';

    $data = array( "message" => $message );

    $fields = array(
        'registration_ids' => $registrationIDs,
        'collapse_key'=> "update_contact_list",
        'data' => $data,
    );
    $headers = array(
        'Authorization: key=' . $apiKey,
        'Content-Type: application/json'
    );

    // Open connection
    $ch = curl_init();

    // Set the URL, number of POST vars, POST data
    curl_setopt( $ch, CURLOPT_URL, $url);
    curl_setopt( $ch, CURLOPT_POST, true);
    curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true);
    //curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fields));

    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    // curl_setopt($ch, CURLOPT_POST, true);
    // curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode( $fields));

    // Execute post
    $result = curl_exec($ch);

    // Close connection
    curl_close($ch);
    echo $result;
    //print_r($result);
    //var_dump($result);
?>				