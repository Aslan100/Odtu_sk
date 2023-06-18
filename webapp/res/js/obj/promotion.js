function Promotion(id, title, isSiblingDiscount, discountRatio, discountAmount, overridesOthers)
{
	this.id = id;
	this.title = title;
	this.isSiblingDiscount = isSiblingDiscount;
	this.discountRatio = discountRatio;
	this.discountAmount = discountAmount;
	this.overridesOthers = overridesOthers;
	
	var that = this;
}

Promotion.prototype.reset = function()
{
	this.id = -1;
	this.title = null;
	this.isSiblingDiscount = false;
	this.discountRatio = -1;
	this.discountAmount = -1;
	this.overridesOthers = false;
}

Promotion.prototype.parseFromXMLElement = function(element)
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
		
		this.isSiblingDiscount = element.getAttribute("is_sibling_discount") === "true";
		this.discountRatio = parseFloat(element.getAttribute("discount_ratio"));
		this.discountAmount = parseFloat(element.getAttribute("discount_amount"));
		this.overridesOthers = element.getAttribute("overrides_others") === "true";
	}
	catch(err)
	{
		this.reset();
	}
}

Promotion.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Promotion.prototype.compliesWith = function(filterObj)
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

Promotion.prototype.getValue = function(attribute)
{
	var output = "";
	
	for(let property in this)
	{
		if(property == attribute)
			return this[property];
	}
		
	return null;
}