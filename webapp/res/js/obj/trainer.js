function Trainer(id, primaryBranch, label, level, height, weight, bloodType, idNo, birthDate, placeOfBirth, homeLocation, mothersName, fathersName, loginable)
{
	this.id = id;
	this.primaryBranch = primaryBranch;
	this.label = label;
	this.level = level;
	
	this.height = height;
	this.weight = weight;
	this.bloodType = bloodType;
	
	this.idNo = idNo;
	this.birthDate = birthDate;
	this.placeOfBirth = placeOfBirth;
	this.homeLocation = homeLocation;
	this.mothersName = mothersName;
	this.fathersName = fathersName;
	
	this.loginable = loginable;
	
	var that = this;
}

Trainer.prototype.reset = function()
{
	this.id = -1;
	this.primaryBranch = null;
	this.label = null;
	this.level = -1;
	
	this.height = -1;
	this.weight = -1;
	this.bloodType = null;
	
	this.idNo = null;
	this.birthDate = null;
	this.placeOfBirth = null;
	this.mothersName = null;
	this.fathersName = null;
	
	if(this.homeLocation != null)
		this.homeLocation.reset();
		
	if(this.loginable != null)
		this.loginable.reset();
}

Trainer.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("primary_branch"))
			this.primaryBranch = branches[branches.indexOf(element.getAttribute("primary_branch"))];
		
		if(element.hasAttribute("label"))
			this.label = trainerLabels[trainerLabels.indexOf(element.getAttribute("label"))];
		
		this.level = parseInt(element.getAttribute("level"));
		
		if(element.hasAttribute("height"))
			this.height = parseInt(element.getAttribute("height"));
			
		if(element.hasAttribute("weight"))
			this.weight = parseFloat(element.getAttribute("weight"));
		
		if(element.hasAttribute("blood_type"))
			this.bloodType = element.getAttribute("blood_type");
		
		if(element.hasAttribute("id_no"))
			this.idNo = element.getAttribute("id_no");
			
		if(element.hasAttribute("birth_date"))
			this.birthDate = element.getAttribute("birth_date");
		
		if(element.hasAttribute("mothers_name"))
			this.mothersName = element.getAttribute("mothers_name");
			
		if(element.hasAttribute("fathers_name"))
			this.fathersName = element.getAttribute("fathers_name");	
			
		let cityEls = element.getElementsByTagName("city");
		
		if(cityEls != null)
		{
			this.placeOfBirth = new City();
			this.placeOfBirth.parseFromXMLElement(cityEls[0]);
		}
		
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

Trainer.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Trainer.prototype.compliesWith = function(filterObj)
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

Trainer.prototype.getValue = function(attribute)
{
	var output = "";
	
	for(let property in this)
	{
		if(property == attribute)
			return this[property];
	}
		
	return null;
}

Trainer.prototype.getCard = function()
{
	let cardHtml = "";
	cardHtml += "<div class='athlete_card' data-id='" + this.loginable.loginableId + "'>";
	cardHtml += "<header>";
	cardHtml += "<figure></figure>";
	cardHtml += "<section>";
	cardHtml += "<h2>" + this.loginable.name + "<br>" + this.loginable.surname + "</h2>";
	cardHtml += "<p>" + trainerLabelVals[trainerLabels.indexOf(this.label)] + "</p>";
	
	cardHtml += "<div class='card_control'>";
	cardHtml += "<button id='delete_item_button' class='clear_style'></button>";
	cardHtml += "<button id='edit_item_button' class='clear_style'></button>";
	cardHtml += "<button id='close_card_button' class='clear_style'>&#x274C;</button>";
	cardHtml += "</div>";
	
	cardHtml += "</section>";
	cardHtml += "</header>";
	cardHtml += "<div class='data'>";
	
	cardHtml += "<table>";
	cardHtml += "<tr><td class='page_title' colspan='2'>TEMEL BİLGİLER</td></tr>";
	cardHtml += "<tr><td class='label'>E-POSTA</td><td>" + this.loginable.email + "</td></tr>";
	cardHtml += "<tr><td class='label'>BRANŞ</td><td>" + (this.primaryBranch  != null ? branchVals[branches.indexOf(this.primaryBranch)] : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>SEVİYE</td><td>" + (this.level > 0 ? this.level + ". Seviye" : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>TELEFON</td><td>" + (this.loginable.phoneNumber != null ? this.loginable.phoneNumber : "-") +"</td></tr>";
	cardHtml += "<tr><td class='label'>HES KODU</td><td>" + (this.loginable.hesCode != null ? this.loginable.hesCode : "-") +"</td></tr>";
	cardHtml += "</table>";
	
	cardHtml += "<table>";
	cardHtml += "<tr><td class='page_title' colspan='2'>KİŞİSEL BİLGİLER</td></tr>";
	cardHtml += "<tr><td class='label'>T.C. KİMLİK NO</td><td>" + (this.idNo != null ? this.idNo : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>DOĞUM TARİHİ</td><td>" + this.birthDate + "</td></tr>";
	cardHtml += "<tr><td class='label'>DOĞUM YERİ</td><td>" + (this.placeOfBirth != null ? this.placeOfBirth.name : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>ANNE ADI</td><td>" + (this.mothersName != null ? this.mothersName : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>BABA ADI</td><td>" + (this.fathersName != null ? this.fathersName : "-") + "</td></tr>";
	cardHtml += "</table>";
	
	if(this.homeLocation == null || this.homeLocation.address == null)
	{
		cardHtml += "<table>";
		cardHtml += "<tr><td class='page_title' colspan='2'>KONUM BİLGİLERİ</td></tr>";
		cardHtml += "<tr><td class='label'>ADRES BİLGİSİ BULUNAMADI</td></tr>";
		cardHtml += "</table>";
	}
	else
	{
		cardHtml += "<table>";
		cardHtml += "<tr><td class='page_title' colspan='2'>KONUM BİLGİLERİ</td></tr>";
		cardHtml += "<tr><td class='label'>ŞEHİR</td><td>" + this.homeLocation.address.city.name + "</td></tr>";
		cardHtml += "<tr><td class='label'>İLÇE</td><td>" + (this.homeLocation.address.district != null ? this.homeLocation.address.district.name : "-") + "</td></tr>";
		cardHtml += "<tr><td class='label'>ADRES</td><td>" + (this.homeLocation.address.addressString != null ? this.homeLocation.address.addressString : "-") + "</td></tr>";
		cardHtml += "<tr><td class='label'>ENLEM (LAT)</td><td>" + (this.homeLocation.address.latitude != null ? this.homeLocation.address.latitude : "-") + "</td></tr>";
		cardHtml += "<tr><td class='label'>BOYLAM (LNG)</td><td>" + (this.homeLocation.address.longitude != null ? this.homeLocation.address.longitude : "-") + "</td></tr>";
		cardHtml += "</table>";
	}
	
	cardHtml += "<table>";
	cardHtml += "<tr><td class='page_title' colspan='2'>FİZİKSEL BİLGİLER</td></tr>";
	cardHtml += "<tr><td class='label'>CİNSİYET</td><td>" + genderVals[genders.indexOf(this.loginable.gender)] + "</td></tr>";
	cardHtml += "<tr><td class='label'>BOY</td><td>" + (this.height > 0 ? this.height + " cm" : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>KİLO</td><td>" + (this.weight > 0 ? this.weight + " kg" : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>KAN GRUBU</td><td>" + bloodTypeVals[bloodTypes.indexOf(this.bloodType)] + "</td></tr>";
	cardHtml += "</table>";
	
	cardHtml += "<div id='licence_data_container' class='placeholder'>";
	cardHtml += "<img src='res/visual/icon/licence.png'/>";
	cardHtml += "<button type='button' id='fetch_licences_button' class='dark'>YÜKLE</button>";
	cardHtml += "</div>";
	
	cardHtml += "<div id='medical_data_container' class='placeholder'>";
	cardHtml += "<img src='res/visual/icon/medical.png'/>";
	cardHtml += "<button type='button' id='fetch_medical_data_button' class='dark'>YÜKLE</button>";
	cardHtml += "</div>";
	
	cardHtml += "</div>";
	cardHtml += "<div class='nav'>";
	
	for(let i = 0; i < 6; i++)
		cardHtml += "<button type='button' class='clear_style " + (i == 0 ? 'selected' : '') + "'></button>";
	
	cardHtml += "</div>";
	cardHtml += "</div>";

	return cardHtml;
}

Trainer.prototype.getServletParameterString = function(mode, modifiedCompany) {}

Trainer.prototype.updateInDB = function(modifiedCompany, callback) {}
