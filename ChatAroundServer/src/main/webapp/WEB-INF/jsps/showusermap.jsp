<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
 <style type="text/css">
      html { height: 100% }
      body { height: 100%; margin: 0; padding: 0 }
      #map_canvas { height: 100% }
    </style>
<script type="text/javascript"
      src="http://maps.googleapis.com/maps/api/js?key=AIzaSyA7l7FhUqqrEDkokqcMIM8-2xzTKlVZJT4&sensor=false">
    </script>
<script type="text/javascript">
	var parentPosition = new google.maps.LatLng(23.079731762449878, 78.837890625);
	var depth = 2;
    var position;
    var marker;
    var map;
	var rectangle;
      function initialize() {
        var myOptions = {
          center: parentPosition,
          zoom: depth,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById("map_canvas"),myOptions);

        <c:forEach items="${AllUsers}" var="oneUser">
        	position = new google.maps.LatLng(<c:out value='${oneUser.lattitude}' />, <c:out value='${oneUser.longitude}' />);
        	 marker = createMarker(position,"<c:out value='${oneUser.nickName}' />");
        </c:forEach>
        
        <c:forEach items="${AllRectangles}" var="oneRectangle">
    	 topPosition = new google.maps.LatLng(<c:out value='${oneRectangle.topLattitude}' />, <c:out value='${oneRectangle.topLongitude}' />);
    	 bottomPosition = new google.maps.LatLng(<c:out value='${oneRectangle.bottomLattitude}' />, <c:out value='${oneRectangle.bottomLongitude}' />);
    	 
    	 rectangle = createRectangle(topPosition,bottomPosition,"Rectangle");
		 
		var contentOfInfoWindow = "topPosition="+topPosition+"<br>"+"bottomPosition="+bottomPosition;
			var infowindow = new google.maps.InfoWindow({
				content: contentOfInfoWindow
			});

		 google.maps.event.addListener(rectangle, 'click', function() {
			  infowindow.open(map);
			});

    </c:forEach>
        LatLngBounds
	  
      }
      function createMarker(position, name) {
    	  var marker = new google.maps.Marker({
              map:map,
              draggable:false,
              animation: google.maps.Animation.DROP,
              position: position,
              title: name
            });
			var contentOfInfoWindow = "position="+position+"<br>"+"Name="+name;
			var infowindow = new google.maps.InfoWindow({
				content: contentOfInfoWindow
			});
			google.maps.event.addListener(marker, 'click', function() {
			  infowindow.open(map,marker);
			});
    	    return marker;
    	}
      function createRectangle(topPosition,bottomPosition, tooltip) {
	  //alert("topPosition="+topPosition);
	  //alert("bottomPosition="+bottomPosition);
    	  var rectangle = new google.maps.Rectangle();
		  var rectangleBoudry = new google.maps.LatLngBounds(topPosition,bottomPosition);
		  //alert("bottomPosition="+bottomPosition);
    	  var rectOptions = {
    		      strokeColor: "#FF0000",
    		      strokeOpacity: 0.8,
    		      strokeWeight: 2,
    		      fillColor: "#FF0000",
    		      fillOpacity: 0.35,
    		      map: map,
				  clickable: true,
    		      bounds: rectangleBoudry
    		    };
    		    rectangle.setOptions(rectOptions);
				
			return rectangle;
    	}
		

    </script>	
</head>
<body onload="initialize()">
<div id="map_canvas" style="width: 100%;height: 100%"></div>
</body>
</html>
