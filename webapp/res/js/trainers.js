var that = this;
var trainers = [];

$(document).ready(function()
{
	$("table.data_table > tbody > tr").click(function(event)
	{
		$("table.data_table > tbody > tr").removeClass("selected_row");
		$(this).addClass("selected_row");
		that.trainers = [];
		that.getTrainerData($(this).data("code"));
	});
	
	$("button#new_item").click(event => { getRequiredDataAndDisplayForm(); });
	$("button#filters_button").click(function() { makeFilterDialogue(); });
});
	
function getRequiredDataAndDisplayForm(trainer)
{
	let runner = new BordomorAjaxRunner(branchDataURL);
	runner.init();
	runner.setProcDialog("Branş Bilgileri Alınyor");
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let branchEls = response.getElementsByTagName("branch"); 
			let cityRunner = new BordomorAjaxRunner(cityDataURL, "include_districts=true");
			cityRunner.init();
			cityRunner.setProcDialog("Şehir Bilgileri Alınyor");
			cityRunner.setFailDialog();
			
			cityRunner.setCallbacks(function(isValid, response)
			{
				if(isValid)
				{
					let formHtml = getTrainerFormHtml(trainer);
					let newDialogue = new BordomorDialogue(trainer != null ? "ANTRENÖR BİLGİLERİ GÜNCELLEME": "YENİ ANTRENÖR GİRİŞİ", formHtml, trainer != null ? "Güncelle" : "Kaydet", "İptal Et");
					
					newDialogue.setButtonClickCallbacks(function() 
					{ 
						let newTrainerRunner = new BordomorAjaxRunner(trainerManURI, $("form#trainer_form").serialize());
						newTrainerRunner.init("post", true, "text");
						newTrainerRunner.setProcDialog(trainer != null ? "Antrenör Hesabı Güncelleniyor" : "Antrenör Hesabı Tanımlanıyor");
						newTrainerRunner.setFailDialog();
						
						newTrainerRunner.setCallbacks(function() { window.location = window.location.href; });
						
						newDialogue.close();
						newTrainerRunner.run(responseErrorRegExp);
					}, function() { newDialogue.close() });
					
					newDialogue.print(false, true);
					
					let cityEls = response.getElementsByTagName("city");
					let cities = [];
					
					for(let i = 0; i < cityEls.length; i++)
					{
						let nextCity = new City();
						nextCity.parseFromXMLElement(cityEls[i]);
						cities.push(nextCity);
					}
					
					initializeInputs(branchEls, cities, trainer);
					initializeSpecialInputs();
				}
				else
				{
					if(new RegExp(noDataErrorRegExp).test(response))
					{
						let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Şehir Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
						{
							destroyBordomorFullScreenDialog(negativeDialog);
							negativeDialog = null;
						});
					}
					else
						new BordomorInfoDialogue("error", "Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.").print(dialogDisplayDurationMsec);
				}
			});
			
			cityRunner.run(responseErrorRegExp);
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
			{
				let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Branş Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
				{
					destroyBordomorFullScreenDialog(negativeDialog);
					negativeDialog = null;
				});
			}
			else
				new BordomorInfoDialogue("error", "Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.").print(dialogDisplayDurationMsec);
		}
	});
	
	runner.run(responseErrorRegExp);
}	
			
function initializeInputs(branchEls, cities, trainer)
{
	for(let i = 0; i < branchEls.length; i++)
		$("select#branch_select").append($("<option>", { value: branchEls[i].getAttribute("text"), text: branchEls[i].getAttribute("name")}));
	
	for(let i = 0; i < cities.length; i++)
	{
		$("select#city_select").append($("<option>", { value: cities[i].id, text: cities[i].name, "selected": false}));
		$("select#address_city_select").append($("<option>", { value: cities[i].id, text: cities[i].name}));
	}
	
	for(let i = 0; i < bloodTypes.length; i++)
		$("select#blood_type_select").append($("<option>", { value: bloodTypes[i], text: bloodTypeVals[i]}));
	
	for(let i = 5; i >= 1; i--)
		$("select#level_select").append($("<option>", { value: i, text: i + ". Seviye"}));
		
	for(let i = 0; i < trainerLabels.length; i++)
		$("select#label_select").append($("<option>", { value: trainerLabels[i], text: trainerLabelVals[i]}));
		
	for(let i = 0; i < genders.length; i++)
		$("select#gender_select").append($("<option>", { value: genders[i], text: genderVals[i]}));	
	
	$("select#address_city_select").change(event =>
	{
		let selectedOpt = $("select#address_city_select").find("option:selected").index();
		let selectedCity = cities[selectedOpt - 1];
		
		$("select#address_district_select option:not(:disabled)").remove();
		
		for(let i = 0; i < selectedCity.districts.length; i++)
			$("select#address_district_select").append($("<option>", { value: selectedCity.districts[i].id, text: selectedCity.districts[i].name}));
	});
	
	$("form#trainer_form > header > div.pager > button").click(function() 
	{ 
		let shiftCoeff = $(this).index() * -1;
		$(this).siblings().removeClass("selected");
		$(this).addClass("selected");
		$("form.wide_mode.paged_mode > section").each(function() { $(this).css("left", (shiftCoeff*410) + "px") }); 
	});
	
	if(trainer != null)
	{
		$("form#trainer_form input#name_input").val(trainer.loginable.name);
		$("form#trainer_form input#surname_input").val(trainer.loginable.surname);
		$("form#trainer_form input#email_input").val(trainer.loginable.email);
		$("form#trainer_form select#branch_select").val(trainer.primaryBranch);
		$("form#trainer_form select#label_select").val(trainer.label);
		$("form#trainer_form select#level_select").val(trainer.level);
		$("form#trainer_form input#phone_number_input").val(trainer.loginable.phoneNumber);
		$("form#trainer_form input#hes_code_input").val(trainer.loginable.hesCode);
		
		$("form#trainer_form input#id_no_input").val(trainer.idNo);
		$("form#trainer_form input#birth_date_input").val(trainer.birthDate);
		$("form#trainer_form select#city_select").val(trainer.placeOfBirth.id);
		$("form#trainer_form input#mothers_name_input").val(trainer.mothersName);
		$("form#trainer_form input#fathers_name_input").val(trainer.fathersName);
		
		if(trainer.homeLocation != null && trainer.homeLocation.address != null)
		{
			$("form#trainer_form select#address_city_select").val(trainer.homeLocation.address.city.id);
			$("form#trainer_form select#address_city_select").trigger("change");
			$("form#trainer_form select#address_district_select").val(trainer.homeLocation.address.district != null ? trainer.homeLocation.address.district.id : "-1");
			$("form#trainer_form textarea#address_input").val(trainer.homeLocation.address.addressString != null ? trainer.homeLocation.address.addressString : "");
			$("form#trainer_form input#latitude_input").val(trainer.homeLocation.address.latitude != null ? trainer.homeLocation.address.latitude : "");
			$("form#trainer_form input#longitude_input").val(trainer.homeLocation.address.longitude != null ? trainer.homeLocation.address.longitude : "");
		}
		
		$("form#trainer_form select#gender_select").val(trainer.loginable.gender != null ? trainer.loginable.gender : "-1");
		$("form#trainer_form input#height_input").val(trainer.height > 0 ? trainer.height : "");
		$("form#trainer_form input#weight_input").val(trainer.weight > 0 ? trainer.weight : "");
		$("form#trainer_form select#blood_type_select").val(trainer.bloodType != null ? trainer.bloodType : "-1");
	}
}
			
function initializeSpecialInputs()
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
	
	$("input[type=text][mode=phonenumber]").on("input", event => 
	{
		$(event.target).val($(event.target).val().replace(/[^0-9\+\(\)_\s]/g, ""));
		let strippedVal = $(event.target).val().replace(/[^0-9]/g, "");
		
		strippedVal = strippedVal.substring(0, 4) == "0090" ? "90" + strippedVal.substring(4) : strippedVal;
		strippedVal = strippedVal.substring(0, 2) == "00" ? "90" + strippedVal.substring(2) : strippedVal;
		strippedVal = strippedVal.substring(0, 1) == "0" ? "9" + strippedVal : strippedVal; 
			
		if(strippedVal.length > 10)
		{
			let part1 = strippedVal.substring(0, 2);
			let part2 = strippedVal.substring(2, 5);
			let part3 = strippedVal.substring(5, 8);
			let part4 = strippedVal.substring(8, 10);
			let part5 = strippedVal.substring(10, 12);
			
			$(event.target).val("+" + part1 + " (" + part2 + ") " + part3 + " " + part4 + " " + part5);
		}
		else if(strippedVal.length > 8)
		{
			let part1 = strippedVal.substring(0, 2);
			let part2 = strippedVal.substring(2, 5);
			let part3 = strippedVal.substring(5, 8);
			let part4 = strippedVal.substring(8);
			
			$(event.target).val("+" + part1 + " (" + part2 + ") " + part3 + " " + part4);
		}
		else if(strippedVal.length > 5)
		{
			let part1 = strippedVal.substring(0, 2);
			let part2 = strippedVal.substring(2, 5);
			let part3 = strippedVal.substring(5);
			
			$(event.target).val("+" + part1 + " (" + part2 + ") " + part3);
		}
		else if(strippedVal.length > 2)
		{
			let part1 = strippedVal.substring(0, 2);
			let part2 = strippedVal.substring(2);
			
			$(event.target).val("+" + part1 + " (" + part2);
		}
		else if(strippedVal.length > 0)
			$(event.target).val("+" + strippedVal);
	});
	
	$("input[type=text][mode=phonenumber]").trigger("input");
}

function getTrainerFormHtml(trainer)
{
	let formHtml = "";
	formHtml += "<form id='trainer_form' method='post' class='wide_mode paged_mode'>";
	
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
	
	for(let i = 0; i < 3; i++)
		formHtml += "<button type='button' class='clear_style" + (i == 0 ? " selected" : "") + "'></button>";
	
	formHtml += "</div>";
	formHtml += "</header>";
	
	formHtml += "<section>";
	formHtml += "<h3>TEMEL BİLGİLER</h3>";
	formHtml += "<div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='email_input' name='email' placeholder=' ' required/>";
	formHtml += "<label for='email_input'>E-posta Adresi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='branch_select' name='primary_branch'><option value='-1' disabled='disabled' selected></option></select>";
	formHtml += "<label for='branch_select'>Branş</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='label_select' name='label'>><option value='-1' disabled='disabled' selected></select>";
	formHtml += "<label for='label_select'>Ünvan</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='level_select' name='level'><option value='-1' disabled='disabled' selected></option></select>";
	formHtml += "<label for='level_select'>Seviye</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' mode='phonenumber' id='phone_number_input' name='phone_number' placeholder=' ' required/>";
	formHtml += "<label for='phone_number_input'>Telefonu</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>KİŞİSEL BİLGİLER</h3>";
	formHtml += "<div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input id='id_no_input' type='text' name='id_no'placeholder=' ' maxlength='11' required/>";
	formHtml += "<label for='fathers_name_input'>T.C. Kimlik No</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' mode='datepicker' id='birth_date_input' name='birth_date' mode='datepicker' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='birth_date_input'>Doğum Tarihi</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='city_select' name='place_of_birth'><option value='-1' disabled='disabled' selected></option></select>";
	formHtml += "<label for='city_select'>Doğum Yeri</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input id='mothers_name_input' type='text' name='mothers_name' placeholder=' ' maxlength='50'/>";
	formHtml += "<label for='mothers_name_input'>Anne Adı</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input id='fathers_name_input' type='text' name='fathers_name' placeholder=' ' maxlength='50'/>";
	formHtml += "<label for='fathers_name_input'>Baba Adı</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>ADRES BİLGİLERİ</h3>";
	formHtml += "<div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='address_city_select' name='address_city'><option value='-1' disabled='disabled' selected></select>";
	formHtml += "<label for='address_city_select'>Şehir</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='address_district_select' name='address_district'><option value='-1' disabled='disabled' selected></select>";
	formHtml += "<label for='addres_district_select'>İlçe</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container no_height'>";
	formHtml += "<textarea id='address_input' name='address' placeholder=' '></textarea>";
	formHtml += "<label for='address_input'>Adres</label>";
	formHtml += "</div>";
	formHtml += "<div class='inline_input_row equal_size_inputs'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='latitude_input' name='latitude' placeholder=' ' min='0' max='180'/>";
	formHtml += "<label for='latitude_input'>Enlem (Lat)</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='longitude_input' name='longitude' placeholder=' ' min='0' max='180'/>";
	formHtml += "<label for='longitude_input'>Boylam (Lng)</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>FİZİKSEL ÖZELLİKLER</h3>";
	formHtml += "<div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='gender_select' name='gender'><option value='-1' disabled='disabled' selected></option></select>";
	formHtml += "<label for='gender_select'>Cinsiyet</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='height_input' name='height' placeholder=' ' min='0' max='250'/>";
	formHtml += "<label for='height_input'>Boy (cm)</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='weight_input' name='weight' placeholder=' ' min='0' max='150'/>";
	formHtml += "<label for='height_input'>Kilo (kg)</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='blood_type_select' name='blood_type' placeholder=' ' required><option value='-1' disabled='disabled' selected></option></select>";
	formHtml += "<label for='blood_type_select'>Kan Grubu</label>";
	formHtml += "</div>";
	formHtml += "<input type='hidden' name='data_mode' value='" + (trainer != null ? 2 : 1) + "'>";
	
	if(trainer != null)
		formHtml += "<input type='hidden' name='id' value='" + trainer.loginable.loginableId + "'>";
	
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</section>";
	formHtml += "</form>";

	return formHtml;
}

function getTrainerData(code)
{
	let parameters = "code=" + code;
	let runner = new BordomorAjaxRunner(trainerDataURL, parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let trainerEl = response.getElementsByTagName("trainer")[0];
			let fetchedTrainer = new Trainer();
			fetchedTrainer.parseFromXMLElement(trainerEl);
			
			that.displayFetchedData(fetchedTrainer);
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

function displayFetchedData(trainer)
{
	let trainerExists = false;
	
	for(let i = 0; i < trainers.length; i++)
	{
		if(trainers[i].id == trainer.id)
		{
			trainerExists = true;
			break;
		}
	}
	
	if(!trainerExists)
	{
		this.trainers.push(trainer);
		addAthleteCard(trainer);
	}
}

function addAthleteCard(trainer)
{
	let cardHtml = trainer.getCard();
	$("div.data_card *").remove();
	$("div.data_card").append(trainer.getCard());
	$("div.data_card").addClass("displayed");
	
	let addedCard = $("div.data_card").find("div.athlete_card[data-id='" + trainer.loginable.loginableId + "']");
	 
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
						
			for(let i = 0; i < trainers.length; i++)
			{	
				if(trainers[i].loginable.loginableId == id)
					that.trainers.splice(i, 1);
			}
		}, transitionDuration);
	});
	
	$(addedCard).find("button#delete_item_button").click(function(event)
	{
		let confDialogue = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Devam etmek istediğinize emin misiniz?", "Antrenörü Sil", "İptal");
		confDialogue.setChoiceCallbacks(function()
		{
			let deleteRunner = new BordomorAjaxRunner(trainerManURI, "id=" + trainer.id + "&data_mode=0");
			deleteRunner.init("post", true, "text");
			deleteRunner.setProcDialog("Antrenör Hesabı Siliniyor");
			deleteRunner.setFailDialog();
			deleteRunner.setCallbacks(function() { window.location = window.location.href; });
					
			confDialogue.close();
			deleteRunner.run(responseErrorRegExp);
		}); 
		confDialogue.print();
	});
	
	$(addedCard).find("button#edit_item_button").click(function(event)
	{
		getRequiredDataAndDisplayForm(trainer);
	});
	
	$(addedCard).find("button#fetch_licences_button").click(function(event)
	{
		$("div#licence_data_container").children().remove();
		$("div#licence_data_container").removeClass("placeholder");
		$("div#licence_data_container").addClass("loading");
		
		let parameters = "code=" + trainer.loginable.code;
		let licenceRunner = new BordomorAjaxRunner(loginableLicenceDataURI, parameters);
		licenceRunner.init();
		
		licenceRunner.setCallbacks(function(isValid, response)
		{
			$(addedCard).find("div#licence_data_container").removeClass("loading");
			$(addedCard).find("div#licence_data_container").addClass("pager");
			
			if(isValid)
			{
				let licenceEls = response.getElementsByTagName("licence");
				let lifeguardLicenceEls = response.getElementsByTagName("lifeguard_licence");
				
				let licenceHtml = "<div class='controls'><button class='clear_style'></button><button class='clear_style'></button></div>";
				
				let index = 0;
				trainer.loginable.licences = [];
				
				for(let i = 0; licenceEls != null && i < licenceEls.length; i++)
				{
					index++;
					let nextLicence = new Licence();
					nextLicence.parseFromXMLElement(licenceEls[i]);
					licenceHtml += nextLicence.getTable(index);
					trainer.loginable.licences.push(nextLicence);
				}
				
				for(let i = 0; lifeguardLicenceEls != null && i < lifeguardLicenceEls.length; i++)
				{
					index++;
					let nextLicence = new Licence();
					nextLicence.parseFromXMLElement(lifeguardLicenceEls[i]);
					licenceHtml += nextLicence.getTable(index);
					trainer.loginable.licences.push(nextLicence);
				}
				
				licenceHtml += "<table class='new_items_table'>";
				licenceHtml += "<tr><td style='text-align: center; border-bottom: none;'>";
				licenceHtml += "<img src='res/visual/icon/licence_small.png'/>";
				licenceHtml += "<p>Yeni Lisans Ekle</p>";
				licenceHtml += "</td></tr>";
				licenceHtml += "<tr><td style='text-align: center'>";
				licenceHtml += "<button type='button' id='new_licence_button' class='dark'>Antrenör</button>";
				licenceHtml += "<button type='button' id='new_lifeguard_licence_button' class='dark'>Cankurtaran</button>";
				licenceHtml += "</td></tr>";
				licenceHtml += "</table>";
				
				$("div.athlete_card div.data div#licence_data_container").append(licenceHtml);
				initializeTablePagers($("div#licence_data_container"));
				
				$("div.data > div.pager > table button[mode='delete']").click(event =>
				{
					let confDialogue = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Devam etmek istediğinize emin misiniz?", "Lisansı Sil", "İptal");
					confDialogue.setChoiceCallbacks(function()
					{
						let clickedDocId = $(event.target).parent().parent().parent().parent().data("document_id");
						let deleteDocumentRunner = new BordomorAjaxRunner(documentManURI, "data_mode=0&id=" + clickedDocId);
						deleteDocumentRunner.init("post", true, "text");
						deleteDocumentRunner.setProcDialog("Lisans Siliniyor");
						deleteDocumentRunner.setFailDialog();
						
						deleteDocumentRunner.setCallbacks(function() { window.location = window.location.href; });
						
						confDialogue.close();
						deleteDocumentRunner.run(responseErrorRegExp);
					});
					confDialogue.print();
				});
				
				$("div.data > div.pager > table button[mode='edit']").click(event =>
				{
					let clickedDocId = $(event.target).parent().parent().parent().parent().data("document_id");
					let licence = trainer.loginable.findLicence(parseInt(clickedDocId));
					let formHtml = getLicenceForm(trainer, licence.isLifeguardLicence, licence);
					let editDialogue = new BordomorDialogue("LİSANS BİLGİLERİ DÜZENLEME", formHtml, "Güncelle", "İptal Et");
				
					editDialogue.setButtonClickCallbacks(function() 
					{ 
						let editDocumentRunner = new BordomorAjaxRunner(documentManURI, $("form#licence_form").serialize());
						editDocumentRunner.init("post", true, "text");
						editDocumentRunner.setProcDialog("Lisans Tanımlanıyor");
						editDocumentRunner.setFailDialog();
						
						editDocumentRunner.setCallbacks(function() { window.location = window.location.href; });
						
						editDialogue.close();
						editDocumentRunner.run(responseErrorRegExp);
					}, function() { editDialogue.close() });
				
					editDialogue.print(false, true);
					
					for(let i = 0; i < branches.length; i++)
						$("select#branch_select").append($("<option>", { value: branches[i], text: branchVals[i]}));
						
					$("input#document_no_input").val(licence.documentNo);
					$("select#branch_select").val(licence.licenceBranch);
					$("input#valid_from_input").val(licence.validFrom);
					$("input#valid_until_input").val(licence.validUntil);
					
					initializeSpecialInputs();
				});
			}
			else
			{
				if(new RegExp(noDataErrorRegExp).test(response))
				{
					licenceHtml = "<table class='new_items_table'>";
					licenceHtml += "<tr><td style='text-align: center; border-bottom: none;'>";
					licenceHtml += "<img src='res/visual/icon/licence_small.png'/>";
					licenceHtml += "<p>Yeni Lisans Ekle</p>";
					licenceHtml += "</td></tr>";
					licenceHtml += "<tr><td style='text-align: center'>";
					licenceHtml += "<button type='button' id='new_licence_button' class='dark'>Antrenör</button>";
					licenceHtml += "<button type='button' id='new_lifeguard_licence_button' class='dark'>Cankurtaran</button>";
					licenceHtml += "</td></tr>";
					licenceHtml += "</table>";
					
					$("div.athlete_card div.data div#licence_data_container").append(licenceHtml);
				}
				else
					makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
			}
			
			$("button#new_licence_button, button#new_lifeguard_licence_button").click(event => 
			{
				let formHtml = getLicenceForm(trainer, $(event.target).prop("id").indexOf("lifeguard") >= 0, null);
				let newDialogue = new BordomorDialogue("YENİ LİSANS GİRİŞİ", formHtml, "Kaydet", "İptal Et");
				
				newDialogue.setButtonClickCallbacks(function() 
				{ 
					let newDocumentRunner = new BordomorAjaxRunner(documentManURI, $("form#licence_form").serialize());
					newDocumentRunner.init("post", true, "text");
					newDocumentRunner.setProcDialog("Lisans Tanımlanıyor");
					newDocumentRunner.setFailDialog();
					
					newDocumentRunner.setCallbacks(function() { window.location = window.location.href; });
					
					newDialogue.close();
					newDocumentRunner.run(responseErrorRegExp);
				}, function() { newDialogue.close() });
			
				newDialogue.print(false, true);
				
				initializeSpecialInputs();
				
				for(let i = 0; i < branches.length; i++)
					$("select#branch_select").append($("<option>", { value: branches[i], text: branchVals[i]}));
			});
		}, null);
		
		licenceRunner.run(responseErrorRegExp);
	});
	
	$(addedCard).find("button#fetch_medical_data_button").click(function(event)
	{
		$("div#medical_data_container").children().remove();
		$("div#medical_data_container").removeClass("placeholder");
		$("div#medical_data_container").addClass("loading");
		
		let parameters = "code=" + trainer.loginable.code;
		let medicalRunner = new BordomorAjaxRunner(loginableMedicalDataURI, parameters);
		medicalRunner.init();
		
		medicalRunner.setCallbacks(function(isValid, response)
		{
			$(addedCard).find("div#medical_data_container").removeClass("loading");
			
			if(isValid)
			{
				let medicalEl = response.getElementsByTagName("medical_data")[0];
				trainer.loginable.medicalData = new MedicalData();
				trainer.loginable.medicalData.parseFromXMLElement(medicalEl);
				
				$("div.athlete_card div.data div#medical_data_container").after(trainer.loginable.medicalData.getTable());
				$("div#medical_data_container").remove();
				
				$("div.data table button[mode='delete']").click(event =>
				{
					let confDialogue = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Devam etmek istediğinize emin misiniz?", "Sağlık Bilgilerini Sil", "İptal");
					confDialogue.setChoiceCallbacks(function()
					{
						let deleteMedicalsRunner = new BordomorAjaxRunner(medicalDataManURI, "data_mode=0&loginable=" + trainer.loginable.loginableId);
						deleteMedicalsRunner.init("post", true, "text");
						deleteMedicalsRunner.setProcDialog("Sağlık Bilgileri Siliniyor");
						deleteMedicalsRunner.setFailDialog();
						
						deleteMedicalsRunner.setCallbacks(function() { window.location = window.location.href; });
						
						confDialogue.close();
						deleteMedicalsRunner.run(responseErrorRegExp);
					});
					confDialogue.print();
				});
				
				$("div.data table button[mode='edit']").click(event =>
				{
					let formHtml = getMedicalsForm(trainer, trainer.loginable.medicalData);
					let editDialogue = new BordomorDialogue("SAĞLIK BİLGİLERİ DÜZENLEME", formHtml, "Güncelle", "İptal Et");
				
					editDialogue.setButtonClickCallbacks(function() 
					{ 
						let editMedicalsRunner = new BordomorAjaxRunner(medicalDataManURI, $("form#medicals_form").serialize());
						editMedicalsRunner.init("post", true, "text");
						editMedicalsRunner.setProcDialog("Lisans Tanımlanıyor");
						editMedicalsRunner.setFailDialog();
						
						editMedicalsRunner.setCallbacks(function() { window.location = window.location.href; });
						
						editDialogue.close();
						editMedicalsRunner.run(responseErrorRegExp);
					}, function() { editDialogue.close() });
				
					editDialogue.print(false, true);
					
					$("textarea#past_therapies_input").val(trainer.loginable.medicalData.pastTherapies);
					$("textarea#active_issues_input").val(trainer.loginable.medicalData.activeIssues);
					$("textarea#active_medications_input").val(trainer.loginable.medicalData.activeMedications);
					$("textarea#special_care_needs_input").val(trainer.loginable.medicalData.specialCareNeeds);
					
					initializeSpecialInputs();
				});
			}
			else
			{
				if(new RegExp(noDataErrorRegExp).test(response))
				{
					let medicalHtml = "";
					medicalHtml += "<table class='new_items_table'>";
					medicalHtml += "<tr><td style='text-align: center; border-bottom: none;'>";
					medicalHtml += "<img src='res/visual/icon/medical_small.png'/>";
					medicalHtml += "<p>Medikal Bilgi Ekle</p>";
					medicalHtml += "</td></tr>";
					medicalHtml += "<tr><td style='text-align: center'>";
					medicalHtml += "<button type='button' id='new_medicals_button' class='dark'>Oluştur</button>";
					medicalHtml += "</td></tr>";
					medicalHtml += "</table>";
					
					$("div.athlete_card div.data").append(medicalHtml);
				}
				else
					makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
			}
			
			$("button#new_medicals_button").click(event => 
			{
				let formHtml = getMedicalsForm(trainer, null);
				let newDialogue = new BordomorDialogue("YENİ SAĞLIK BİLGİSİ GİRİŞİ", formHtml, "Kaydet", "İptal Et");
				
				newDialogue.setButtonClickCallbacks(function() 
				{ 
					let newMedicalsRunner = new BordomorAjaxRunner(medicalDataManURI, $("form#medicals_form").serialize());
					newMedicalsRunner.init("post", true, "text");
					newMedicalsRunner.setProcDialog("Sağlık Bilgileri Tanımlanıyor");
					newMedicalsRunner.setFailDialog();
					
					newMedicalsRunner.setCallbacks(function() { window.location = window.location.href; });
					
					newDialogue.close();
					newMedicalsRunner.run(responseErrorRegExp);
				}, function() { newDialogue.close() });
			
				newDialogue.print(false, true);
			});
		}, null);
		
		medicalRunner.run(responseErrorRegExp);
	});
}

function getLicenceForm(trainer, lifeguardLicence, licence)
{
	let formHtml = "";
	formHtml += "<form id='licence_form' method='post'>";
	formHtml += "<div style='padding: 15px;'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='document_no_input' name='document_no' placeholder=' ' required/>";
	formHtml += "<label for='document_no_input'>Lisas No</label>";
	formHtml += "</div>";
	
	if(!lifeguardLicence)
	{
		formHtml += "<div class='input_container select_container'>";
		formHtml += "<select id='branch_select' name='licence_branch'><option val='-1' disabled='disabled' selected='selected'></option></select>";
		formHtml += "<label for='branch_select'>Lisans Branşı</label>";
		formHtml += "</div>";
	}
	
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' mode='datepicker' id='valid_from_input' name='valid_from' mode='datepicker' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='valid_from_input'>Geçerlilik Başlangıç Tarihi</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' mode='datepicker' id='valid_until_input' name='valid_until' mode='datepicker' placeholder=' ' maxlength='10' required/>";
	formHtml += "<label for='valid_until_input'>Geçerlilik Bitiş Tarihi</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "<input type='hidden' name='data_mode' value='" + (licence != null ? 2 : 1) + "'>";
	
	if(licence == null)
	{
		formHtml += "<input type='hidden' name='document_type' value='" + (!lifeguardLicence ? 0 : 1) + "'>";
		formHtml += "<input type='hidden' name='owner_loginable' value='" + trainer.loginable.loginableId + "'>";
	}
	
	if(licence != null)
		formHtml += "<input type='hidden' name='id' value='" + licence.documentId + "'>";
		
	formHtml += "</form>";
	
	return formHtml;
}

function getMedicalsForm(trainer, medicalData)
{
	let formHtml = "";
	formHtml += "<form id='medicals_form' method='post'>";
	formHtml += "<div style='padding: 15px;'>";
	formHtml += "<div class='input_container no_height'>";
	formHtml += "<textarea id='past_therapies_input' name='past_therapies' placeholder=' ' required></textarea>";
	formHtml += "<label for='past_therapies_input'>Geçmiş Tedaviler</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container no_height'>";
	formHtml += "<textarea id='active_issues_input' name='active_issues' placeholder=' ' required></textarea>";
	formHtml += "<label for='active_issues_input'>Etkin Durumlar ve Allerjiler</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container no_height'>";
	formHtml += "<textarea id='active_medications_input' name='active_medications' placeholder=' ' required></textarea>";
	formHtml += "<label for='active_medications_input'>Kullanmakta Olduğu İlaçlar</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container no_height'>";
	formHtml += "<textarea id='special_care_needs_input' name='special_care_needs' placeholder=' ' required></textarea>";
	formHtml += "<label for='special_care_needs_input'>Özel İlgi Gerektiren Durumlar</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "<input type='hidden' name='data_mode' value='" + (medicalData != null ? 2 : 1) + "'>";
	formHtml += "<input type='hidden' name='loginable' value='" + trainer.loginable.loginableId + "'>";
		
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
	filterHtml += "<p><strong>DERECE</strong></p>";
	filterHtml += "<div class='inline_input_row'>";
	
	trainerLabels.forEach(function(rank, index) 
	{
		let rankVal = trainerLabelVals[index];
		
		if(index == 3)
		{
			filterHtml += "</div>";
			filterHtml += "<div class='inline_input_row'>";
		}
		
		filterHtml += "<div class='input_container no_height'><input type='checkbox' id='" + rank.toLowerCase() +"_checkbox' value='" + rank + "' data-filter_attr='rank' data-filter_set='groupVal'/><label for='" + rank.toLowerCase() +"_checkbox'>" + rankVal + "</label></div>";	
	});
	
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
		let compatibleTrainers = [];
		
		let trainersRunner = new BordomorAjaxRunner(trainerDataURL);
		trainersRunner.init();
		trainersRunner.setProcDialog("Filtre Hesaplanıyor");
		trainersRunner.setFailDialog();
		trainersRunner.setCallbacks(function(isValid, response)
		{
			if(isValid)
			{
				$("table#trainers_table tr").removeClass("filter_out");
				setTablePagination("table#trainers_table");
				
				let trainerEls = response.getElementsByTagName("trainer");
					
				for(let i = 0; i < trainerEls.length; i++)
				{
					let nextTrainer = new Trainer();
					nextTrainer.parseFromXMLElement(trainerEls[i]);
					
					if(nextTrainer.compliesWith(filterObj))
						compatibleTrainers.push(nextTrainer);
					else
						$("table#trainers_table tr[data-code='" + nextTrainer.loginable.code + "']").addClass("filter_out");
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
				
				setTablePagination("table#trainers_table");
				$("table#trainers_table").find("tfoot td.controls span#filter_data").html(filterDescHtml(filterObj));
				
				$("button#clear_filters_button").click(function()
				{
					filterObj = null;
					$("table#trainers_table tbody tr").removeClass("filter_out");
					setTablePagination("table#trainers_table");
					
					$(this).parent().html("");
				});
			}
			else
				alert("Bad data");
		});
		
		filterObj = getFilterObj();
		filterDialogue.close();
		trainersRunner.run(responseErrorRegExp);
		
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