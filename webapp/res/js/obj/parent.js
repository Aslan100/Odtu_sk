function Parent(id, idNo, homeLocation, vehicleLicencePlate, hasChildren, loginable)
{
	this.id = id;
	this.idNo = idNo;
	this.homeLocation = homeLocation;
	this.vehicleLicencePlate = vehicleLicencePlate;
	this.hasChildren = hasChildren;
	this.loginable = loginable;
	
	var that = this;
}

Parent.prototype.reset = function()
{
	this.id = -1;
	this.idNo = null;
	this.homeLocation = null;
	this.vehicleLicencePlate = null;
	this.hasChildren = false;
	
	if(this.loginable != null)
		this.loginable.reset()
}

Parent.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("id_no"))
			this.idNo = element.getAttribute("id_no");	
		
		if(element.hasAttribute("vehicle_licence_plate"))
			this.vehicleLicencePlate = element.getAttribute("vehicle_licence_plate");
				
		if(element.hasAttribute("has_children"))
			this.hasChildren = element.getAttribute("has_children") === "true";
		
		let locationEls = element.getElementsByTagName("location");
		
		if(locationEls != null)
		{
			this.homeLocation = new Location();
			this.homeLocation.parseFromXMLElement(locationEls[0]);
		}
		
		this.loginable = new Loginable();
		this.loginable.parseFromXMLElement(element);
	}
	catch(err) 
	{
		this.reset();
	}
}

Parent.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Parent.prototype.compliesWith = function(filterObj)
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

Parent.prototype.getValue = function(attribute)
{
	var output = "";
	
	for(let property in this)
	{
		if(property == attribute)
			return this[property];
	}
		
	return null;
}