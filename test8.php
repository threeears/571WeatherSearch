<?php
//echo "test php!";

//retrieve information from servlet url parameters

$city=$_GET["location"];
$temUnit=$_GET["tempUnit"];
$type=$_GET["type"];

/*$city="11111";
$temUnit="f";
$type="city";

echo "<br>".$city;
echo "<br>".$temUnit;
echo "<br>".$type;*/


 //generate new url to get information from yahoo api

$myappid="?appid=6gz2b9PV34EamrFOrlM8dtPcMXlh9_JlXdMqKed.9.XnIbgms5ZtpQ0UcSw6qVeM";
$string="http://where.yahooapis.com/v1/";
$place=$city;
$search=array(",","'",".");
$place=str_ireplace($search,"+",$city);//replace the special acceptable notation from url
if(($type)=="city")
{
	$string.="places".'$and'."(.q(".$place."),.type(7));start=0;count=5".$myappid;
	//echo $string;
	$string=URLencode($string);
	//echo $string;
	//echo "<br><br><br>";
}
else //check zip only
{
	$string.="concordance/usps/".$place.$myappid;
	$stirng=URLencode($string);
	//echo $string;
	//echo "<br>";
}

if(@simplexml_load_file($string))
 {
   //echo"success!";
   $xml=simplexml_load_file($string);
   
 // echo file_get_contents($string);
  // print_r($xml);
/*find array weoid: */
 $weoid=array();//create weoid array to store weoid value
 $num=0;
 if($type=="city") //case one : input the city
 {
		foreach($xml->children() as $child)
		{
		 //echo $child->getName().":".$child."<br>";
   
   
			foreach($child->children() as $childatt)
			{
        
					if($childatt->getName()=="woeid")
					{
				  
						$weoid[$num]=$childatt;
						// echo "----".$weoid[$num]->getName().":".$weoid[$num]."<br>";
						$num=$num+1;
						}
			}
		}
 }
 else
 { 
		foreach($xml->children() as $child)
		{
		     if($child->getName()=="woeid");
			 {
			     $weoid[$num]=$child;
				 // echo "----".$weoid[$num]->getName().":".$weoid[$num]."<br>";
				 $num=$num+1;
			}
		}
}

 /*check the valid number of city weather information*/
for($i=0;$i<count($weoid);$i++)
 {
	$string2="http://weather.yahooapis.com/forecastrss?";
	
	$string2.="w=".$weoid[$i]."&u=".$temUnit;
	//echo $string2;
	$string2=URLencode($string2);
	//echo "<br><br>".file_get_contents($string2);
	$entries=simplexml_load_file($string2);
	$string2=URLencode($string2);
//	print_r($entries);
	if($entries->channel->title=="Yahoo! Weather - Error")//check if the information of city exist
	{
	  $num=$num-1;  
	}
}


/*create another url for each city to access yahoo forecast information*/


 
 $newxml=header("Content-type: text/xml");
 $newxml.="<?xml version='1.0' encoding='UTF-8'?><weather>";

   for($i=0;$i<count($weoid);$i++)
 {
		$str="http://weather.yahooapis.com/forecastrss?";
		
		$str.="w=".$weoid[$i]."&u=".$temUnit;
		
		//echo "<br><br>".file_get_contents($str)."<br><br>";

		$string2=URLencode($str);
		//echo $string2."<br>";
		$entries=simplexml_load_file($string2);
		
		$str = str_replace("&", "&amp;", $str);
		$newxml.="<feed>".$str."</feed>";
		$namespaces=$entries->getNamespaces(true);
		//var_dump( $namespaces);
	if($entries->channel->title=="Yahoo! Weather - Error")//check if the information of city exist
	{
	  
	  continue;
	}
	/*link detail presentation*/
		$link=$entries->channel->link;
		
		$newxml.="<link>".$link."</link>";
        
		
	/*city region and country present*/
	$dc=$entries->channel->children($namespaces['yweather'])->location;
	//echo $dc->getName();
	$att=$dc->attributes();
		if($att['city']=="")
		$att['city']="N/A";
		if($att['region']=="")
		$att['region']="N/A";
		if($att['country']=="")
		$att['country']="N/A";
	$newxml.="<location city=\"".$att['city']."\" region=\"".$att['region']."\" country=\"".$att['country']."\"/>";
   
	
	/*tempreture presentation*/
		$temp=$entries->channel->item->children($namespaces['yweather'])->condition;
		$tempatt=$temp->attributes();
			if($tempatt['temp']=='')
			$tempatt['temp']='N/A';
			if($tempatt['text']=='')
			$tempatt['text']='N/A';
		$tem_unit=$entries->channel->children($namespaces['yweather'])->units;
		$units=$tem_unit->attributes();
			if($units['temperature']=="")
			$units['temperature']="N/A";
	$newxml.="<units temperature=\"".$units."\"/>";
	$newxml.="<condition text=\"".$tempatt['text']."\" temp=\"".$tempatt['temp']."\"/>";
	
	/*image presentatoin*/
		$description=$entries->channel->item->description;
		$imgpattern= '/src="(.*?)"/i';
		preg_match($imgpattern,$description,$matches);
		$image=$matches[1];
		$text=$entries->channel->item->children($namespaces['yweather'])->condition;
		$text_value=$text->attributes();
    $newxml.="<img>".$image."</img>";
	
	/*five day information*/
	   $forecast=$entries->channel->item->children($namespaces['yweather'])->forecast;
	   for($i=0;$i<5;$i++)
	   {
	     $info=$forecast[$i]->attributes();
	     $newxml.="<forecast day=\"".$info['day']."\" low=\"".$info['low']."\" high=\"".$info['high']."\" text=\"".$info['text']."\"/>";
	   }
	 $newxml.="</weather>";
	 echo $newxml;
  
  }
}
else
{  $err=header("Content-type: text/xml");
 $err.="<?xml version='1.0' encoding='UTF-8'?><info>No Information Found></info>";
 echo $err;
 }
	
?>


 

