var trainings = [];
var that = this;

$(document).ready(function()
{
 	trainings.forEach((training, index) =>
	{
		let startTime = getTimeString(new Date(training.startTime), 0);
		let endTime = getTimeString(new Date(training.endTime), 0);
		
		let nextTrainingTime = new Date(training.startTime);
		let nextContainer = null;
		let isInitial = true;
		let containerLevel = 1;
		
		while(nextTrainingTime.getTime() < training.endTime)
		{
			let nextDayNumber = getDateString(nextTrainingTime).substring(0, 2);
			let nextTrainingTimeStr = getTimeString(nextTrainingTime).replace(":", "_");
			nextContainer = $("tr#" + nextDayNumber + "_row_" + containerLevel +" td#" + nextDayNumber + "_row_" + containerLevel + "_" + nextTrainingTimeStr);
			
			if(nextContainer.hasClass("scheduled"))
				containerLevel++;
			
			nextTrainingTime = new Date(nextTrainingTime.getTime() + 15*60*1000);
		}
		
		nextTrainingTime = new Date(training.startTime);
		
		let totalTrainingContainerWidth = 0;
		let teamNameSpan = null;
		
		while(nextTrainingTime.getTime() < training.endTime)
		{
			let nextDayNumber = getDateString(nextTrainingTime).substring(0, 2);
			let nextTrainingTimeStr = getTimeString(nextTrainingTime).replace(":", "_");
			nextContainer = $("tr#" + nextDayNumber + "_row_" + containerLevel +" td#" + nextDayNumber + "_row_" + containerLevel + "_" + nextTrainingTimeStr);
			
			if(isInitial)
			{
				$(nextContainer).addClass("beginning");
				$(nextContainer).find("span.colour_indicator").eq(0).css("background-color", training.location.representingColour);
				teamNameSpan = $(nextContainer).find("span > span:not(.colour_indicator)").eq(0);
				$(teamNameSpan).html(training.team.name);
				
				isInitial = false;
			}
			 
			$(nextContainer).addClass("scheduled");
			$(nextContainer).prop("title", training.team.name);
			$(nextContainer).find(" > span").eq(0).attr("data-id", training.id);
			nextTrainingTime = new Date(nextTrainingTime.getTime() + 15*60*1000);
			totalTrainingContainerWidth += $(nextContainer).outerWidth();
		}
		
		$(teamNameSpan).css("max-width", (totalTrainingContainerWidth - 20) + "px");
		$(nextContainer).addClass("end");
	});
	
	$("td.time_cell.scheduled > span").click(function(event) 
	{
		event.stopPropagation();
		getTrainingData($(this).data("id"));
	});
	
	$("button#new_item").click(event => 
	{
		let formHtml = getTrainingForm();
		let newDialogue = new BordomorDialogue("YENİ ANTRENMAN GİRİŞİ", formHtml, "Kaydet", "İptal Et");
				
		newDialogue.setButtonClickCallbacks(function() 
		{ 
			let newTrainingRunner = new BordomorAjaxRunner(trainingManURI, $("form#training_form").serialize());
			newTrainingRunner.init("post", true, "text");
			newTrainingRunner.setProcDialog("Antrenman Tanımlanıyor");
			newTrainingRunner.setFailDialog();
				
			newTrainingRunner.setCallbacks(function() { window.location = window.location.href; });
				
			newDialogue.close();
			newTrainingRunner.run(responseErrorRegExp);
  		}, function() { newDialogue.close() });
			
		newDialogue.print(false, true);
		
		initializeDatePickerInputs();
		
		timeVals.forEach((hour, index) => $("select#start_time_select, select#end_time_select").append($("<option>", { value: hour, text:hour })));
		hourVals.forEach((hour, index) => { if(index < 4) $("select#duration_hour_select").append($("<option>", { value: hour, text: hour })); });
		minValsShort.forEach((minute, index) => $("select#duration_minute_select").append($("<option>", { value: minute, text: minute })) );
		branches.forEach((value, index) => $("select#branch_select").append($("<option>", { value: value, text: branchVals[index] })));
		
		$("select#training_days_select[multiple]").multiselect({ placeholder: "Antrenman Günleri" });
		weekDays.forEach((value, index) => $("select#training_days_select").append($("<option>", { value: (index + 1), text: weekDays[index], selected: false })));
		$("select#training_days_select[multiple]").multiselect("reload");
		$("select#training_days_select[multiple]").change(event =>
		{
			let selectedOpts = "";
			$("li.ms-reflow.selected input[type=checkbox]").each(function(index) { selectedOpts += $(this).val() != "-1" ? "," + $(this).val() : ""; });
			$("input[type=hidden][name='planned_time_values']").eq(0).val(selectedOpts.substring(1));
		});
		
		$("select#branch_select").change(event => 
		{			
			let selectedBranch = $(event.target).find("option:selected").val();
			$(event.target).parent().siblings("div.input_container").addClass("loading");
			$(event.target).parent().siblings("div.select_container").each(function()
			{
				let select = $(this).find("select").eq(0);
				$(select).find("option:not(:disabled)").remove();
				$(select).prop("disabled", "disabled");
			});
			
			getTrainingParameterData(selectedBranch, function(isValid, response)
			{
				$(event.target).parent().siblings("div.input_container").removeClass("loading");
				
				if(isValid)
				{
					let data = that.parseData(response);
					let teams = data[0];
					let trainers = data[1];
					let locations = data[2];
					
					teams.forEach((team, index) => $("select#team_select").append($("<option>", { value: team.id, text: team.name })));
					locations.forEach((location, index) => $("select#location_select").append($("<option>", { value: location.id, text: (location.name + " (" + location.address.city.name + ")") })));
					
					if(trainers.length > 0)
					{
						trainers.forEach((trainer, index) => $("select#trainer_select").append($("<option>", { value: trainer.id, text: trainer.loginable.fullName() })));
						$("select#trainer_select").prop("disabled", false);
					}
					
					$("select#team_select, select#location_select").prop("disabled", false);
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
		});
		
		$("input[type='radio'][name='duration_mode']").change(event => 
		{
			let mode = parseInt($("input[type='radio'][name='duration_mode']:checked").val());
			
			$("div.input_container[data-type='daily'][data-mode='" + Math.abs(mode - 1) + "']").find("input, select").prop("disabled", "disabled");
			$("div.input_container[data-type='daily'][data-mode='" + mode + "']").find("input, select").prop("disabled", false);
		});
		
		$("input[type='checkbox'][name='duration_mode']").change(event => 
		{
			let mode = $("input[type='checkbox'][name='duration_mode']").is(":checked") ? 1 : 0;
			
			$("div.input_container[data-type='weekly'][data-mode='" + Math.abs(mode - 1) + "']").find("input, select").prop("disabled", "disabled");
			$("div.input_container[data-type='weekly'][data-mode='" + mode + "']").find("input, select").prop("disabled", false);
		});
		
		$("input[type='radio'][name='plan_type']").change(event => 
		{
			let mode = $("input[type='radio'][name='plan_type']:checked").val();
			
			if(mode == "DAYS")
			{
				$("div#weekly_plan_options input, div#weekly_plan_options select").prop("disabled", "disabled");
				$("div#weekly_plan_options").css("display", "none");
				$("div#daily_plan_options").css("display", "block");
				$("div#daily_plan_options input, div#daily_plan_options select").each(function()
				{
					if($(this).prop("type") != "radio")
						$(this).val("");
					else
						$(this).prop("checked", false);
						
					$(this).prop("disabled", false);
				});
				
				$(this).find("input[type='radio']#date_mode_radio").trigger("click");
				$("select#reccurrence_unit_select").find("option").prop("disabled", false);
				$("select#reccurrence_unit_select").find("option").eq(0).prop("disabled", "disabled");
				$("select#reccurrence_unit_select").find("option").eq(0).prop("selected", "selected");
			}
			else
			{
				$("div#daily_plan_options input, div#daily_plan_options select").prop("disabled", "disabled");
				$("div#daily_plan_options").css("display", "none");
				$("div#weekly_plan_options").css("display", "block");
				$("div#weekly_plan_options input, div#weekly_plan_options select").each(function()
				{
					if($(this).prop("type") != "checkbox")
						$(this).val("");
					else
						$(this).prop("checked", false);
						
					$(this).prop("disabled", false);
				});
				
				$("select#training_days_select[multiple]").multiselect("reload");
				$(this).find("input[type='checkbox']#duration_mode_checkbox").trigger("change");
				$("select#reccurrence_unit_select").find("option").prop("disabled", "disabled");
				$("select#reccurrence_unit_select").find("option").eq(2).prop("disabled", false);
				$("select#reccurrence_unit_select").find("option").eq(0).prop("selected", "selected");
			}
		});
		
		$("input[type='radio'][name='plan_type']").trigger("change");
		$("input[type='checkbox']#recurring_training_checkbox").change(event => { $("input[data-group='reccurence'], select[data-group='reccurence']").prop("disabled", $(event.target).is(":checked") ? false : "disabled"); });
	});
	
	$("button#filters_button").click(function()
	{
		//makeFilterDialogue();
	});
	
	$("button#today_button").click(function() { window.location.href = "http://localhost:8080/ODTU_SK/trainings.jsp" });
	
	let now = new Date();
	
	$("td.time_cell").each(function()
	{
		let cellDate = $(this).data("date");
		
		let cellDay = cellDate.substring(0, 2);
		let cellMonth = cellDate.substring(3, 5);
		let cellYear = cellDate.substring(6);
		
		let formattedDate = new Date(cellYear + "-" + cellMonth + "-" + cellDay + " 23:59:59");
		
		if(formattedDate.getTime() < now.getTime())
			$(this).addClass("past_day");
	});
	
	$("tr[data-is_selected_item='true']").trigger("click");
});

function getTrainingData(id)
{
	let parameters = "id=" + id;
	let runner = new BordomorAjaxRunner(trainingDataURI, parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let trainingEl = response.getElementsByTagName("training")[0];
			let fetchedTraining = new Training();
			fetchedTraining.parseFromXMLElement(trainingEl);
			
			that.displayFetchedData(fetchedTraining);
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
			{
				let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Antrenman Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
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

function displayFetchedData(training)
{
	let container = $("span[data-id=" + training.id + "]").parent(".beginning").eq(0);
	$(container).append("<div class='data_card'>" + training.getCard() + "</div>");
	let addedCard = $("div.athlete_card[data-id='" + training.id + "']");
	let addedCardZIndex = 20 + $("div.athlete_card").length;
	$(addedCard).css("z-index", addedCardZIndex);
	
	$(addedCard).find("div.nav > button").click(function(event)
	{
		event.preventDefault();
		
		$(this).siblings().removeClass("selected");
		$(this).addClass("selected");
		
		let index = $(this).index();
		let shift = -1*100*index + "%";
		
		$(addedCard).find("div.data").eq(0).css("left", shift);
	});
	
	$(addedCard).find("button#close_card_button").click(function(event) { $(addedCard).parent().remove(); });
	$(addedCard).find("button#delete_item_button").click(function(event) 
	{ 
		let confDialog = null;
		
		if(training.isGroupParent)
		{
			confDialog = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Bu antrenman bir antrenman grubunun sahibi durumda. Bu antrenmanın sistemden kaldırılması, bu antrenmana bağlı bulunan tüm grubun sistemden kaldırılması ile sonuçlanacaktır.<br><br>Ne yapmak istiyorsunuz?", "Tümünü Grubu Sil", "İptal");
			confDialog.setChoiceCallbacks(function(){ deleteTraining(training.id) });
		} 
		else if(training.ownerTraining != null)
		{
			confDialog = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Bu antrenman bir antrenman grubunun parçası. Yapmak istediğini işlemi seçiniz.", "Tümünü Grubu Sil", "Bu Antrenmanı Sil");
			confDialog.setChoiceCallbacks(function(){ deleteTraining(training.ownerTraining.id) }, function(){ deleteTraining(training.id) });
		}
		else
		{
			confDialog = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Devam etmek istediğinize emin misiniz?", "Antrenmanı Sil", "İptal");
			confDialog.setChoiceCallbacks(function(){ deleteTraining(training.id) });
		}
		
		confDialog.print();
	
		function deleteTraining(trainingId)
		{
			let deleteRunner = new BordomorAjaxRunner(trainingManURI, "id=" + trainingId + "&data_mode=0");
			deleteRunner.init("post", true, "text");
			deleteRunner.setProcDialog("Antrenman Siliniyor");
			deleteRunner.setFailDialog();
			deleteRunner.setCallbacks(function() { window.location = window.location.href; });
			
			confDialog.close();
			deleteRunner.run(responseErrorRegExp);
		}
	});
	
	$(addedCard).find("button#edit_item_button").click(function(event) 
	{  
		let formHtml = getTrainingForm(training);
		let editDialogue = new BordomorDialogue("ANTRENMAN BİLGİLERİNİ DÜZENLE", formHtml, "Düzenle", "İptal Et");
				
		editDialogue.setButtonClickCallbacks(function() 
		{ 
			let trainingRunner = new BordomorAjaxRunner(trainingManURI, $("form#training_form").serialize());
			trainingRunner.init("post", true, "text");
			trainingRunner.setProcDialog("Antrenman Düzenleniyor");
			trainingRunner.setFailDialog();
				
			trainingRunner.setCallbacks(function() { window.location = window.location.href; });
				
			editDialogue.close();
			trainingRunner.run(responseErrorRegExp);
  		}, function() { editDialogue.close() });
			
		editDialogue.print(false, true);
		
		initializeDatePickerInputs();
		
		timeVals.forEach((hour, index) => $("select#start_time_select, select#end_time_select").append($("<option>", { value: hour, text:hour })));
		hourVals.forEach((hour, index) => { if(index < 4) $("select#duration_hour_select").append($("<option>", { value: hour, text: hour })); });
		minValsShort.forEach((minute, index) => $("select#duration_minute_select").append($("<option>", { value: minute, text: minute })) );
		
		$("select#branch_select option").remove();
		$("select#branch_select").append($("<option>", { value: training.team.branch, text: branchVals[branches.indexOf(training.team.branch)], selected: true }));
		
		$("input[type='radio'][name='duration_mode']").change(event => 
		{
			let mode = parseInt($("input[type='radio'][name='duration_mode']:checked").val());
			
			$("div.input_container[data-type='daily'][data-mode='" + Math.abs(mode - 1) + "']").find("input, select").prop("disabled", "disabled");
			$("div.input_container[data-type='daily'][data-mode='" + mode + "']").find("input, select").prop("disabled", false);
		});
		
		$("select#branch_select").change(event => 
		{			
			let selectedBranch = $(event.target).find("option:selected").val();
			$(event.target).parent().siblings("div.input_container").addClass("loading");
			$(event.target).parent().siblings("div.select_container").each(function()
			{
				let select = $(this).find("select").eq(0);
				$(select).find("option:not(:disabled)").remove();
				$(select).prop("disabled", "disabled");
			});
			
			getTrainingParameterData(selectedBranch, function(isValid, response)
			{
				$(event.target).parent().siblings("div.input_container").removeClass("loading");
				
				if(isValid)
				{
					let data = that.parseData(response);
					let teams = data[0];
					let trainers = data[1];
					let locations = data[2];
					
					teams.forEach((team, index) => $("select#team_select").append($("<option>", { value: team.id, text: team.name })));
					locations.forEach((location, index) => $("select#location_select").append($("<option>", { value: location.id, text: (location.name + " (" + location.address.city.name + ")") })));
					
					if(trainers.length > 0)
					{
						trainers.forEach((trainer, index) => $("select#trainer_select").append($("<option>", { value: trainer.id, text: trainer.loginable.fullName() })));
						$("select#trainer_select").prop("disabled", false);
					}
					
					$("select#team_select, select#location_select").prop("disabled", false);
					
					$("select#team_select").val(training.team.id);
					$("select#trainer_select").val(training.trainer != null ? training.trainer.id : -1);
					$("select#location_select").val(training.location.id);
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
		});
		
		$("select#branch_select").trigger("change");
		
		$("input#start_date_input").val(getDateString(new Date(training.startTime)));
		$("select#start_time_select").val(getTimeString(new Date(training.startTime)));
		$("input#end_date_input").val(getDateString(new Date(training.endTime)));
		$("select#end_time_select").val(getTimeString(new Date(training.endTime)));
	});
}

function getTrainingParameterData(branch, positiveCallback, negativeCallback)
{
	let runner = new BordomorAjaxRunner(trainingParameterDataURI, "branch=" + branch);
	runner.init();
	runner.setFailDialog();
	runner.setCallbacks(positiveCallback, negativeCallback);
	runner.run(responseErrorRegExp);
}

function parseData(response)
{
	let teamEls = response.getElementsByTagName("team");
	let trainerEls = response.getElementsByTagName("trainer");
	let locationEls = response.getElementsByTagName("location");
	
	let teams = [];
	let trainers = [];
	let locations = [];
				
	for(let i = 0; teamEls != null && i < teamEls.length; i++)
	{
		let nextTeam = new Team();
		nextTeam.parseFromXMLElement(teamEls[i]);
		teams.push(nextTeam);
	}
	
	for(let i = 0; trainerEls != null && i < trainerEls.length; i++)
	{
		let nextTrainer = new Trainer();
		nextTrainer.parseFromXMLElement(trainerEls[i]);
		trainers.push(nextTrainer);
	}
	
	for(let i = 0; locationEls != null && i < locationEls.length; i++)
	{
		let nextLocation = new Location();
		nextLocation.parseFromXMLElement(locationEls[i]);
		locations.push(nextLocation);
	}
	
	return [teams, trainers, locations];
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

function getTrainingForm(training)
{
	let formHtml = "";
	formHtml += "<form id='training_form' method='post' class='wide_mode'>";
	
	formHtml += "<section>";
	formHtml += "<h3>TEMEL BİLGİLER</h3>";
	formHtml += "<div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='branch_select' name='branch'><option val='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='branch_select'>Spor Dalı</label>"
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='team_select' name='team' disabled='disabled'><option val='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='team_select'>Takım</label>"
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='trainer_select' name='trainer' disabled='disabled'><option val='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='trainer_select'>Antrenör</label>"
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='location_select' name='location' disabled='disabled'><option val='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='location_select'>Tesis</label>"
	formHtml += "</div>";
	
	if(training == null)
	{
		formHtml += "<div>";
		formHtml += "<div class='inline_input_row'>";
		formHtml += "<div class='input_container'>";
		formHtml += "<input type='radio' id='daily_plan_type_radio' name='plan_type' checked='checked' value='DAYS'/>";
		formHtml += "<label for='daily_plan_type_radio'>Günlük Plan</label>";
		formHtml += "</div>";
		formHtml += "<div class='input_container'>";
		formHtml += "<input type='radio' id='weekly_plan_type_radio' name='plan_type' value='WEEKS'/>";
		formHtml += "<label for='weekly_plan_type_radio'>Haftalık Plan</label>";
		formHtml += "</div>";
		formHtml += "</div>";
	}
	
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>PLAN BİLGİLERİ</h3>";
	formHtml += "<div id='daily_plan_options' style='margin-top: 13px'>";
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
	formHtml += "<input type='radio' id='date_mode_radio' name='duration_mode' checked='checked' value='0'/>";
	formHtml += "<label for='date_mode_radio'>Tarih Seçimi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='radio' id='duration_mode_radio' name='duration_mode' value='1'/>";
	formHtml += "<label for='duration_mode_radio'>Süre Seçimi</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container' data-type='daily' data-mode='0'>";
	formHtml += "<input type='text' mode='datepicker' id='end_date_input' name='end_date' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='end_date_input'>Bitiş Tarihi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container' style='flex: 1' data-type='daily' data-mode='0'>";
	formHtml += "<select id='end_time_select' name='end_time' style='min-width: auto'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='end_time_select'>Saat</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row equal_size_inputs'>";
	formHtml += "<div class='input_container select_container' data-type='daily' data-mode='1'>";
	formHtml += "<select id='duration_hour_select' name='duration_hour' style='min-width: auto' disabled='disabled'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='duration_hour_select'>Saat</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container' data-type='daily' data-mode='1'>";
	formHtml += "<select id='duration_minute_select' name='duration_minute' style='min-width: auto' disabled='disabled'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='duration_minute_select'>Dakika</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	if(training == null)
	{
		formHtml += "<div id='weekly_plan_options' style='margin-top: 13px'>";
		
		formHtml += "<div class='input_container select_container'>";
		formHtml += "<select multiple id='training_days_select' disabled='disabled'></select>";
		formHtml += "<input type='hidden' name='planned_time_values' value='-1'/>";
		formHtml += "</div>";
		
		formHtml += "<div class='inline_input_row'>";
		formHtml += "<div class='input_container select_container' style='flex: 1'>";
		formHtml += "<select id='start_time_select' name='start_time' style='min-width: auto;'><option value='-1' disabled='disabled' selected='selected'></option></select>";
		formHtml += "<label for='start_time_select'>Başlangıç Saati</label>";
		formHtml += "</div>";
		formHtml += "<div class='input_container select_container' style='flex: 1' data-type='weekly' data-mode='0'>";
		formHtml += "<select id='end_time_select' name='end_time' style='min-width: auto'><option value='-1' disabled='disabled' selected='selected'></option></select>";
		formHtml += "<label for='end_time_select'>Bitiş Saati</label>";
		formHtml += "</div>";
		formHtml += "</div>";
		
		formHtml += "<div class='input_container' style='margin-top: 0px;'>";
		formHtml += "<input type='checkbox' id='duration_mode_checkbox' name='duration_mode' value='1'/>";
		formHtml += "<label for='duration_mode_checkbox'>Süre Seçimi</label>";
		formHtml += "</div>";
		
		formHtml += "<div class='inline_input_row'>";
		formHtml += "<div class='input_container select_container' style='flex: 1' data-type='weekly' data-mode='1'>";
		formHtml += "<select id='duration_hour_select' name='duration_hour' style='min-width: auto' disabled='disabled'><option value='-1' disabled='disabled' selected='selected'></option></select>";
		formHtml += "<label for='duration_hour_select'>Saat</label>";
		formHtml += "</div>";
		formHtml += "<div class='input_container select_container' style='flex: 1' data-type='weekly' data-mode='1'>";
		formHtml += "<select id='duration_minute_select' name='duration_minute' style='min-width: auto' disabled='disabled'><option value='-1' disabled='disabled' selected='selected'></option></select>";
		formHtml += "<label for='duration_minute_select'>Dakika</label>";
		formHtml += "</div>";
		formHtml += "</div>";
		
		formHtml += "<div class='input_container'>";
		formHtml += "<input type='text' mode='datepicker' id='start_date_input' name='start_date' placeholder=' ' maxlength='10' required/>";
		formHtml += "<label for='start_date_input'>Plan Başlangıç Tarihi</label>";
		formHtml += "</div>";
		
		formHtml += "</div>";
	}
	
	formHtml += "</section>";
	
	if(training == null)
	{
		formHtml += "<section>";
		formHtml += "<h3>TEKRAR SEÇENEKLERİ</h3>";
		formHtml += "<div>";
		formHtml += "<div class='input_container'>";
		formHtml += "<input type='checkbox' id='recurring_training_checkbox' name='is_reccurring_training' value='true' checked='checked'/>";
		formHtml += "<label for='recurring_training_checkbox'>Tekrar Eden Antrenman</label>";
		formHtml += "</div>";
		
		formHtml += "<div class='inline_input_row'>";
		formHtml += "<div class='input_container select_container' style='flex: 1'>";
		formHtml += "<select id='reccurrence_rate_select' name='reccurrence_rate' style='min-width: auto' data-group='reccurence'><option value='-1' disabled='disabled' selected='selected'></option>";
		
		for(let i = 1; i < 31; i++)
			formHtml += "<option value='" + i + "'>" + i + "</option>";
		
		formHtml +=  "</select>"; 
		formHtml += "<label for='reccurrence_select'>Döngü</label>";
		formHtml += "</div>";
		formHtml += "<div class='input_container select_container' style='flex: 1'>";
		formHtml += "<select id='reccurrence_unit_select' name='reccurrence_unit' style='min-width: auto' data-group='reccurence'><option value='-1' disabled='disabled' selected='selected'></option><option value='DAYS'>Gün</option><option value='WEEKS'>Hafta</option><option value='MONTHS'>Ay</option></select>";
		formHtml += "<label for='reccurrence_unit_select'>Sıklık</label>";
		formHtml += "</div>";
		formHtml += "</div>";
		formHtml += "<div class='input_container'>";
		formHtml += "<input type='text' mode='datepicker' id='reccurrence_end_date_input' name='reccurrence_end_date' placeholder=' ' maxlength='10'  data-group='reccurence' required/>";
		formHtml += "<label for='reccurrence_end_date_input'>Son Tarih</label>";
		formHtml += "</div>";
		
		formHtml += "<div class='input_container select_container'>";
		formHtml += "<select id='conflict_mode_select' name='conflict_mode'>";
		formHtml += "<option value='0' selected='selected'>Tüm Antrenmanları Planla</option>";
		formHtml += "<option value='1'>Çakışan Antrenmanları Planlama</option>";
		formHtml += "<option value='2'>Çakışma Olursa İptal Et</option>";
		formHtml += "</select>";
		formHtml += "<label for='reccurrence_unit_select'>Çakışmalar</label>";
		formHtml += "</div>";
		
		formHtml += "</div>";
		formHtml += "</section>";
	}
	
	formHtml += "<input type='hidden' name='data_mode' value='" + (training == null ? 1 : 2) + "'>";
	
	if(training != null)
		formHtml += "<input type='hidden' name='id' value='" + training.id + "'>";
		
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