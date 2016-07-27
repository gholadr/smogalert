<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:fb="http://ogp.me/ns/fb#" >
 <head>
    <meta property="og:image" content="http://i.imgur.com/sN1B51f.png" />
 <head/>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="co.ghola.backend.entity.AirQualitySample" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ page import="org.joda.time.*" %>

<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the Greetings belonging to the selected Guestbook.
      List<AirQualitySample> aqis = ObjectifyService.ofy()
          .load()
          .type(AirQualitySample.class) // We want only Greetings
          .order("-ts")       // Most recent first - date is indexed.
          .limit(1)             // Only show 5 of them.
          .list();
    if (aqis.isEmpty()) {
%>
<strong>Oh snap!</strong> <a href="#" class="alert-link">No Samples Found</a> Please Start the Fetch RSS again
<%
    } else {

      // Look at all of our greetings
        for (AirQualitySample aqi : aqis) {
            String airQualityIndex;
            String message;
            String timeStamp;
            DateTime d = new DateTime(aqi.getTimestamp() * 1000, DateTimeZone.UTC);
            String dateText =d.toString("MM/dd");
            String timeText =d.toString("h a");
            String datetimeText = String.format("%s at %s", dateText, timeText);
            pageContext.setAttribute("airQualityIndex",aqi.getAqi()+" AQI");
            pageContext.setAttribute("message", "Level: "+aqi.getMessage());
            pageContext.setAttribute("timeStamp","Time: "+datetimeText);
            pageContext.setAttribute("location","Location: "+"Ho Chi Minh City");
            pageContext.setAttribute("by","AQI sample measured by air quality sensor from the US Embassy");
%>

<%
        }
    }
%>
</html>


<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Khoi Bui</title>


    <!-- Bootstrap Core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href="http://fonts.googleapis.com/css?family=Lora:400,700,400italic,700italic" rel="stylesheet" type="text/css">
    <link href="http://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">

    <!-- Theme CSS -->
    <link href="css/grayscale.min.css" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body id="page-top" data-spy="scroll" data-target=".navbar-fixed-top">

<!-- Navigation -->
<nav class="navbar navbar-custom navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-main-collapse">
                Menu <i class="fa fa-bars"></i>
            </button>
            <a class="navbar-brand page-scroll" href="#page-top">
                <span class="light">Air Quality Index is now :&emsp;</span><b>${fn:escapeXml(airQualityIndex)}</b>
            </a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse navbar-right navbar-main-collapse">
            <ul class="nav navbar-nav">
                <!-- Hidden li included to remove active class from about link when scrolled up past about section -->
                <li class="hidden">
                    <a href="#page-top"></a>
                </li>
                <li>
                    <a class="page-scroll" href="#about">About</a>
                </li>
                <li>
                    <a class="page-scroll" href="#download">Download</a>
                </li>
                <li>
                    <a class="page-scroll" href="#contact">Contact</a>
                </li>
            </ul>
        </div>
        <!-- /.navbar-collapse -->
    </div>
    <!-- /.container -->
</nav>

<!-- Intro Header -->
<header class="intro">
    <div class="intro-body">
        <div class="container">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <h1 class="brand-heading">Khoi Bui</h1>
                    <div class="jumbotron" style="background-color: rgba(0, 0, 0, 0.5);
">
                        <p class="intro-text">The most powerful app to check Air Quality
                            in <br>Ho Chi Minh</br></p>
                        <a href="#about" class="btn btn-circle page-scroll">
                            <i class="fa fa-angle-double-down animated" style="margin-top:8px"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>

<!-- About Section -->
<section id="about" class="container content-section text-center">
    <div class="container">
        <div class="section">

            <!--   Icon Section   -->
            <div class="row">
                <div class="col s12 m4">
                    <div class="icon-block">
                        <h1 class="brand-heading">About Khoi Bui</h1>
                        <h4 class="center"><font color="#4FC3F7">Update Frequenly</font></h4>
                        <p class="light">We did most of the heavy lifting for you to provide real time information about Air Quality in Ho Chi Minh.</p>
                    </div>
                </div>

                <div class="col s12 m4">
                    <div class="icon-block">
                        <h4 class="center"><font color="#80D8FF">User Experience Focused</font></h4>
                        <p class="light">By utilizing elements and principles of Material Design, we using Flat Model Design that always keep up with the latest trends. By doing this will enhance our UI and UX experience.</p>
                    </div>
                </div>

                <div class="col s12 m4">
                    <div class="icon-block">
                        <h4 class="center"><font color="#80D8FF">Easy to Use</font></h4>
                        <p class="light">Just by clicking on Air Quality on the navigation bar. Will take you to more Information Air Quality page.</p>
                    </div>
                </div>
            </div>

        </div>
    </div>
</section>

<!-- Download Section -->
<section id="download" class="content-section text-center">
    <div class="download-section">
        <div class="container">
            <div class="col-lg-8 col-lg-offset-2">
            <div class="jumbotron" style="background-color: rgba(0, 0, 0, 0.5);
            ">
                    <h2>Download Khoi Bui</h2>
                    <p>You can download Khoi Bui for free on the preview page at Google Play Store.</p>
                    <a href="http://startbootstrap.com/template-overviews/grayscale/" class="btn btn-default btn-lg" style="background-color:="#4FC3F7"><FONT COLOR="#4FC3F7">Visit Download Page</font></a>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Contact Section -->
<section id="contact" class="container content-section text-center">
    <div class="row">
        <div class="col-lg-8 col-lg-offset-2">
            <h2>Contact Us</h2>
            <p>Feel free to email us to provide some feedback on our app, or to just say hello!</p>
            <p><a href="mailto:feedback@startbootstrap.com">david@mysquar.com</a>
            </p>
            <ul class="list-inline banner-social-buttons">
                <li>
                    <a href="https://twitter.com/SBootstrap" class="btn btn-default btn-lg"><i class="fa fa-twitter fa-fw"></i> <span class="network-name">Twitter</span></a>
                </li>
                <li>
                    <a href="https://github.com/IronSummitMedia/startbootstrap" class="btn btn-default btn-lg"><i class="fa fa-github fa-fw"></i> <span class="network-name">Github</span></a>
                </li>
                <li>
                    <a href="https://plus.google.com/+Startbootstrap/posts" class="btn btn-default btn-lg"><i class="fa fa-google-plus fa-fw"></i> <span class="network-name">Google+</span></a>
                </li>
            </ul>
        </div>
    </div>
</section>

<!-- Map Section -->
<div class="container" style="margin-top:20px">
    <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3919.574792560306!2d106.68525451463888!3d10.767216992327782!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f181a063ce9%3A0xe6cc50389f63d4d1!2zQ2FvIOG7kWMgdsSDbiBwaMOybmcgVmltZWRpbWV4IEhvw6AgQsOsbmg!5e0!3m2!1svi!2s!4v1469589382737" width="1113" height="500" frameborder="0" style="border:0" allowfullscreen></iframe>
</div>

<!-- Footer -->
<footer>
    <div class="container text-center">
        <p>Copyright &copy; Khoi Bui 2016</p>
    </div>
</footer>

<!-- jQuery -->
<script src="vendor/jquery/jquery.js"></script>

<!-- Bootstrap Core JavaScript -->
<script src="vendor/bootstrap/js/bootstrap.min.js"></script>

<!-- Plugin JavaScript -->
<script src="http://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>

<!-- Google Maps API Key - Use your own API key to enable the map feature. More information on the Google Maps API can be found at https://developers.google.com/maps/ -->
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCRngKslUGJTlibkQ3FkfTxj3Xss1UlZDA&sensor=false"></script>

<!-- Theme JavaScript -->
<script src="js/grayscale.min.js"></script>

</body>

</html>
