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
      // Look at all of our greetings
        for (AirQualitySample aqi : aqis) {
            String airQualityIndex;
            String message;
            String timeStamp;
            DateTime d = new DateTime(aqi.getTimestamp() * 1000, DateTimeZone.UTC);
            String timeText =d.toString("h a ");
            String datetimeText = String.format("at %s", timeText);
            pageContext.setAttribute("airQualityIndex",aqi.getAqi()+" AQI");
            pageContext.setAttribute("message", "Level: "+aqi.getMessage());
            pageContext.setAttribute("timeStamp",datetimeText);
        }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Khói Bụi. A real-time, accurate air quality app for Ho Chi Minh City.">
    <meta name="author" content="">
     <meta property="og:title" content="Khói Bụi" />
     <meta property="og:site_name" content="Khói Bụi"/>
     <meta property="og:url" content="http://khoibui.co" />
     <meta property="og:image" content="http://i.imgur.com/sN1B51f.png" />
     <meta property="og:description" />
    <title>Khói Bụi</title>
    <!-- favicon -->
    <link rel="shortcut icon" href="img/favicon.ico"/>
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
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-main-collapse">
                Menu <i class="fa fa-bars"></i>
            </button>
            <a class="navbar-brand">
                <span class="light">Air Quality Index ${fn:escapeXml(timeStamp)} is ${fn:escapeXml(airQualityIndex)}</span>
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
                <li>
                    <a class="page-scroll" href="/">VN</a>
                </li>

                    <a href='https://play.google.com/store/apps/details?id=com.disruptorbeam.StarTrekTimelines&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' height="50" width="129" src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>
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
                <br/>
                    <h1 class="brand-heading">
Khói Bụi</h1>
                    <div class="jumbotron" style="background-color: rgba(0, 0, 0, 0.5);
">
                        <p class="intro-text">Breathe Easy.</br>
                          A real-time, accurate air quality app for Ho Chi Minh City is here. </br>
                          <span class="small"></span>
                          </p>

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
                        <h1 class="brand-heading">About Khói Bụi</h1>
                        <p class="light">Khói Bụi measures the Air Quality Index (<a href="https://en.wikipedia.org/wiki/Air_quality_index">AQI</a>), a number used by governmental and international health organizations to indicate air pollution.
                        When pollution blankets the city, it affects everyone, regardless of where you live in Ho Chi Minh City. Groups most at risk are infants, the elderly and people with chronic respiratory illness.</p>
                        <h4 class="center"><font color="#4FC3F7">Accurate Air Quality Monitoring</font></h4>
                        <p class="light">The app fetches AQI in  real-time from the US Embassy air quality monitoring station in District 1, Ho Chi Minh City. </p>
                    </div>
                </div>

                <div class="col s12 m4">
                    <div class="icon-block">
                        <h4 class="center"><font color="#80D8FF">Instant Alert</font></h4>
                        <p class="light">Real-time notifications sent directly to your phone when a significant change in air quality occurs, helping you to safely plan your outdoor activities.</p>
                    </div>
                </div>

                <div class="col s12 m4">
                    <div class="icon-block">
                        <h4 class="center"><font color="#80D8FF">Easy to Use</font></h4>
                        <p class="light">The app is intuitive and simple, giving you the only information you need to breathe easy in Ho Chi Minh city.</p>
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
                    <h2>Download Khói Bụi</h2>
                    <p>You can download Khói Bụi for free from the Google Play Store.</p>
                    <a href='https://play.google.com/store/apps/details?id=com.disruptorbeam.StarTrekTimelines&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' height="100" width="255" src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>
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
            <p><a href="mailto:feedback@startbootstrap.com">gholadr@gmail.com</a>
            </p>
            <!--
            <ul class="list-inline banner-social-buttons">
                <li>
                    <a href="https://twitter.com/SBootstrap" class="btn btn-default btn-lg"><i class="fa fa-twitter fa-fw"></i> <span class="network-name">Twitter</span></a>
                </li>

                <li>
                    <a href="https://github.com/gholap" class="btn btn-default btn-lg"><i class="fa fa-github fa-fw"></i> <span class="network-name">Github</span></a>
                </li>

                <li>
                    <a href="https://plus.google.com/u/2/106428478761684802811/posts" class="btn btn-default btn-lg"><i class="fa fa-google-plus fa-fw"></i> <span class="network-name">Google+</span></a>
                </li>
            </ul>
        </div>
    </div>
</section>



<!-- Footer -->
<footer>
    <div class="center">
        <p>Copyright &copy; Khoi Bui 2016</p>
    </div>
</footer>
<!--
<!-- jQuery -->
<script src="vendor/jquery/jquery.js"></script>

<!-- Bootstrap Core JavaScript -->
<script src="vendor/bootstrap/js/bootstrap.min.js"></script>

<!--
<!-- Plugin JavaScript -->
<script src="http://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>


<!-- Google Maps API Key - Use your own API key to enable the map feature. More information on the Google Maps API can be found at https://developers.google.com/maps/ -->
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCRngKslUGJTlibkQ3FkfTxj3Xss1UlZDA&sensor=false"></script>

<!-- Theme JavaScript -->
<script src="js/grayscale.min.js"></script>

</body>

</html>
