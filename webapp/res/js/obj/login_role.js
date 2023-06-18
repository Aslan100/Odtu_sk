function LoginRole(id, title, isEmbedded, targetedType, isDefinitive, userMod, roleMod, branchMod, teamMod, athleteMod, facilityMod, eventMod, paymentMod, creationTime, lastModifiedTime)
{
	this.id = id;
	this.title = title;
	this.isEmbedded = isEmbedded;
	this.targetedType = targetedType;
	this.isDefinitive = isDefinitive;
	
	this.userMod = userMod;
	this.roleMod = roleMod;
	this.branchMod = branchMod;
	this.teamMod = teamMod;
	this.athleteMod = athleteMod;
	this.facilityMod = facilityMod;
	this.eventMod = eventMod;
	this.paymentMod = paymentMod;
	
	this.creationTime = creationTime;
	this.lastModifiedTime = lastModifiedTime;
	
	var that = this;
}

LoginRole.prototype.reset = function()
{
	this.id = -1;
	this.title = null;
	this.isEmbedded = false;
	this.targetedType = null;
	this.isDefinitive = false;
	
	this.userMod = null;
	this.roleMod = null;
	this.branchMod = null;
	this.teamMod = null;
	this.athleteMod = null;
	this.facilityMod = null;
	this.eventMod = null;
	this.paymentMod = null;
	
	this.creationTime = -1;
	this.lastModifiedTime = -1;
}

LoginRole.prototype.parseFromXMLElement = function(element)
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
		
		this.isEmbedded = (element.getAttribute("is_embedded") === "true");
			
		if(element.hasAttribute("targeted_type"))
			this.targetedType = element.getAttribute("targeted_type");
				
		this.isDefinitive = (element.getAttribute("is_definitive") === "true");
		
		this.userMod = element.getAttribute("user_mod");
		this.roleMod = element.getAttribute("role_mod");
		this.branchMod = element.getAttribute("branch_mod");
		this.teamMod = element.getAttribute("team_mod");
		this.athleteMod = element.getAttribute("athlete_mod");
		this.facilityMod = element.getAttribute("facility_mod");
		this.eventMod = element.getAttribute("event_mod");
		this.paymentMod = element.getAttribute("payment_mod");
		
		if(element.hasAttribute("creation_time"))
			this.creationTime = new Number((element.getAttribute("creation_time")));
		
		if(element.hasAttribute("last_modified_time"))
			this.lastModifiedTime = new Number((element.getAttribute("last_modified_time")));
	}
	catch(err) 
	{
		this.reset();
	}
}

LoginRole.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";<br>";
	}
		
	return output.length > 0 ? output : null;
}

LoginRole.prototype.getServletParameterString = function(mode, modifiedCompany) {}

LoginRole.prototype.createCertificateInDB = function(callback) {}

LoginRole.prototype.updateInDB = function(modifiedCompany, callback) {}

LoginRole.prototype.deleteCertificateFromDB = function(callback) {}