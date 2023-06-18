function District()
{
	this.id = -1;
	this.name = null;
	
	var that = this;
}

function District(id, name)
{
	this.id = id;
	this.name = name;
	
	var that = this;
}

District.prototype.equals = function(newDistrict)
{
	try
	{
		return (this.id === newDistrict.id && this.name === newDistrict.name);
	}
	catch(err)
	{
		return false;
	}
}

District.prototype.clone = function()
{
	var copyDistrict = new District(this.id, this.name);
	
	return copyDistrict;
}

District.prototype.cloneFrom = function(setterDistrict)
{
	try
	{
		this.id = parseInt(setterDistrict.id);
		
		if(isNaN(this.id))
			throw "";
			
		this.name = setterDistrict.name;
	}
	catch(err)
	{
		this.reset();
	}
}

District.prototype.reset = function()
{
	this.id = -1;
	this.name = null;
}

District.prototype.parseFromXMLElement = function(element)
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
	}
	catch(err) 
	{
		this.reset();
	}
}

District.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\n";
	}
		
	return output.length > 0 ? output : null;
}