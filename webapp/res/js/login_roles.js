var roles = [];

var selectedRole = null;
var selectedRow = null;
var isFetching = false;
var that = this;

$(document).ready(function()
{
	initializeBordomorFullScreenResources("res/visual/dialog_res");
	
	/*$("#new_item_button").click(function(){ getExamDataAndDisplayForm(null); });
	$("#delete_item_button").click(function(){ that.selectedETraining.deleteFromDB(deleteETrainingCallback); });
	$("#edit_item_button").click(function(){ getExamDataAndDisplayForm(that.selectedETraining) });*/
	
	attachRowSelectionEventHandler();
	//$("tr[data-is_selected_item='true']").trigger("click");
});

function attachRowSelectionEventHandler()
{
	$(document).find("#data_table tbody tr").each(function()
	{
		$(this).click(function(event) 
		{
			event.stopPropagation();
			
			if(this != that.selectedRow && !that.isFetching)
			{
				that.isFetching = true;
				
				let id = parseInt($(this).data("id"));
				$(that.selectedRow).toggleClass("selected_row");
				$(this).toggleClass("selected_row");
				that.selectedRow = this;
				
				getRoleData(id);
			}
		});
	});
}

function getRoleData(id)
{
	let parameters = "id=" + id;
	let runner = new BordomorAjaxRunner("query/get_login_role_data.jsp", parameters);
	runner.init();
	runner.setFailDialog();
	
	runner.setCallbacks(function(isValid, response)
	{
		if(isValid)
		{
			let trEl = response.getElementsByTagName("login_role")[0];
			that.selectedRole = new LoginRole();
			that.selectedRole.parseFromXMLElement(trEl);
			that.displaySelectedRoleData();
		}
		else
		{
			if(new RegExp(noDataErrorRegExp).test(response))
			{
				let negativeDialog = makeBordomorFullScreenNegativeResultDialog("Yetki Grubu Bilgisi Bulunamadı", "Lütfen sistem yöneticinize bilgi verin.", "Kapat", function()
				{
					destroyBordomorFullScreenDialog(negativeDialog);
					negativeDialog = null;
				});
			}
			else
				makeBordomorFullScreenNegativeInfoDialog("Sunucudan gelen bilgilerde bir hata tespit edildi!\nLütfen sistem yöneticinize bilgi verin.", dialogDisplayDurationMsec);
				
			that.isFetching = false;	
		}
	}, null);
	
	let html = "<p class='strong' style='font-size: 12px; font-weight: 700; margin-bottom: 15px;'>Yetki Grubu Sorgulanıyor...</p>";
	$("section#data_container").html(html);
	$("section#data_container").css("display", "block");

	runner.run(responseErrorRegExp);
	that.isFetching = false;
}

function displaySelectedRoleData()
{
	/*$("div.placeholder").removeClass("shown");
	
	if(!$("div#single_selection_data_container").hasClass("shown"))
		$("div#single_selection_data_container").addClass("shown");

	$("#e_training_title").html(this.selectedETraining.shareable.title);
	$("#e_training_info").html(this.selectedETraining.shareable.description != null ? this.selectedETraining.shareable.description : "");
	$("#e_training_code").html("<span>Kod:</span>" + this.selectedETraining.code);
	$("#e_training_lang").html("<span>Eğitim Dili:</span>" + languageTRVals[languageVals.indexOf(this.selectedETraining.shareable.lang)]);
	$("#e_training_cert").html("<span>Sertifika:</span>" + (this.selectedETraining.certType != null ? certificateTypeStr[certificateTypeVals.indexOf(this.selectedETraining.certType)] : "Sertifika tanımı yok"));
	$("#e_training_price").html("<span>Fiyat (KDV Hariç):</span>" + this.selectedETraining.shareable.price + " " + this.selectedETraining.shareable.currency);
	
	let resCount = this.selectedETraining.resources != null ? this.selectedETraining.resources.length : 0;	
	let shareCount = this.selectedETraining.shareable.shares != null ? this.selectedETraining.shareable.shares.length : 0;
	
	$("#final_exam").html("<span>Sınav:</span>" + (this.selectedETraining.finalExam != null ? this.selectedETraining.finalExam.shareable.title : "Sınav tanımı yok"));
	$("#training_resources").html(resCount > 0 ? "<a href=\"javascript:void(0)\">" + resCount + " eğitim kaynağı</a>" : "<a href=\"javascript:void(0)\">Tanımlı kaynak yok</a>");
	$("#training_shares").html(shareCount > 0 ? "<a href=\"javascript:void(0)\">" + shareCount + " aktif paylaşım</a>" : "<a href=\"javascript:void(0)\">Tanımlı aktif paylaşım yok</a>");
	
	if(this.selectedETraining.state == "ACTIVE")
	{
		$("button#activate_e_training_button").css("display", "none");
		$("button#suspend_e_training_button").css("display", "inline-block");
		$("button#create_share_button").prop("disabled", false);
	}
	else if(this.selectedETraining.state == "SUSPENDED" || this.selectedETraining.state == "PENDING")
	{
		$("button#suspend_e_training_button").css("display", "none");
		$("button#activate_e_training_button").css("display", "inline-block");
		$("button#create_share_button").prop("disabled", "disabled");
	}
	
	$("#training_resources > a").click(function(){ makeResListDialog(that.selectedETraining); });
	$("#training_shares > a").click(function(){ makeShareListDialog(); });*/
	
	let html = "<p class='strong' style='font-size: 12px; font-weight: 700; margin-bottom: 15px;'>" + this.selectedRole.title + "</p>";
	html += "<p style='font-size: 11.5px; font-weight: 400;'><span style='display: inline-block; font: inherit; font-weight: 700; min-width: 175px'>Kullanıcı Hesap Kontrolü:</span>" + manipulationModeStr[manipulationModes.indexOf(this.selectedRole.userMod)] + "<p>";
	html += "<p style='font-size: 11.5px; font-weight: 400;'><span style='display: inline-block; font: inherit; font-weight: 700; min-width: 175px'>Yetki Grubu Kontrolü:</span>" + manipulationModeStr[manipulationModes.indexOf(this.selectedRole.roleMod)] + "<p>";
	html += "<p style='font-size: 11.5px; font-weight: 400;'><span style='display: inline-block; font: inherit; font-weight: 700; min-width: 175px'>Branş Bilgieri Kontrolü:</span>" + manipulationModeStr[manipulationModes.indexOf(this.selectedRole.branchMod)] + "<p>";
	html += "<p style='font-size: 11.5px; font-weight: 400;'><span style='display: inline-block; font: inherit; font-weight: 700; min-width: 175px'>Takım Bilgileri Kontrolü:</span>" + manipulationModeStr[manipulationModes.indexOf(this.selectedRole.teamMod)] + "<p>";
	html += "<p style='font-size: 11.5px; font-weight: 400;'><span style='display: inline-block; font: inherit; font-weight: 700; min-width: 175px'>Sporcu Bilgileri Kontrolü:</span>" + manipulationModeStr[manipulationModes.indexOf(this.selectedRole.athleteMod)] + "<p>";
	html += "<p style='font-size: 11.5px; font-weight: 400;'><span style='display: inline-block; font: inherit; font-weight: 700; min-width: 175px'>Tesis Bilgileri Kontrolü:</span>" + manipulationModeStr[manipulationModes.indexOf(this.selectedRole.facilityMod)] + "<p>";
	html += "<p style='font-size: 11.5px; font-weight: 400;'><span style='display: inline-block; font: inherit; font-weight: 700; min-width: 175px'>Ödeme Bilgileri Kontrolü:</span>" + manipulationModeStr[manipulationModes.indexOf(this.selectedRole.paymentMod)] + "<p>";
	
	$("section#data_container").html(html);
	$("section#data_container").css("display", "block");
	this.isFetching = false;
}