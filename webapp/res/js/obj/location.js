function Location(id, name, description, representingColour, address, weekTotals)
{
	this.id = id;
	this.name = name;
	this.description = description;
	this.representingColour = representingColour;
	this.address = address;
	this.weekTotals = weekTotals;
	
	var that = this;
}

Location.prototype.reset = function()
{
	this.id = -1;
	this.name = null;
	this.description = null;
	this.representingColour = null;
	this.weekTotals = null;
	
	if(this.address != null)
		this.address.reset()
}

Location.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("name"))
			this.name = element.getAttribute("name");
			
		if(element.hasAttribute("description"))
			this.description = element.getAttribute("description");
		
		if(element.hasAttribute("representing_colour"))
			this.representingColour = element.getAttribute("representing_colour");
			
		if(element.hasAttribute("week_totals"))
		{
			let totals = element.getAttribute("week_totals").split("-");
			this.weekTotals = [];
			
			for(let i = 0; i < totals.length; i++)
				this.weekTotals[i] = new Number(totals[i]);
		}	
		
		this.address = new Address();
		this.address.parseFromXMLElement(element.getElementsByTagName("address")[0]);
	}
	catch(err) 
	{
		this.reset();
	}
}

Location.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Location.prototype.compliesWith = function(filterObj)
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

Location.prototype.getValue = function(attribute)
{
	var output = "";
	
	for(let property in this)
	{
		if(property == attribute)
			return this[property];
	}
		
	return null;
}

Location.prototype.getCard = function()
{
	let cardHtml = "";
	cardHtml += "<div class='athlete_card' data-id='" + this.id + "'>";
	cardHtml += "<header>";
	cardHtml += "<figure></figure>";
	cardHtml += "<section>";
	cardHtml += "<h2>" + this.name + "</h2>";
	cardHtml += "<p>Tesis</p>";
	
	cardHtml += "<div class='card_control'>";
	cardHtml += "<button id='delete_athlete_button' class='clear_style'></button>";
	cardHtml += "<button id='edit_athlete_button' class='clear_style'></button>";
	cardHtml += "<button id='close_card_button' class='clear_style'>&#x274C;</button>";
	cardHtml += "</div>";
	
	cardHtml += "</section>";
	cardHtml += "</header>";
	cardHtml += "<div class='data'>";
	
	cardHtml += "<table>";
	cardHtml += "<tr><td class='label'>KONUM</td><td>" + this.address.latitude + ", " + this.address.longitude + "</td></tr>";
	cardHtml += "<tr><td class='label'>ADRES</td><td>" + (this.address.addressString != null ? this.address.addressString : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>İLÇE</td><td>" + (this.address.district != null ? this.address.district : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>ŞEHİR</td><td>" + this.address.city + "</td></tr>";
	cardHtml += "</table>";
	
	cardHtml += "</div>";
	
	return cardHtml;
}

Location.prototype.getServletParameterString = function(mode, modifiedCompany) {}

Location.prototype.updateInDB = function(modifiedCompany, callback) {}
