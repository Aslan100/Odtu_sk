function Event(id, name, type, branch, location, startTime, endTime, meetingPoint, meetingTime, accomodationLocation, breakUpPoint, breakUpTime, parentContributionAmount)
{
	this.id = id;
	this.name = name;
	this.type = type;
	this.branch = branch;
	this.location = location;
	this.startTime = startTime;
	this.endTime = endTime;
	
	this.meetingPoint = meetingPoint;
	this.meetingTime = meetingTime;
	this.accomodationLocation = accomodationLocation;
	this.breakUpPoint = breakUpPoint;
	this.breakUpTime = breakUpTime;
	
	this.parentContributionAmount = parentContributionAmount;
	this.squad = null;
	var that = this;
}

Event.prototype.reset = function()
{
	this.id = -1;
	this.name = null;
	this.type = null;
	this.branch = null;	
	
	if(this.location != null)
		this.location.reset();
	
	this.startTime = -1;
	this.endTime = -1;
	
	if(this.meetingPoint != null)
		this.meetingPoint.reset();
	
	this.meetingTime = -1;
	
	if(this.breakUpPoint != null)
		this.breakUpPoint.reset();
	
	this.breakUpTime = -1;
	
	this.parentContributionAmount = -1;
}

Event.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("type"))
			this.type = eventTypes[eventTypes.indexOf(element.getAttribute("type"))];
			
		if(element.hasAttribute("branch"))
			this.branch = branches[branches.indexOf(element.getAttribute("branch"))];
		
		if(element.hasAttribute("start_time"))
			this.startTime = new Number(element.getAttribute("start_time"));
			
		if(element.hasAttribute("end_time"))
			this.endTime = new Number(element.getAttribute("end_time"));
		
		if(element.hasAttribute("meeting_time"))
			this.meetingTime = new Number(element.getAttribute("meeting_time"));
		
		if(element.hasAttribute("break_up_time"))
			this.breakUpTime = new Number(element.getAttribute("break_up_time"));
				
		this.parentContributionAmount = parseFloat(element.getAttribute("parent_contribution_amount"));
		
		let locNodes = element.getElementsByTagName("location");
		
		if(locNodes != null && locNodes.length > 0)
		{
			this.location = new Location();
			this.location.parseFromXMLElement(locNodes[0]);
	    }
	    
	    let meetingPointNodes = element.getElementsByTagName("meeting_point");
		
		if(meetingPointNodes != null && meetingPointNodes.length > 0)
		{
			this.meetingPoint = new Location();
			this.meetingPoint.parseFromXMLElement(meetingPointNodes[0]);
	    }
		
		let accomodationLocNodes = element.getElementsByTagName("accomodation_location");
		
		if(accomodationLocNodes != null && accomodationLocNodes.length > 0)
		{
			this.accomodationLocation = new Location();
			this.accomodationLocation.parseFromXMLElement(accomodationLocNodes[0]);
	    }
	    
	    let breakUpPointNodes = element.getElementsByTagName("break_up_point");
		
		if(breakUpPointNodes != null && breakUpPointNodes.length > 0)
		{
			this.breakUpPoint = new Location();
			this.breakUpPoint.parseFromXMLElement(breakUpPointNodes[0]);
	    }
	}
	catch(err) 
	{
		this.reset();
	}
}

Event.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Event.prototype.getCard = function()
{
	let cardHtml = "";
	
	cardHtml += "<div class='event_card' data-id='" + this.id + "'>";
	cardHtml += "<header style='background-color: " + this.location.representingColour + "' >";
	cardHtml += "<section>"
	cardHtml += "<h2>" + this.name + "<br>" + eventTypeVals[eventTypes.indexOf(this.type)].toUpperCase() + "</h2>";
	cardHtml += "<button type='button' id='fetch_squad_button' class='transparent'>Kafile Bilgileri</button>";
	
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
	cardHtml += "<tr><td class='label'>ETKİNLİK BRANŞI</td><td>" + branchVals[branches.indexOf(this.branch)] + "</td></tr>";
	cardHtml += "<tr><td class='label'>BAŞLANGIÇ TARİHİ</td><td>" + getDateString(new Date(this.startTime)) + "</td></tr>";
	cardHtml += "<tr><td class='label'>SAAT</td><td>" + getTimeString(new Date(this.startTime)) + "</td></tr>";
	cardHtml += "<tr><td class='label'>BİTİŞ TARİHİ</td><td>" + getDateString(new Date(this.endTime)) + "</td></tr>";
	cardHtml += "<tr><td class='label'>SAAT</td><td>" + getTimeString(new Date(this.endTime)) + "</td></tr>";
	cardHtml += "<tr><td class='label'>KATKI PAYI</td><td>" + (this.parentContributionAmount > 0 ? this.parentContributionAmount + " TL" : "0 TL") + "</td></tr>";
	cardHtml += "</table>";
	
	if(this.location == null || this.location.address == null)
	{
		cardHtml += "<table>";
		cardHtml += "<tr><td class='page_title' colspan='2'>KONUM BİLGİLERİ</td></tr>";
		cardHtml += "<tr><td class='label'>KONUM BİLGİSİ BULUNAMADI</td></tr>";
		cardHtml += "</table>";
	}
	else
	{
		cardHtml += "<table>";
		cardHtml += "<tr><td class='page_title' colspan='2'>KONUM BİLGİLERİ</td></tr>";
		cardHtml += "<tr><td class='label'>TESİS ADI</td><td>" + this.location.name + "</td></tr>";
		cardHtml += "<tr><td class='label'>ŞEHİR</td><td>" + this.location.address.city.name + "</td></tr>";
		cardHtml += "<tr><td class='label'>İLÇE</td><td>" + (this.location.address.district != null ? this.location.address.district.name : "-") + "</td></tr>";
		cardHtml += "<tr><td style='text-align: left !important; padding-left: 10px !important'><a href='' class='button_like dark'>Detaylar</a></td></tr>";
		cardHtml += "</table>";
	}
	
	cardHtml += "<div id='meeting_point_data_container' class='placeholder'>";
	cardHtml += "<img src='res/visual/icon/event_meeting_point.png'/>";
	cardHtml += "<button type='button' id='fetch_meeting_point_button' class='dark'>YÜKLE</button>";
	cardHtml += "</div>";
	
	cardHtml += "<div id='accomodation_data_container' class='placeholder'>";
	cardHtml += "<img src='res/visual/icon/event_accomodation.png'/>";
	cardHtml += "<button type='button' id='fetch_accomodation_button' class='dark'>YÜKLE</button>";
	cardHtml += "</div>";
	
	cardHtml += "<div id='transportaion_data_container' class='placeholder'>";
	cardHtml += "<img src='res/visual/icon/event_transportation.png'/>";
	cardHtml += "<button type='button' id='fetch_transportation_button' class='dark'>YÜKLE</button>";
	cardHtml += "</div>";
	
	cardHtml += "</div>";
	cardHtml += "<div class='nav'>";
	
	for(let i = 0; i < 5; i++)
		cardHtml += "<button class='clear_style " + (i == 0 ? 'selected' : '') + "'></button>";
	
	cardHtml += "</div>";
	cardHtml += "</div>";
	
	return cardHtml;
}

Event.prototype.getSquadCard = function()
{
	let cardHtml = "<div class='squad_card' data-id='" + this.id + "'>";
	cardHtml += "<div class='data'>";
	cardHtml += "<div class='inline_input_row'>";
	
	cardHtml += "<div class='input_container select_container'>";
	cardHtml += "<select id='member_role_select' name='member_role'><option value='-1' disabled='disabled' selected></select>";
	cardHtml += "<label for='member_role_select'>Katılımcı Türü</label>";
	cardHtml += "</div>";
	
	cardHtml += "<div class='input_container select_container'>";
	cardHtml += "<select id='gender_select' name='gender' disabled='disabled'><option value='-1' disabled='disabled' selected></select>";
	cardHtml += "<label for='gender_select'>Kategori</label>";
	cardHtml += "</div>";
	
	cardHtml += "<div class='input_container select_container' style='min-width: 188px; max-width: 188px;'>";
	cardHtml += "<select id='team_select' name='team' disabled='disabled'><option value='-1' disabled='disabled' selected></select>";
	cardHtml += "<label for='team_select'>Takım</label>";
	cardHtml += "</div>";
	
	cardHtml += "<div class='input_container select_container' style='min-width: 188px; max-width: 188px;'>";
	cardHtml += "<select multiple id='member_select'></select>";
	cardHtml += "<input type='hidden' id='selected_members_input' name='selected_members' value='-1'/>";
	cardHtml += "</div>";
	
	cardHtml += "<div class='input_container'>";
	cardHtml += "<button type='button' id='add_members_button' style='margin-top: 0px; height: 46px;'>Ekle</button>";
	cardHtml += "</div>";
	
	cardHtml += "</div>";
	
	if(this.squad == null || this.squad.length == 0)
	{
		cardHtml += "<div class='placeholder'>";
		cardHtml += "<img src='res/visual/icon/event_squad.png'/>";
		cardHtml += "</div>";
	}
	else
	{
		cardHtml += "<table cellpadding='0' cellspacing='0'>";
		cardHtml += "<thead>";
		cardHtml += "<tr><th>Görevi</th><th>Adı</th><th>Soyadı</th><th>Doğum Tarihi</th><th class='collapse'></th></tr>";
		cardHtml += "</thead>";
		cardHtml += "<tbody>";
		
		for(let i = 0; i < this.squad.length; i++)
			cardHtml += "<tr data-id=" + this.squad[i][0] + "><td>" + this.squad[i][4] + "</td><td>" + this.squad[i][1] + "</td><td>" + this.squad[i][2] + "</td><td>" + (this.squad[i][3] != null ? this.squad[i][3] : "-") + "</td><td class='collapse'><button type='button' class='clear_style'><img src='res/visual/icon/trash_black.png'/></button></td></tr>";
			
		cardHtml += "</tbody></table>";	
	}
	
	cardHtml += "</div>";
	
	return cardHtml;
}
Event.prototype.getServletParameterString = function(mode, modifiedCompany) {}

Event.prototype.updateInDB = function(modifiedCompany, callback) {}