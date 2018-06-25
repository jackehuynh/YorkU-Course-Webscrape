<?php

ini_set('error_reporting', E_ALL);
ini_set('display_errors', 'On');
ini_set('max_execution_time', 120);


$conn = mysqli_connect("localhost", 'root', '', '2017-2018');

$textfile = file_get_contents('fallwinter2018testrun.txt');

$explode = explode("\n", $textfile);

$subject = '';
$course = '';
$description = '';
$course_id = '';
$subject_id = '';
$courseinfo_id = '';
$insertID = '';

for ($i = 0; $i < count($explode); $i++) {

    if (strpos($explode[$i], '{') == true) {
        $subject = str_replace('{', '', $explode[$i]);
        $subject = str_replace('}', '', $subject);
        $sql = "INSERT INTO subject(subjectname) VALUES('$subject')";
        $conn->query($sql);
        $subject_id = $conn->insert_id;
    } else if (strpos($explode[$i], '[') == true) {
        $description = str_replace('[', '', $explode[$i]);
        $description = str_replace(']', '', $description);
        $sql = "INSERT INTO courseinfo(description) VALUES ('$description')";
        $conn->query($sql);
        $courseinfo_id = $conn->insert_id;
    } else {
        $course = $explode[$i];
        $sql = "INSERT INTO course(title) VALUES ('$course')";
        $conn->query($sql);
        $course_id = $conn->insert_id;
    }

    //get the key of the last record which has been inserted into DB otherwise it will return 0

    if ($subject_id) {
        $insert = "INSERT INTO course(subject_id) VALUES ('$subject_id')";
        $conn->query($insert);
    }
    if ($course_id) {
        $insert = "INSERT INTO courseinfo(course_id) VALUES ('$course_id')";
        $conn->query($insert);
    }
//        $sql = "SELECT subject_id FROM subject WHERE subjectname='$subject'";
//        $result = $conn->query($sql);
//        $row = $result->fetch_assoc();
//        $course_id = 
//    }
}
?>