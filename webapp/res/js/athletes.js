var that = this;
var athletes = [];

$(document).ready(function()
{
	$("table.data_table > tbody > tr").click(function(event)
	{
		$("table.data_table > tbody > tr").removeClass("selected_row");
		$(this).addClass("selected_row");
		that.athletes = [];
		that.getAthleteData($(this).data("code"));
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
				let formHtml = getNewAthleteFormHtml();
				let newDialogue = new BordomorDialogue("YENİ SPORCU GİRİŞİ", formHtml, "Kaydet", "İptal Et");
				
				newDialogue.setButtonClickCallbacks(function() 
				{ 
					let newPlayerRunner = new BordomorAjaxRunner(athleteManURI, $("form#new_player_form").serialize());
					newPlayerRunner.init("post", true, "text");
					newPlayerRunner.setProcDialog("Sporcu Hesabı Tanımlanıyor");
					newPlayerRunner.setFailDialog();
					
					newPlayerRunner.setCallbacks(function() { window.location = window.location.href; });
					
					newDialogue.close();
					newPlayerRunner.run(responseErrorRegExp);
	  
				}, function() { newDialogue.close() });
				newDialogue.print(false, true);
				
				let branchEls = response.getElementsByTagName("branch");
				
				for(let i = 0; i < branchEls.length; i++)
					$("select#branch_select").append($("<option>", { value: branchEls[i].getAttribute("text"), text: branchEls[i].getAttribute("name")}));
				
				for(let i = 0; i < bloodTypes.length; i++)
					$("select#blood_type_select").append($("<option>", { value: bloodTypes[i], text: bloodTypeVals[i]}));
				
				initializeDatePickerInputs();
				
				let teamQueryable = () => 
				{
					let branchVal = $("select#branch_select").find(":selected").val();
					let birthDateValid = $("input#birth_date_input").val().match(/^[0-9]{2}[\/][0-9]{2}[\/][0-9]{4}$/); 
					
					return branchVal != "-1" && birthDateValid;
				};
				
				$("select#branch_select").change(event => 
				{ 
					if(teamQueryable()) 
						getSuitableTeams($("select#branch_select").val(), $("input#birth_date_input").val(), $("select#team_select")); 
				});
				
				$("input#birth_date_input").on("input", event => 
				{ 
					if(teamQueryable()) 
						getSuitableTeams($("select#branch_select").val(), $("input#birth_date_input").val(), $("select#team_select")); 
				});
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
	
	$("button#filters_button").click(function()
	{
		makeFilterDialogue();
	});
});

function getSuitableTeams(branch, birthDate, select)
{
	let parameters = "branch=" + branch + "&birth_date=" + birthDate;
	let teamRunner = new BordomorAjaxRunner(teamDataURL, parameters);
	teamRunner.init();
	
	teamRunner.setCallbacks(function(isValid, response)
	{
		$(select).parent(".select_container").removeClass("loading");
		
		if(isValid)
		{
			let teamEls = response.getElementsByTagName("team");
			
			$(select).find("option:not(:disabled)").remove();
			
			for(let i = 0; i < teamEls.length; i++)
				$(select).append($("<option>", { value: teamEls[i].getAttribute("id"), text: teamEls[i].getAttribute("name")}));
				
			$(select).prop("disabled", false);
		}
	});
	
	$(select).prop("disabled", "disabled");
	$(select).find("option:not(:disabled)").remove();
	$(select).parent(".select_container").addClass("loading");
	teamRunner.run(responseErrorRegExp);
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

function getNewAthleteFormHtml()
{
	let formHtml = "";
	formHtml += "<form id='new_player_form' method='post'>";
	
	formHtml += "<header>";
	formHtml += "<figure></figure>";
	formHtml += "<section>";
	formHtml += "<div class='input_container'>";
	formHtml += "<label for='name_input'>Adı:</label>";
	formHtml += "<input type='text' id='name_input' name='name'/>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<label for='surname_input'>Soyadı:</label>";
	formHtml += "<input type='text' id='surname_input' name='surname'/>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<label for='branch_select'>Branşı:</label>";
	formHtml += "<select id='branch_select' name='primary_branch'><option value='-1' disabled='disabled' selected>Bir seçim yapın</option></select>";
	formHtml += "</div>";
	formHtml += "</section>";
	formHtml += "</header>";
	
	formHtml += "<section>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='email_input' name='email' placeholder=' ' required/>";
	formHtml += "<label for='email_input'>E-posta Adresi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input id='birth_date_input' type='text' name='birth_date' mode='datepicker' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='birth_date_input'>Doğum Tarihi</label>";
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='team_select' name='primary_team' disabled='disabled'><option value='-1' disabled='disabled' selected>Takımı</option></select>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='blood_type_select' name='blood_type' placeholder=' ' required><option value='-1' disabled='disabled' selected>Kan Grubu</option></select>";
	formHtml += "</div>";
	formHtml += "<input type='hidden' name='data_mode' value='1'>";
	formHtml += "<input type='hidden' name='update_mode' value='0'>";
	formHtml += "</section>";
	formHtml += "</form>";

	return formHtml;
}

function getEditAthleteFormHtml(athlete)
{
	alert(athlete);
	let formHtml = "";
	formHtml += "<form id='edit_player_form' class='wide_mode paged_mode' method='post'>";
	
	formHtml += "<header>";
	formHtml += "<figure></figure>";
	formHtml += "<section>";
	formHtml += "<div class='input_container'>";
	formHtml += "<label for='name_input'>Adı:</label>";
	formHtml += "<input type='text' id='name_input' name='name'/>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<label for='surname_input'>Soyadı:</label>";
	formHtml += "<input type='text' id='surname_input' name='surname'/>";
	formHtml += "</div>";
	formHtml += "</section>";
	formHtml += "<div class='pager'>";
	
	for(let i = 0; i < 2; i++)
		formHtml += "<button type='button' class='clear_style" + (i == 0 ? " selected" : "") + "'></button>";
	
	formHtml += "</div>";
	formHtml += "</header>";
	
	formHtml += "<section>";
	formHtml += "<h3>TEMEL BİLGİLER</h3>";
	formHtml += "<div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<select id='branch_select' name='primary_team' disabled='disabled'><option value='-1' disabled='disabled' selected>Branşı</option></select>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input id='birth_date_input' type='text' name='birth_date' mode='datepicker' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='birth_date_input'>Doğum Tarihi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='height_input' name='height' min='0' max='250' placeholder=' ' required/>";
	formHtml += "<label for='height_input' >Boy (cm)</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='weight_input' name='weight' min='0' max='200' placeholder=' ' required/>";
	formHtml += "<label for='weight_input'>Kilo (kg)</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<select id='team_select' name='primary_team' disabled='disabled'><option value='-1' disabled='disabled' selected>Takım Listesi İçin Bekleyin</option></select>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<select id='blood_type_select' name='blood_type' placeholder=' ' required><option value='-1' disabled='disabled' selected>Kan Grubu</option></select>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>KİŞİSEL BİLGİLER</h3>";
	formHtml += "<div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='email_input' name='email' placeholder=' ' required/>";
	formHtml += "<label for='email_input'>E-posta Adresi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='name_input' name='email' placeholder=' ' required/>";
	formHtml += "<label for='name_input'>Adı</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='surname_input' name='email' placeholder=' ' required/>";
	formHtml += "<label for='surname_input'>Soyadı</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='id_no_input' name='weight' placeholder=' ' required/>";
	formHtml += "<label for='id_no_input'>Kimlik Numarası</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='phone_input' name='weight' mode='phonenumber' placeholder=' ' required/>";
	formHtml += "<label for='phone_input'>Telefon Numarası</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='school_input' name='weight' mode='phonenumber' placeholder=' ' required/>";
	formHtml += "<label for='school_input'>Okulu</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>MEDİKAL BİLGİLER</h3>";
	formHtml += "<div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<textarea id='theraphies_input' placeholder=' ' required></textarea>";
	formHtml += "<label for='theraphies_input'>Geçmiş/Aktif Terapileri</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<textarea id='medication_input' placeholder=' ' required></textarea>";
	formHtml += "<label for='mediacation_input'>Kullandığı İlaçlar</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<textarea id='allergies_input' placeholder=' ' required></textarea>";
	formHtml += "<label for='allergies_input'>Alerjileri</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<textarea id='handicaps_input' placeholder=' ' required></textarea>";
	formHtml += "<label for='handicaps_input'>Özel Durumları</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</section>";
	
		
	formHtml += "<input type='hidden' name='data_mode' value='2'>";
	formHtml += "<input type='hidden' name='update_mode' value='2'>";
	formHtml += "</form>";

	return formHtml;
}

function getAthleteData(code)
{
	let parameters = "code=" + code;
	let runner = new BordomorAjaxRunner(athleteDataURL, parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let athleteEl = response.getElementsByTagName("athlete")[0];
			let fetchedAthlete = new Athlete();
			fetchedAthlete.parseFromXMLElement(athleteEl);
			
			that.displayFetchedData(fetchedAthlete);
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

function displayFetchedData(athlete)
{
	let athleteExists = false;
	
	for(let i = 0; i < athletes.length; i++)
	{
		if(athletes[i].id == athlete.id)
		{
			athleteExists = true;
			break;
		}
	}
	
	if(!athleteExists)
	{
		this.athletes.push(athlete);
		addAthleteCard(athlete);
	}
}

function addAthleteCard(athlete)
{
	let cardHtml = athlete.getCard();
	$("div.data_card *").remove();
	$("div.data_card").append(athlete.getCard());
	$("div.data_card").addClass("displayed");
	
	let addedCard = $("div.data_card").find("div.athlete_card[data-id='" + athlete.loginable.loginableId + "']");
	 
	$(addedCard).find("div.nav > button").click(function(event)
	{
		event.preventDefault();
		
		$(this).siblings().removeClass("selected");
		$(this).addClass("selected");
		
		let index = $(this).index();
		let shift = -1*100*index + "%";
		
		$(addedCard).find("div.data").eq(0).css("left", shift);
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
						
			for(let i = 0; i < athletes.length; i++)
			{	
				if(athletes[i].loginable.loginableId == id)
					that.athletes.splice(i, 1);
			}
		}, transitionDuration);
	});
	
	$(addedCard).find("div.data > div.pager button#prev_page").click(function(event)
	{
		$(addedCard).find("div.data > div.pager > table").css("left", "0px");
	});
	
	$(addedCard).find("div.data > div.pager button#next_page").click(function(event)
	{
		let tableWidth = $(this).parent().parent().find("table").eq(0).outerWidth();
		$(addedCard).find("div.data > div.pager > table").css("left", -1*tableWidth + "px");
	});
	
	$(addedCard).find("button#delete_athlete_button").click(function(event)
	{
		let confDialogue = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Devam etmek istediğinize emin misiniz?", "Sporcuyu Sil", "İptal");
		confDialogue.setChoiceCallbacks(function()
		{
			let deleteRunner = new BordomorAjaxRunner(athleteManURI, "id=" + athlete.id + "&data_mode=0");
			deleteRunner.init("post", true, "text");
			deleteRunner.setProcDialog("Sporcu Hesabı Siliniyor");
			deleteRunner.setFailDialog();
			deleteRunner.setCallbacks(function() { window.location = window.location.href; });
					
			confDialogue.close();
			deleteRunner.run(responseErrorRegExp);
		}); 
		confDialogue.print();
	});
	
	$(addedCard).find("button#edit_athlete_button").click(function(event)
	{
		let formHtml = getEditAthleteFormHtml(athlete);
		let newDialogue = new BordomorDialogue("SPORCU BİLGERİ GÜNCELLEME", formHtml, "Kaydet", "İptal Et");
		newDialogue.print(false, true);
	});
	
	$(addedCard).find("button#fetch_payment_plan_button").click(function(event)
	{
		$("div#payment_plan_container").children().remove();
		$("div#payment_plan_container").removeClass("placeholder");
		$("div#payment_plan_container").addClass("loading");
		
		let parameters = "athlete=" + athlete.id;
		let paymentPlanRunner = new BordomorAjaxRunner(paymentPlanDataURI, parameters);
		paymentPlanRunner.init();
		
		paymentPlanRunner.setCallbacks(function(isValid, response)
		{
			let planContainer = $(addedCard).find("div#payment_plan_container");
			$(planContainer).removeClass("loading");
			
			if(isValid)
			{
				$(planContainer).addClass("pager");
				
				let planEl = response.getElementsByTagName("payment_plan")[0];
				let paymentPlan = new PaymentPlan();
				paymentPlan.parseFromXMLElement(planEl);
				
				$(planContainer).append(paymentPlan.getCard());
				
				$(".pie_chart")[0].style.setProperty("--end_angle", "" + ((100 - paymentPlan.getLiabilityRatio())*360/100) + "deg");
				$(".pie_chart .percentage_value").html("%" + (100 - paymentPlan.getLiabilityRatio()).toFixed(1));
				initializeTablePagers($("div#payment_plan_container"));
				
				$(planContainer).find("td button#plan_details_button").click(event => { displayPlanData(paymentPlan); });
			}
			else
			{
				if(new RegExp(noDataErrorRegExp).test(response))
					$(addedCard).find("div#payment_plan_container").addClass("no_data");
				else
					makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
			}
		}, null);
		
		paymentPlanRunner.run(responseErrorRegExp);
	});
	
	function displayPlanData(plan)
	{
		let container = $("div.main_container_overlay");
		$(container).addClass("display");
		$(container).append(plan.getOverlayCard());
		
		$("div#total_liability_chart.pie_chart")[0].style.setProperty("--end_angle", "" + (plan.getTotalLiabilityRatio()*360/100) + "deg");
		$("div#total_liability_chart.pie_chart .percentage_value").html("%" + plan.getTotalLiabilityRatio().toFixed(1));
		
		$("div#paid_total_chart.pie_chart")[0].style.setProperty("--end_angle", "" + ((100 - plan.getLiabilityRatio())*360/100) + "deg");
		$("div#paid_total_chart.pie_chart .percentage_value").html("%" + (100 - plan.getLiabilityRatio()).toFixed(1));
		
		$("div#remaining_total_chart.pie_chart")[0].style.setProperty("--end_angle", "" + (plan.getLiabilityRatio()*360/100) + "deg");
		$("div#remaining_total_chart.pie_chart .percentage_value").html("%" + plan.getLiabilityRatio().toFixed(1));
		
		let addedCard = $("div#payment_plan_details_card[data-id='" + plan.id + "']");
		$(addedCard).find("button#close_card_button").click(function(e) { $(container).removeClass("display"); $(addedCard).remove(); });
		
		$(addedCard).find("td button.switch_state").click(function()  
		{
			let processedPayment = null;
			let clickedPaymentId = parseInt($(this).parent().parent().data("id"));
			
			for(let i = 0; i < plan.payments.length; i++)
			{
				if(plan.payments[i].id == clickedPaymentId)
				{
					processedPayment = plan.payments[i]; 
					break;
				}
			}
			
			let paymentStateRunner = new BordomorAjaxRunner(paymentManURI, "id=" + processedPayment.id + "&data_mode=2&update_mode=" + (processedPayment.isPacified ? "1" : "2"));
			paymentStateRunner.init("post", true, "text");
			paymentStateRunner.setProcDialog("Ödeme Güncelleniyor");
			paymentStateRunner.setFailDialog();
			paymentStateRunner.setCallbacks(function() { $("button#refresh_button").trigger("click") });
			
			paymentStateRunner.run(responseErrorRegExp);
		});
		
		$(addedCard).find("button#freeze_athlete_button").click(event => 
		{
			let freezeRunner = new BordomorAjaxRunner(athleteManURI, "id=" + athlete.id + "&data_mode=2&update_mode=2");
			freezeRunner.init("post", true, "text");
			freezeRunner.setProcDialog("Sporcu Kaydı Donduruluyor");
			freezeRunner.setFailDialog();
			freezeRunner.setCallbacks(function() { window.location = window.location.href; });
					
			let confDialogue = new BordomorConfirmationDialogue("<p>Hesap dondurma işlemi, sporcuya ait aktif ve ileri vade ödemeleri pasifleştircek, bu ödemelere ait tutarları plan yükümlülüğünden otomatik olarak düşecektir.</p><p>Buna ek olarak henüz ödenmemiş olan geçmiş vade ödemeleri de pasifleştirmek istiyor musunuz?.</p>", "Evet", "Hayır");
			confDialogue.setChoiceCallbacks(function()
			{
				freezeRunner.params = "id=" + athlete.id + "&data_mode=2&update_mode=3";
				freezeRunner.run(responseErrorRegExp);
				confDialogue.close();
			}, function() 
			{ 
				freezeRunner.run(responseErrorRegExp);
				confDialogue.close(); 
			});
			
			confDialogue.print(false, true);
		});
		
		$(addedCard).find("button#unfreeze_athlete_button").click(event => 
		{
			let unfreezeRunner = new BordomorAjaxRunner(athleteManURI, "id=" + athlete.id + "&data_mode=2&update_mode=4");
			unfreezeRunner.init("post", true, "text");
			unfreezeRunner.setProcDialog("Sporcu Kaydı İptal Ediliyor");
			unfreezeRunner.setFailDialog();
			unfreezeRunner.setCallbacks(function() { window.location = window.location.href; });
			
			unfreezeRunner.run(responseErrorRegExp);
		});
		
		$(addedCard).find("button#unsubscribe_athlete_button").click(event => 
		{
			let unsubcscribeRunner = new BordomorAjaxRunner(athleteManURI, "id=" + athlete.id + "&data_mode=2&update_mode=5");
			unsubcscribeRunner.init("post", true, "text");
			unsubcscribeRunner.setProcDialog("Sporcu Kaydı İptal Ediliyor");
			unsubcscribeRunner.setFailDialog();
			unsubcscribeRunner.setCallbacks(function() { window.location = window.location.href; });
			
			unsubcscribeRunner.run(responseErrorRegExp);
		});
		
		$(addedCard).find("button#refresh_button").click(event => 
		{
			let parameters = "id=" + plan.id;
			let paymentPlanRunner = new BordomorAjaxRunner(paymentPlanDataURI, parameters);
			paymentPlanRunner.init();
			
			paymentPlanRunner.setCallbacks(function(isValid, response)
			{
				if(isValid)
				{
					$("div#payment_plan_details_card").remove();
					
					let planEl = response.getElementsByTagName("payment_plan")[0];
					let paymentPlan = new PaymentPlan();
					paymentPlan.parseFromXMLElement(planEl);
					
					displayPlanData(paymentPlan);
				}
				else
				{
					if(new RegExp(noDataErrorRegExp).test(response))
						$(addedCard).find("div#payment_plan_container").addClass("no_data");
					else
						makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
				}
			}, null);
			
			paymentPlanRunner.run(responseErrorRegExp);
		});
		
		$(addedCard).find("button#add_promotion_button").click(event =>
		{
			let parameters = "plan=" + plan.id;
			let promotionDataRunner = new BordomorAjaxRunner(promotionDataURI, parameters);
			promotionDataRunner.setProcDialog("Uygulanabilir İndirim Tanımları Alınıyor");
			promotionDataRunner.setFailDialog();
			promotionDataRunner.init();
			
			promotionDataRunner.setCallbacks(function(isValid, response)
			{
				if(isValid)
				{
					let promotions = [];
					let promotionEls = response.getElementsByTagName("promotion");
					
					for(let i = 0; i < promotionEls.length; i++)
					{
						let nextPromotion = new Promotion();
						nextPromotion.parseFromXMLElement(promotionEls[i]);
						promotions.push(nextPromotion);
					}
					
					makePromotionChoiceDialogue(plan, promotions);
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
			
			promotionDataRunner.run(responseErrorRegExp);
		});
		
		$(addedCard).find("p.promotion > button.remove_item").click(event =>
		{
			let parameters = "id=" + plan.id + "&promotion=" + $(event.target).data("id") + "&data_mode=2&update_mode=2";
			let promotionRemoverRunner = new BordomorAjaxRunner(paymentPlanManURI, parameters);
			promotionRemoverRunner.setProcDialog("İndirim Kaldırılıyor");
			promotionRemoverRunner.setFailDialog();
			promotionRemoverRunner.init("post", true, "text");
		
			promotionRemoverRunner.setCallbacks(function(isValid, response)
			{
				if(isValid)
					$("button#refresh_button").trigger("click");
				else
				{
					makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
				}
			}, null);
			
			promotionRemoverRunner.run(responseErrorRegExp);
		});
	}
}

function makePromotionChoiceDialogue(plan, promotions)
{
	let choiceHtml = "";
	choiceHtml += "<form>";
	choiceHtml += "<div>";
	choiceHtml += "<p><strong>PLANA UYGULANABİLİR İNDİRİM TANIMLARI</strong></p>";
	choiceHtml += "<div class='input_container select_container'>";
	choiceHtml += "<select id='promotion_select'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	choiceHtml += "<label>İndirim Tanımı</label>";
	choiceHtml += "</div>";
	choiceHtml += "</div>";
	
	let promotionDialogue = new BordomorDialogue("İNDİRİM TANIMLA", choiceHtml, "Uygula", "İptal");
	promotionDialogue.setButtonClickCallbacks(function(isValid, response)
	{
		let parameters = "id=" + plan.id + "&promotion=" + $("select#promotion_select").find("option:selected").val() + "&data_mode=2&update_mode=1" ;
		let promotionApplierRunner = new BordomorAjaxRunner(paymentPlanManURI, parameters);
		promotionApplierRunner.setProcDialog("İndirim Uygulanıyor");
		promotionApplierRunner.setFailDialog();
		promotionApplierRunner.init("post", true, "text");
		
		promotionApplierRunner.setCallbacks(function(isValid, response)
		{
			if(isValid)
				$("button#refresh_button").trigger("click");
			else
			{
				makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
			}
		}, null);
		
		promotionDialogue.close();
		promotionApplierRunner.run(responseErrorRegExp);
	}, function() { promotionDialogue.close(); });
	
	promotionDialogue.print();
	
	for(let i = 0; i < promotions.length; i++)
	{
		let nextText = promotions[i].title + " (Oran: " + promotions[i].discountRatio + " Miktar: " + promotions[i].discountAmount + " TL)";
		$("select#promotion_select").append($("<option>", { value: promotions[i].id, text: nextText}));
	}
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
	
	filterHtml += "<div style='margin-top: 20px'>";
	filterHtml += "<p><strong>EK ÖZELLİKLER</strong></p>";
	filterHtml += "<div class='input_container'><input type='text' placeholder=' ' required data-filter_attr='idNo' data-filter_set='val'/><label>T.C. Kimlik No</label></div>";
	filterHtml += "<div class='input_container'><input type='text' placeholder=' ' required data-filter_attr='school' data-filter_set='val'/><label>Okulu</label></div>";
	
	filterHtml += "<div class='inline_input_row'>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='allergy_checkbox' value='allergy' data-filter_attr='medicalData' data-filter_set='groupVal'/><label for='allergy_checkbox'>Alerji Bilgisi</label></div>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='therapy_checkbox' value='therapy' data-filter_attr='medicalData' data-filter_set='groupVal'/><label for='therapy_checkbox'>Tedavi Bilgisi</label></div>";
	filterHtml += "<div class='input_container no_height'><input type='checkbox' id='medication_checkbox' value='medication' data-filter_attr='medicalData' data-filter_set='groupVal'/><label for='medication_checkbox'>İlaç Bilgisi</label></div>";
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