function Address(id, addressString, postCode, latitude, longitude, district, city)
{
	this.id = id;
	this.addressString = addressString;
	this.postCode = postCode;
	this.latitude = latitude;
	this.longitude = longitude;
	this.district = district;
	this.city = city;
	
	var that = this;
}

Address.prototype.reset = function()
{
	this.id = -1;
	this.addressString = null;
	this.postCode = null;
	this.latitude = -1;
	this.longitude = -1;
	this.district = null;
	this.city = null;
}

Address.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("address_string"))
			this.addressString = element.getAttribute("address_string");
			
		if(element.hasAttribute("post_code"))
			this.postCode = element.getAttribute("post_code");
			
		if(element.hasAttribute("latitude"))
			this.latitude = parseFloat(element.getAttribute("latitude"));
		
		if(element.hasAttribute("longitude"))
			this.longitude = parseFloat(element.getAttribute("longitude"));
		
		let cityEls = element.getElementsByTagName("city");
		
		if(cityEls.length > 0)
		{
			this.city = new City();
			this.city.parseFromXMLElement(cityEls[0]);
		}
		
		let districtEls = element.getElementsByTagName("district");
		
		if(districtEls.length > 0)
		{
			this.district = new District();
			this.district.parseFromXMLElement(districtEls[0]);
		}
	}
	catch(err) 
	{
		this.reset();
	}
}

Address.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Address.prototype.compliesWith = function(filterObj)
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

Address.prototype.getValue = function(attribute)
{
	var output = "";
	
	for(let property in this)
	{
		if(property == attribute)
			return this[property];
	}
		
	return null;
}

Address.prototype.getCard = function()
{
	let cardHtml = "";
	cardHtml += "<div class='athlete_card' data-id='" + this.loginable.loginableId + "'>";
	cardHtml += "<header>";
	cardHtml += "<figure></figure>";
	cardHtml += "<section>";
	cardHtml += "<h2>" + this.loginable.name + "<br>" + this.loginable.surname + "</h2>";
	cardHtml += "<p>" + (this.primaryBranch  != null ? branchVals[branches.indexOf(this.primaryBranch)] : "Branşı Yok") + "</p>";
	
	cardHtml += "<div class='card_control'>";
	cardHtml += "<button id='delete_athlete_button' class='clear_style'></button>";
	cardHtml += "<button id='edit_athlete_button' class='clear_style'></button>";
	cardHtml += "<button id='close_card_button' class='clear_style'>&#x274C;</button>";
	cardHtml += "</div>";
	
	cardHtml += "</section>";
	cardHtml += "</header>";
	cardHtml += "<div class='data'>";
	
	cardHtml += "<table>";
	cardHtml += "<tr><td class='label'>TAKIMI</td><td>" + (this.primaryTeam != null ? this.primaryTeam : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>DOĞUM TARİHİ</td><td>" + this.birthDate + "</td></tr>";
	cardHtml += "<tr><td class='label'>YAŞ GRUBU</td><td>" + ageGroupVals[ageGroups.indexOf(this.ageGroup)] + "</td></tr>";
	cardHtml += "<tr><td class='label'>BOY</td><td>" + (this.height > 0 ? this.height + " cm" : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>KİLO</td><td>" + (this.weight > 0 ? this.weight + " kg" : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>CİNSİYET</td><td>" + genderVals[genders.indexOf(this.loginable.gender)] + "</td></tr>";
	cardHtml += "<tr><td class='label'>KAN GRUBU</td><td>A RH(+)</td></tr>";
	cardHtml += "</table>";
	
	cardHtml += "<table>";
	cardHtml += "<tr><td class='label'>T.C. KİMLİK NO</td><td>" + (this.idNo != null ? this.idNo : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>E-POSTA</td><td>" + this.loginable.email + "</td></tr>";
	cardHtml += "<tr><td class='label'>TELEFON</td><td>" + (this.loginable.phoneNumber != null ? this.loginable.phoneNumber : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>OKUL</td><td>" + (this.school != null ? this.school : "-") + "</td></tr>";
	cardHtml += "</table>";
	
	cardHtml += "<div class='pager'>";
	cardHtml += "<div class='controls'>";
	cardHtml += "<button id='prev_page' class='clear_style'></button>";
	cardHtml += "<button id='next_page' class='clear_style'></button>";
	cardHtml += "</div>";
	cardHtml += "<table>";
	cardHtml += "<tr><td colspan='2' class='label' style='color: var(--system_red); border-bottom: none; padding-bottom: 5px;'>1. VELİ</td></tr>";
	cardHtml += "<tr><td class='label'>ADI</td><td>13894416826</td></tr>";
	cardHtml += "<tr><td class='label'>TELEFON</td><td>+90 555 274 65 50</td></tr>";
	cardHtml += "<tr><td class='label'>E-POSTA</td><td>oguz.aykun@gmail.com</td></tr>";
	cardHtml += "<tr><td class='label'>ADRES</td><td>Güzeltepe Mah. Ahmet Rasim Sokak No: 35/1 Çankaya/Ankara</td></tr>";
	cardHtml += "<tr><td class='label'>PLAKA</td><td>06 DC 5197</td></tr>";
	cardHtml += "</table>";
	cardHtml += "<table>";
	cardHtml += "<tr><td colspan='2' class='label' style='color: var(--system_red); border-bottom: none; padding-bottom: 5px;'>2. VELİ</td></tr>";
	cardHtml += "<tr><td class='label'>ADI</td><td>13894416826</td></tr>";
	cardHtml += "<tr><td class='label'>TELEFON</td><td>+90 555 274 65 50</td></tr>";
	cardHtml += "<tr><td class='label'>E-POSTA</td><td>oguz.aykun@gmail.com</td></tr>";
	cardHtml += "<tr><td class='label'>ADRES</td><td>-</td></tr>";
	cardHtml += "<tr><td class='label'>PLAKA</td><td>-</td></tr>";
	cardHtml += "</table>";
	cardHtml += "</div>";
	
	cardHtml += "<table>";
	cardHtml += "<tr><td class='label'>LİSANS NO</td><td>89657123</td></tr>";
	cardHtml += "<tr><td class='label'>VERİLİŞ TARİHİ</td><td>12.10.2005</td></tr>";
	cardHtml += "<tr><td class='label'>GEÇERLİLİK TARİHİ</td><td>12.10.2006</td></tr>";
	cardHtml += "<tr><td class='label'>VERİLDİĞİ YER</td><td>ANKARA</td></tr>";
	cardHtml += "</table>";
	
	cardHtml += "</div>";
	cardHtml += "<div class='nav'>";
	
	for(let i = 0; i < 4; i++)
		cardHtml += "<button class='clear_style " + (i == 0 ? 'selected' : '') + "'></button>";
	
	cardHtml += "</div>";
	cardHtml += "</div>";

	return cardHtml;
}

Address.prototype.getServletParameterString = function(mode, modifiedCompany) {}

Address.prototype.updateInDB = function(modifiedCompany, callback) {}
