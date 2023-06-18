function Athlete(id, height, weight, birthDate, bloodType, primaryBranch, idNo, school, membership, ageGroup, loginable)
{
	this.id = id;
	this.height = height;
	this.weight = weight;
	this.birthDate = birthDate;
	this.ageGroup = null;
	this.bloodType = bloodType;
	this.primaryBranch = primaryBranch;
	this.idNo = idNo;
	this.school = school;
	this.membership = membership;
	this.ageGroup = ageGroup;
	this.loginable = loginable;
	
	var that = this;
}

Athlete.prototype.reset = function()
{
	this.id = -1;
	this.height = -1;
	this.weight = -1;
	this.birthDate = null;
	this.ageGroup = null;
	this.bloodType = null;
	this.primaryBranch = null;
	this.idNo = null;
	this.school = null;
	this.membership = null;
	this.ageGroup = null;
	
	if(this.loginable != null)
		this.loginable.reset()
}

Athlete.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("height"))
			this.height = parseInt(element.getAttribute("height"));
			
		if(element.hasAttribute("weight"))
			this.weight = parseFloat(element.getAttribute("weight"));
		
		if(element.hasAttribute("birth_date"))
			this.birthDate = element.getAttribute("birth_date");
		
		if(element.hasAttribute("age_group"))
			this.ageGroup = ageGroups[ageGroups.indexOf(element.getAttribute("age_group"))];
			
		if(element.hasAttribute("blood_type"))
			this.bloodType = element.getAttribute("blood_type");
		
		if(element.hasAttribute("primary_branch"))
			this.primaryBranch = branches[branches.indexOf(element.getAttribute("primary_branch"))];
			
		if(element.hasAttribute("id_no"))
			this.idNo = element.getAttribute("id_no");	
			
		if(element.hasAttribute("school"))
			this.school = element.getAttribute("school");
				
		if(element.hasAttribute("membership"))
			this.membership = membershipStates[membershipStates.indexOf(element.getAttribute("membership"))];
		
		if(element.hasAttribute("age_group"))
			this.ageGroup = ageGroups[ageGroups.indexOf(element.getAttribute("age_group"))];
		
		this.loginable = new Loginable();
		this.loginable.parseFromXMLElement(element);
	}
	catch(err) 
	{
		this.reset();
	}
}

Athlete.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Athlete.prototype.compliesWith = function(filterObj)
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

Athlete.prototype.getValue = function(attribute)
{
	var output = "";
	
	for(let property in this)
	{
		if(property == attribute)
			return this[property];
	}
		
	return null;
}

Athlete.prototype.getCard = function()
{
	let cardHtml = "";
	cardHtml += "<div class='athlete_card' data-id='" + this.loginable.loginableId + "'>";
	cardHtml += "<header>";
	cardHtml += "<figure class='" + this.loginable.state.toLowerCase() + "'></figure>";
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
	cardHtml += "<tr><td class='page_title' colspan='2'>TEMEL BİLGİLER</td></tr>";
	cardHtml += "<tr><td class='label'>TAKIMI</td><td>" + (this.primaryTeam != null ? this.primaryTeam : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>DOĞUM TARİHİ</td><td>" + this.birthDate + "</td></tr>";
	cardHtml += "<tr><td class='label'>YAŞ GRUBU</td><td>" + ageGroupVals[ageGroups.indexOf(this.ageGroup)] + "</td></tr>";
	cardHtml += "<tr><td class='label'>BOY</td><td>" + (this.height > 0 ? this.height + " cm" : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>KİLO</td><td>" + (this.weight > 0 ? this.weight + " kg" : "-") + "</td></tr>";
	cardHtml += "<tr><td class='label'>CİNSİYET</td><td>" + genderVals[genders.indexOf(this.loginable.gender)] + "</td></tr>";
	cardHtml += "<tr><td class='label'>KAN GRUBU</td><td>A RH(+)</td></tr>";
	cardHtml += "</table>";
	
	cardHtml += "<table>";
	cardHtml += "<tr><td class='page_title' colspan='2'>KİŞİSEL BİLGİLER</td></tr>";
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
	
	/*cardHtml += "<table>";
	cardHtml += "<tr><td class='page_title' colspan='2'>LİSANS BİLGİLERİ</td></tr>";
	cardHtml += "<tr><td class='label'>LİSANS NO</td><td>89657123</td></tr>";
	cardHtml += "<tr><td class='label'>VERİLİŞ TARİHİ</td><td>12.10.2005</td></tr>";
	cardHtml += "<tr><td class='label'>GEÇERLİLİK TARİHİ</td><td>12.10.2006</td></tr>";
	cardHtml += "<tr><td class='label'>VERİLDİĞİ YER</td><td>ANKARA</td></tr>";
	cardHtml += "</table>";*/
	
	cardHtml += "<div id='licence_container' class='placeholder'>";
	cardHtml += "<img src='res/visual/icon/licence.png'/>";
	cardHtml += "<button type='button' id='fetch_licence_button' class='dark'>YÜKLE</button>";
	cardHtml += "</div>";
	
	cardHtml += "<div id='payment_plan_container' class='placeholder'>";
	cardHtml += "<img src='res/visual/icon/payment.png'/>";
	cardHtml += "<button type='button' id='fetch_payment_plan_button' class='dark'>YÜKLE</button>";
	cardHtml += "</div>";
	
	cardHtml += "</div>";
	cardHtml += "<div class='nav'>";
	
	for(let i = 0; i < 5; i++)
		cardHtml += "<button class='clear_style " + (i == 0 ? 'selected' : '') + "'></button>";
	
	cardHtml += "</div>";
	cardHtml += "</div>";

	return cardHtml;
}

Athlete.prototype.getServletParameterString = function(mode, modifiedCompany) {}

Athlete.prototype.updateInDB = function(modifiedCompany, callback) {}
