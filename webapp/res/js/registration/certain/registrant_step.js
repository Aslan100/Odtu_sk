$(document).ready(function()
{
	$("form#registrant_form button#submit_button").click(function(event)
	{
		event.preventDefault();
		
		let formIndex = $(this).parent().parent().index();
		let stepRunner = new BordomorAjaxRunner(certainRegistrationStep3URI, $("form#registrant_form").serialize());
		stepRunner.init();
		stepRunner.setFailDialog();
		
		stepRunner.setCallbacks(function(isValid, response)
		{
			if(isValid)
				goToTrainingSelectionStep(formIndex + 1);
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
	
	$("input#address_check_checkbox").click(function(event)
	{
		if($(this).is(":checked"))
		{
			$("select#parent_city_select").val($("select#athlete_city_select").val());
			$("select#parent_city_select").trigger("change");
			$("select#parent_district_select").val($("select#athlete_district_select").val());
			$("select#parent_neighbourhood_select").val($("select#athlete_neighbourhood_select").val());
			$("textarea#parent_address_area").val($("textarea#athlete_address_area").val());
			
			$("section#parent_address_section select, section#parent_address_section textarea").prop("disabled", "disabled");
		}
		else
		{
			$("section#parent_address_section select, section#parent_address_section textarea").prop("disabled", false);
			
			$("select#parent_city_select").val(1);
			$("select#parent_district_select").val(1);
			$("select#parent_neighbourhood_select").val(1);
			$("textarea#parent_address_area").val("");
		}
	});
	
	$("form#registrant_form select#parent_city_select").change(function(event)
	{
		$("select#parent_district_select").find("option").remove();
		
		let cityIndex = $(this).find("option:selected").index();
		let districts = cities[cityIndex].districts;
		
		for(let i = 0; i < districts.length; i++)
			$("select#parent_district_select").append($("<option>", { value: districts[i].id, text: districts[i].name}));
	});
	
	$("select#parent_city_select").trigger("change");
	
	$("form#registrant_form input#id_no_input").on("input", event => 
	{
		let inputContainer = $(event.target).parent("div.input_container");
		$(event.target).val($(event.target).val().replace(/[^0-9]/g, ""));
		let fieldVal = $(event.target).val().trim();
		
		if(fieldVal.length == 11)
		{
			let idControlRunner = new BordomorAjaxRunner(parentDataURL, "id_no=" + fieldVal);
			idControlRunner.init();
			
			idControlRunner.setCallbacks(function(isValid, response)
			{
				$(inputContainer).removeClass("loading");
				let form = $("form#registrant_form"); 
				
				if(isValid)
				{
					let parent = new Parent();
					parent.parseFromXMLElement(response.getElementsByTagName("parent")[0]);
					populateFields(form, parent);	
						
					$(form).find("input[type='text'], input[type='checkbox'], select, textarea").each(function() 
					{ 	
						$(this).prop("disabled", ($(this).data("always_enabled") == false ? "disabled" : false)); 
					});
				}
				else
				{
					$(form).find("input[type='text'], input[type='checkbox'], select, textarea").each(function() { $(this).prop("disabled", false) });
					$(form).find("input#registrant_id").val("");
					$(inputContainer).addClass(new RegExp(noDataErrorRegExp).test(response) ? "success" : "warning");
				}
			}, null);
			
			$(inputContainer).removeClass("loading success error");
			$(inputContainer).addClass("loading");
			
			idControlRunner.run(responseErrorRegExp);
		}
		else
			$(inputContainer).removeClass("loading success error");
			
		function populateFields(form, parent)
		{
			$(form).find("input#name_input").val(parent.loginable.name);
			$(form).find("input#surname_input").val(parent.loginable.surname);
			$(form).find("input#email_input").val(parent.loginable.email);
			$(form).find("input#phone_number_input").val(parent.loginable.phoneNumber);
			$(form).find("input#vehicle_plate_input").val(parent.vehicleLicencePlate);
					
			$(form).find("input#address_check_checkbox").prop("checked", false);
			$(form).find("select#parent_city_select").val(parent.homeLocation.address.city.id + "");
			$(form).find("select#parent_city_select").trigger("change");
			$(form).find("select#parent_district_select").val(parent.homeLocation.address.district.id + "");
			
			if(parent.homeLocation.address.addressString != null)
				$(form).find("textarea#parent_address_area").val(parent.homeLocation.address.addressString + "");
				
			$(form).find("input#registrant_id_input").val(parent.id + "");
			$(form).find("input#is_sibling_registration_input").val(parent.hasChildren + "");	
		}	
	});
	
	$("form#registrant_form input#email_input").on("input", event => 
	{
		let fieldVal = $(event.target).val().trim();
		let inputContainer = $(event.target).parent("div.input_container");
		
		if(fieldVal.length >= 5 && fieldVal.indexOf("@") > 0)
		{
			let idControlRunner = new BordomorAjaxRunner(athleteDataURL, "email=" + fieldVal);
			idControlRunner.init("POST", true, "text");
			
			idControlRunner.setCallbacks(function(isValid, response)
			{
				$(inputContainer).removeClass("loading");
				
				if(isValid)
					$(inputContainer).addClass("error");
				else
					$(inputContainer).addClass(new RegExp(noDataErrorRegExp).test(response) ? "success" : "warning");
			}, null);
			
			$(inputContainer).removeClass("loading success error");
			$(inputContainer).addClass("loading");
			
			idControlRunner.run(responseErrorRegExp);
		}
		else
			$(inputContainer).removeClass("loading success error");
	});
});

function goToTrainingSelectionStep(formIndex)
{
	let trainingSelectionResReceived = false;
	let googleMapsResReceived = false;
	let isSkipping = false;
	
	$.ajax(
    {
		url: "../../res/js/registration/certain/training_selection_step.js", dataType: "script", async: true, cache: false,
		success: function() 
		{
			trainingSelectionRedReceived = true;
			
			if(googleMapsResReceived)
				skipStep();   
		}
	});
	
	$.ajax(
    {
		url: "https://maps.googleapis.com/maps/api/js?key=AIzaSyBwKkHsWOrKCk-BAeyeWXo898QUXaC5GTM&callback=initMap", 
		dataType: "script", 
		async: true, 
		cache: true,
		success: function() 
		{
			googleMapsResReceived = true;
			
			if(trainingSelectionRedReceived)
				skipStep();
		}
	});
	
	function skipStep()
	{
		if(!isSkipping)
		{
			isSkipping = true;
			goToFormUnit(formIndex);
		}
	}
}