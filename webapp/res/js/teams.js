var that = this;
var teams = [];
var playerPool = [];

$(document).ready(function()
{
	$("table.data_table > tbody > tr").click(function(event)
	{
		$("table.data_table > tbody > tr").removeClass("selected_row");
		$(this).addClass("selected_row");
		
		that.teams = [];
		that.getTeamData(parseInt($(this).data("id")));
	});
	
	$("button#new_item").click(event => 
	{
		let runner = new BordomorAjaxRunner(branchDataURL);
		runner.init();
		runner.setProcDialog("Branş Bilgileri Alınyor");
		runner.setFailDialog();
		
		runner.setCallbacks(function(isValid, response)
		{
			if(isValid)
			{
				let formHtml = getTeamForm();
				let newDialogue = new BordomorDialogue("YENİ TAKIM GİRİŞİ", formHtml, "Kaydet", "İptal Et");
				
				newDialogue.setButtonClickCallbacks(function() 
				{
					let newTeamRunner = new BordomorAjaxRunner(teamManURI, $("form#team_form").serialize());
					newTeamRunner.init("post", true, "text");
					newTeamRunner.setProcDialog("Takım Tanımlanıyor");
					newTeamRunner.setFailDialog();
					
					newTeamRunner.setCallbacks(function() { window.location = window.location.href; });
					
					newDialogue.close();
					newTeamRunner.run(responseErrorRegExp);
				}, function() { newDialogue.close() });
				newDialogue.print(false, true);
				
				let branchEls = response.getElementsByTagName("branch");
				
				for(let i = 0; i < branchEls.length; i++)
					$("select#branch_select").append($("<option>", { value: branchEls[i].getAttribute("text"), text: branchEls[i].getAttribute("name")}));
				
				ageGroups.forEach((group, index) => { $("select#age_category_select").append($("<option>", { value: group, text: ageGroupVals[index]})); });
				genders.forEach((gender, index) => { $("select#gender_category_select").append($("<option>", { value: gender, text: (index < 2 ? genderVals[index] : "Karma Takım")})); });
				let ageGroupVal = ageGroup => parseInt(ageGroup.substring(1));
				
				$("select#age_category_select").change(event => 
				{
					$("select#age_groups_select[multiple]").unbind("change");
					
					if($("select#age_groups_select option:disabled").length == 1)
						$("select#age_groups_select[multiple]").multiselect({ placeholder: "Yaş Grupları" });
					
					$("select#age_groups_select option").remove();
					ageGroups.forEach((group, index) => 
					{ 
						if(ageGroupVal(group) <= ageGroupVal($(event.target).val()) && ageGroupVal(group) > ageGroupVal($(event.target).val()) - 5)
							$("select#age_groups_select").append($("<option>", { value: group, text: ageGroupVals[index]})); 
					});
					
					$("select#age_groups_select[multiple]").multiselect("reload");
					$("select#age_groups_select[multiple]").prop("disabled", false);
	 				$("select#age_groups_select[multiple]").change(event =>
	 				{
	 					let selectedOpts = "";
	 					$("li.ms-reflow.selected input[type=checkbox]").each(function(index) { selectedOpts += $(this).val() != "-1" ? "," + $(this).val() : ""; });
	 					$("input[type=hidden][name='age_groups']").eq(0).val(selectedOpts.substring(1));
	 				});
				});
				
				$("select#branch_select").change(event => {  getSuitableTrainers($("select#branch_select").val(), $("select#trainer_select")); });
			}
			else
			{
				if(new RegExp(noDataErrorRegExp).test(response))
				{
					let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Sporcu Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
					{
						destroyBordomorFullScreenDialog(negativeDialog);
						negativeDialog = null;
					});
				}
				else
					new BordomorInfoDialogue("error", "Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.").print(dialogDisplayDurationMsec);
			}
		}, null);
		
		runner.run(responseErrorRegExp);
	});
	
	$("tr[data-is_selected_item='true']").trigger("click");
});

function getSuitableTrainers(branch, select)
{
	let parameters = "primary_branch=" + branch;
	let trainerRunner = new BordomorAjaxRunner(trainerDataURL, parameters);
	trainerRunner.init();
	
	trainerRunner.setCallbacks(function(isValid, response)
	{
		$(select).parent(".select_container").removeClass("loading");
		
		if(isValid)
		{
			let trainerEls = response.getElementsByTagName("trainer");
			
			for(let i = 0; i < trainerEls.length; i++)
				$(select).append($("<option>", { value: trainerEls[i].getAttribute("id"), text: (trainerEls[i].getAttribute("name") + " " + trainerEls[i].getAttribute("surname"))}));
				
			$(select).prop("disabled", false);
		}
	});
	
	$(select).prop("disabled", "disabled");
	$(select).find("option:not(:disabled)").remove();
	$(select).find("option:disabled").prop("selected", true);
	$(select).parent(".select_container").addClass("loading");
	trainerRunner.run(responseErrorRegExp);
}

function getTeamData(id)
{
	let parameters = "id=" + id;
	let runner = new BordomorAjaxRunner(teamDataURL, parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			that.playerPool = [];
			
			let teamEl = response.getElementsByTagName("team")[0];
			let fetchedTeam = new Team();
			fetchedTeam.parseFromXMLElement(teamEl);
			
			let playerPoolEl = response.getElementsByTagName("player_pool")[0];
			
			let poolEls = playerPoolEl != null ? playerPoolEl.getElementsByTagName("athlete") : null;
			
			for(let i = 0; poolEls != null && i < poolEls.length; i++)
			{
				let nextAthlete = new Athlete();
				nextAthlete.parseFromXMLElement(poolEls[i]);
				playerPool.push(nextAthlete);
			}
			
			that.displayFetchedData(fetchedTeam);
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
			{
				let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Sporcu Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
				{
					destroyBordomorFullScreenDialog(negativeDialog);
					negativeDialog = null;
				});
			}
			else
				makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
		}
	}, null);
	
	runner.run(responseErrorRegExp);
}

function getTeamForm(team)
{
	let formHtml = "";
	formHtml += "<form id='team_form' method='post'>";
	
	formHtml += "<header>";
	formHtml += "<figure></figure>";
	formHtml += "<section>";
	formHtml += "<div class='input_container'>";
	formHtml += "<label for='name_input'>Adı:</label>";
	formHtml += "<input type='text' id='name_input' name='name'/>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<label for='branch_select'>Branşı:</label>";
	formHtml += "<select id='branch_select' name='branch'><option value='-1' disabled='disabled' selected>Bir seçim yapın</option></select>";
	formHtml += "</div>";
	formHtml += "</section>";
	formHtml += "</header>";
	
	formHtml += "<section>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='age_category_select' name='age_category'><option value='-1' disabled='disabled' selected>Yaş Kategorisi</option></select>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select multiple id='age_groups_select' disabled='disabled'><option value='-1' disabled='disabled' selected>Yaş Grupları</option></select>";
	formHtml += "<input type='hidden' name='age_groups' value='-1'/>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='gender_category_select' name='gender_category'><option value='-1' disabled='disabled' selected>Cinsiyet Kategorisi</option></select>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='trainer_select' name='trainer_id' disabled='disabled'><option value='-1' disabled='disabled' selected>Antrenör</option></select>";
	formHtml += "</div>";
	
	if(team == null)
	{
		formHtml += "<div class='input_container'>";
		formHtml += "<input type='checkbox' id='auto_fill_squad_checkbox' name='auto_fill_squad' value='true'/><label for='auto_fill_squad_checkbox'>Kadroyu otomatik oluştur</label></select>";
		formHtml += "</div>";
	}
	
	formHtml += "</div>";	
	formHtml += "<input type='hidden' name='data_mode' value='" + (team == null ? 1 : 2) + "'>";
	
	if(team != null)
		formHtml += "<input type='hidden' name='id' value='" + team.id + "'>";
	
	formHtml += "</section>";
	formHtml += "</form>";

	return formHtml;
}

function displayFetchedData(team)
{
	let teamExists = false;
	
	for(let i = 0; i < teams.length; i++)
	{
		if(teams[i].id == team.id)
		{
			teamExists = true;
			break;
		}
	}
	
	if(!teamExists)
	{
		this.teams.push(team);
		addTeamCard(team);
	}
}

function addTeamCard(team)
{
	let cardHtml = team.getCard();
	
	let playerPoolHtml = "";
	playerPoolHtml += "<div class='team_card' data-id='-1'>";
	playerPoolHtml += "<div class='data'>";
	
	if(this.playerPool != null && this.playerPool.length > 0)
	{
		playerPoolHtml += "<table>";
		playerPoolHtml += "<thead><tr><th colspan='100' style='background-color: var(--system_gray); color: var(--system_white)'>SPORCU HAVUZU (" + this.playerPool.length + " SPORCU)</th></tr><tr><th>KOD</th><th>ADI SOYADI</th><th>DOĞUM TARİHİ</th><th>YAŞ GRUBU</th><th></th></tr></thead>";
		playerPoolHtml += "<tbody>";
		
		for(let i = 0; i < this.playerPool.length; i++)
			playerPoolHtml += "<tr data-code='" + playerPool[i].loginable.code + "'><td>" + this.playerPool[i].loginable.code + "</td><td>" + this.playerPool[i].loginable.fullName() + "</td><td>" + this.playerPool[i].birthDate + "</td><td style='border-right: none'>" + this.playerPool[i].ageGroup + "</td><td style='border-left: none'><button class='clear_style'>Ekle</button></td></tr>";
		
		playerPoolHtml += "</tbody>";
		playerPoolHtml += "</table>";
	}
	else
		playerPoolHtml += "<table><tr><td class='label'>TAKIMA KAYITLI OYUNCU BULUNMUYOR</td></tr></table>";
	
	playerPoolHtml += "</div>";
	playerPoolHtml += "</div>";
	
	$("div.data_card *").remove();
	$("div.data_card").append(cardHtml);
	$("div.data_card").append(playerPoolHtml);
	$("div.data_card").addClass("displayed");
	
	let addedCard = $("div.data_card").find("div.team_card[data-id='" + team.id + "']");
	let addedPool = $("div.data_card").find("div.team_card[data-id='-1']").eq(0);
	
	$(addedCard).find("tr").click(function() 
	{  
		$(addedCard).find("tr").removeClass("selected");
		$(this).addClass("selected");
		
		$(addedCard).find("div.player_controls").addClass("displayed");
		
		$(addedCard).find("button#remove_player_button").click(function()
		{
			let parameters = "id=" + team.id + "&code=" + $(addedCard).find("tr.selected").eq(0).data("code") + "&data_mode=0";
			let squadRunner = new BordomorAjaxRunner(squadManURI, parameters);
			squadRunner.init("post", true, "text");
			squadRunner.setProcDialog();
			
			squadRunner.run(responseErrorRegExp);
		});
	});
	
	$(addedPool).find("tr").click(function() 
	{  
		$(addedPool).find("tr").removeClass("selected");
		$(this).addClass("selected");
		$(this).find("button").click(function()
		{
			let parameters = "id=" + team.id + "&code=" + $(addedPool).find("tr.selected").eq(0).data("code") + "&data_mode=1";
			let squadRunner = new BordomorAjaxRunner(squadManURI, parameters);
			squadRunner.init("post", true, "text");
			squadRunner.setProcDialog();
			
			squadRunner.run(responseErrorRegExp);
		});
	});
	
	$("div.team_card div.nav > button").click(function(event)
	{
		event.preventDefault();
		
		$("div.team_card div.nav > button").removeClass("selected");
		$(this).addClass("selected");
		
		let index = $(this).index();
		let shift = -1*100*index + "%";
		
		$(this).parent().parent().parent().find("div.data").eq(0).css("left", shift);
	});
	
	$(addedCard).find("button#delete_team_button").click(function(event)
	{
		let confDialogue = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Devam etmek istediğinize emin misiniz?", "Takımı Sil", "İptal");
		confDialogue.setChoiceCallbacks(function()
		{
			let deleteRunner = new BordomorAjaxRunner(teamManURI, "id=" + team.id + "&data_mode=0");
			deleteRunner.init("post", true, "text");
			deleteRunner.setProcDialog("Takım Siliniyor");
			deleteRunner.setFailDialog();
			deleteRunner.setCallbacks(function() { window.location = window.location.href; });
					
			confDialogue.close();
			deleteRunner.run(responseErrorRegExp);
		}); 
		confDialogue.print();
	});
	
	$(addedCard).find("button#edit_team_button").click(function(event)
	{
		let runner = new BordomorAjaxRunner(branchDataURL);
		runner.init();
		runner.setProcDialog("Branş Bilgileri Alınyor");
		runner.setFailDialog();
		
		runner.setCallbacks(function(isValid, response)
		{
			if(isValid)
			{
				let formHtml = getTeamForm(that.teams[0]);
				let editDialogue = new BordomorDialogue("TAKIM GÜNCELLEME", formHtml, "Kaydet", "İptal Et");
				editDialogue.setButtonClickCallbacks(function() 
				{
					let editRunner = new BordomorAjaxRunner(teamManURI, $("form#team_form").serialize());
					editRunner.init("post", true, "text");
					editRunner.setProcDialog("Takım Güncelleniyor");
					editRunner.setFailDialog();
					
					editRunner.setCallbacks(function() { window.location = window.location.href; });
					
					editDialogue.close();
					editRunner.run(responseErrorRegExp);
				}, function() { editDialogue.close() });
				
				editDialogue.print(false, true);
				
				$("input#name_input").val(that.teams[0].name);
				let branchEls = response.getElementsByTagName("branch");
				
				for(let i = 0; i < branchEls.length; i++)
					$("select#branch_select").append($("<option>", { value: branchEls[i].getAttribute("text"), text: branchEls[i].getAttribute("name")}));
				
				ageGroups.forEach((group, index) => { $("select#age_category_select").append($("<option>", { value: group, text: ageGroupVals[index]})); });
				genders.forEach((gender, index) => { $("select#gender_category_select").append($("<option>", { value: gender, text: (index < 2 ? genderVals[index] : "Karma Takım")})); });
				let ageGroupVal = ageGroup => parseInt(ageGroup.substring(1));
				
				$("select#age_category_select").change(event => 
				{
					$("select#age_groups_select[multiple]").unbind("change");
					
					if($("select#age_groups_select option:disabled").length == 1)
						$("select#age_groups_select[multiple]").multiselect({ placeholder: "Yaş Grupları" });
					
					$("select#age_groups_select option").remove();
					
					ageGroups.forEach((group, index) => 
					{ 
						if(ageGroupVal(group) <= ageGroupVal($(event.target).val()) && ageGroupVal(group) > ageGroupVal($(event.target).val()) - 5)
						{
							let optSelected = false;
							
							if(that.teams[0].ageGroups != null)
							{
								let startGroupVal = ageGroupVal(that.teams[0].ageGroups.startGroup);
								let finalGroupVal = ageGroupVal(that.teams[0].ageGroups.finalGroup);
								
								optSelected = startGroupVal <= ageGroupVal(group) && finalGroupVal >= ageGroupVal(group);
							}
							
							$("select#age_groups_select").append($("<option>", { value: group, text: ageGroupVals[index], selected: optSelected}));
						} 
					});
					
					$("select#age_groups_select[multiple]").multiselect("reload");
					$("select#age_groups_select[multiple]").prop("disabled", false);
					$("select#age_groups_select[multiple]").change(event =>
	 				{
	 					let selectedOpts = "";
	 					$("li.ms-reflow.selected input[type=checkbox]").each(function(index) { selectedOpts += $(this).val() != "-1" ? "," + $(this).val() : ""; });
	 					$("input[type=hidden][name='age_groups']").eq(0).val(selectedOpts.substring(1));
	 				});
	 				
	 				$("select#age_groups_select[multiple]").trigger("change");
				});
				
				$("select#branch_select").change(event => { getSuitableTrainers($("select#branch_select").val(), $("select#trainer_select")); });
				
				$("select#branch_select").val(that.teams[0].branch);
				$("select#age_category_select").val(that.teams[0].ageCategory);
				$("select#gender_category_select").val(that.teams[0].genderCategory);
				
				$("select#branch_select").trigger("change");
				$("select#trainer_select").val(that.teams[0].trainer != null ? that.teams[0].trainer.id : "");
				$("select#age_category_select").trigger("change");
			}
		});
		
		runner.run(responseErrorRegExp);
	});
	
	$(addedCard).find("button#close_card_button").click(function(event)
	{
		let transitionDuration = $("div.data_card").css("transition-duration");
		transitionDuration = parseFloat(transitionDuration.substring(0, transitionDuration.length - 1));
		transitionDuration = parseInt(transitionDuration*1000);
		 
		$("div.data_card").removeClass("displayed");
		
		setTimeout(function()
		{
			$(addedCard).remove();
			
			let id = $(addedCard).data("id");
						
			for(let i = 0; i < teams.length; i++)
			{	
				if(teams[i].id == id)
					that.teams.splice(i, 1);
			}
		}, transitionDuration);
	});
}