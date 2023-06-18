$(document).ready(function()
{
	$("form#branch_settings_form button#submit_button").click(function(event)
	{
		event.preventDefault();
		
		let form = $("form#branch_settings_form");
		let inputs = $("form#branch_settings_form").find("input");
		let select = $("select#branch_select");
		
		let settingsRunner = new BordomorAjaxRunner(branchManURI, $("form#branch_settings_form").serialize());
		settingsRunner.init();
		settingsRunner.setFailDialog();
		
		settingsRunner.setCallbacks(function(isValid, response)
		{
			$(select).parent(".input_container.select_container").removeClass("loading");
				
			if(isValid)
			{
				$(inputs).each(function() { $(this).prop("disabled", false); });
				alert("Branş ayarları güncellendi");
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
		
		$(inputs).each(function() 
		{ 
			$(this).prop("disabled", "disabled");
			$(this).parent(".input_container").addClass("loading"); 
		});
		
		$(select).parent(".input_container.select_container").addClass("loading");
		
		settingsRunner.run(responseErrorRegExp);
	});
	
	$("select#branch_select").change(function(event)
	{
		let form = $("form#branch_settings_form");
		let inputs = $("form#branch_settings_form").find("input");
		
		let parameters = "id=" + $(this).val();
		let branchRunner = new BordomorAjaxRunner(branchDataURL, parameters);
		branchRunner.init();
		
		branchRunner.setCallbacks(function(isValid, response)
		{
			$(inputs).each(function() { $(this).parent(".input_container").removeClass("loading") });
					
			if(isValid)
			{
				let branchEl = response.getElementsByTagName("branch")[0];
				
				$("input#title_input").val(branchEl.getAttribute("title"));
				$("input#daily_price_input").val(branchEl.getAttribute("daily_price"));
				$("input#weekly_price_input").val(branchEl.getAttribute("weekly_price"));
				$("input#monthly_price_input").val(branchEl.getAttribute("monthly_price"));
				$("input#annual_price_input").val(branchEl.getAttribute("annual_price"));
				$("input#sibling_discount_ratio_input").val((new Number(branchEl.getAttribute("sibling_discount_ratio"))*100).toFixed(2));
				$("input#penalty_rate_input").val((new Number(branchEl.getAttribute("penalty_rate"))*100).toFixed(2));
				
				$(inputs).each(function() { $(this).prop("disabled", false); });
			}
			else
				alert("Veriler Alınırken Bir Hata Oluştu");
		});
		
		$(inputs).each(function() 
		{ 
			$(this).prop("disabled", "disabled");
			$(this).parent(".input_container").addClass("loading"); 
		});
		
		branchRunner.run(responseErrorRegExp);
	});
});