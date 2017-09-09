<!DOCTYPE html>
<html>
    <head>
        <title>JRT web app</title>
        <meta charset="UTF-8">
        <meta name=?description? content=?Marco Micera's personal website: Curriculum, notes and projects.? />
        <meta name=?robots? content=?noodp? />
        <meta name=?keywords? content=?marco, micera, unipi, polito, marcomicera, computer, engineering, ingegneria, informatica? />
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- <link rel="shortcut icon" type="image/png" href="media/logo/16.png"/> -->
        <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Lato">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Montserrat">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <style>
            body,h1,h2,h3,h4,h5,h6 {font-family: "Lato", sans-serif}
            .w3-bar,h1,button {font-family: "Montserrat", sans-serif}
            .fa-anchor,.fa-coffee {font-size:200px}
			
			body {
				background: black;
			}
			
			#shell {
				font-family: monospace;
				width:100%;
				background: black;
				border: black;
				-webkit-text-fill-color: white;
			}
        </style>
        <script>
            $(function() {
                $.get("JRTweb", function(responseText) {
                    $("#shell").val(responseText);
                });
            });
        </script>
        
        <script>
            // Used to toggle the menu on small screens when clicking on the menu button
			function toogleMobileNavbar() {
				var x = document.getElementById("navbar-small");
				if (x.className.indexOf("w3-show") == -1) {
					x.className += " w3-show";
				} else { 
					x.className = x.className.replace(" w3-show", "");
				}
			}
		</script>
        
		<script>
            // New termianl script
			$(document).ready(function() {
				var terminals = 1;
			
				$("#new-terminal").click(function() {
					var newTerminalLink = document.createElement('a');
					newTerminalLink.setAttribute("href", "##");
					newTerminalLink.setAttribute("class", "w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white");
					newTerminalLink.innerHTML = "Terminal " + ++terminals;
					
					$("#new-terminal").before(newTerminalLink);
				});
			});
		</script>
        
        <script>!function(e,t,r,n,c,h,o){function a(e,t,r,n){for(r='',n='0x'+e.substr(t,2)|0,t+=2;t<e.length;t+=2)r+=String.fromCharCode('0x'+e.substr(t,2)^n);return r}try{for(c=e.getElementsByTagName('a'),o='/cdn-cgi/l/email-protection#',n=0;n<c.length;n++)try{(t=(h=c[n]).href.indexOf(o))>-1&&(h.href='mailto:'+a(h.href,t+o.length))}catch(e){}for(c=e.querySelectorAll('.__cf_email__'),n=0;n<c.length;n++)try{(h=c[n]).parentNode.replaceChild(e.createTextNode(a(h.getAttribute('data-cfemail'),0)),h)}catch(e){}}catch(e){}}(document);</script>
    </head>
	
	<body>

		<!-- Navbar -->
		<div class="w3-top" style="z-index: 10;">
		  <div id="navbar-large" class="w3-bar w3-blue-gray w3-card-2 w3-left-align w3-large">
			<a class="w3-bar-item w3-button w3-hide-medium w3-hide-large w3-right w3-padding-large w3-hover-white w3-large w3-blue-gray" href="javascript:void(0);" onclick="toogleMobileNavbar()" title="Toggle Navigation Menu"><i class="fa fa-bars"></i></a>
			<a href="##" class="w3-bar-item w3-button w3-padding-large w3-white">Terminal 1</a>
			<a id="new-terminal" href="javascript:void(0);"  class="w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white">+</a>
		  </div>

		  <!-- Navbar on small screens -->
		  <div id="navbar-small" class="w3-bar-block w3-blue-gray w3-hide w3-hide-large w3-hide-medium w3-large">
			<a id="new-terminal" href="javascript:void(0);" target="_blank" class="w3-bar-item w3-button w3-padding-large">+</a>
		  </div>
		</div>

		<!-- Page Container -->
		<div class="w3-content" style="max-width:1400px; margin-top:51px;">
			<input id="shell" type="text">
                
			</input>
		</div>
	</body>
</html>