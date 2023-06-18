function Loginable(loginableId, code, email, name, surname, gender, phoneNumber, hesCode, state, creationTime, lastModifiedTime)
{
	this.loginableId = loginableId;
	this.code = null;
	this.email = email;
	this.name = name;
	this.surname = surname;
	this.gender = gender;
	this.phoneNumber = phoneNumber;
	this.hesCode = hesCode;
	this.state = state;
	this.creationTime = creationTime;
	this.lastModifiedTime = lastModifiedTime;
	
	this.licences = null;
	this.medicalData = null;
	var that = this;
}

Loginable.prototype.reset = function()
{
	this.loginableId = -1;
	this.code = null;
	this.email = null;
	this.name = null;
	this.surname = null;
	this.gender = null;
	this.hesCode = null;
	this.state = null;
	this.creationTime = -1;
	this.lastModifiedTime = -1;
	
	this.licences = null;
	this.medicalData = null;
}

Loginable.prototype.parseFromXMLElement = function(element)
{
	try
	{
		this.reset();
		
		if(element.hasAttribute("loginable_id"))
		{
			this.loginableId = parseInt(element.getAttribute("loginable_id"));
			
			if(isNaN(this.loginableId))
				throw "";
		}
		
		if(element.hasAttribute("code"))
			this.code = element.getAttribute("code");
			
		if(element.hasAttribute("email"))
			this.email = element.getAttribute("email");
		
		if(element.hasAttribute("name"))
			this.name = element.getAttribute("name");
			
		if(element.hasAttribute("surname"))
			this.surname = element.getAttribute("surname");
			
		if(element.hasAttribute("gender"))
			this.gender = genders[genders.indexOf(element.getAttribute("gender"))];
		
		if(element.hasAttribute("phone_number"))
			this.phoneNumber = element.getAttribute("phone_number");
				
		if(element.hasAttribute("hes_code"))
			this.hesCode = element.getAttribute("hes_code");
					
		if(element.hasAttribute("state"))
			this.state = loginableStates[loginableStates.indexOf(element.getAttribute("state"))];
			
		if(element.hasAttribute("creation_time"))
			this.creationTime = new Number(element.getAttribute("creation_time"));
		
		if(element.hasAttribute("last_modified_time"))
			this.lastModifiedTime = new Number(element.getAttribute("last_modified_time"));
	}
	catch(err) 
	{
		this.reset();
	}
}

Loginable.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\n";
	}
		
	return output.length > 0 ? output : null;
}

Loginable.prototype.compliesWith = function(filterObj)
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

Loginable.prototype.isValid = function()
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

Loginable.prototype.fullName = function()
{
	let fullName = "";
	
	fullName += this.name != null ? (" " + this.name) : "";
	fullName += this.surname != null ? (" " + this.surname) : "";
	
	if(fullName.length > 0)
		return fullName.substring(1);
		
	return null;	 
}

Loginable.prototype.findLicence = function(documentId)
{
	for(let i = 0; this.licences != null && i < this.licences.length; i++)
	{
		if(this.licences[i].documentId == parseInt(documentId))
			return this.licences[i];
	}
	
	return null;
}

/*Lisans*/
function Licence(id, ownerLoginableId, documentNo, validFrom, validUntil, licenceBranch, isLifeguardLicence)
{
	this.id = id;
	this.ownerLoginableId = ownerLoginableId;
	this.documentNo = documentNo;
	this.validFrom = validFrom;
	this.validUntil = validUntil;
	this.licenceBranch = licenceBranch;
	this.isLifeguardLicence = isLifeguardLicence;
	
	this.documentId = null;
	var that = this;
}

Licence.prototype.reset = function()
{
	this.id = -1;
	this.ownerLoginableId = -1;
	this.documentNo = null;
	this.validFrom = null;
	this.validUntil = null;
	this.licenceBranch = null;
	this.isLifeguardLicence = false;
}

Licence.prototype.parseFromXMLElement = function(element)
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
		
		this.documentId = parseInt(element.getAttribute("document_id"));
		this.ownerLoginableId = element.getAttribute("owner_loginable_id");
		
		if(element.hasAttribute("document_no"))
			this.documentNo = element.getAttribute("document_no");
			
		if(element.hasAttribute("valid_from"))
			this.validFrom = element.getAttribute("valid_from");
			
		if(element.hasAttribute("valid_until"))
			this.validUntil = element.getAttribute("valid_until");
			
		if(element.hasAttribute("licence_branch"))
			this.licenceBranch = branches[branches.indexOf(element.getAttribute("licence_branch"))];
		
		this.isLifeguardLicence = element.tagName == "lifeguard_licence";
	}
	catch(err) 
	{
		this.reset();
	}
}

Licence.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Licence.prototype.getValue = function(attribute)
{
	var output = "";
	
	for(let property in this)
	{
		if(property == attribute)
			return this[property];
	}
		
	return null;
}

Licence.prototype.getTable = function(order)
{
	licenceHtml = "<table data-document_id='" + this.documentId + "'>";
	licenceHtml += "<tr><td colspan='2' class='page_title'>" + order + ". LİSANS</td></tr>";
	licenceHtml += "<tr><td class='label'>LİSANS TİPİ</td><td>" + (this.isLifeguardLicence ? "Cankurataran Lisansı" : "Antrenör Lisansı") + "</td></tr>";
	licenceHtml += "<tr><td class='label'>LİSANS NO</td><td>" + this.documentNo + "</td></tr>";
	
	if(!this.isLifeguardLicence)
		licenceHtml += "<tr><td class='label'>LİSANS BRANŞI</td><td>" + branchVals[branches.indexOf(this.licenceBranch)] + "</td></tr>";
		
	licenceHtml += "<tr><td class='label'>BAŞLANGIÇ</td><td>" + (this.validFrom != null ? this.validFrom : "-") + "</td></tr>";
	licenceHtml += "<tr><td class='label'>BİTİŞ</td><td>" + this.validUntil + "</td></tr>";
	licenceHtml += "<tr><td colspan='2'><button type='button' class='clear_style' mode='delete'>Sil</button><button type='button' class='clear_style' mode='edit'>Düzenle</button></td></tr>";
	licenceHtml += "</table>";
				
	return licenceHtml;
}

/*Medikal*/
function MedicalData(id, pastTherapies, activeIssues, activeMedications, specialCareNeeds)
{
	this.id = id;
	this.pastTherapies = pastTherapies;
	this.activeIssues = activeIssues;
	this.activeMedications = activeMedications;
	this.specialCareNeeds = specialCareNeeds;
	
	var that = this;
}

MedicalData.prototype.reset = function()
{
	this.id = -1;
	this.pastTherapies = null;
	this.activeIssues = null;
	this.activeMedications = null;
	this.specialCareNeeds = null;
}

MedicalData.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("past_therapies"))
			this.pastTherapies = element.getAttribute("past_therapies");
			
		if(element.hasAttribute("active_issues"))
			this.activeIssues = element.getAttribute("active_issues");
		
		if(element.hasAttribute("active_medications"))
			this.activeMedications = element.getAttribute("active_medications");
			
		if(element.hasAttribute("special_care_needs"))
			this.specialCareNeeds = element.getAttribute("special_care_needs");
	}
	catch(err) 
	{
		this.reset();
	}
}

MedicalData.prototype.getValue = function(attribute)
{
	var output = "";
	
	for(let property in this)
	{
		if(property == attribute)
			return this[property];
	}
		
	return null;
}

MedicalData.prototype.getTable = function()
{
	medicalsHtml = "<div class='table_wrapper'>";
	medicalsHtml += "<table>";
	medicalsHtml += "<tr><td class='page_title'>SAĞLIK BİLGİLERİ</td><td style='border-bottom: none; width: 50px;'><button type='button' class='clear_style' mode='delete'></button><button type='button' class='clear_style' mode='edit'></button></td></tr>";
	
	if(this.pastTherapies != null)
	{
		medicalsHtml += "<tr><td class='label' style='border-bottom: none; padding-bottom: 0px;'>GEÇMİŞ TEDAVİLERİ</td></tr>";
		medicalsHtml += "<tr><td colspan='2' style='text-align: left; padding-left: 10px;'>" + this.pastTherapies + "</td></tr>";
	}
	
	if(this.activeIssues != null)
	{
		medicalsHtml += "<tr><td class='label' style='border-bottom: none; padding-bottom: 0px;'>ETKİN DURUM VE ALLERJİ BİLGİLERİ</td></tr>";
		medicalsHtml += "<tr><td colspan='2' style='text-align: left; padding-left: 10px;'>" + this.activeIssues + "</td></tr>";
	}
	
	if(this.activeMedications != null)
	{
		medicalsHtml += "<tr><td class='label' style='border-bottom: none; padding-bottom: 0px;'>KULLANMAKTA OLDUĞU İLAÇLAR</td></tr>";
		medicalsHtml += "<tr><td colspan='2' style='text-align: left; padding-left: 10px;'>" + this.activeMedications + "</td></tr>";
	}
	
	if(this.specialCareNeeds != null)
	{
		medicalsHtml += "<tr><td class='label' style='border-bottom: none; padding-bottom: 0px;'>ÖZEL İLGİ GEREKTİREN DURUMLARI</td></tr>";
		medicalsHtml += "<tr><td colspan='2' style='text-align: left; padding-left: 10px;'>" + this.specialCareNeeds + "</td></tr>";
	}
	
	medicalsHtml += "</table>";
	medicalsHtml += "</div>";
				
	return medicalsHtml;
}