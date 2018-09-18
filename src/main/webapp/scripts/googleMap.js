/**
 * Reload Google Map
 */
function reloadMap(latitude, longitude, mapType, zoom) {
	var mapTypeId = google.maps.MapTypeId.ROADMAP;
	if (mapType == 'SATELLITE') {
		mapTypeId = google.maps.MapTypeId.SATELLITE;
	} else if (mapType == 'HYBRID') {
		mapTypeId = google.maps.MapTypeId.HYBRID;
	} else if (mapType == 'TERRAIN') {
		mapTypeId = google.maps.MapTypeId.TERRAIN;
	}
	var mapOptions = {
		zoom : parseInt(zoom),
		zoomControl : true,
		center : new google.maps.LatLng(latitude, longitude),
		mapTypeId : mapTypeId,
		disableDefaultUI : true,
		scrollwheel : true,
		disableDoubleClickZoom : true,
	};
	var map = new google.maps.Map(document.getElementById("map_canvas"),
			mapOptions);
	keepMapParameter(map, $('#latitude'), $('#longitude'), $('#mapType'), $('#zoom'));
	return map;
}

function keepMapParameter(map, saveLat, saveLng, saveMapType, saveZoom) {
	// lưu các thông số của bản đồ lại trong các Html input để sử dụng trong lần
	// load sau
	google.maps.event.addListener(map, 'center_changed', function() {
		var center = map.getCenter();
		saveLat.val(center.lat());
		saveLng.val(center.lng());
	});
	google.maps.event.addListener(map, 'zoom_changed', function() {
		saveZoom.val(map.getZoom());
	});
}

function createMarker(langtitude, longtitude, map, image, description) {
	var myLatlng = new google.maps.LatLng(langtitude, longtitude);
	var marker = new google.maps.Marker({
		position : myLatlng,
		map : map
	});
	marker.setIcon(image);
	google.maps.event.addListener(marker, 'click', function() {
		map.setZoom(map.getZoom()+1);
	    map.setCenter(marker.getPosition());
	});
	if (description != undefined && description != '') {
		var infowindow = new google.maps.InfoWindow({
			content : description
		});
		google.maps.event.addListener(marker, 'mouseover', function() {
			infowindow.open(map, marker);
		});
		google.maps.event.addListener(marker, 'mouseout', function() {
			infowindow.close();
		});
	}
	return marker;
}

function createMarkerNumbered(index, langtitude, longtitude, map, image, description) {
	//Tao label icon 
	var myLatlng = new google.maps.LatLng(langtitude, longtitude);
	var marker = new MarkerWithLabel({
	    position: myLatlng,
	    map: map,	   
	    labelContent: ''+index,
	    labelAnchor: new google.maps.Point(25, 45),
	    labelClass: "labels", // the CSS class for the label
	    labelInBackground: false,
	    icon: image
	  });
//	var marker = new google.maps.Marker({
//		position : myLatlng,
//		map : map,
//		icon : image
//	});
	//marker.setIcon(image);
	google.maps.event.addListener(marker, 'click', function() {
		map.setZoom(map.getZoom()+1);
	    map.setCenter(marker.getPosition());
	});
	
	if (description != undefined && description != '') {
		var infowindow = new google.maps.InfoWindow({
			content : description
		});
		google.maps.event.addListener(marker, 'mouseover', function() {
			infowindow.open(map, marker);
		});
		google.maps.event.addListener(marker, 'mouseout', function() {
			infowindow.close();
		});
	}
	return marker;
}

function createMarkerImage(imagePath, originWidth, originHeight, scaledWidth,
		scaleHeight) {
	// var markerImage = new google.maps.MarkerImage(image, new
	// google.maps.Size(originWidth, originHeight),
	// new google.maps.Point(0, 0), new google.maps.Point(originWidth,
	// originHeight),
	// new google.maps.Size(scaledWidth, scaleHeight));
	var icon = {
		url : imagePath,
		labelOrigin : new google.maps.Point(5,5),
		scaledSize : new google.maps.Size(scaledWidth, scaleHeight)
	};
	
	return icon;
}

function createMarkerFromDsThongTinCskh(dsThongTinCskh, map, storeImage,
		workerImage) {
	var description = "";
	var index = 1;
	 $.each(dsThongTinCskh, function(i, ttCskh) {
		/*if (ttCskh.viDoDBH != 0) {
			createMarker(ttCskh.viDoDBH, ttCskh.kinhDoDBH, map, storeImage,
					ttCskh.tenDBH);
		}*/
		if (ttCskh.viDoNv != 0) {
			description = "NVBH: "+ ttCskh.tenNV + "<br/>";
			description += "Tên: " + ttCskh.tenDBH + "<br/>"
				+ "Địa chỉ: " + ttCskh.diaChi + "<br/>"
				+ "Số ĐT: " + ttCskh.soDienThoai + "<br/>";
			if (ttCskh.thoiGianCskh != '') {
				description += "Chăm sóc: " + ttCskh.thoiGianCskh + "<br/>"
			}
			if (ttCskh.thoiGianBH != '') {
				description += "Bán hàng: " + ttCskh.thoiGianBH + "<br/>";
			}
			var image = storeImage;
			if(ttCskh.flagPhone==1){
				image = workerImage;
			}
			createMarker(ttCskh.viDo, ttCskh.kinhDo, map,
					image, description);
			vd = ttCskh.viDo;
			kd = ttCskh.kinhDo;
		}
	});
	 map.setCenter(new google.maps.LatLng(vd, kd)); 
}

function createMarkerFromDsLoTrinh(dsLoTrinh, map, pointImage,pointStartImage,pointEndImage) {
	var description = "";
	var isStart = true;
	var kd = 0, vd = 0;
	 $.each(dsLoTrinh, function(i, loTrinh) {
		if (loTrinh.viDo != 0) {
			description = loTrinh.tenNV  + "<br/>" + loTrinh.ngayGio;
			if(isStart){
				createMarker(loTrinh.viDo, loTrinh.kinhDo, map, pointStartImage,
						description);
				isStart = false;
			} else {
				vd = loTrinh.viDo;
				kd = loTrinh.kinhDo;
				createMarker(loTrinh.viDo, loTrinh.kinhDo, map, pointImage,
					description);
			}
		}
		
	});
	 if(vd!=0){
		createMarker(vd, kd, map, pointEndImage,
					description);
		map.setCenter(new google.maps.LatLng(vd, kd));
	 } 
}

function mapZoomIn(event) {
	var renderArea = $('#renderAreaForGoogleMap');
	var fullScreenContainer = $('#fullscreenContainer');
	var mapCanvas = $('#map_canvas');
	renderArea = renderArea.detach();
	fullScreenContainer.append(renderArea);
	renderArea.css('marginTop', '0px');
	mapCanvas.css('height', '700px');
	fullScreenContainer.show();
	updateMapZoomLink(event);
	var map = reloadMap($('#latitude').val(), $('#longitude').val(), $(
			'#mapType').val(), $('#zoom').val());
	createMarkerFromDsThongTinCskh(dsThongTinCskh, map, storeImage, workerImage);
	createMarkerFromDsLoTrinh(dsLoTrinh, map,pointImage,tenNV,pointStartImage,pointEndImage);
}

function mapZoomOut(event) {
	var renderArea = $('#renderAreaForGoogleMap');
	var mapContainer = $('#content');
	var fullScreenContainer = $('#fullscreenContainer');
	var mapCanvas = $('#map_canvas');
	renderArea = renderArea.detach();
	mapContainer.append(renderArea);
	renderArea.css('marginTop', '20px');
	mapCanvas.css('height', '700px');
	fullScreenContainer.hide();
	updateMapZoomLink(event);
	var map = reloadMap($('#latitude').val(), $('#longitude').val(), $(
			'#mapType').val(), $('#zoom').val());
	createMarkerFromDsThongTinCskh(dsThongTinCskh, map, storeImage, workerImage);
	createMarkerFromDsLoTrinh(dsLoTrinh, map,pointImage,tenNV,pointStartImage,pointEndImage);
}

function updateMapZoomLink(event) {
	var target = $('#zoomLink');
	var renderArea = $('#renderAreaForGoogleMap');
	// Remove old class
	target.removeClass('zoomOut');
	target.removeClass('zoomIn');
	// Zoom column
	var zoomLinkColumn = $('#zoomLinkFilterColumn');
	zoomLinkColumn.remove();
	// Check status
	if (renderArea.parent().attr('id') == 'fullscreenContainer') {
		// Event
		target.attr('onclick', 'mapZoomOut(event)');
		// Class
		target.addClass('zoomOut');
		// Parent
		$('#zoomLinkFullCotainer').append(zoomLinkColumn);
		// Label
		var label = $('#zoomOutLabel').val();
		target.html(label);
	} else {
		// Event
		target.attr('onclick', 'mapZoomIn(event)');
		// Class
		target.addClass('zoomIn');
		// Parent
		$('#filterContainer').append(zoomLinkColumn);
		// Label
		var label = $('#zoomInLabel').val();
		target.html(label);
	}
}