function City(id, name, code, districts)
{
	this.id = id;
	this.name = name;
	this.code = code;
	this.districts = districts;
	
	var that = this;
}

City.prototype.reset = function()
{
	this.id = -1;
	this.name = null;
	this.code = null;
	this.districts = null;
}

City.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("code"))
			this.code = element.getAttribute("code");
			
		let districtEls = element.getElementsByTagName("district");
		
		for(let i = 0; districtEls != null && i < districtEls.length; i++)
		{
			if(this.districts == null)
				this.districts = [];
				
			let nextDistrict = new District();
			nextDistrict.parseFromXMLElement(districtEls[i]);
			this.districts.push(nextDistrict);
		}
	}
	catch(err) 
	{
		this.reset();
	}
}

City.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\n";
	}
		
	return output.length > 0 ? output : null;
}