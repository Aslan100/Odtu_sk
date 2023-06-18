var events = [];
var that = this;

$(document).ready(function()
{
 	events.forEach((event, index) =>
	{
		let startTime = getTimeString(new Date(event.startTime), 0);
		let endTime = getTimeString(new Date(event.endTime), 0);
		
		let nextEventTime = new Date(event.startTime);
		let nextContainer = null;
		let isInitial = true;
		let containerLevel = 2;
		
		while(nextEventTime.getTime() < event.endTime)
		{
			let dateStr = getDateString(nextEventTime);
			let day = dateStr.substring(0,2);
			let month = dateStr.substring(3,5);
			
			nextContainer = $("td#" + day + "_" + month + "_row_" + containerLevel + "[data-date='" + dateStr + "']");
			
			while(nextContainer.hasClass("scheduled") && containerLevel <= 4)
			{
				nextContainer = $("td#" + day + "_" + month + "_row_" + containerLevel + "[data-date='" + dateStr + "']");
				containerLevel++;
			}
			
			$(nextContainer).addClass("beginning");
			$(nextContainer).addClass("end");
			$(nextContainer).addClass("scheduled");
			
			$(nextContainer).find("span.colour_indicator").eq(0).css("background-color", event.location.representingColour);
			$(nextContainer).find("span > span:not(.colour_indicator)").eq(0).html(event.name);
			
			$(nextContainer).find(" > span").eq(0).attr("data-id", event.id);
			nextEventTime = new Date(nextEventTime.getTime() + 24*60*60*1000);
		}
	});
	
	$("td.time_cell.scheduled > span").click(function(event) 
	{
		event.stopPropagation();
		getEventData($(this).data("id"));
	});
	
	$("button#new_item").click(event => 
	{
		getEventParameterData(function(isValid, response)
		{
			if(isValid)
			{
				let data = that.parseData(response);
				let branches = data[0];
				let cities = data[1];
				let locations = data[2];
				
				let formHtml = getTrainingForm();
				let newDialogue = new BordomorDialogue("YENİ ETKİNLİK GİRİŞİ", formHtml, "Kaydet", "İptal Et");
						
				newDialogue.setButtonClickCallbacks(function() 
				{ 
					let newEventRunner = new BordomorAjaxRunner(eventManURI, $("form#event_form").serialize());
					newEventRunner.init("post", true, "text");
					newEventRunner.setProcDialog("Etkinlik Tanımlanıyor");
					newEventRunner.setFailDialog();
						
					newEventRunner.setCallbacks(function() { window.location = window.location.href; });
						
					newDialogue.close();
					newEventRunner.run(responseErrorRegExp);
		  		}, function() { newDialogue.close() });
					
				newDialogue.print(false, true);
				
				initializeDatePickerInputs();
				branches.forEach((branch, index) => $("select#branch_select").append($("<option>", { value: branch.substring(0, branch.indexOf("|")), text: branch.substring(branch.indexOf("|") + 1) })));
				cities.forEach((city, index) => $("select#city_select").append($("<option>", { value: city.id, text: city.name })));
				
				if(locations.length > 0)
					locations.forEach((location, index) => $("select#location_select").append($("<option>", { value: location.id, text: (location.name + " (" + location.address.city.name + ")") })));
						
				eventTypes.forEach((value, index) => $("select#event_type_select").append($("<option>", { value: value, text: eventTypeVals[index] })));
				timeVals.forEach((hour, index) => $("select#start_time_select, select#end_time_select").append($("<option>", { value: hour, text:hour })));
				
				$("input[type='radio'][name='location_mode']").change(event => 
				{
					let mode = parseInt($("input[type='radio'][name='location_mode']:checked").val());
					
					$("div.input_container[data-mode='" + Math.abs(mode - 1) + "']").find("input, select").prop("disabled", "disabled");
					$("div.input_container[data-mode='" + mode + "']").find("input, select").prop("disabled", false);
				});
				
				$("select#city_select").change(event =>
				{
					let selectedOpt = $("select#city_select").find("option:selected").index();
					let selectedCity = cities[selectedOpt - 1];
					
					$("select#district_select option:not(:disabled)").remove();
					
					for(let i = 0; i < selectedCity.districts.length; i++)
						$("select#district_select").append($("<option>", { value: selectedCity.districts[i].id, text: selectedCity.districts[i].name}));
				});
				
				$("input[type='radio'][name='location_mode']").trigger("change");
				
				$("form#event_form > header > div.pager > button").click(function() 
				{ 
					let shiftCoeff = $(this).index() * -1;
					$(this).siblings().removeClass("selected");
					$(this).addClass("selected");
					$("form.wide_mode.paged_mode > section").each(function() { $(this).css("left", (shiftCoeff*410) + "px") }); 
				});
			}
			else
			{
				if(new RegExp(noDataErrorRegExp).test(response))
				{
					let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Etkinlik Verileri Alınamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
					{
						destroyBordomorFullScreenDialog(negativeDialog);
						negativeDialog = null;
					});
				}
				else
					new BordomorInfoDialogue("error", "Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.").print(dialogDisplayDurationMsec);
			}
		}, null);
	});
	
	$("button#filters_button").click(function()
	{
		//makeFilterDialogue();
	});
	
	$("button#today_button").click(function() { window.location.href = "http://localhost:8080/ODTU_SK/events.jsp" });
	
	let now = new Date();
	
	$("td.time_cell, td.day_cell").each(function()
	{
		let cellDate = $(this).data("date");
		
		let cellDay = cellDate.substring(0, 2);
		let cellMonth = cellDate.substring(3, 5);
		let cellYear = cellDate.substring(6);
		
		let formattedDate = new Date(cellYear + "-" + cellMonth + "-" + cellDay + " 23:59:59");
		
		if(formattedDate.getTime() < now.getTime())
			$(this).addClass("past_day");
		
		if(parseInt($(this).parent().prop("id").substring(5, 6)) == 1 && (parseInt(cellDay) >= 23 && parseInt(cellDay) <= 31))
			$(this).addClass("months_past_day");
			
		if(parseInt($(this).parent().prop("id").substring(5, 6)) > 4 && (parseInt(cellDay) >= 1 && parseInt(cellDay) <= 6))
			$(this).addClass("future_day");
	});
	
	$("tr[data-is_selected_item='true']").trigger("click");
});

function getEventData(id)
{
	let parameters = "id=" + id;
	let runner = new BordomorAjaxRunner(eventDataURI, parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let eventEl = response.getElementsByTagName("event")[0];
			let fetchedEvent = new Event();
			fetchedEvent.parseFromXMLElement(eventEl);
			
			that.displayFetchedData(fetchedEvent);
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
			{
				let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Etkinlik Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
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

function displayFetchedData(event)
{
	let container = $("div.main_container_overlay");
	$(container).addClass("display");
	$(container).append("<div class='data_card'>" + event.getCard() + "</div>");
	let addedCard = $("div.event_card[data-id='" + event.id + "']");
	
	$(addedCard).find("button#fetch_squad_button").click(function(e)
	{
		getEventSquad(event);
	});
	
	$(addedCard).find("div.nav > button").click(function(e)
	{
		e.preventDefault();
		
		$(this).siblings().removeClass("selected");
		$(this).addClass("selected");
		
		let index = $(this).index();
		let shift = -1*100*index + "%";
		
		$(addedCard).find("div.data").eq(0).css("left", shift);
	});
	
	$(addedCard).find("button#close_card_button").click(function(e) { $(container).removeClass("display"); $(addedCard).parent().remove(); });
	$(addedCard).find("button#delete_item_button").click(function(e) 
	{ 
		let confDialog = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Devam etmek istediğinize emin misiniz?", "Etkinliği Sil", "İptal");
		confDialog.setChoiceCallbacks(function(){ deleteTraining(event.id) });
		
		confDialog.print();
	
		function deleteTraining(eventId)
		{
			let deleteRunner = new BordomorAjaxRunner(eventManURI, "id=" + event.id + "&data_mode=0");
			deleteRunner.init("post", true, "text");
			deleteRunner.setProcDialog("Etkinlik Siliniyor");
			deleteRunner.setFailDialog();
			deleteRunner.setCallbacks(function() { window.location = window.location.href; });
			
			confDialog.close();
			deleteRunner.run(responseErrorRegExp);
		}
	});
	
	$(addedCard).find("button#edit_item_button").click(function(e) 
	{  
		let formHtml = getTrainingForm(event);
		let editDialogue = new BordomorDialogue("ANTRENMAN BİLGİLERİNİ DÜZENLE", formHtml, "Düzenle", "İptal Et");
				
		editDialogue.setButtonClickCallbacks(function() 
		{ 
			let eventRunner = new BordomorAjaxRunner(eventManURI, $("form#event_form").serialize());
			eventRunner.init("post", true, "text");
			eventRunner.setProcDialog("Antrenman Düzenleniyor");
			eventRunner.setFailDialog();
				
			eventRunner.setCallbacks(function() { window.location = window.location.href; });
				
			editDialogue.close();
			eventRunner.run(responseErrorRegExp);
  		}, function() { editDialogue.close() });
			
		editDialogue.print(false, true);
		
		initializeDatePickerInputs();
		
		timeVals.forEach((hour, index) => $("select#start_time_select, select#end_time_select").append($("<option>", { value: hour, text:hour })));
		hourVals.forEach((hour, index) => { if(index < 4) $("select#duration_hour_select").append($("<option>", { value: hour, text: hour })); });
		minValsShort.forEach((minute, index) => $("select#duration_minute_select").append($("<option>", { value: minute, text: minute })) );
		
		$("select#branch_select option").remove();
		$("select#branch_select").append($("<option>", { value: event.team.branch, text: branchVals[branches.indexOf(event.team.branch)], selected: true }));
		
		$("input[type='radio'][name='location_mode']").change(event => 
		{
			let mode = parseInt($("input[type='radio'][name='duration_mode']:checked").val());
			alert(mode);
			$("div.input_container[data-mode='" + Math.abs(mode - 1) + "']").find("input, select").prop("disabled", "disabled");
			$("div.input_container[data-mode='" + mode + "']").find("input, select").prop("disabled", false);
		});
		
		$("input[type='radio'][name='location_mode']").trigger("change");
		
		$("input#start_date_input").val(getDateString(new Date(event.startTime)));
		$("select#start_time_select").val(getTimeString(new Date(event.startTime)));
		$("input#end_date_input").val(getDateString(new Date(event.endTime)));
		$("select#end_time_select").val(getTimeString(new Date(event.endTime)));
	});
}

function getEventSquad(event)
{
	let parameters = "id=" + event.id + "&mode=1";
	let runner = new BordomorAjaxRunner(eventDataURI, parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			event.squad = [];
			
			let loginableContainerEl = response.getElementsByTagName("event_members")[0]; 
			let loginableEls = loginableContainerEl.getElementsByTagName("*");
			
			for(let i = 0; i < loginableEls.length; i++)
			{
				if(loginableEls[i].hasAttribute("email"))
				{
					let nextLoginableId = parseInt(loginableEls[i].getAttribute("loginable_id"));
					let nextLoginableName = loginableEls[i].getAttribute("name");
					let nextLoginableSurname = loginableEls[i].getAttribute("surname");
					let nextLoginableBirthDate = loginableEls[i].hasAttribute("birth_date") ? loginableEls[i].getAttribute("birth_date") : null;
					let nextLoginableType = loginableEls[i].tagName == "athlete" ? "Sporcu" : "Antrenör";
					  
					event.squad.push([nextLoginableId, nextLoginableName, nextLoginableSurname, nextLoginableBirthDate, nextLoginableType]);
				}
			}
			
			let dataCardContainer = $("div.data_card"); 
			$(dataCardContainer).prepend(event.getSquadCard());
			
			$("div.squad_card tr td button").click(e => 
			{
				let parameters = "id=" + event.id + "&selected_members=" + $(e.target).parent().parent().parent().data("id") + "&data_mode=2&update_mode=0";
				let deleteRunner = new BordomorAjaxRunner(eventManURI, parameters);
				deleteRunner.init("POST", true, "text");
				deleteRunner.setFailDialog();
				
				deleteRunner.setCallbacks(function(isValid, response)
				{
					if(isValid && response == "sonuc:1")
						alert("Silme başarılı");
					else
						alert("Silme tamamlanamadı");
				});
				
				deleteRunner.run(responseErrorRegExp);
			});
			
			memberRoles.forEach((role, index) => $("select#member_role_select").append($("<option>", { value: role, text: memberRoleVals[index] })));
			genders.forEach((gender, index) => { if(index < 2) $("select#gender_select").append($("<option>", { value: gender, text: genderVals[index] })) });
			$("select#member_select[multiple]").multiselect({ placeholder: "Kafile Üyesi" });
			
			$("select#member_role_select").change(e => 
			{
				let selectedRole = $(e.target).val();
				$("select#gender_select").prop("disabled", "disabled");
				$("select#team_select").prop("disabled", "disabled");
				
				if(selectedRole == memberRoles[0] || selectedRole == memberRoles[1] || selectedRole == memberRoles[2])
					getPossibleLoginables(event, selectedRole);
				else if(selectedRole == memberRoles[3])
				{
					$("select#gender_select").prop("disabled", false);
					$("select#gender_select").change(e2 => getPossibleTeams(event, $(e2.target).val()));
				}
				else 
					alert("Bu özellik yakında desteklenecektir");
			});
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
			{
				let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Kafile Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
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

function getPossibleTeams(event, gender)
{
	let parameters = "id=" + event.id + "&gender=" + gender;
	let runner = new BordomorAjaxRunner(eventTeamDataURI, parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let teamEls = response.getElementsByTagName("team");
			
			for(let i = 0; i < teamEls.length; i++)
			{
				if(i == 0)
					$("select#team_select").prop("disabled", false);
					
				let nextTeam = new Team();
				nextTeam.parseFromXMLElement(teamEls[i]);
				
				$("select#team_select option:disabled").remove();
				$("select#team_select").append($("<option>", { value: nextTeam.id, text: nextTeam.name, selected: i == 0 }));
			}
			
			$("select#team_select").change(e => getPossibleLoginables(event, memberRoles[3], $("select#team_select").val()));
			$("select#team_select").trigger("change");
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
			{
				let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Antrenör Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
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

function getPossibleLoginables(event, memberRole, teamId)
{
	let parameters = "id=" + event.id + "&member_role=" + memberRole + (teamId != null ? "&team=" + teamId : "");
	let runner = new BordomorAjaxRunner(eventSquadLoginableDataURI, parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let memberContainerEl = response.getElementsByTagName("possible_members")[0];
			let memberEls = memberContainerEl.getElementsByTagName("*");
			
			for(let i = 0; i < memberEls.length; i++)
			{
				let nextMemberLoginableId = memberEls[i].getAttribute("loginable_id");
				let nextMemberName = memberEls[i].getAttribute("name") + " " + memberEls[i].getAttribute("surname"); 
				$("select#member_select").append($("<option>", { value: nextMemberLoginableId, text: nextMemberName, selected: i == 0 }));
			}
			
			$("select#member_select[multiple]").multiselect("reload");
			$("select#member_select[multiple]").change(e =>
			{
				let selectedOpts = "";
				$("li.ms-reflow.selected input[type=checkbox]").each(function(index) { selectedOpts += $(this).val() != "-1" ? "," + $(this).val() : ""; });
				$("input[type=hidden][name='selected_members']").eq(0).val(selectedOpts.substring(1));
			});
			
			$("select#member_select[multiple]").trigger("change");
			
			$("button#add_members_button").click(e =>
			{
				let parameters = "id=" + event.id + "&member_role=" + $("select#member_role_select").val() + "&selected_members=" + $("input#selected_members_input").val() + "&data_mode=2&update_mode=1";
				let addMembersrunner = new BordomorAjaxRunner(eventManURI, parameters);
				addMembersrunner.init("POST", true, "text");
				addMembersrunner.setFailDialog();
							
				addMembersrunner.setCallbacks(function(isValid, response)
				{
					if(isValid && response == "sonuc:1")
						alert("Ekleme Başarılı");
					else
					{
						if(new RegExp(noDataErrorRegExp).test(response))
						{
							let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Ekleme Başarısız Oldu", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
							{
								destroyBordomorFullScreenDialog(negativeDialog);
								negativeDialog = null;
							});
						}
						else
							makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
					}
				}, null);
				
				addMembersrunner.run(responseErrorRegExp);
			});
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
			{
				let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Antrenör Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
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

function getEventParameterData(positiveCallback, negativeCallback)
{
	let runner = new BordomorAjaxRunner(eventParameterDataURI);
	runner.init();
	runner.setFailDialog();
	runner.setProcDialog("Etkinlik Parametreleri Alınıyor");
	runner.setCallbacks(positiveCallback, negativeCallback);
	runner.run(responseErrorRegExp);
}

function parseData(response)
{
	let branchEls = response.getElementsByTagName("branch");
	let cityEls = response.getElementsByTagName("city");
	let locationEls = response.getElementsByTagName("location");
	
	let branches = [];
	let cities = [];
	let locations = [];
				
	for(let i = 0; branchEls != null && i < branchEls.length; i++)
		branches.push(branchEls[i].getAttribute("text") + "|" + branchEls[i].getAttribute("name"));
	
	for(let i = 0; cityEls != null && i < cityEls.length; i++)
	{
		let nextCity = new City();
		nextCity.parseFromXMLElement(cityEls[i]);
		cities.push(nextCity);
	}
	
	for(let i = 0; locationEls != null && i < locationEls.length; i++)
	{
		let nextLocation = new Location();
		nextLocation.parseFromXMLElement(locationEls[i]);
		locations.push(nextLocation);
	}
	
	return [branches, cities, locations];
}

function initializeDatePickerInputs()
{
	let posStr = (el, className) => $(el).hasClass(className) ? "top left" : "bottom left";
	let hidePicker = picker => 
	{
		let initialVal = $(picker).val();
		$(picker).datepicker("hide");
		$(picker).blur();
		$(picker).val(initialVal);
	};
				
	$("input[type=text][mode=datepicker]").each(function()
	{
		let thisPicker = this;
		$(this).datepicker({language: "tr", position: posStr(this, "top_aligned")});
		
		$(".io_container").scroll(() => { hidePicker(thisPicker) });
		$(window).resize(() => { hidePicker(thisPicker) }); 
		
		$(this).on("click", event => { $(this).select() });
		$(this).on("input", event => 
		{
			$(event.target).val($(event.target).val().replace(/[^0-9\/]/g, ""));
			let strippedVal = $(event.target).val().replace(/[^0-9]/g, "");
			
			try
			{
				if(strippedVal.length > 4)
				{
					let part1 = strippedVal.substring(0, 2);
					let part2 = strippedVal.substring(2, 4);
					let part3 = strippedVal.substring(4);
					
					$(event.target).val(part1 + "/" + part2 + "/" + part3);
				}
				else if(strippedVal.length > 2)
				{
					let part1 = strippedVal.substring(0, 2);
					let part2 = strippedVal.substring(2);
					
					$(event.target).val(part1 + "/" + part2);
				}
				else if(strippedVal.length > 0)
					$(event.target).val(strippedVal);
			}
			catch(err) {}
		});
	});
}

function getTrainingForm(event)
{
	let formHtml = "";
	formHtml += "<form id='event_form' method='post' class='wide_mode'>";
	
	/*formHtml += "<header>";
	formHtml += "<figure></figure>";
	formHtml += "<section>";
	formHtml += "<div>";
	formHtml += "<input type='text' id='name_input' name='event_name' placeholder='Etkinliğin Adı'/>";
	formHtml += "</div>";
	formHtml += "</section>";
	formHtml += "<div class='pager'>";
	
	for(let i = 0; i < 5; i++)
		formHtml += "<button type='button' class='clear_style" + (i == 0 ? " selected" : "") + "'></button>";
	
	formHtml += "</div>";
	formHtml += "</header>";*/
	
	formHtml += "<section>";
	formHtml += "<h3>TEMEL BİLGİLER</h3>";
	formHtml += "<div>";
	
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='branch_select' name='event_branch'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='branch_select'>Etkinlik Branşı</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='event_type_select' name='event_type'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='event_type_select'>Etkinlik Türü</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='event_name_input' name='event_name' placeholder=' '/>";
	formHtml += "<label for='event_name_input'>Etkinlik Adı</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' mode='datepicker' id='start_date_input' name='start_date' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='start_date_input'>Başlangıç Tarihi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container' style='flex: 1'>";
	formHtml += "<select id='start_time_select' name='start_time' style='min-width: auto;'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='start_time_select'>Saat</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' mode='datepicker' id='end_date_input' name='end_date' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='end_date_input'>Bitiş Tarihi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container' style='flex: 1' data-type='daily'>";
	formHtml += "<select id='end_time_select' name='end_time' style='min-width: auto'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='end_time_select'>Saat</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='parent_contribution_input' name='parent_contribution_amount' value='0' min='0' placeholder=' ' required/>";
	formHtml += "<label for='parent_contribution_input'>Veli Katkı Payı</label>";
	formHtml += "</div>";
	
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>ETKİNLİK NOKTASI BİLGİLERİ</h3>";
	formHtml += "<div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='radio' id='saved_location_mode_radio' name='location_mode' checked='checked' value='0'/>";
	formHtml += "<label for='saved_location_mode_radio'>Kayıtlı Etkinlik Noktası</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='radio' id='new_location_mode_radio' name='location_mode' value='1'/>";
	formHtml += "<label for='new_location_mode_radio'>Yeni Konum</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container select_container' data-mode='0'>";
	formHtml += "<select id='location_select' name='location'><option val='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='location_select'>Etkinlik Yeri</label>";
	formHtml += "</div>";
	
	formHtml += "<div id='new_location_options' style='margin-top: 13px;'>";
	formHtml += "<div class='input_container' data-mode='1'>";
	formHtml += "<input type='text' id='new_location_name_input' name='location_name' placeholder=' '/>";
	formHtml += "<label for='new_location_name_input'>Konum Adı</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container select_container' style='flex: 1' data-mode='1'>";
	formHtml += "<select id='city_select' name='city'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='city_select'>Şehir</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container select_container' style='flex: 1' data-mode='1'>";
	formHtml += "<select id='district_select' name='district'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='district_select'>İlçe</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container' data-mode='1'>";
	formHtml += "<input type='text' id='address_input' name='address' placeholder=' '/>";
	formHtml += "<label for='address_input'>Adres</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container' style='flex: 1' data-mode='1'>";
	formHtml += "<input type='text' id='latitude_input' name='latitude' placeholder=' '/>";
	formHtml += "<label for='latitude_input'>Enlem</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container' style='flex: 1' data-mode='1'>";
	formHtml += "<input type='text' id='longitude_input' name='longitude' placeholder=' '/>";
	formHtml += "<label for='longitude_input'>Boylam</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "</div>";
	formHtml += "</section>";
	
	/*
	formHtml += "<section>";
	formHtml += "<h3>BULUŞMA VE DAĞILMA BİLGİLERİ</h3>";
	formHtml += "<div>";
	
	formHtml += "<div class='input_container' data-mode='1'>";
	formHtml += "<input type='text' id='new_location_name_input' name='location_name' placeholder=' '/>";
	formHtml += "<label for='new_location_name_input'>Buluşma Noktası Adı</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container' style='flex: 1' data-mode='1'>";
	formHtml += "<input type='text' id='latitude_input' name='latitude' placeholder=' '/>";
	formHtml += "<label for='latitude_input'>Enlem</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container' style='flex: 1' data-mode='1'>";
	formHtml += "<input type='text' id='longitude_input' name='longitude' placeholder=' '/>";
	formHtml += "<label for='longitude_input'>Boylam</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' mode='datepicker' id='end_date_input' name='end_date' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='end_date_input'>Buluşma Tarihi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container' style='flex: 1' data-type='daily'>";
	formHtml += "<select id='end_time_select' name='end_time' style='min-width: auto'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='end_time_select'>Saati</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div style='margin-top: 13px'>";
	formHtml += "<div class='input_container' data-mode='1'>";
	formHtml += "<input type='text' id='new_location_name_input' name='location_name' placeholder=' '/>";
	formHtml += "<label for='new_location_name_input'>Dağılma Noktası Adı</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container' style='flex: 1' data-mode='1'>";
	formHtml += "<input type='text' id='latitude_input' name='latitude' placeholder=' '/>";
	formHtml += "<label for='latitude_input'>Enlem</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container' style='flex: 1' data-mode='1'>";
	formHtml += "<input type='text' id='longitude_input' name='longitude' placeholder=' '/>";
	formHtml += "<label for='longitude_input'>Boylam</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' mode='datepicker' id='end_date_input' name='end_date' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='end_date_input'>Dağılma Tarihi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container' style='flex: 1' data-type='daily'>";
	formHtml += "<select id='end_time_select' name='end_time' style='min-width: auto'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='end_time_select'>Saat</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>KONKALAMA BİLGİLERİ</h3>";
	formHtml += "<div>";
	
	formHtml += "<div class='input_container' data-mode='1'>";
	formHtml += "<input type='text' id='new_location_name_input' name='location_name' placeholder=' '/>";
	formHtml += "<label for='new_location_name_input'>Konum Adı</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container select_container' style='flex: 1' data-mode='1'>";
	formHtml += "<select id='city_select' name='city'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='city_select'>Şehir</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container select_container' style='flex: 1' data-mode='1'>";
	formHtml += "<select id='district_select' name='district'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='district_select'>İlçe</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container' data-mode='1'>";
	formHtml += "<input type='text' id='address_input' name='address' placeholder=' '/>";
	formHtml += "<label for='address_input'>Adres</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container' style='flex: 1' data-mode='1'>";
	formHtml += "<input type='text' id='latitude_input' name='latitude' placeholder=' '/>";
	formHtml += "<label for='latitude_input'>Enlem</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container' style='flex: 1' data-mode='1'>";
	formHtml += "<input type='text' id='longitude_input' name='longitude' placeholder=' '/>";
	formHtml += "<label for='longitude_input'>Boylam</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>ULAŞIM BİLGİLERİ</h3>";
	formHtml += "<div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='radio' id='departure_mode_radio' name='departure_mode' checked='checked' value='0'/>";
	formHtml += "<label for='saved_location_mode_radio'>Gidiş Bilgileri</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='radio' id='arrival_mode_radio' name='departure_mode' value='1'/>";
	formHtml += "<label for='new_location_mode_radio'>Dönüş Bilgileri</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container' data-mode='1'>";
	formHtml += "<select id='method_select' name='method'><option val='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='method_input'>Ulaşım Yolu</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' mode='datepicker' id='departure_input' name='start_date' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='departure_input'>Kalkış Tarihi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container' style='flex: 1'>";
	formHtml += "<select id='departure_time_select' name='start_time' style='min-width: auto;'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='deprature_time_select'>Saat</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container' data-mode='1'>";
	formHtml += "<input type='text' id='new_location_name_input' name='location_name' placeholder=' '/>";
	formHtml += "<label for='new_location_name_input'>Araç Plakası / Sefer No</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container' data-mode='1'>";
	formHtml += "<input type='text' id='new_location_name_input' name='location_name' placeholder=' '/>";
	formHtml += "<label for='new_location_name_input'>Sürücü (Yetkili) Adı</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container' data-mode='1'>";
	formHtml += "<input type='text' id='new_location_name_input' name='location_name' placeholder=' '/>";
	formHtml += "<label for='new_location_name_input'>Sürücü İletişim No</label>";
	formHtml += "</div>";
	
	formHtml += "</div>";
	
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>ULAŞIM NOTLARI</h3>";
	formHtml += "<div>";
	
	formHtml += "<div class='input_container'>";
	formHtml += "<textarea id='transportaion_notes'></textarea>";
	formHtml += "</div>";
	
	
	formHtml += "</div>";
	formHtml += "</section>";
	*/
	formHtml += "<input type='hidden' name='data_mode' value='" + (event == null ? 1 : 2) + "'>";
	
	if(event != null)
		formHtml += "<input type='hidden' name='id' value='" + event.id + "'>";
		
	formHtml += "</form>";

	return formHtml;
}


/*Filtre Ekranları*/
var filterObj;

function makeFilterDialogue()
{
	let filterHtml = "";
	filterHtml += "<form id='filter_form'>";
	filterHtml += "<div>";
	filterHtml += "<p><strong>DOĞUM TARİHİ</strong></p>";
	filterHtml += "<div class='inline_input_row'>";
	filterHtml += "<div class='input_container'><input type='text' placeholder=' ' required data-filter_attr='birthDate' data-filter_set='initVal'/><label>Başlangıç</label></div>";
	filterHtml += "<div class='input_container'><input type='text' placeholder=' ' required data-filter_attr='birthDate' data-filter_set='finalVal'/><label>Bitiş</label></div>";
	filterHtml += "</div>";
	filterHtml += "</div>";
	
	filterHtml += "<div style='margin-top: 20px'>";
	filterHtml += "<p><strong>CİNSİYET</strong></p>";
	filterHtml += "<div class='inline_input_row'>";
	filterHtml += "<div class='input_container no_height'><input type='radio' id='female_radio' name='gender' value='FEMALE' data-filter_attr='gender' data-filter_set='val'/><label for='female_radio'>Kız</label></div>";
	filterHtml += "<div class='input_container no_height'><input type='radio' id='male_radio' name='gender' value='MALE' data-filter_attr='gender' data-filter_set='val'/><label for='male_radio'>Erkek</label></div>";
	filterHtml += "</div>";
	filterHtml += "</div>";
	
	filterHtml += "<div style='margin-top: 20px'>";
	filterHtml += "<p><strong>YAŞ GRUBU</strong></p>";
	
	ageGroups.forEach((value, index) => 
	{
		if(index % 4 == 0)
			filterHtml += (index != 0 ? "</div>" : "") + "<div class='inline_input_row'>";
			
		filterHtml += "<div class='input_container no_height'><input type='checkbox' id='" + value + "_checkbox' value='" + value + "' data-filter_attr='ageGroup' data-filter_set='groupVal'/><label for='" + value + "_checkbox'>" + ageGroupVals[index] + "</label></div>";
	});
	
	filterHtml += "</div>";
	
	filterHtml += "<div style='margin-top: 20px'>";
	filterHtml += "<p><strong>BOY (CM)</strong></p>";
	filterHtml += "<div class='inline_input_row'>";
	filterHtml += "<div class='input_container'><input type='number' placeholder=' ' required data-filter_attr='height' data-filter_set='initVal'/><label>En Az</label></div>";
	filterHtml += "<div class='input_container'><input type='number' placeholder=' ' required data-filter_attr='height' data-filter_set='finalVal'/><label>En Çok</label></div>";
	filterHtml += "</div>";
	filterHtml += "</div>";

	filterHtml += "<div style='margin-top: 20px'>";
	filterHtml += "<p><strong>KİLO (KG)</strong></p>";
	filterHtml += "<div class='inline_input_row'>";
	filterHtml += "<div class='input_container'><input type='number' placeholder=' ' required data-filter_attr='weight' data-filter_set='initVal'/><label>En Az</label></div>";
	filterHtml += "<div class='input_container'><input type='number' placeholder=' ' required data-filter_attr='weight' data-filter_set='finalVal'/><label>En Çok</label></div>";
	filterHtml += "</div>";
	filterHtml += "</div>";
	
	filterHtml += "<div style='margin-top: 20px'>";
	filterHtml += "<p><strong>KAN GRUBU</strong></p>";
	filterHtml += "<div class='inline_input_row'>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='a_rh_+_checkbox' value='A_RH_POS' data-filter_attr='bloodType' data-filter_set='groupVal'/><label for='a_rh_+_checkbox'>A RH(+)</label></div>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='b_rh_+_checkbox' value='B_RH_POS' data-filter_attr='bloodType' data-filter_set='groupVal'/><label for='b_rh_+_checkbox'>B RH(+)</label></div>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='0_rh_+_checkbox' value='ZR_RH_POS' data-filter_attr='bloodType' data-filter_set='groupVal'/><label for='0_rh_+_checkbox'>0 RH(+)</label></div>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='ab_rh_+_checkbox' value='AB_RH_POS' data-filter_attr='bloodType' data-filter_set='groupVal'/><label for='ab_rh_+_checkbox'>AB RH(+)</label></div>";
	filterHtml += "</div>";
	filterHtml += "<div class='inline_input_row'>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='a_rh_-_checkbox' value='A_RH_NEG' data-filter_attr='bloodType' data-filter_set='groupVal'/><label for='a_rh_-_checkbox'>A RH(-)</label></div>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='b_rh_-_checkbox' value='B_RH_NEG' data-filter_attr='bloodType' data-filter_set='groupVal'/><label for='b_rh_-_checkbox'>B RH(-)</label></div>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='0_rh_-_checkbox' value='ZR_RH_NEG' data-filter_attr='bloodType' data-filter_set='groupVal'/><label for='0_rh_-_checkbox'>0 RH(-)</label></div>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='ab_rh_-_checkbox' value='AB_RH_NEG' data-filter_attr='bloodType' data-filter_set='groupVal'/><label for='ab_rh_-_checkbox'>AB RH(-)</label></div>";
	filterHtml += "</div>";
	filterHtml += "</div>";
	
	filterHtml += "</div>";
	filterHtml += "</form>";

	let filterDialogue = new BordomorDialogue("TABLOYU FİLTRELE", filterHtml, "Uygula", "İptal");
	filterDialogue.setButtonClickCallbacks(function()
	{ 
		let compatibleAthletes = [];
		
		let athletesRunner = new BordomorAjaxRunner(athleteDataURL);
		athletesRunner.init();
		athletesRunner.setProcDialog("Filtre Hesaplanıyor");
		athletesRunner.setFailDialog();
		athletesRunner.setCallbacks(function(isValid, response)
		{
			if(isValid)
			{
				$("table#athletes_table tr").removeClass("filter_out");
				setTablePagination("table#athletes_table");
				
				let athleteEls = response.getElementsByTagName("athlete");
					
				for(let i = 0; i < athleteEls.length; i++)
				{
					let nextAthlete = new Athlete();
					nextAthlete.parseFromXMLElement(athleteEls[i]);
					
					if(nextAthlete.compliesWith(filterObj))
						compatibleAthletes.push(nextAthlete);
					else
						$("table#athletes_table tr[data-code='" + nextAthlete.loginable.code + "']").addClass("filter_out");
				}
				
				let filterDescHtml = filter =>
				{
					let html = "";
					
					filter.filters.forEach(function(nextFilter, index)
					{
						html += "<span>" + nextFilter.attr + "<button class='clear_style'>Kaldır</button></span>";
					});
					
					return "<span><button id='clear_filters_button' class='clear_style' style='color: var(--system_red)'><span style='display: inline-block; font: 400 11px Symbols; margin: 0px; margin-right: 5px; color: var(--system_red)'>&#x274C;</span>Temizle</button></span>";
				};
				
				setTablePagination("table#athletes_table");
				$("table#athletes_table").find("tfoot td.controls span#filter_data").html(filterDescHtml(filterObj));
				
				$("button#clear_filters_button").click(function()
				{
					filterObj = null;
					$("table#athletes_table tbody tr").removeClass("filter_out");
					setTablePagination("table#athletes_table");
					
					$(this).parent().html("");
				});
			}
			else
				alert("Bad data");
		});
		
		filterObj = getFilterObj();
		filterDialogue.close();
		athletesRunner.run(responseErrorRegExp);
		
		function getFilterObj()
		{
			let getVal = input => 
			{
				if($(input).prop("type") == "text" || $(input).prop("type") == "number")
					return $(input).val();
				else if($(input).prop("type") == "checkbox" || $(input).prop("type") == "radio")
					return $(input).is(":checked") ? $(input).val() : null;
				else
					return $(input).find("option:selected") ? $(input).find("option:selected").val() : null;
			};
		
			let filterObj = {};
			filterObj.filters = [];
			
			$("form#filter_form input").each(function(event)
			{
				let attrName = $(this).data("filter_attr");
				let attrFound = false;
				let thatInput = this;
				
				let inputVal = getVal($(this));
				
				if(attrName)
				{
					filterObj.filters.forEach(function(val, index)
					{
						let nextFilter = val;
						
						if(val.attr == attrName && inputVal)
						{
							let val = $(thatInput).data("filter_set") == "val" ? inputVal : null;
							let minVal = $(thatInput).data("filter_set") == "initVal" ? inputVal : null;
							let maxVal = $(thatInput).data("filter_set") == "finalVal" ? inputVal : null;
							let groupVal = $(thatInput).data("filter_set") == "groupVal" ? inputVal : null;  
							
							if(val)
								nextFilter["val"] = val;
							else if(minVal)
								nextFilter["initVal"] = minVal;
							else if(maxVal)
								nextFilter["finalVal"] = maxVal;
							else if(groupVal)
								nextFilter["groupVal"].push(groupVal);
						
							attrFound = true;
						}
					});
					
					if(!attrFound)
					{
						let val = $(thatInput).data("filter_set") == "val" ? inputVal : null;
						let minVal = $(this).data("filter_set") == "initVal" ? inputVal : null;
						let maxVal = $(this).data("filter_set") == "finalVal" ? inputVal : null;
						let groupVal = $(this).data("filter_set") == "groupVal" ? inputVal : null;
						
						if(inputVal && (val || minVal || maxVal || groupVal))
						{
							let newFilter = {"attr": attrName};
							
							if(val)
								newFilter["val"] = val;
							else if(minVal)
								newFilter["initVal"] = minVal;
							else if(maxVal)
								newFilter["finalVal"] = maxVal;
							else if(groupVal)
								newFilter["groupVal"] = [groupVal];
								
							filterObj.filters.push(newFilter);
						}
					}
				}
			});
			
			return filterObj;
		};
	}, function(){ filterDialogue.close() });
	filterDialogue.print(); 
}