function PaymentSchema(schemaId, title, branch, type, paymentPeriod, periodUnit, paymentDayIndex, paymentDaySubindex, 
		maxUnpaidDays, failureAction, cancellationAction, autoRenew, maxCancellableDays, isPriceModificationProtected, creationTime, lastModifiedTime)
{
	this.schemaId = schemaId;
	this.title = title;
	this.branch = branch;
	this.type = type;
	this.paymentPeriod = paymentPeriod;
	this.periodUnit = periodUnit;
	this.paymentDayIndex = paymentDayIndex;
	this.paymentDaySubindex = paymentDaySubindex;
	this.maxUnpaidDays = maxUnpaidDays;
	this.failureAction = failureAction;
	this.cancellationAction = cancellationAction;
	this.autoRenew = autoRenew;
	this.maxCancellableDays = maxCancellableDays;
	this.isPriceModificationProtected = isPriceModificationProtected;
	this.creationTime = creationTime;
	this.lastModifiedTime = lastModifiedTime;
	
	var that = this;
}

PaymentSchema.prototype.reset = function()
{
	this.schemaId = -1;
	this.title = null;
	this.branch = null;
	this.type = null;
	this.paymentPeriod = -1;
	this.periodUnit = null;
	this.paymentDayIndex = -1;
	this.paymentDaySubindex = -1;
	this.maxUnpaidDays = -1;
	this.failureAction = null;
	this.cancellationAction = null;
	this.autoRenew = false;
	this.maxCancellableDays = -1;
	this.isPriceModificationProtected = false;
	this.creationTime = -1;
	this.lastModifiedTime = -1;
}

PaymentSchema.prototype.parseFromXMLElement = function(element)
{
	try
	{
		this.reset();
		
		if(element.hasAttribute("schema_id"))
		{
			this.schemaId = parseInt(element.getAttribute("schema_id"));
			
			if(isNaN(this.schemaId))
				throw "";
		}
		
		if(element.hasAttribute("title"))
			this.title = element.getAttribute("title");
		
		this.type = schemaTypes[schemaTypes.indexOf(element.tagName.toUpperCase())];
		this.paymentPeriod = parseInt(element.getAttribute("payment_period"));
		
		if(element.hasAttribute("period_unit"))
			this.periodUnit = timeUnits[timeUnits.indexOf(element.getAttribute("period_unit"))];
		
		this.paymentDayIndex = parseInt(element.getAttribute("payment_day_index"));
		this.paymentDaySubindex = parseInt(element.getAttribute("payment_day_subindex"));
		this.maxUnpaidDays = parseInt(element.getAttribute("max_unpaid_days"));
		
		if(element.hasAttribute("failure_action"))
			this.failureAction = paymentFailureActions[paymentFailureActions.indexOf(element.getAttribute("failure_action"))];
		
		if(element.hasAttribute("cancellation_action"))
			this.cancellationAction = subscriptionCancellationActions[subscriptionCancellationActions.indexOf(element.getAttribute("cancellation_action"))];
		
		this.autoRenew = element.getAttribute("auto_renew") === "true";
		this.maxCancellableDays = parseInt(element.getAttribute("max_cancellable_days"));
		this.isPriceModificationProtected = element.getAttribute("is_price_modification_protected") === "true";
		
		this.creationTime = new Number(element.getAttribute("creation_time"));
		this.lastModifiedTime = new Number(element.getAttribute("last_modified_time"));
		
		let branchEls = element.getElementsByTagName("branch");
		
		if(branchEls.length > 0)
		{
			this.branch = new Branch();
			this.branch.parseFromXMLElement(branchEls[0]);
		}
	}
	catch(err) 
	{
		this.reset();
	}
}

PaymentSchema.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\n";
	}
		
	return output.length > 0 ? output : null;
}

PaymentSchema.prototype.getTermsAndConditions = function(parentName, athleteName)
{
	let percentageDiscount = (this.branch.getSiblingDiscountRatio()*100).toFixed(2);
	let percentageRate = (this.branch.penaltyRate*100).toFixed(2);
	
	let text = "";
	text += "<p style='text-align: left; max-width: 700px; margin: 10px 10px 15px 10px; line-height: 1.6em;'>Sayın <strong>" + parentName + "</strong>,</p>";
	text += "<p style='text-align: left; max-width: 700px; margin: 10px 10px 15px 10px; line-height: 1.6em;'>Seçmiş olduğunuz ödeme planı bu sayfada gösterilen tarihlerde ve tutarlarda, hesabınıza borç olarak yapılandırılacaktır.</p>";
	text += "<h3 style='font: var(--header_font_h3); font-size: 13px; margin: 10px 10px 5px 10px'>İndirimler:</h3>";
	text += "<ol style='padding-left: 15px;'>";
	text += "<li style='text-align: left; max-width: 700px; margin: 10px 10px 15px 10px; line-height: 1.6em;'>Velisi aynı olan sporculardan ilki tam aidat öderken, diğer sporculara %" + percentageDiscount + " kadar kardeş indirimi uygulanır.</li>";
	text += "</ol>";
	text += "<h3 style='font: var(--header_font_h3); font-size: 13px; margin: 10px 10px 5px 10px'>Gecikmeler:</h3>";
	text += "<ol style='padding-left: 15px;'>";
	text += "<li style='text-align: left; max-width: 700px; margin: 10px 10px 15px 10px; line-height: 1.6em;'>Ödemenizin aidat dönemi içinde ödenmemesi durumunda aylık %" + percentageRate + " kadar gecikme faizi, takip eden ayın/ayların ilk gününde gecikme faizi olarak hesabınıza borç olarak tahakkuk edecektir.</li>";	
	text += "<li style='text-align: left; max-width: 700px; margin: 10px 10px 15px 10px;  line-height: 1.6em;'>Gecikme faizinin tahakkundan " + this.maxUnpaidDays + " gün sonra, eğer aidat ödemeniz hala gerçekleşmemişse, hesabının askıya alınacak ve ödeme yapmadan etkinleştirilmeyecektir.</li>";
	text += "</ol>"; 
	text += "<h3 style='font: var(--header_font_h3); font-size: 13px; margin: 10px 10px 5px 10px'>İlişik Kesme Koşulları:</h3>";
	text += "<ol style='padding-left: 15px;'>";
	text += "<li style='text-align: left; max-width: 700px; margin: 10px 10px 15px 10px;  line-height: 1.6em;'>Velisi bulunduğunuz  " + athleteName + "’in kulübümz üyeliğinden ayrılmayı istemesi durumunda: " + subscriptionCancellationActionDescs[subscriptionCancellationActions.indexOf(this.cancellationAction)] + "</li>";
	text += "</ol>";
	text += "<h3 style='font: var(--header_font_h3); font-size: 13px; margin: 10px 10px 5px 10px'>Plan Yenileme:</h3>";
	text += "<ol style='padding-left: 15px;'>";
	text += "<li style='text-align: left; max-width: 700px; margin: 10px 10px 15px 10px; line-height: 1.6em;'>Ödeme Planı döneminin sonunda güncel aidat ile yeni bir ödeme planı hesabınıza otomatik yapılandırılacaktır. Yeni yapılandırılan ödeme planını " + this.maxCancellableDays + " gün içinde reddetmeniz durumunda hesabınız herhangibir borç yapılandırılmasına gitmeksizin askıya alınacaktır.</li>"; 
	text += "<li style='text-align: left; max-width: 700px; margin: 10px 10px 15px 10px; line-height: 1.6em;'>Aidat güncellemesinin Ödeme Planı Dönemi bitmeden yapılması durumunda, daha önce yapılandırılmış ödeme planı " + (this.isPriceModificationProtected ? "sabit kalacaktır" : "değişecektir") + ".</li>";
	text += "</ol>";
	
	return text;
}

PaymentSchema.prototype.compliesWith = function(filterObj)
{
	let self = this;
	let searchFails = false;
	
	filterObj.filters.forEach(function(nextFilter, index)
	{
		let nextAttr = nextFilter.attr;
		
		for(let property in self)
		{
			if(property == nextAttr)
			{
				let vals = nextFilter.groupVal;
				let val = nextFilter.val;
				let minVal = nextFilter.initVal;
				let maxVal = nextFilter.finalVal;
				
				if(val != null && self[property] != val)
					searchFails = true;
				else if(minVal != null || maxVal != null)
				{
					if(!isNaN(self[property]))
					{
						if((minVal != null && self[property] < new Number(minVal)) || (maxVal != null && self[property] > new Number(maxVal)))
							searchFails = true;
					}
					else
					{
						let convertDate = (dateStr) => { let dateVals = dateStr.split("/"); return dateVals[2] + "-" + dateVals[1] + dateVals[0] };
						let propDate = convertDate(self[property]);
						let minDate =  minVal != null ? convertDate(minVal) : null;
						let maxDate =  maxVal != null ? convertDate(maxVal) : null;
						
						if((minDate != null && propDate < minDate) || (maxDate != null && propDate > maxDate))
							 searchFails = true;
					}
					
					
				}	
				if(vals != null && vals.indexOf(self[property]) == -1)
					searchFails = true;
				
				if(searchFails)
					break;
			}
		}
	});
	 
	return !searchFails;
}

PaymentSchema.prototype.isValid = function()
{
	isValid = true;

	if(this.email == null || !new RegExp(emailRegExp).test(this.email))
		isValid = false;
	
	if(this.name == null || !new RegExp(nameRegExp).test(this.name))
		isValid = false;
	
	if(this.surname == null || !new RegExp(surnameRegExp).test(this.surname))
		isValid = false;
		
	return isValid;	
}

PaymentSchema.prototype.getListItem = function()
{
	let schemaEl = "";
	schemaEl += "<div class='input_container'>";
	schemaEl += "<input type='radio' id='schema_radio_" + this.schemaId + "' name='schema' value='" + this.schemaId + "'>";
	schemaEl += "<label for='schema_radio_" + this.schemaId + "'>";
	schemaEl += "<strong>" + this.title + "</strong><br>";
	schemaEl += "Ödeme Periyodu: " + this.paymentPeriod + " " + timeUnitVals[timeUnits.indexOf(this.periodUnit)] + "<br>";
	schemaEl += "<span>Ödeme Dönemi: -</span>";
	schemaEl += "</label>";
	schemaEl += "</div>";
		
	return schemaEl;
}