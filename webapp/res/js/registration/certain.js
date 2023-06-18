var cities = [];
var formUnitCount = -1;
var that = this;

$(document).ready(function()
{
	that.formUnitCount = $("form.form_unit").length;
	
	$("form.form_unit button.back").click(function(event)
	{
		event.preventDefault();
		
		let form = $(this).parent().parent();
		let stepBackRunner = new BordomorAjaxRunner(certainRegistrationStepBackURI, "code=" + $(form).find("input[name='code']").val());
		stepBackRunner.init("POST", true, "text");
		stepBackRunner.setFailDialog();
		
		stepBackRunner.setCallbacks(function(isValid, response)
		{
			if(isValid)
			{
				$(form)[0].reset();
				goToFormUnit($(form).index() - 1);
			}
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
	
		stepBackRunner.run(responseErrorRegExp);
	});
	
	$("form.form_unit button.cancel").click(function(event)
	{
		event.preventDefault();
		
		let form = $(this).parent().parent();
		let stepBackRunner = new BordomorAjaxRunner(certainRegistrationStepBackURI, "code=" + $(form).find("input[name='code']").val() + "&is_cancellation=true");
		stepBackRunner.init("POST", true, "text");
		stepBackRunner.setFailDialog();
		
		stepBackRunner.setCallbacks(function(isValid, response)
		{
			if(isValid)
			{
				$("form.form_unit").each(function() { $(this)[0].reset(); });
				window.location.href = portalIndexURL + "/registration/index.jsp";
			}
			else
			{
				if(new RegExp(noDataErrorRegExp).test(response))
				{
					let negativeDialog = makeBordomorFullScreenNegativeResultDialog("İptal İşlemi Sırasında Bir Hata Oluştu", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
					{
						destroyBordomorFullScreenDialog(negativeDialog);
						negativeDialog = null;
					});
				}
				else
					makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
			}
		}, null);
	
		stepBackRunner.run(responseErrorRegExp);
	});
	
	if(!isNaN(parseInt($("body").data("last_completed_step"))))
	{
		let lastCompletedStep = parseInt($("body").data("last_completed_step"));
		let formIndex = lastCompletedStep + 1;
		goToFormUnit(formIndex, true);
	}
});

function goToFormUnit(formIndex, jumpUnanimated)
{
	if(jumpUnanimated == true)
		$("form.form_unit").css("transition", "0s");
		
	$("h2#step_data").html("Adım " + (formIndex + 1) + "/" + that.formUnitCount);
			
	$("form.form_unit").each(function(index)
	{
		let shiftAmount = -1*formIndex*$(this).outerWidth();
		$(this).css("left", shiftAmount + "px");
	});
	
	if(jumpUnanimated == true)
		$("form.form_unit").css("transition", "0.3s");
}