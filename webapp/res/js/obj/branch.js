function Branch(id, title, prices, penaltyRate)
{
	this.id = id;
	this.title = title;
	this.prices = prices;
	this.penaltyRate = penaltyRate;
	
	this.promotions = null;
	var that = this;
}

Branch.prototype.reset = function()
{
	this.id = -1;
	this.title = null;
	this.prices = null;
	this.penaltyRate = -1;
	this.promotions = null;
}

Branch.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("title"))
			this.title = element.getAttribute("title");
		
		this.prices = [];
		
		this.prices.push(parseFloat(element.getAttribute("daily_price")));
		this.prices.push(parseFloat(element.getAttribute("weekly_price")));
		this.prices.push(parseFloat(element.getAttribute("monthly_price")));
		this.prices.push(parseFloat(element.getAttribute("annual_price")));
		
		this.penaltyRate = parseFloat(element.getAttribute("penalty_rate"));
		
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

Branch.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\n";
	}
		
	return output.length > 0 ? output : null;
}

Branch.prototype.compliesWith = function(filterObj)
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

Branch.prototype.isValid = function()
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

Branch.prototype.getSiblingDiscountRatio = function()
{
	let ratio = 0;
	
	for(let i = 0; this.promotions != null && i < this.promotions.length; i++)
	{
		if(this.promotions[i].isSiblingDiscount)
			return this.promotions[i].discountRatio;
	}
	
	return ratio;
}