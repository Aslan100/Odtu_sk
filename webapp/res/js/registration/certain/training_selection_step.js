var map, marker;
var zoomLevel = 16;

$(document).ready(function()
{
	fetchTrainings();
	
	$("form#training_selection_form button#submit_button").click(function(event)
	{
		event.preventDefault();
		
		let formIndex = $(this).parent().parent().index();
		let stepRunner = new BordomorAjaxRunner(certainRegistrationStep4URI, $("form#training_selection_form").serialize());
		stepRunner.init();
		stepRunner.setFailDialog();
		
		stepRunner.setCallbacks(function(isValid, response)
		{
			if(isValid)
				goToPaymentSchemaSelectionStep(formIndex + 1);
			else
			{
				if(new RegExp(noDataErrorRegExp).test(response))
				{
					let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Kayıt İşlemi Sırasında Bir Hata Oluştu", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
					{
						destroyBordomorFullScreenDialog(negativeDialog);
						negativeDialog = null;
					});
				}
				else
					makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
			}
		}, null);
	
		stepRunner.run(responseErrorRegExp);
	});
	
	$("form#training_selection_form button#refresh_button").click(event => fetchTrainings());
});

function fetchTrainings()
{
	let trainingsRunner = new BordomorAjaxRunner(suitableTrainingsForRegistrationDataURI, "code=" + $("input[name='code']").val());
	trainingsRunner.init();
	
	trainingsRunner.setCallbacks(function(isValid, response)
	{
		$("form#training_selection_form section#training_list").removeClass("loading");
		
		if(isValid)
		{
			let trainingEls = response.getElementsByTagName("training");
			
			for(let i = 0; i < trainingEls.length; i++)
			{
				let nextTraining = new Training();
				nextTraining.parseFromXMLElement(trainingEls[i]);
				
				let trainingEl = "<div class='input_container'>";
				trainingEl += "<input type='radio' id='training_radio_" + (i + 1) + "' name='training' value='" + nextTraining.id + "' data-lat='" + nextTraining.location.address.latitude + "' data-lng = '" + nextTraining.location.address.longitude + "'>";
				trainingEl += "<label for='training_radio_" + (i + 1) + "'>";
				trainingEl += "<strong>" + nextTraining.team.name + "</strong><br>";
				trainingEl += getDateTimeString(new Date(nextTraining.startTime)) + " - " + getDateTimeString(new Date(nextTraining.endTime)) + "<br>";
				trainingEl += "<span>" + nextTraining.location.address.addressString + "<br>";
				trainingEl += nextTraining.location.address.district != null ? nextTraining.location.address.district.name + "/" : "";
				trainingEl += nextTraining.location.address.city.name + "</span>";
				trainingEl += "</label>";
				trainingEl += "</div>";
				
				$("form#training_selection_form section#training_list").append(trainingEl);
			}
			
			$("form#training_selection_form section#training_list").removeClass("placeholder");
			
			$("form#training_selection_form section#training_list > div.input_container > input").click(function(event)
			{
				let lat = parseFloat($(this).data("lat"));
				let lng = parseFloat($(this).data("lng"));
				
				if(lat > 0 && lng > 0)
				{
					$("div#map").removeClass("loading no_data");
					initMap(lat, lng);
					$("div.map_controls").addClass("active");
				}
				else
				{
					$("div#map").removeClass("loading");
					$("div#map").addClass("no_data");
				}
			});
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
				$("form#training_selection_form section#training_list").addClass("no_data");
			else
				$("form#training_selection_form section#training_list").addClass("error");
		}
	}, null);
	
	$("form#training_selection_form section#training_list").removeClass("no_data");
	$("form#training_selection_form section#training_list").addClass("loading");
	trainingsRunner.run(responseErrorRegExp);
}

function goToPaymentSchemaSelectionStep(formIndex)
{
	$.ajax(
    {
		url: "../../res/js/registration/certain/payment_schema_selection_step.js", dataType: "script", async: true, cache: true,
		success: function() { goToFormUnit(formIndex); }
	});
}

var mapStyles = [
    {elementType: 'geometry', stylers: [{color: '#ebe3cd'}]},
      {elementType: 'labels.text.fill', stylers: [{color: '#523735'}]},
      {elementType: 'labels.text.stroke', stylers: [{color: '#f5f1e6'}]},
      {
        featureType: 'administrative',
        elementType: 'geometry.stroke',
        stylers: [{color: '#c9b2a6'}]
      },
      {
        featureType: 'administrative.land_parcel',
        elementType: 'geometry.stroke',
        stylers: [{color: '#dcd2be'}]
      },
      {
        featureType: 'administrative.land_parcel',
        elementType: 'labels.text.fill',
        stylers: [{color: '#ae9e90'}]
      },
      {
        featureType: 'landscape.natural',
        elementType: 'geometry',
        stylers: [{color: '#dfd2ae'}]
      },
      {
        featureType: 'poi',
        elementType: 'geometry',
        stylers: [{color: '#dfd2ae'}]
      },
      {
        featureType: 'poi',
        elementType: 'labels.text.fill',
        stylers: [{color: '#93817c'}]
      },
      {
        featureType: 'poi.park',
        elementType: 'geometry.fill',
        stylers: [{color: '#a5b076'}]
      },
      {
        featureType: 'poi.park',
        elementType: 'labels.text.fill',
        stylers: [{color: '#447530'}]
      },
      {
        featureType: 'road',
        elementType: 'geometry',
        stylers: [{color: '#f5f1e6'}]
      },
      {
        featureType: 'road.arterial',
        elementType: 'geometry',
        stylers: [{color: '#fdfcf8'}]
      },
      {
        featureType: 'road.highway',
        elementType: 'geometry',
        stylers: [{color: '#f8c967'}]
      },
      {
        featureType: 'road.highway',
        elementType: 'geometry.stroke',
        stylers: [{color: '#e9bc62'}]
      },
      {
        featureType: 'road.highway.controlled_access',
        elementType: 'geometry',
        stylers: [{color: '#e98d58'}]
      },
      {
        featureType: 'road.highway.controlled_access',
        elementType: 'geometry.stroke',
        stylers: [{color: '#db8555'}]
      },
      {
        featureType: 'road.local',
        elementType: 'labels.text.fill',
        stylers: [{color: '#806b63'}]
      },
      {
        featureType: 'transit.line',
        elementType: 'geometry',
        stylers: [{color: '#dfd2ae'}]
      },
      {
        featureType: 'transit.line',
        elementType: 'labels.text.fill',
        stylers: [{color: '#8f7d77'}]
      },
      {
        featureType: 'transit.line',
        elementType: 'labels.text.stroke',
        stylers: [{color: '#ebe3cd'}]
      },
      {
        featureType: 'transit.station',
        elementType: 'geometry',
        stylers: [{color: '#dfd2ae'}]
      },
      {
        featureType: 'water',
        elementType: 'geometry.fill',
        stylers: [{color: '#9cc0f9'}]
      },
      {
        featureType: 'water',
        elementType: 'labels.text.fill',
        stylers: [{color: '#92998d'}]
      }
  ];

function initMap(lat, lng)
{
	try
	{
		map = new google.maps.Map(document.getElementById("map"), 
		{
			center: {lat: parseFloat(lat), lng: parseFloat(lng)},
			zoom: zoomLevel,
			disableDefaultUI: true,
			styles: mapStyles
		});
		
		marker = new google.maps.Marker(
		{
		    position: {lat: parseFloat(lat), lng: parseFloat(lng)},
		    map: map,
		    title: "Konum Bilgisi",
		    draggable: false, 
	        animation: google.maps.Animation.DROP
		});
		
		map.setCenter(marker.getPosition());
		
		$("#zoom_map_in").click(function()
		{
			if(map && zoomLevel < 19)
			{
				map.setZoom(zoomLevel + 1);
				zoomLevel++;
			}
		});
	
		$("#zoom_map_out").click(function()
		{
			if(map && zoomLevel > 10)
			{
				map.setZoom(zoomLevel - 1);
				zoomLevel--;
			}
		});
	
		$("#open_big_map").click(function()
		{
			let mapUrl = window.open("https://www.google.com/maps?q=" + lat + "," + lng);
			
			if(mapUrl) 
			{
				mapUrl.focus();
			} else {
			    //Browser has blocked it
			    alert("Tarayıcı ayarlarınız bu işleme izin vermiyor. Açılabilir pencereler engelini kaldırıp, tekrar deneyin.");
			}
		});
	}
	catch(err)
	{
		$("div#map").addClass("no_data");
	}
}