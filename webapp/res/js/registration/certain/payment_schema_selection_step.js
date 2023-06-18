var paymentSchemas = [];
var selectedSchema = null;
var selectedPlan = null;
var that = this;

$(document).ready(function()
{
	fetchPaymentSchemas();
	
	$("form#payment_schema_selection_form button#submit_button").click(function(event)
	{
		event.preventDefault();
		
		let formIndex = $(this).parent().parent().index();
		let stepRunner = new BordomorAjaxRunner(certainRegistrationStep5URI, $("form#payment_schema_selection_form").serialize());
		stepRunner.init();
		stepRunner.setFailDialog();
		
		stepRunner.setCallbacks(function(isValid, response)
		{
			if(isValid)
			{
				let regCode = $("form#payment_schema_selection_form").find("input[name='code']").val();
				window.location.href = "../../registration/payment.jsp?code=" + regCode;
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
	
		stepRunner.run(responseErrorRegExp);
	});
	
	$("form#payment_schema_selection_form button#discount_coupon_button").click(function(event)
	{
		if(that.selectedSchema != null)
		{
			let inputHtml = "";
			inputHtml += "<p style='text-align: left'>Tarafınıza iletilmiş bir indirim kuponuna sahipseniz, kupon kodunuzu bu bölüme girerek indirimden yararlanabilirsiniz.</p>";
			inputHtml += "<div class='input_container'>";
			inputHtml += "<input type='text' id='code_input' placeholder=' '>";
			inputHtml += "<label for='code_input'>Kupon Kodunuz</label>";
			inputHtml += "</div>";
			
			let couponDialogue = new BordomorConfirmationDialogue(inputHtml, "Uygula", "Vazgeç");
			
			couponDialogue.setChoiceCallbacks(function() 
			{
				let couponCode = $("input#code_input").val();
				
				if(couponCode.trim().length > 0)
				{
					fetchPaymentPlan(that.selectedSchema, couponCode.trim().length > 0 ? couponCode.trim() : null);
					$("form#payment_schema_selection_form input#coupon_code_input").val(couponCode);
					couponDialogue.close();
				}
			}, null);
			
			couponDialogue.print();
		}
	});
	
	$("form#payment_schema_selection_form button#refresh_button").click(event => fetchPaymentSchemas());
});

function fetchPaymentSchemas()
{
	this.selectedSchema = null;
	this.paymentSchemas = [];
	
	let schemaContainer = $("form#payment_schema_selection_form section#schema_list");
	let planContainer = $("form#payment_schema_selection_form section#payment_plan_container");
	let buttonsContainer = $("form#payment_schema_selection_form > div.controls");
	 
	$(buttonsContainer).find("button#submit_button").prop("disabled", "disabled");
	$(buttonsContainer).find("button#discount_coupon_button").prop("disabled", "disabled");
	$(schemaContainer).removeClass("loading");
	
	let schemaRunner = new BordomorAjaxRunner(suitablePaymentSchemasForRegistrationDataURI, "code=" + $("form#training_selection_form input[name='code']").val());
	schemaRunner.init();
	
	schemaRunner.setCallbacks(function(isValid, response)
	{
		$(schemaContainer).removeClass("loading");
		
		if(isValid)
		{
			parsePaymentSchemas(response);
			let listHtml = "<div class='list_container gray'>";
			
			for(let i = 0; i < that.paymentSchemas.length; i++)
				listHtml += that.paymentSchemas[i].getListItem();	
			
			listHtml += "</div>";
			$(schemaContainer).append(listHtml);
			$(schemaContainer).removeClass("placeholder");
			
			$(schemaContainer).find("input[type='radio'][name='schema']").click(event => 
			{
				for(let i = 0; i < that.paymentSchemas.length; i++)
				{
					if(that.paymentSchemas[i].schemaId == $(event.target).val())
					{
						that.selectedSchema = that.paymentSchemas[i]; 
						fetchPaymentPlan(that.selectedSchema);
						break;
					}
				} 
			});
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
				$(schemaContainer).addClass("no_data");
			else
				$(schemaContainer).addClass("error");
		}
	}, null);
	
	$(schemaContainer).removeClass("no_data error");
	$(schemaContainer).addClass("placeholder loading");
	$(schemaContainer).find("> *:not(h3)").remove();
	
	$(planContainer).removeClass("no_data error");
	$(planContainer).addClass("placeholder no_data");
	schemaRunner.run(responseErrorRegExp);
	
	function parsePaymentSchemas(response)
	{
		let schemaEls = response.getElementsByTagName("interval_constrained_schema");
	
		for(let i = 0; i < schemaEls.length; i++)
		{
			let nextSchema = new PaymentSchema();
			nextSchema.parseFromXMLElement(schemaEls[i]);
			this.paymentSchemas.push(nextSchema);
		}
			
		schemaEls = response.getElementsByTagName("equidistributed_schema");
			
		for(let i = 0; i < schemaEls.length; i++)
		{
			let nextSchema = new PaymentSchema();
			nextSchema.parseFromXMLElement(schemaEls[i]);
			this.paymentSchemas.push(nextSchema);
		}
	}
}

function fetchPaymentPlan(schema, couponCode)
{
	let planContainer = $("form#payment_schema_selection_form section#payment_plan_container");
	let buttonsContainer = $("form#payment_schema_selection_form > div.controls");
	
	$(buttonsContainer).find("button#submit_button").prop("disabled", "disabled");
	$(planContainer).find("div.placeholder").remove();
	$(planContainer).addClass("loading_state");
	
	let params = "code=" + $("form#payment_schema_selection_form input[name='code']").val() + "&schema=" + schema.schemaId;
	params += couponCode != null ? "&coupon_code=" + couponCode : "";
	
	let paymentPlanRunner = new BordomorAjaxRunner(paymentPlanForRegistrationDataURI, params);
	paymentPlanRunner.init();
	paymentPlanRunner.setCallbacks(function(isValid, response)
	{
		$(planContainer).removeClass("loading");
		
		if(isValid)
		{
			$(planContainer).removeClass("placeholder");
			
			that.selectedPlan = new PaymentPlan();
			that.selectedPlan.parseFromXMLElement(response.getElementsByTagName("payment_plan")[0]);
			
			printPaymentPlan(planContainer, that.selectedPlan);
			$("form#payment_schema_selection_form button#discount_coupon_button").prop("disabled", false);	
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
				$(planContainer).addClass("no_data");
			else
				$(planContainer).addClass("error");
		}
	});
	
	$(planContainer).removeClass("no_data error");
	$(planContainer).addClass("placeholder loading");
	$(planContainer).find("> *:not(h3)").remove();
	paymentPlanRunner.run(responseErrorRegExp);
	
	function printPaymentPlan(planContainer, paymentPlan)
	{
		let tableHtml = "<div class='table_container gray'>";
		tableHtml += "<div class='body'>";
		tableHtml += paymentPlan.getTable(true);
		tableHtml += "</div></div>";
		tableHtml += "<div class='input_container checkbox_container'>";
		tableHtml += "<input type='checkbox' id='plan_conditions_acceptance_checkbox'>";
		tableHtml += "<label for='plan_conditions_acceptance_checkbox'><a id='plan_conditions_viewer' href='javascript:void(0)'>Plan şart ve koşullarını</a> kabul ediyorum</label>";
		tableHtml += "</div>";
				
		$(planContainer).append(tableHtml);
		
		$("a#plan_conditions_viewer").click(function(event)
		{
			event.preventDefault();
			event.stopPropagation();
			
			let termsDialogue = new BordomorDialogue("Ödeme Planı Şartları ve Koşulları", schema.getTermsAndConditions("Ayşe Ayşen Aykun", "Oğuz Aykun Türkmen"));
			termsDialogue.print();
		});
		
		$("input#plan_conditions_acceptance_checkbox").click(function(event)
		{
			let button = $("form#payment_schema_selection_form > div.controls > button.forward");
			$(button).prop("disabled", $(this).is(":checked") ? false : "disabled");
		});
	}
}