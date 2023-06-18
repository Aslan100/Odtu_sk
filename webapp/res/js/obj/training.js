function Training(id, team, location, startTime, endTime, trainer, ownerTraining, isGroupParent)
{
	this.id = id;
	this.team = team;
	this.location = location;
	this.startTime = startTime;
	this.endTime = endTime;
	this.trainer = trainer;
	this.ownerTraining = ownerTraining;
	this.isGroupParent = isGroupParent;	
	
	var that = this;
}

Training.prototype.reset = function()
{
	this.id = -1;
	
	if(this.team != null)
		this.team.reset();
		
	if(this.location != null)
		this.location.reset();
	
	this.startTime = -1;
	this.endTime = -1;
	
	if(this.trainer != null)
		this.trainer.reset();
		
	if(this.ownerTraining != null)
		this.ownerTraining.reset();
}

Training.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("start_time"))
			this.startTime = new Number(element.getAttribute("start_time"));
			
		if(element.hasAttribute("end_time"))
			this.endTime = new Number(element.getAttribute("end_time"));
		
		this.isGroupParent = element.getAttribute("is_group_parent") === "true";
		
		let teamNodes = element.getElementsByTagName("team");
		
		if(teamNodes != null && teamNodes.length > 0)
		{
			this.team = new Team();
			this.team.parseFromXMLElement(teamNodes[0]);
	    }
		
		let locNodes = element.getElementsByTagName("location");
		
		if(locNodes != null && locNodes.length > 0)
		{
			this.location = new Location();
			this.location.parseFromXMLElement(locNodes[0]);
	    }
	    
		let trainerNodes = element.getElementsByTagName("trainer");
		
		if(trainerNodes != null && trainerNodes.length > 0)
		{
			this.trainer = new Trainer();
	        this.trainer.parseFromXMLElement(trainerNodes[0]);
	    }
		
		let ownerTrainingNodes = element.getElementsByTagName("training");
		
		if(ownerTrainingNodes != null && ownerTrainingNodes.length > 0)
		{
			this.ownerTraining = new Training();
			this.ownerTraining.parseFromXMLElement(ownerTrainingNodes[0]);
	    }
    }
	catch(err) 
	{
		this.reset();
	}
}

Training.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Training.prototype.getCard = function()
{
	let cardHtml = "";
	
	cardHtml += "<div class='athlete_card' data-id='" + this.id + "'>";
	cardHtml += "<header style='background-color: " + this.location.representingColour + "' >";
	cardHtml += "<section>"
	cardHtml += "<h2>" + this.team.name + "<br>ANTRENMANI</h2>";
	
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
	cardHtml += "<tr><td class='label'>TARİH</td><td>" + getDateString(new Date(this.startTime)) + "</td></tr>";
	cardHtml += "<tr><td class='label'>BAŞLANGIÇ</td><td>" + getTimeString(new Date(this.startTime)) + "</td></tr>";
	cardHtml += "<tr><td class='label'>BİTİŞ</td><td>" + getTimeString(new Date(this.endTime)) + "</td></tr>";
	cardHtml += "<tr><td class='label'>ANTRENÖR</td><td>" + (this.trainer != null ? this.trainer.loginable.fullName() : "-") + "</td></tr>";
	cardHtml += "</table>";
	
	if(this.location == null || this.location.address == null)
	{
		cardHtml += "<table>";
		cardHtml += "<tr><td class='page_title' colspan='2'>KONUM BİLGİLERİ</td></tr>";
		cardHtml += "<tr><td class='label'>TESİS BİLGİSİ BULUNAMADI</td></tr>";
		cardHtml += "</table>";
	}
	else
	{
		cardHtml += "<table>";
		cardHtml += "<tr><td class='page_title' colspan='2'>TESİS BİLGİLERİ</td></tr>";
		cardHtml += "<tr><td class='label'>TESİS ADI</td><td>" + this.location.name + "</td></tr>";
		cardHtml += "<tr><td class='label'>ŞEHİR</td><td>" + this.location.address.city.name + "</td></tr>";
		cardHtml += "<tr><td class='label'>İLÇE</td><td>" + (this.location.address.district != null ? this.location.address.district.name : "-") + "</td></tr>";
		cardHtml += "<tr><td style='text-align: left !important; padding-left: 10px !important'><a href='' class='button_like dark'>Detaylar</a></td></tr>";
		cardHtml += "</table>";
	}
	
	if(this.team.players != null)
	{
		cardHtml += "<div class='table_wrapper'>";
		cardHtml += "<table>";
		cardHtml += "<tr><td class='page_title' colspan='2'>ANTRENMAN KADROSU</td></tr>";
		
		for(let i = 0; i < this.team.players.length; i++)
			cardHtml += "<tr><td style='text-align: left !important; padding-left: 10px !important'>" + this.team.players[i].loginable.fullName() + "</td><td>" + this.team.players[i].birthDate + "</td></tr>";
		
		cardHtml += "</table>";
		cardHtml += "</div>";
	}
	
	cardHtml += "</div>";
	cardHtml += "<div class='nav'>";
	
	for(let i = 0; i < (this.team.players == null ? 2 : 3); i++)
		cardHtml += "<button class='clear_style " + (i == 0 ? 'selected' : '') + "'></button>";
	
	cardHtml += "</div>";
	cardHtml += "</div>";
	
	return cardHtml;
}

Training.prototype.getServletParameterString = function(mode, modifiedCompany) {}

Training.prototype.updateInDB = function(modifiedCompany, callback) {}