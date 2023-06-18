function PaymentPlan(id, code, liable, paidFor, generatorSchema, isDefinitive, payments)
{
	this.id = id;
	this.code = code;
	this.liable = liable;
	this.paidFor = paidFor;
	this.generatorSchema = generatorSchema;
	this.isDefinitive = isDefinitive;
	this.payments = payments;
	
	this.promotions = null;
	this.planTotal = -1;
	this.totalPayment = -1;
	this.totalLiability = -1;
	this.liability = -1;
	
	var that = this;
}

PaymentPlan.prototype.reset = function()
{
	this.id = -1;
	this.code = null;
	this.liable = null;
	this.paidFor = null;
	this.generatorSchema = null;
	this.isDefinitive = false;
	this.payments = null;
	
	this.promotions = null;
	this.planTotal = -1;
	this.totalPayment = -1;
	this.totalLiability = -1;
	this.liability = -1;
}

PaymentPlan.prototype.parseFromXMLElement = function(element)
{
	try
	{
		this.reset();
		
		if(element.hasAttribute("id"))
		{
			this.id = parseInt(element.getAttribute("id"));
			
			if(isNaN(this.id))
				throw "";
		}
		
		if(element.hasAttribute("code"))
			this.code = element.getAttribute("code");
		
		if(element.hasAttribute("is_definitive"))
			this.isDefinitive = element.getAttribute("is_definitive") === "true";
		
		if(element.hasAttribute("plan_total"))
			this.planTotal = parseFloat(element.getAttribute("plan_total"));
			
		if(element.hasAttribute("total_payment"))
			this.totalPayment = parseFloat(element.getAttribute("total_payment"));
			
		if(element.hasAttribute("total_liability"))
			this.totalLiability = parseFloat(element.getAttribute("total_liability"));
		
		if(element.hasAttribute("liability"))
			this.liability = parseFloat(element.getAttribute("liability"));
		
		let liableEls = element.getElementsByTagName("parent");
		
		if(liableEls.length > 0)
		{
			this.liable = new Parent();
			this.liable.parseFromXMLElement(liableEls[0]);
		}
		
		let paidForEls = element.getElementsByTagName("athlete");
		
		if(paidForEls.length > 0)
		{
			this.paidFor = new Athlete();
			this.paidFor.parseFromXMLElement(paidForEls[0]);
		}
		
		let schemaEls = element.getElementsByTagName("interval_constrained_schema");
		schemaEls = schemaEls.length > 0 ? schemaEls : element.getElementsByTagName("equidistributed_schema"); 
		
		if(schemaEls.length > 0)
		{
			this.generatorSchema = new PaymentSchema();
			this.generatorSchema.parseFromXMLElement(schemaEls[0]);
		}
		
		let paymentEls = element.getElementsByTagName("payment");
		
		if(paymentEls.length > 0)
		{
			this.payments = [];
			
			for(let i = 0; i < paymentEls.length; i++)
			{
				let nextPayment = new Payment();
				nextPayment.parseFromXMLElement(paymentEls[i]);
				this.payments.push(nextPayment);
			}
		}
		
		let promotionEls = element.getElementsByTagName("promotion");
		
		if(promotionEls.length > 0)
		{
			this.promotions = [];
			
			for(let i = 0; i < promotionEls.length; i++)
			{
				let nextPromotion = new Promotion();
				nextPromotion.parseFromXMLElement(promotionEls[i]);
				this.promotions.push(nextPromotion);
			}
		}
	}
	catch(err)
	{
		alert(err);
		this.reset();
	}
}

PaymentPlan.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

PaymentPlan.prototype.compliesWith = function(filterObj)
{
	let self = this;
	let searchFails = false;
	
	let loginableComplies = this.loginable != null ? this.loginable.compliesWith(filterObj) : true;
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
	 
	return !searchFails && loginableComplies;
}

PaymentPlan.prototype.getValue = function(attribute)
{
	var output = "";
	
	for(let property in this)
	{
		if(property == attribute)
			return this[property];
	}
		
	return null;
}

PaymentPlan.prototype.getTotalLiabilityRatio = function()
{
	try
	{
		return (this.totalLiability/this.planTotal).toFixed(2)*100;
	}
	catch(err)
	{
		return "-";
	}
}

PaymentPlan.prototype.getLiabilityRatio = function()
{
	try
	{
		return (this.liability/this.totalLiability).toFixed(2)*100;
	}
	catch(err)
	{
		return "-";
	}
}

PaymentPlan.prototype.getDueDate = function()
{
	try
	{
		if(this.payments != null && this.payments.length > 0)
			return getDateString(new Date(this.payments[this.payments.length - 1].dueDate), 2);
		
		return "-";	
	}
	catch(err)
	{
		return "-";
	}
}

PaymentPlan.prototype.getCard = function()
{
	let summaryHtml = "<div class='controls'><button class='clear_style'></button><button class='clear_style'></button></div>";
	summaryHtml += "<table>";
	summaryHtml += "<tr><td colspan='4' class='page_title'>AKTİF ÖDEME PLAN ÖZETİ</td></tr>";
	
	summaryHtml += "<tr>";
	summaryHtml += "<td class='centered no_border' colspan='4'>";
	summaryHtml += "<div class='pie_chart_container'>";
	summaryHtml += "<div class='pie_chart theme_modern theme_green'><p class='percentage_value'>%0</p></div>";
	summaryHtml += "<p><span>" + getPriceString(this.liability) + "</span> TL<br>KALAN TOPLAM</p>";
	summaryHtml += "</div>";
	summaryHtml += "</td></tr>";
	
	summaryHtml += "<tr><td class='label centered'>Toplamı</td><td class='label centered'>Vadesi</td><td class='label centered'>Ödeme Sayısı</td></tr>";
	summaryHtml += "<tr><td class='centered no_border'>" + getPriceString(this.planTotal) + " TL</td><td class='centered no_border'>" + this.getDueDate() + "</td><td class='centered no_border'>" + this.payments.length + "</td></tr>";
	
	summaryHtml += "<tr><td colspan='4' class='centered no_border' style='padding-top: 20px 0px'><button id='plan_details_button' class='dark'>DETAY</button></td></tr>";
	summaryHtml += "</table>";
	
	return summaryHtml;
}

PaymentPlan.prototype.getTable = function(onlyFinancials)
{
	let tableElHtml = onlyFinancials ? "<table class='low_padding'>" : "<table>";
	let headerElHtml = onlyFinancials ? "<thead><th></th><th>VADESİ</th><th>AİDAT</th><th>İNDİRİM " + (this.promotions != null ? " *" : "") + "</th><th>ÖDEME TUTARI</th></thead>" 
			: "<thead><tr><th>VADESİ</th><th>DÖNEMİ</th><th>TUTAR</th><th>ÖDENEN</th><th>ÖDEME TARİHİ</th><th>KALAN BAKİYE</th><th>ÖDEME DURUMU</th><th></th></thead>";    	
	
	let planHtml = tableElHtml + headerElHtml + "<tbody>";
	
	if(onlyFinancials)
	{
		for(let i = 0; i < this.payments.length; i++)
		{
			planHtml += "<tr><td class='collapse row_counter'>" + (i + 1) + ".</td>";
			planHtml += "<td style='padding-left: 5px;' class='collapse'>" + getDateString(new Date(this.payments[i].dueDate), 2) + "</td>";
			planHtml += "<td>" + getPriceString(this.payments[i].actualAmount) + " TL</td>";
			planHtml += "<td>" + (this.payments[i].discount > 0 ? getPriceString(this.payments[i].discount) : "-") + "</td>";
			planHtml += "<td><strong>" + getPriceString(this.payments[i].amount) + " TL</strong></td></tr>";
		}
	}
	else
	{
		for(let i = 0; i < this.payments.length; i++)
		{
			let nextState = this.payments[i].isPacified ? "pacified" : this.payments[i].state.toLowerCase();
			let nextStateText = this.payments[i].isPacified ? "Pasif" : paymentStateVals[paymentStates.indexOf(this.payments[i].state)];
			
			planHtml += "<tr " + (this.payments[i].isPacified ? "class='pacified'" : '') + " data-id='" + this.payments[i].id + "'>";
			planHtml += "<td><strong>" + getDateString(new Date(this.payments[i].dueDate)) + "</strong></td>";
			planHtml += "<td class='collapse center_aligned'>" + this.payments[i].getPeriodString() + "</td>";
			planHtml += "<td>" + getPriceString(this.payments[i].amount) + " TL</td>";
			planHtml += "<td>" + getPriceString(this.payments[i].paidAmount > 0 ? this.payments[i].paidAmount : 0) + " TL</td>";
			planHtml += "<td>" + (this.payments[i].paidDate > 0 ? getDateString(new Date(this.payments[i].paidDate)) : "-") + "</td>";
			planHtml += "<td>" + getPriceString(this.payments[i].amount - (this.payments[i].paidAmount > 0 ? this.payments[i].paidAmount : 0)) + " TL</td>";
			planHtml += "<td class='collapse'><span class='payment_state " + nextState + "'>" + nextStateText + "</span></td>";
			
			if(this.payments[i].paidAmount < this.payments[i].amount)
			{
				let switchButtonState = this.paidFor.membership == "STANDARD" ? "" : "disabled='disabled'";
				let elementTitle = this.payments[i].isPacified ? "Ödemeyi etkinleştir" : "Ödemeyi pasifize et";
			 	let imgSrc = portalIndexURL + "/res/visual/icon/" + (this.payments[i].isPacified ? "play.png" : "pause.png");
				
				planHtml += "<td class='collapse'><button class='clear_style switch_state' title='" + elementTitle + "'" + switchButtonState + "><img src='" + imgSrc + "'/></button></td>";
			}
			else
				planHtml += "<td class='collapse'></td>";
			
			planHtml += "</tr>";
		}
	}
	
	planHtml += "</tbody>";
	
	if(onlyFinancials && this.promotions != null)
	{
		planHtml += "<tfoot><tr><td colspan='5'><strong>(*) Aşağıdaki indirimlerden yararlanıyorsunuz:<br>";
		
		for(let i = 0; i < this.promotions.length; i++)
			planHtml += "&#8212; " + this.promotions[i].title + "<br>";
		 
		planHtml += "</td></tr></strong></tfoot>";
	}
	
	planHtml += "</table>";
	
	return planHtml;
}

PaymentPlan.prototype.getOverlayCard = function()
{
	let stateFn = state => { return state == "STANDARD" || state == "FROZEN" }; 
	let controlsAllowed = stateFn(this.paidFor.membership);
	
	let cardHtml = "<div id='payment_plan_details_card' class='table_container' data-id=" + this.id + ">";
	cardHtml += "<header><h1>PLAN DETAYLARI</h1><button id='close_card_button' class='clear_style'>&#x274C;</button></header>";
	
	cardHtml += "<div class='balance_container'>";
	cardHtml += "<img src='" + portalIndexURL + "/res/visual/icon/navigation_panel/payment.png'/>";
	cardHtml += "<p><span>" + getPriceString(this.planTotal) + "</span> TL<br>TOPLAM</p>";
	
	if(controlsAllowed)
	{
		cardHtml += "<div class='controls'>";
		
		if(this.paidFor.membership == "STANDARD")
			cardHtml += "<button id='freeze_athlete_button'>DONDUR</button>";
		else
			cardHtml += "<button id='unfreeze_athlete_button'>KAYDI AÇ</button>";
		
		cardHtml += "<button id='unsubscribe_athlete_button'>İLİŞİK KES</button>";
		cardHtml += "<button id='refresh_button' class='dark'><img src='" + portalIndexURL + "/res/visual/icon/refresh_white.png'></button>";
		cardHtml += "</div>"
	}
	
	cardHtml += "</div>";
	
	cardHtml += "<div class='body'>";
	cardHtml += "<table id='plan_data_table'>";
	cardHtml += "<thead>";
	cardHtml += "<tr>";
	cardHtml += "<th class='state_header bigger'></th>";
	cardHtml += "<th>SPORCU ADI</th>";
	cardHtml += "<th>YÜKÜMLÜ ADI</th>";
	cardHtml += "<th>PLAN VADESİ</th>";
	cardHtml += "<th>PLAN ŞEMASI</th>";
	cardHtml += "<th class='collapse'>TANIMLI İNDİRİMLER";
	cardHtml += controlsAllowed ? "<button id='add_promotion_button' class='clear_style' title='Yeni indirim tanımla'><img src='" + portalIndexURL + "/res/visual/icon/plus.png'/></button>" : "";
	cardHtml += "</th>";
	cardHtml += "</tr>";
	cardHtml += "</thead>";
	
	cardHtml += "<tbody>";
	cardHtml += "<tr>";
	cardHtml += "<td class='state_cell bigger " + this.paidFor.loginable.state.toLowerCase() + "'></td>";
	cardHtml += "<td>" + this.paidFor.loginable.fullName() + "<br><span class='secondary_info'>Kayıt Tarihi: " + getDateString(new Date(new Number(this.paidFor.loginable.creationTime)), 2); + "</span></td>";
	cardHtml += "<td>" + this.liable.loginable.fullName() + "</td>";
	cardHtml += "<td>" + this.getDueDate() + "</td>";
	cardHtml += "<td>" + (this.generatorSchema != null ? this.generatorSchema.title : "-") + "</td>";
	cardHtml += "<td class='collapse'>"
	
	if(this.promotions != null)
	{
		for(let i = 0; i < this.promotions.length; i++)
		{
			cardHtml += "<p class='promotion'>" + this.promotions[i].title;
			cardHtml += controlsAllowed ? "<button class='clear_style remove_item' title='İndirimi iptal et' data-id='" + this.promotions[i].id + "'>&#10060;</button>" : "";
			cardHtml += "</p>";	
		}
	}
	else
		cardHtml += "<span class='secondary_info'>Tanımlanmamış</span>";
		
	"</td>";
	
	cardHtml += "</tbody>";
	cardHtml += "</table>";
	cardHtml += "</div>";
	
	cardHtml += "<div class='stats'>";
	
	cardHtml += "<div>";
	cardHtml += "<div class='pie_chart_container'>";
	cardHtml += "<div id='total_liability_chart' class='pie_chart bigger theme_modern theme_yellow'>";
	cardHtml += "<p class='percentage_value'>%0</p>";
	cardHtml += "</div>";
	cardHtml += "<p><span>" + getPriceString(this.totalLiability) + "</span> TL<br>YÜKÜMLÜLÜK</p>";
	cardHtml += "</div>";
	cardHtml += "</div>";
	
	cardHtml += "<div>";
	cardHtml += "<div class='pie_chart_container'>";
	cardHtml += "<div id='paid_total_chart' class='pie_chart bigger theme_modern theme_green'>";
	cardHtml += "<p class='percentage_value'>%0</p>";
	cardHtml += "</div>";
	cardHtml += "<p><span>" + getPriceString(this.totalPayment) + "</span> TL<br>ÖDENEN</p>";
	cardHtml += "</div>";
	cardHtml += "</div>";
	
	cardHtml += "<div>";
	cardHtml += "<div class='pie_chart_container'>";
	cardHtml += "<div id='remaining_total_chart' class='pie_chart bigger theme_modern'>";
	cardHtml += "<p class='percentage_value'>%0</p>";
	cardHtml += "</div>";
	cardHtml += "<p><span>" + getPriceString(this.liability) + "</span> TL<br>KALAN</p>";
	cardHtml += "</div>";
	cardHtml += "</div>";
	
	cardHtml += "</div>";
	cardHtml += "<div class='body'>";
	cardHtml += this.getTable();
	cardHtml += "</div>";
	cardHtml += "</div>";
	
	return cardHtml;
}

PaymentPlan.prototype.getServletParameterString = function(mode, modifiedCompany) {}

PaymentPlan.prototype.updateInDB = function(modifiedCompany, callback) {}
