<#assign content>
<!-- NAV BAR BEGINS HERE -->
<nav id="sesh-nav" class="navbar navbar-fixed-top navbar-default">
  <div class="container-fluid main-navbar">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle sesh-navbar-toggle" data-toggle="collapse" data-target="#sesh-navbar">
        <span class="icon-bar sesh-icon-bar"></span>
        <span class="icon-bar sesh-icon-bar"></span>
        <span class="icon-bar sesh-icon-bar"></span>                       
      </button>
      <a class="navbar-brand sesh-navbar-font page-scroll" id="sesh-brand" href="#page-top">Sesh</a>
      <!-- navbar-header -->
    </div>
    <div class="collapse navbar-collapse navbar-right" id="sesh-navbar">
      <ul class="nav navbar-nav navbar-right">
        <li><a class="sesh-navbar-font page-scroll" href="#experience">Experience</a></li>
        <li><a class="sesh-navbar-font page-scroll" href="#">What We Do</a></li>
        <li><a class="sesh-navbar-font page-scroll" href="#">Features</a></li>
      </ul>
    <!-- collapse navbar -->
    </div>
    <!-- container-fluid -->
  </div>
<!-- NAV BAR ENDS HERE -->
</nav>

<header>
    <div class="container">
        <div class="row">
            <div class="col-sm-7">
                <div class="header-content">
                    <div class="header-content-inner">
                        <h1>Sesh will change how you listen to music with other people.</h1>
                        <a href="#download" class="btn page-scroll">Login with Spotify</a>
                    </div>
                </div>
            </div>
            <div class="col-sm-5">
                <div class="device-container">
                    <div class="device-mockup iphone6_plus portrait white">
                        <div class="device">
                            <div class="screen">
                                <!-- Demo image for screen mockup, you can put an image here, some HTML, an animation, video, or anything else! -->
                                <!-- <img src="img/demo-screen-1.jpg" class="img-responsive" alt=""> -->
                            </div>
                            <div class="button">
                                <!-- You can hook the "home button" to some JavaScript events or just remove it -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>

<section id="experience">
    <div class="row">
        
    </div>
    <div class="row">
        
    </div>
</section>

<section id="experience">
    <div class="slideshow-container">
        <div class="experience-slide">
            <p>Sesh respects the inherit dynamics that typically exist within a social music-listening environment. A sesh will always have one host with executive control over the order of songs and the playback status.</p>
        </div>
        <div class="experience-slide">
            <p>A guest can influence what's going to be played next by making a song request. The other guests in the sesh can upvote or downvote requests to further influence their listening experience.</p>
        </div>
        <div class="experience-slide">
            <p>Song requests will be ranked based on how popular they are at the moment amongst the people attending the sesh. The host can then make informed decisions on what to play next based on these requests.</p>
        </div>

        <div class="experience-slide">
            <p>The Sesh experience combats the discontinuity created when multiple people are trying to take over the aux or make sure their song is played next, thereby promoting group consensus on music selection.</p>
        </div>

        <div id="dots" style="text-align:center">
          <span class="dot" onclick="currentSlide(1)"></span> 
          <span class="dot" onclick="currentSlide(2)"></span> 
          <span class="dot" onclick="currentSlide(3)"></span> 
          <span class="dot" onclick="currentSlide(4)"></span> 
        </div>

    </div>
</section>

<!-- Bootstrap Core JavaScript -->
<script src="../lib/bootstrap/js/bootstrap.min.js"></script>

<!-- Plugin JavaScript -->
<script src="../js/jquery.easing.min.js"></script>

<!-- Theme JavaScript -->
<script src="../js/main.js"></script>


</#assign>
<#include "main.ftl">