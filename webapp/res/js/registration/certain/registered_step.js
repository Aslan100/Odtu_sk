$(document).ready(function()
{
	$("form#registered_data_form button#submit_button").click(function(event)
	{
		event.preventDefault();
		
		let formIndex = $(this).parent().parent().index();
		let stepRunner = new BordomorAjaxRunner(certainRegistrationStep1URI, $("form#registered_data_form").serialize());
		stepRunner.init();
		stepRunner.setFailDialog();
		
		stepRunner.setCallbacks(function(isValid, response)
		{
			if(isValid)
				goToMedicalDataStep(formIndex + 1);
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
	
	$("form#registered_data_form select#athlete_city_select").change(function(event)
	{
		$("select#athlete_district_select").find("option").remove();
		
		let cityIndex = $(this).find("option:selected").index();
		let districts = cities[cityIndex].districts;
		
		for(let i = 0; i < districts.length; i++)
			$("select#athlete_district_select").append($("<option>", { value: districts[i].id, text: districts[i].name}));
	});
	
	$("select#athlete_city_select").trigger("change");
	
	$("form#registered_data_form input#id_no_input").on("input", event => 
	{
		$(event.target).val($(event.target).val().replace(/[^0-9]/g, ""));
		let fieldVal = $(event.target).val().trim();
		let inputContainer = $(event.target).parent("div.input_container");
		
		if(fieldVal.length == 11)
		{
			let idControlRunner = new BordomorAjaxRunner(athleteDataURL, "id_no=" + $(event.target).val());
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
	
	$("form#registered_data_form input#email_input").on("input", event => 
	{
		let fieldVal = $(event.target).val().trim();
		let inputContainer = $(event.target).parent("div.input_container");
		
		if(fieldVal.length >= 5 && fieldVal.indexOf("@") > 0)
		{
			let idControlRunner = new BordomorAjaxRunner(athleteDataURL, "email=" + $(event.target).val());
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

function goToMedicalDataStep(formIndex)
{
	$.ajax(
    {
		url: "../../res/js/registration/certain/medical_data_step.js", dataType: "script", async: true, cache: false,
		success: function() { goToFormUnit(formIndex); }
	});
}