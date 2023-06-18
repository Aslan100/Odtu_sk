function Payment(id, periodStart, periodEnd, actualAmount, discount, amount, dueDate, paidAmount, paidDate, isPacified, state)
{
	this.id = id;
	this.periodStart = periodStart;
	this.periodEnd = periodEnd;
	this.actualAmount = actualAmount;
	this.discount = discount;
	this.amount = amount;
	this.dueDate = dueDate;
	this.paidAmount = paidAmount;
	this.paidDate = paidDate;
	this.isPacified = isPacified;
	this.state = state;
	
	var that = this;
}

Payment.prototype.reset = function()
{
	this.id = -1;
	this.periodStart = -1;
	this.periodEnd = -1;
	this.actualAmount = -1;
	this.discount = -1;
	this.amount = -1;
	this.dueDate = -1;
	this.paidAmount = -1;
	this.paidDate = -1;
	this.isPacified = false;
	this.state = null;
}

Payment.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("period_start"))
			this.periodStart = new Number(element.getAttribute("period_start"));
			
		if(element.hasAttribute("period_end"))
			this.periodEnd = new Number(element.getAttribute("period_end"));	
		
		if(element.hasAttribute("actual_amount"))
			this.actualAmount = parseFloat(element.getAttribute("actual_amount"));
		
		if(element.hasAttribute("discount"))
			this.discount = parseFloat(element.getAttribute("discount"));
		
		if(element.hasAttribute("amount"))
			this.amount = parseFloat(element.getAttribute("amount"));
		
		if(element.hasAttribute("due_date"))
			this.dueDate = new Number(element.getAttribute("due_date"));
		
		if(element.hasAttribute("paid_amount"))
			this.paidAmount = parseFloat(element.getAttribute("paid_amount"));
		
		if(element.hasAttribute("paid_date"))
			this.paidDate = new Number(element.getAttribute("paid_date"));
			
		if(element.hasAttribute("is_pacified"))
			this.isPacified = element.getAttribute("is_pacified") === "true";
		
		if(element.hasAttribute("state"))
			this.state = element.getAttribute("state");
	}
	catch(err) 
	{
		this.reset();
	}
}

Payment.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Payment.prototype.compliesWith = function(filterObj)
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

Payment.prototype.getValue = function(attribute)
{
	var output = "";
	
	for(let property in this)
	{
		if(property == attribute)
			return this[property];
	}
		
	return null;
}

Payment.prototype.getPeriodString = function()
{
	if(this.periodStart > 0 && this.periodEnd > 0)
		return getDateString(new Date(this.periodStart)) + " &#8212; " + getDateString(new Date(this.periodEnd));
		
	return "-";	
}