$(document).ready(function()
{
	$("input#responsibility_acceptance_checkbox").click(function(event)
	{
		let button = $("form#medical_data_form > div.controls > button.forward");
		$(button).prop("disabled", $(this).is(":checked") ? false : "disabled");
	});
	
	$("form#medical_data_form button#submit_button").click(function(event)
	{
		event.preventDefault();
		
		let formIndex = $(this).parent().parent().index();
		let stepRunner = new BordomorAjaxRunner(certainRegistrationStep2URI, $("form#medical_data_form").serialize());
		stepRunner.init();
		stepRunner.setFailDialog();
		
		stepRunner.setCallbacks(function(isValid, response)
		{
			if(isValid)
				goToRegistrantStep(formIndex + 1);
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
});

function goToRegistrantStep(formIndex)
{
	$.ajax(
    {
		url: "../../res/js/registration/certain/registrant_step.js", dataType: "script", async: true, cache: false,
		success: function() { goToFormUnit(formIndex); }
	});
}