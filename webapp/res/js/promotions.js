var that = this;
var selectedPromotion = null;

$(document).ready(function()
{
	$("table.data_table > tbody > tr").click(function(event)
	{
		$("table.data_table > tbody > tr").removeClass("selected_row");
		$("table.data_table > tbody > tr > td > div").removeClass("active");
		$(this).find("button#delete_promotion_button, button#edit_promotion_button").unbind("click");
		
		$(this).addClass("selected_row");
		$(this).find("td div").addClass("active");
		that.getPromotionData($(this).data("id"));
		
		$(this).find("button#delete_promotion_button").click(function(event)
		{
			event.stopPropagation();
			
			if(that.selectedPromotion.isSiblingDiscount == false)
			{
				let confDialogue = new BordomorConfirmationDialogue("<strong>Dikkat!<br>Bu işlem geri alınamaz.</strong><br><br>Devam etmek istediğinize emin misiniz?", "Promosyonu Sil", "İptal");
				confDialogue.setChoiceCallbacks(function()
				{
					let deleteRunner = new BordomorAjaxRunner(promotionManURI, "id=" + that.selectedPromotion.id + "&data_mode=0");
					deleteRunner.init("post", true, "text");
					deleteRunner.setProcDialog("Promosyon Siliniyor");
					deleteRunner.setFailDialog();
					deleteRunner.setCallbacks(function() { window.location = window.location.href; });
							
					confDialogue.close();
					deleteRunner.run(responseErrorRegExp);
				});
				
				confDialogue.print();
			}
			else
				alert("Kardeş inidirmi silinemez.");
		});
		
		$(this).find("button#edit_promotion_button").click(event => { event.stopPropagation(); fetchBranchesAndDisplayForm(that.selectedPromotion); });
		$(this).find("button#get_code_button").click(event => { event.stopPropagation(); getPromotionCode(that.selectedPromotion); });
	});
	
	$("button#new_item").click(event => { event.stopPropagation(); fetchBranchesAndDisplayForm() });
	
	$("button#filters_button").click(function()
	{
		//makeFilterDialogue();
	});
	
	$("tr[data-is_selected_item='true']").trigger("click");
});

function getPromotionCode(promotion)
{
	let promotionCodeRunner = new BordomorAjaxRunner(promotionCodeGeneratorURI, "promotion=" + promotion.id);
	promotionCodeRunner.init("post", true, "text");
	promotionCodeRunner.setProcDialog("Promosyon Kodu Oluşturuluyor");
	promotionCodeRunner.setFailDialog();
	
	promotionCodeRunner.setCallbacks(function(isValid, response)
	{
		if(isValid)
			alert(response.substring(response.indexOf(":") + 1));
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
			{
				let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Promosyon Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
				{
					destroyBordomorFullScreenDialog(negativeDialog);
					negativeDialog = null;
				});
			}
			else
				new BordomorInfoDialogue("error", "Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.").print(dialogDisplayDurationMsec);
		}
	}, null);
	
	promotionCodeRunner.run(responseErrorRegExp);
}

function fetchBranchesAndDisplayForm(promotion)
{
	let branchRunner = new BordomorAjaxRunner(branchDataURL);
	branchRunner.init();
	branchRunner.setProcDialog("Branş Bilgileri Alınyor");
	branchRunner.setFailDialog();
	
	branchRunner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let dialogueTitle = promotion == null ? "YENİ PROMOSYON TANIMI GİRİŞİ" : "PROMOSYON BİLGİSİ DÜZENLEME";
			let buttonLabel = promotion == null ? "Kaydet" : "Güncelle";
			let procMsg = promotion == null ? "Promosyon Tanımlanıyor" : "Promosyon Güncelleniyor";
			let promotionDialogue = new BordomorDialogue(dialogueTitle, getPromotionForm(promotion), buttonLabel, "İptal Et");
			
			promotionDialogue.setButtonClickCallbacks(function() 
			{ 
				let procRunner = new BordomorAjaxRunner(promotionManURI, $("form#promotion_form").serialize());
				procRunner.init("post", true, "text");
				procRunner.setProcDialog(procMsg);
				procRunner.setFailDialog();
				
				procRunner.setCallbacks(function() { window.location = window.location.href; });
				
				promotionDialogue.close();
				procRunner.run(responseErrorRegExp);
	  
			}, function() { promotionDialogue.close() });
			
			promotionDialogue.print(false, true);
			
			let branchEls = response.getElementsByTagName("branch");
			
			for(let i = 0; i < branchEls.length; i++)
				$("select#branch_select").append($("<option>", { value: branchEls[i].getAttribute("id"), text: branchEls[i].getAttribute("title")}));
				
			if(promotion != null)
			{
				$("select#branch_select").val(promotion.branch != null ? promotion.branch.id : "");
				$("input#title_input").val(promotion.title);
				$("input#discount_ratio_input").val((promotion.discountRatio*100).toFixed(2));
				$("input#discount_amount_input").val(promotion.discountAmount.toFixed(2));
				$("input#overrides_others_checkbox").prop("checked", promotion.overridesOthers == true ? "checked" : false);
			}	
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
	
	branchRunner.run(responseErrorRegExp);
}

function getPromotionForm(promotion)
{
	let formHtml = "";
	formHtml += "<form id='promotion_form' method='post'>";
	
	formHtml += "<section>";
	formHtml += "<h3>TEMEL BİLGİLER</h3>";
	formHtml += "<div>";

	formHtml += "<div class='input_container select_container'>";
	formHtml += "<select id='branch_select' name='branch'><option value='-1' disabled='disabled' selected='selected'></option></select>";
	formHtml += "<label for='branch_select'>Branşı</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='text' id='title_input' name='title' placeholder=' '/>";
	formHtml += "<label for='title_input'>Adı</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='discount_ratio_input' name='discount_ratio' min='0' max='100' step='5' placeholder=' '/>";
	formHtml += "<label for='discount_ratio_input'>Oran (%)</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container'>";
	formHtml += "<input type='number' id='discount_amount_input' name='discount_amount' min='0' max='2500' step='100' placeholder=' '/>";
	formHtml += "<label for='discount_amount_input'>Tutar</label>";
	formHtml += "</div>";
	
	formHtml += "<div class='input_container checkbox_container'>";
	formHtml += "<input type='checkbox' id='overrides_others_checkbox' name='overrides_others' value='true'/>";
	formHtml += "<label for='overrides_others_checkbox'>Diğer İndirimlerin Üstüne Yaz</label>";
	formHtml += "</div>";
	
	formHtml += "</div>";
	
	formHtml += "</div>";
	formHtml += "<input type='hidden' name='data_mode' value='" + (promotion == null ? 1 : 2) + "'>";
	
	if(promotion != null)
		formHtml += "<input type='hidden' name='id' value='" + promotion.id + "'>";
		
	formHtml += "</section>";
	formHtml += "</form>";

	return formHtml;
}

function getPromotionData(id)
{
	let parameters = "id=" + id;
	let runner = new BordomorAjaxRunner(promotionDataURI, parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let promotionEl = response.getElementsByTagName("promotion")[0];
			that.selectedPromotion = new Promotion();
			that.selectedPromotion.parseFromXMLElement(promotionEl);
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