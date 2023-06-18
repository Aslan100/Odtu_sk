var that = this;
var locations = [];

$(document).ready(function()
{
	$("table.data_table > tbody > tr").click(function(event)
	{
		$("table.data_table > tbody > tr").removeClass("selected_row");
		$("table.data_table > tbody > tr > td > div").removeClass("active");
		$(this).find("button#delete_location_button, button#edit_location_button").unbind("click");
		
		$(this).addClass("selected_row");
		$(this).find("td div").addClass("active");
		that.getLocationData($(this).data("code"));
		let id = $(this).data("code");
		
		$(this).find("button#delete_location_button").click(function(event)
		{
			event.stopPropagation();
			
			let confDialogue = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Devam etmek istediğinize emin misiniz?", "Tesisi Sil", "İptal");
			confDialogue.setChoiceCallbacks(function()
			{
				let deleteRunner = new BordomorAjaxRunner(locationManURI, "id=" + id + "&data_mode=0");
				deleteRunner.init("post", true, "text");
				deleteRunner.setProcDialog("Tesis Siliniyor");
				deleteRunner.setFailDialog();
				deleteRunner.setCallbacks(function() { window.location = window.location.href; });
						
				confDialogue.close();
				deleteRunner.run(responseErrorRegExp);
			}); 
			confDialogue.print();
		});
		
		$(this).find("button#edit_location_button").click(function(event)
		{
			event.stopPropagation();
			
			getCityData(function(isValid, response)
			{
				if(isValid)
				{
					let cities = that.parseCities(response);
					let formHtml = getLocationForm(that.locations[0]);
					let editDialogue = new BordomorDialogue("TESİS BİLGİSİ DÜZENLEME", formHtml, "Kaydet", "İptal Et");
					
					editDialogue.setButtonClickCallbacks(function() 
					{ 
						let editRunner = new BordomorAjaxRunner(locationManURI, $("form#location_form").serialize());
						editRunner.init("post", true, "text");
						editRunner.setProcDialog("Tesis Güncelleniyor");
						editRunner.setFailDialog();
						
						editRunner.setCallbacks(function() { window.location = window.location.href; });
						
						editDialogue.close();
						editRunner.run(responseErrorRegExp);
		  
					}, function() { editDialogue.close() });
					
					editDialogue.print(false, true);
					
					for(let i = 0; i < cities.length; i++)
						$("select#city_select").append($("<option>", { value: cities[i].id, text: cities[i].name}));
					
					$("select#city_select").change(event => 
					{
						$("select#district_select option:not(:disabled)").remove();
						let selectedCity = cities[$("select#city_select option:selected").index() - 1];
						
						for(let i = 0; i < selectedCity.districts.length; i++) 
							$("select#district_select").append($("<option>", { value: selectedCity.districts[i].id, text: selectedCity.districts[i].name}));
							
						$("select#district_select").prop("disabled", false);
					});
					
					$("input#name_input").val(that.locations[0].name);
					$("input#desc_input").val(that.locations[0].description);
					$("input#colour_code_picker").val(that.locations[0].representingColour);
					$("select#city_select").val(that.locations[0].address.city.id);
					$("select#city_select").trigger("change");
					
					if(that.locations[0].address.district != null)
						$("select#district_select").val(that.locations[0].address.district.id);
						
					$("input#address_input").val(that.locations[0].address.addressString);
					$("input#post_code_input").val(that.locations[0].address.postCode);
					$("input#latitude_input").val(that.locations[0].address.latitude > -1 ? that.locations[0].address.latitude : null);
					$("input#longitude_input").val(that.locations[0].address.longitude > -1 ? that.locations[0].address.longitude : null);
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
			});
		});
	});
	
	$("button#new_item").click(event => 
	{
		getCityData(function(isValid, response)
		{
			if(isValid)
			{
				let cities = that.parseCities(response);
				let formHtml = getLocationForm();
				let newDialogue = new BordomorDialogue("YENİ TESİS GİRİŞİ", formHtml, "Kaydet", "İptal Et");
				
				newDialogue.setButtonClickCallbacks(function() 
				{ 
					let newLocationRunner = new BordomorAjaxRunner(locationManURI, $("form#location_form").serialize());
					newLocationRunner.init("post", true, "text");
					newLocationRunner.setProcDialog("Tesis Tanımlanıyor");
					newLocationRunner.setFailDialog();
					
					newLocationRunner.setCallbacks(function() { window.location = window.location.href; });
					
					newDialogue.close();
					newLocationRunner.run(responseErrorRegExp);
	  
				}, function() { newDialogue.close() });
				
				newDialogue.print(false, true);
				
				$("input[type=text][mode=phonenumber]").trigger("input");
				
				for(let i = 0; i < cities.length; i++)
					$("select#city_select").append($("<option>", { value: cities[i].id, text: cities[i].name}));
					
				$("select#city_select").change(event => 
				{ 
					$("select#district_select option:not(:disabled)").remove();
					let selectedCity = cities[$("select#city_select option:selected").index() - 1];
					
					for(let i = 0; i < selectedCity.districts.length; i++) 
						$("select#district_select").append($("<option>", { value: selectedCity.districts[i].id, text: selectedCity.districts[i].name}));
						
					$("select#district_select").prop("disabled", false);
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
	});
	
	$("button#filters_button").click(function()
	{
		//makeFilterDialogue();
	});
	
	$("tr[data-is_selected_item='true']").trigger("click");
});

function getCityData(positiveCallback, negativeCallback)
{
	let runner = new BordomorAjaxRunner(cityDataURL, "include_districts=true");
	runner.init();
	runner.setProcDialog("Şehir Bilgileri Alınyor");
	runner.setFailDialog();
	runner.setCallbacks(positiveCallback, negativeCallback);
	runner.run(responseErrorRegExp);
}

function parseCities(response)
{
	let cityEls = response.getElementsByTagName("city");
	let cities = [];
				
	for(let i = 0; cityEls != null && i < cityEls.length; i++)
	{
		let nextCity = new City();
		nextCity.parseFromXMLElement(cityEls[i]);
		cities.push(nextCity);
	}
	
	return cities;
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

function getLocationForm(location)
{
	let formHtml = "";
	formHtml += "<form id='location_form' method='post'>";
	
	formHtml += "<header>";
	formHtml += "<figure></figure>";
	formHtml += "<section>";
	formHtml += "<div class='input_container'>";
	formHtml += "<label for='name_input'>Adı:</label>";
	formHtml += "<input type='text' id='name_input' name='name'/>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<label for='desc_input'>Detay:</label>";
	formHtml += "<input type='text' id='desc_input' name='description' placeholder=' '/>";
	formHtml += "</div>";
	formHtml += "<div class='input_container colour_container'>";
	formHtml += "<label for='desc_input'>Renk Kodu:</label>";
	formHtml += "<div>"
	formHtml += "<input type='color' id='colour_code_picker' name='colour_code' required/>";
	formHtml += "</div>";
	formHtml += "</div>";
	formHtml += "</section>";
	formHtml += "</header>";
	
	formHtml += "<section>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='city_select' name='city_id'><option value='-1' disabled='disabled' selected>Şehir</option></select>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='district_select' name='district_id' disabled='disabled'><option value='-1' disabled='disabled' selected>İlçe</option></select>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='address_input' name='address' placeholder=' '/>";
	formHtml += "<label for='address_input'>Adres</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='post_code_input' name='post_code' placeholder=' '/>";
	formHtml += "<label for='post_code_input'>Posta Kodu</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='latitude_input' name='latitude' placeholder=' '/>";
	formHtml += "<label for='latitude_input'>Enlem (Lat)</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='longitude_input' name='longitude' placeholder=' '/>";
	formHtml += "<label for='longitude_input'>Boylam (Lng)</label>";
	formHtml += "</div>";
	formHtml += "<input type='hidden' name='data_mode' value='" + (location == null ? 1 : 2) + "'>";
	
	if(location != null)
		formHtml += "<input type='hidden' name='id' value='" + location.id + "'>";
	
	formHtml += "</section>";	
		
	formHtml += "</form>";

	return formHtml;
}

function getLocationData(id)
{
	let parameters = "id=" + id;
	let runner = new BordomorAjaxRunner(locationDataURL, parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let locationEl = response.getElementsByTagName("location")[0];
			let fetchedLoc = new Location();
			fetchedLoc.parseFromXMLElement(locationEl);
			
			that.locations = [];
			that.locations.push(fetchedLoc);
			
			if(fetchedLoc.address.latitude > 0 && fetchedLoc.address.longitude > 0)
			{
				$("div#map").removeClass("loading no_data");
				initMap(fetchedLoc.address.latitude, fetchedLoc.address.longitude);
				$("div.map_controls").addClass("active");
			}
			else
			{
				$("div#map").removeClass("loading");
				$("div#map").addClass("no_data");
			}
			
			let percentages = [];
			$("div.stats_container div.indicator").removeClass("low");
			$("div.stats_container div.indicator").removeClass("moderate");
			
			for(let i = 0; fetchedLoc.weekTotals != null && i < fetchedLoc.weekTotals.length; i++)
				percentages.push(Math.round(100*fetchedLoc.weekTotals[i]/(18*60*60*1000)));
			
			if(fetchedLoc.weekTotals == null)
				percentages = [0, 0, 0, 0, 0, 0, 0];
			
			percentages.forEach((value, index) => 
			{
				$("div.stats_container div.indicator").eq(index).css("width", (value + "%"));
				$("div.stats_container span.percentage").eq(index).html("%" + value);
				
				if(value < 35)
					$("div.stats_container div.indicator").eq(index).addClass("low");
				else if(value >= 35 && value < 85)
					$("div.stats_container div.indicator").eq(index).addClass("moderate");
			});
			
			$("button#go_to_location_button").prop("disabled", false);
			$("button#go_to_location_button").click(event => window.location.href = "trainings.jsp?location=" + fetchedLoc.id);
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
			{
				let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Tesis Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
				{
					destroyBordomorFullScreenDialog(negativeDialog);
					negativeDialog = null;
				});
			}
			else
				makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
		}
	}, null);
	
	$("div#map").remove();
	$("div#map_container div.map_frame").append("<div id='map'></div>");
	$("div#map").addClass("loading");
	
	$("div.map_controls button").unbind("click");
	$("div.map_controls").removeClass("active");
			
	$("button#go_to_location_button").unbind("click");
	$("button#go_to_location_button").prop("disabled", "disabled");		
	runner.run(responseErrorRegExp);
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


/*Harita*/
var map, marker;
var zoomLevel = 16;
var mapStyles = [
	            {elementType: 'geometry', stylers: [{color: '#ebe3cd'}]},
	              {elementType: 'labels.text.fill', stylers: [{color: '#523735'}]},
	              {elementType: 'labels.text.stroke', stylers: [{color: '#f5f1e6'}]},
	              {
	                featureType: 'administrative',
	                elementType: 'geometry.stroke',
	                stylers: [{color: '#c9b2a6'}]
	              },
	              {
	                featureType: 'administrative.land_parcel',
	                elementType: 'geometry.stroke',
	                stylers: [{color: '#dcd2be'}]
	              },
	              {
	                featureType: 'administrative.land_parcel',
	                elementType: 'labels.text.fill',
	                stylers: [{color: '#ae9e90'}]
	              },
	              {
	                featureType: 'landscape.natural',
	                elementType: 'geometry',
	                stylers: [{color: '#dfd2ae'}]
	              },
	              {
	                featureType: 'poi',
	                elementType: 'geometry',
	                stylers: [{color: '#dfd2ae'}]
	              },
	              {
	                featureType: 'poi',
	                elementType: 'labels.text.fill',
	                stylers: [{color: '#93817c'}]
	              },
	              {
	                featureType: 'poi.park',
	                elementType: 'geometry.fill',
	                stylers: [{color: '#a5b076'}]
	              },
	              {
	                featureType: 'poi.park',
	                elementType: 'labels.text.fill',
	                stylers: [{color: '#447530'}]
	              },
	              {
	                featureType: 'road',
	                elementType: 'geometry',
	                stylers: [{color: '#f5f1e6'}]
	              },
	              {
	                featureType: 'road.arterial',
	                elementType: 'geometry',
	                stylers: [{color: '#fdfcf8'}]
	              },
	              {
	                featureType: 'road.highway',
	                elementType: 'geometry',
	                stylers: [{color: '#f8c967'}]
	              },
	              {
	                featureType: 'road.highway',
	                elementType: 'geometry.stroke',
	                stylers: [{color: '#e9bc62'}]
	              },
	              {
	                featureType: 'road.highway.controlled_access',
	                elementType: 'geometry',
	                stylers: [{color: '#e98d58'}]
	              },
	              {
	                featureType: 'road.highway.controlled_access',
	                elementType: 'geometry.stroke',
	                stylers: [{color: '#db8555'}]
	              },
	              {
	                featureType: 'road.local',
	                elementType: 'labels.text.fill',
	                stylers: [{color: '#806b63'}]
	              },
	              {
	                featureType: 'transit.line',
	                elementType: 'geometry',
	                stylers: [{color: '#dfd2ae'}]
	              },
	              {
	                featureType: 'transit.line',
	                elementType: 'labels.text.fill',
	                stylers: [{color: '#8f7d77'}]
	              },
	              {
	                featureType: 'transit.line',
	                elementType: 'labels.text.stroke',
	                stylers: [{color: '#ebe3cd'}]
	              },
	              {
	                featureType: 'transit.station',
	                elementType: 'geometry',
	                stylers: [{color: '#dfd2ae'}]
	              },
	              {
	                featureType: 'water',
	                elementType: 'geometry.fill',
	                stylers: [{color: '#9cc0f9'}]
	              },
	              {
	                featureType: 'water',
	                elementType: 'labels.text.fill',
	                stylers: [{color: '#92998d'}]
	              }
	          ];

function initMap(lat, lng)
{
	try
	{
		map = new google.maps.Map(document.getElementById("map"), 
		{
			center: {lat: parseFloat(lat), lng: parseFloat(lng)},
			zoom: zoomLevel,
			disableDefaultUI: true,
			styles: mapStyles
		});
		
		marker = new google.maps.Marker(
		{
		    position: {lat: parseFloat(lat), lng: parseFloat(lng)},
		    map: map,
		    title: "Konum Bilgisi",
		    draggable: false, 
	        animation: google.maps.Animation.DROP
		});
		
		map.setCenter(marker.getPosition());
		
		$("#zoom_map_in").click(function()
		{
			if(map && zoomLevel < 19)
			{
				map.setZoom(zoomLevel + 1);
				zoomLevel++;
			}
		});
	
		$("#zoom_map_out").click(function()
		{
			if(map && zoomLevel > 10)
			{
				map.setZoom(zoomLevel - 1);
				zoomLevel--;
			}
		});
	
		$("#open_big_map").click(function()
		{
			let mapUrl = window.open("https://www.google.com/maps?q=" + lat + "," + lng);
			
			if(mapUrl) 
			{
				mapUrl.focus();
			} else {
			    //Browser has blocked it
			    alert("Tarayıcı ayarlarınız bu işleme izin vermiyor. Açılabilir pencereler engelini kaldırıp, tekrar deneyin.");
			}
		});
	}
	catch(err)
	{
		$("div#map").addClass("no_data");
	}
}