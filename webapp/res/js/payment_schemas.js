var that = this;
var locations = [];

$(document).ready(function()
{
	$("table.data_table > tbody > tr").click(function(event)
	{
		$("table.data_table > tbody > tr").removeClass("selected_row");
		$("table.data_table > tbody > tr > td > div").removeClass("active");
		$(this).find("button#delete_schema_button, button#edit_schema_button").unbind("click");
		
		$(this).addClass("selected_row");
		$(this).find("td div").addClass("active");
		let id = $(this).data("id");
		
		$(this).find("button#delete_schema_button").click(function(event)
		{
			let confDialogue = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Devam etmek istediğinize emin misiniz?", "Plan Şemasını Sil", "İptal");
			confDialogue.setChoiceCallbacks(function()
			{
				let deleteRunner = new BordomorAjaxRunner(paymentSchemaManURI, "id=" + id + "&data_mode=0");
				deleteRunner.init("post", true, "text");
				deleteRunner.setProcDialog("Plan Şeması Siliniyor");
				deleteRunner.setFailDialog();
				deleteRunner.setCallbacks(function() { window.location = window.location.href; });
						
				confDialogue.close();
				deleteRunner.run(responseErrorRegExp);
			});
			
			confDialogue.print();
		});
		
		$(this).find("button#edit_schema_button").click(function(event)
		{
			/*event.stopPropagation();*/
		});
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
				let formHtml = getPaymentSchemaForm();
				let newDialogue = new BordomorDialogue("YENİ ÖDEME ŞEMASI GİRİŞİ", formHtml, "Kaydet", "İptal Et");
				
				newDialogue.setButtonClickCallbacks(function() 
				{ 
					let newSchemaRunner = new BordomorAjaxRunner(paymentSchemaManURI, $("form#payment_schema_form").serialize());
					newSchemaRunner.init("post", true, "text");
					newSchemaRunner.setProcDialog("Ödeme Şeması Tanımlanıyor");
					newSchemaRunner.setFailDialog();
					
					newSchemaRunner.setCallbacks(function() { window.location = window.location.href; });
					
					newDialogue.close();
					newSchemaRunner.run(responseErrorRegExp);
		  
				}, function() { newDialogue.close() });
				
				newDialogue.print(false, true);
				
				let branchEls = response.getElementsByTagName("branch");
				
				for(let i = 0; i < branchEls.length; i++)
					$("select#branch_select").append($("<option>", { value: branchEls[i].getAttribute("id"), text: branchEls[i].getAttribute("title")}));
				
				for(let i = 0; i < 30; i++)
					$("select#payment_period_select").append($("<option>", { value: i + 1, text: i + 1 }));
				
				for(let i = 0; i < timeUnits.length; i++)
					$("select#period_unit_select").append($("<option>", { value: timeUnits[i], text: periodicTimePhraseVals[i] }));
					
				for(let i = 0; i < paymentFailureActions.length; i++)
					$("select#failure_action_select").append($("<option>", { value: paymentFailureActions[i], text: paymentFailureActionVals[i] }));
					
				for(let i = 0; i < subscriptionCancellationActions.length; i++)
					$("select#cancellation_action_select").append($("<option>", { value: subscriptionCancellationActions[i], text: subscriptionCancellationActionVals[i], title: subscriptionCancellationActionDescs[i] }));
					
				for(let i = 0; i < 31; i++)
					$("select#interval_start_day_select").append($("<option>", { value: dayVals[i], text: i + 1 }));	
				
				for(let i = 0; i < monthVals.length; i++)
					$("select#interval_start_month_select").append($("<option>", { value: monthVals[i], text: months[i] }));
				
				for(let i = 0; i < 31; i++)
					$("select#interval_end_day_select").append($("<option>", { value: dayVals[i], text: i + 1 }));	
				
				for(let i = 0; i < monthVals.length; i++)
					$("select#interval_end_month_select").append($("<option>", { value: monthVals[i], text: months[i] }));
				
				for(let i = 0; i < 6; i++)
					$("select#payment_day_index_select").append($("<option>", { value: dayVals[i], text: i + 1 }));	
				
				for(let i = 0; i < 31; i++)
					$("select#payment_day_subindex_select").append($("<option>", { value: dayVals[i], text: i + 1 }));	
				
				$("input[type='radio'][name='type']").change(event => 
				{
					let index = $(event.target).parent().index();
					$("select[data-mode], input[data-mode]").prop("disabled", "disabled");
					$("select[data-mode='" + index + "'], input[data-mode='" + index + "']").prop("disabled", false);
				});
				
				$("input[type='radio'][name='type']").eq(0).trigger("change");
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
		//makeFilterDialogue();
	});
	
	$("tr[data-is_selected_item='true']").trigger("click");
});

function getPaymentSchemaForm(schema)
{
	let formHtml = "";
	formHtml += "<form id='payment_schema_form' method='post' class='wide_mode'>";
	
	formHtml += "<section>";
	formHtml += "<h3>TEMEL BİLGİLER</h3>";
	formHtml += "<div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='title_input' name='title' placeholder=' '/>";
	formHtml += "<label for='title_input'>Adı</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='branch_select' name='branch'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='branch_select'>Branşı</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row equal_size_inputs'>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='payment_period_select' name='payment_period'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='payment_period_select'>Döngü</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='period_unit_select' name='period_unit'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='period_unit_select'>Döngü Birimi</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row equal_size_inputs'>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='payment_day_index_select' name='payment_day_index'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='payment_day_index_select'>Ödeme Gün (1)</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='payment_day_subindex_select' name='payment_day_subindex'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='payment_day_subindex_select'>Ödeme Gün (2)</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>ZAMAN BİLGİLERİ</h3>";
	formHtml += "<div>";
	
	formHtml += "<div class='inline_input_row'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='radio' id='interval_contrained_type_radio' name='type' value='INTERVAL_CONSTRAINED' checked='checked'/>";
	formHtml += "<label for='interval_contrained_type_radio'>Tarih Kısıtlamalı Plan</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='radio' id='equidistributed_type_radio' name='type' value='EQUIDISTRIBUTED'/>";
	formHtml += "<label for='equidistributed_type_radio'>Eşit Taksit Dağıtımlı Plan</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row equal_size_inputs'>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='interval_start_day_select' name='interval_start_day' data-mode='0'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='interval_start_day_select'>Başlangıç Gün</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='interval_start_month_select' name='interval_start_month' data-mode='0'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='interval_start_month_select'>Başlangıç Ay</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row equal_size_inputs'>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='interval_end_day_select' name='interval_end_day' data-mode='0'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='interval_end_day_select'>Bitiş Gün</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='interval_end_month_select' name='interval_end_month' data-mode='0'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='interval_end_month_select'>Bitiş Ay</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='installments_input' name='number_of_installments' data-mode='1' min='1' max='12' step='1' placeholder=' '/>";
	formHtml += "<label for='installments_input'>Ödeme Sayısı</label>";
	formHtml += "</div>";
	
	formHtml += "</div>";
	formHtml += "</section>";
	
	formHtml += "<section>";
	formHtml += "<h3>İLERİ DÜZEY ÖZELLİKLER</h3>";
	formHtml += "<div>";
	
	formHtml += "<div class='inline_input_row equal_size_inputs'>";
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='failure_action_select' name='failure_action'><option value='-1' disabled='disabled' selected='selected'></select>";
	formHtml += "<label for='failure_action_select'>Başarısız Ödemede</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='max_unpaid_days_input' name='max_unpaid_days' min='1' max='30' step='1' placeholder=' '/>";
	formHtml += "<label for='max_unpaid_days_input'>İzin Verilen Gün</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='cancellation_action_select' name='cancellation_action'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='cancellation_action_select'>Üyelik İptalinde</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='max_cancellable_days_input' name='max_cancellable_days' min='1' max='30' step='1' placeholder=' '/>";
	formHtml += "<label for='max_cancellable_days_input'>Plan Yenilemede İptal Süresi</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='inline_input_row equal_size_inputs'>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='checkbox' id='auto_renew_checkbox' name='auto_renew' value='true'/>";
	formHtml += "<label for='auto_renew_checkbox'>Plan Otomatik Yenilenir</label>";
	formHtml += "</div>";
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='checkbox' id='is_price_modification_protected_checkbox' name='is_price_modification_protected' value='true'/>";
	formHtml += "<label for='is_price_modification_protected_checkbox'>Fiyat Korumalı Plan</label>";
	formHtml += "</div>";
	formHtml += "</div>";
	
	formHtml += "</div>";
	
	formHtml += "<input type='hidden' name='data_mode' value='" + (schema == null ? 1 : 2) + "'>";
	
	if(schema != null)
		formHtml += "<input type='hidden' name='id' value='" + schema.id + "'>";
	
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