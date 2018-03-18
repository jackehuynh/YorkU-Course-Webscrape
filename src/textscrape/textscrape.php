<?php

$file = file_get_contents("2018-03-17-Summer2018.txt");

$text = explode("\n", $file);
$course = array();

//$subjectPattern = '/\{([^\]]*)\}/';
//$titlePattern = '/\*([^\]].*)\*/';
//$descPattern = '/\[([^\]]*)\]/';
//
//preg_match_all($subjectPattern, $file, $match); // match subject
//$course['subject'] = $match;
//print_r($course['subject'][1]);
//
//preg_match_all($titlePattern, $file, $match); // match course title
//$course['title'] = $match;
//print_r($course['title'][1]);
//
//preg_match_all($descPattern, $file, $match); // match course description
//$course['description'] = $match;
//print_r($course['description'][1]);
//
//preg_match_all($coursePattern, $file, $match);
//$course['code'] = $match;
//print_r($course['code'][1]);
//for ($i = 0; $i < count($course['subject'][0]); $i++) {
//    print_r($course['subject'][0][$i]);
//    for ($j = 0; $j < count($course['title']))
//}


//$subject = '';
//$title = '';
//$code = '';
//$description = '';

for ($i = 0; $i < count($text); $i++) {
    if (strpos($text[$i], '{') !== false) { // subject
        $subject = str_replace("{", "", $text[$i]);
        $subject = str_replace("}", "", $subject);
//        echo $subject;
    } else if (strpos($text[$i], '*') !== false) {  // course title
        $title = str_replace("*", "", $text[$i]);
        $title = str_replace("*", "", $title);
        $code = str_replace("-", "", $title);
//        echo $subject . ": " . $title;
    } else if (strpos($text[$i], '[') !== false) {  // course description
        $description = str_replace("[", "", $text[$i]);
        $description = str_replace("]", "", $description);
        echo $subject . " > " . $title . " : " . $description;
    }
    echo "<br>";
}
?>