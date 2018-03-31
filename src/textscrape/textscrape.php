<?php
// inserts course, subject, title, and description into DB

$file = file_get_contents("2018-03-17-Summer2018.txt");

$text = explode("\n", $file);

$conn = mysqli_connect('localhost', 'root', '', 'courses');

for ($i = 0; $i < count($text); $i++) {
    if (strpos($text[$i], '{') !== false) { // subject
        $subject = trim($text[$i], "{}");
        $subject = mysqli_real_escape_string($conn, $subject);
    } else if (strpos($text[$i], '*') !== false) {  // course title
        $title = trim($text[$i], "*");
        $courseCode = substr($title, 0, strpos($title, "-"));
        $title = strstr($title, '-');
        $title = trim($title, "- ");
        
        $title = mysqli_real_escape_string($conn, $title);
        $courseCode = mysqli_real_escape_string($conn, $courseCode);
        $result = "INSERT into courses (Subject, Title, Course) VALUES ('$subject','$title','$courseCode')";
        $sql = mysqli_query($conn, $result);
        $last_id = $conn->insert_id;
    } else if (strpos($text[$i], '[') !== false) {  // course description
        $description = trim($text[$i], "[]");
        
        echo $subject . " > " . $courseCode . " - " . $title . " : " . $description;
        
        $description = mysqli_real_escape_string($conn, $description);
        $result = "INSERT into info (description, ID) VALUES ('$description', $last_id)";
        $sql = mysqli_query($conn, $result);
    }
    echo "<br>";
}
?>