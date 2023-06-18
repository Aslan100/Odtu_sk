function Team(id, name, ageCategory, ageGroups, branch, genderCategory, trainer)
{
	this.id = id;
	this.name = name;
	this.ageCategory = ageCategory;
	this.ageGroups = ageGroups;
	this.branch = branch;
	this.genderCategory = genderCategory;
	this.trainer = trainer;
	this.players = null;
		
	var that = this;
}

Team.prototype.reset = function()
{
	this.id = -1;
	this.name = null;
	this.ageCategory = null;
	this.ageGroups = null;
	this.branch = null;
	this.genderCategory = null;
	this.trainer = null;
	this.players = null;
}

Team.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("age_category"))
			this.ageCategory = ageGroups[ageGroups.indexOf(element.getAttribute("age_category"))];
		
		if(element.hasAttribute("branch"))
			this.branch = element.getAttribute("branch");
		
		if(element.hasAttribute("gender_category"))
			this.genderCategory = genders[genders.indexOf(element.getAttribute("gender_category"))];
		
		let ageGroupNodes = element.getElementsByTagName("age_group_interval");
		
		if(ageGroupNodes != null && ageGroupNodes.length > 0)
		{
			this.ageGroups = new AgeGroupInterval();
	        this.ageGroups.parseFromXMLElement(ageGroupNodes[0]);
	    }
		
		let trainerNodes = element.getElementsByTagName("trainer");
		
		if(trainerNodes != null && trainerNodes.length > 0)
		{
			this.trainer = new Trainer();
	        this.trainer.parseFromXMLElement(trainerNodes[0]);
	    }
		
		let athleteNodes = element.getElementsByTagName("athlete");
		
		if(athleteNodes != null && athleteNodes.length > 0)
        {
        	this.players = [];
        	
        	for(let i = 0; i < athleteNodes.length; i++)
        	{
	        	let nextPlayer = new Athlete();
	        	nextPlayer.parseFromXMLElement(athleteNodes[i]);
	        	this.players.push(nextPlayer);
        	}
        }
    }
	catch(err) 
	{
		this.reset();
	}
}

Team.prototype.toString = function()
{
	var output = "";
	
	for (var property in this)
	{
		if(this.hasOwnProperty(property))
			output += property + ": " + this[property] + ";\r\n";
	}
		
	return output.length > 0 ? output : null;
}

Team.prototype.getCard = function()
{
	let cardHtml = "";
	cardHtml += "<div class='team_card' data-id='" + this.id + "'>";
	cardHtml += "<header>";
	cardHtml += "<figure></figure>";
	cardHtml += "<section>"
	cardHtml += "<h2>" + this.name + "</h2>";
	cardHtml += "<p>" + (this.branch  != null ? branchVals[branches.indexOf(this.branch)] : "-") + "</p>";
	
	cardHtml += "<div class='card_control'>";
	cardHtml += "<button id='delete_team_button' class='clear_style'></button>";
	cardHtml += "<button id='edit_team_button' class='clear_style'></button>";
	cardHtml += "<button id='close_card_button' class='clear_style'>&#x274C;</button>";
	cardHtml += "</div>";
	
	cardHtml += "</section>";
	cardHtml += "</header>";
	cardHtml += "<div class='data'>";
	
	if(this.players != null && this.players.length > 0)
	{
		cardHtml += "<table>";
		cardHtml += "<thead><tr><th>SPORCU</th><th>DOĞUM TARİHİ</th></tr></thead>";
		cardHtml += "<tbody>";
		
		for(let i = 0; i < this.players.length; i++)
			cardHtml += "<tr data-code='" + this.players[i].loginable.code + "'><td>" + this.players[i].loginable.fullName() + "</td><td>" + this.players[i].birthDate + "</td></tr>";
		
		cardHtml += "</tbody>";
		cardHtml += "</table>";
	}
	else
		cardHtml += "<table><tr><td class='label' style='text-align: center;'>TAKIMA KAYITLI OYUNCU BULUNMUYOR</td></tr></table>";
	
	cardHtml += "</div>";
	cardHtml += "<div class='player_controls'><button id='remove_player_button'>Takımdan Çıkart</button></div>";
	cardHtml += "</div>";
	
	return cardHtml;
}

Team.prototype.getServletParameterString = function(mode, modifiedCompany) {}

Team.prototype.updateInDB = function(modifiedCompany, callback) {}

//Yaş Grubu
function AgeGroupInterval(id, startGroup, finalGroup)
{
	this.id = id;
	this.startGroup = startGroup;
	this.finalGroup = finalGroup;
}

AgeGroupInterval.prototype.reset = function()
{
	this.id = -1;
	this.startGroup = null;
	this.finalGroup = null;
}

AgeGroupInterval.prototype.parseFromXMLElement = function(element)
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
		
		if(element.hasAttribute("start_group"))
			this.startGroup = ageGroups[ageGroups.indexOf(element.getAttribute("start_group"))];
		
		if(element.hasAttribute("final_group"))
			this.finalGroup = ageGroups[ageGroups.indexOf(element.getAttribute("final_group"))];
	}
	catch(err) 
	{
		this.reset();
	}
}
