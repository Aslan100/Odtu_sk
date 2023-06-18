var bordomorNewFullScreenButtonContainerPositiveButton = null;
var bordomorNewFullScreenButtonContainerNeutralButton = null;
var bordomorFullScreenResourceFolderPath = null;

var thatDialogue = null;

function BordomorDialogue(title, content, positiveButtonLabel, negativeButtonLabel, neutralButtonLabel)
{
	this.id = null;
	this.title = title;
	this.content = content;
	
	this.positiveButtonLabel = positiveButtonLabel;
	this.negativeButtonLabel = negativeButtonLabel;
	this.neutralButtonLabel = neutralButtonLabel;
	
	this.positiveCallback = null;
	this.negativeCallback = null;
	this.neutralCallback = null;
	
	this.onLoadCallback = null;
	this.onCloseCallback = null;
	
	thatDialogue = this;
}

BordomorDialogue.prototype.setButtonClickCallbacks = function(positiveCallback, negativeCallback, neutralCallback)
{
	this.positiveCallback = positiveCallback;
	this.negativeCallback = negativeCallback;
	this.neutralCallback = neutralCallback;
}

BordomorDialogue.prototype.setEventCallbacks = function(onLoadCallback, onCloseCallback)
{
	this.onLoadCallback = onLoadCallback;
	this.onCloseCallback = onCloseCallback;
}

BordomorDialogue.prototype.close = function()
{
	let dialogue = document.getElementById(this.id);
	document.body.removeChild(dialogue);
}

BordomorDialogue.prototype.setCloseable = function(isCloseable)
{
	let dialogue = document.getElementById(this.id);
	let closeButton = document.getElementById("closer_" + this.id);
		
	if(!isCloseable)
	{
		closeButton.onclick = function() { return false; }
		dialogue.onclick = function() { return false; }
	}
	else
	{
		closeButton.onclick = function(e)
		{
			if(thatDialogue.onCloseCallback != null)
				thatDialogue.onCloseCallback(thatDialogue);
			
			thatDialogue.close();
		}
			
		dialogue.onclick = function(e)
		{
			let target;
			let event = e;
			
			if(!event)
				event = window.event;
			
			if(event.target)
				target = event.target;
			else if (event.srcElement)
				target = event.srcElement;
				
			if(target == dialogue)
			{
				if(thatDialogue.onCloseCallback != null)
					thatDialogue.onCloseCallback(thatDialogue);
			
				thatDialogue.close();
			}
		};
	}
}

BordomorDialogue.prototype.print = function(isSmallScale, hasContentPadding)
{
	this.id = Math.random().toString(36).slice(2, 7) + "_" + Math.random().toString(36).slice(2, 7) + "_" + new Date().getTime();
	
	let newDialogue = document.createElement("div");
	newDialogue.setAttribute("id", this.id);
    newDialogue.setAttribute("class", "bordomor_full_screen_container");
		
	let dialogueContainer = document.createElement("div");
	dialogueContainer.setAttribute("class", "bordomor_dialogue_container" + (isSmallScale ? " small_scale" : ""));
	
	newDialogue.appendChild(dialogueContainer);
	
	if(this.title != null && this.title.length > 0)
	{
		let titleContainer = document.createElement("div");
		titleContainer.setAttribute("class", "bordomor_title_container");
		
		let dialogueTitle = document.createElement("h3");
		dialogueTitle.setAttribute("class", "bordomor_title");
		dialogueTitle.appendChild(document.createTextNode(this.title));
		titleContainer.appendChild(dialogueTitle);
		
		let closeButton = document.createElement("button");
		closeButton.setAttribute("id", "closer_" + this.id);
		closeButton.setAttribute("class", "bordomor_dialogue_close_button");
		closeButton.appendChild(document.createTextNode("\u274C"));
		titleContainer.appendChild(closeButton);
		
		dialogueContainer.appendChild(titleContainer);
	}
	
	if(this.content != null)
	{
		let contentContainer = document.createElement("div");
		contentContainer.setAttribute("class", "bordomor_content_container" + (hasContentPadding ? " no_content_padding" : ""));
		contentContainer.innerHTML = this.content;
		dialogueContainer.appendChild(contentContainer);
	}
	
	let hasButtons = () => { this.positiveButtonLabel != null || this.negativeButtonLabel != null || this.neutralButtonLabel != null };
	
	if(hasButtons)
	{
		let buttonContainer = document.createElement("div");
		buttonContainer.setAttribute("class", "bordomor_buttons_container");
		dialogueContainer.appendChild(buttonContainer);
		
		if(this.positiveButtonLabel != null)
		{
			let positiveButton = document.createElement("button");
			positiveButton.appendChild(document.createTextNode(this.positiveButtonLabel));
			buttonContainer.appendChild(positiveButton);
			
			if(this.positiveCallback != null)
				positiveButton.onclick = function() { thatDialogue.positiveCallback(this); }
		}
		
		if(this.neutralButtonLabel != null)
		{
			let neutralButton = document.createElement("button");
			neutralButton.appendChild(document.createTextNode(this.neutralButtonLabel));
			buttonContainer.appendChild(neutralButton);
			
			if(this.neutralCallback != null)
				neutralButton.onclick = function() { thatDialogue.neutralCallback(this); }
		}
		
		if(this.negativeButtonLabel != null)
		{
			let negativeButton = document.createElement("button");
			negativeButton.appendChild(document.createTextNode(this.negativeButtonLabel));
			buttonContainer.appendChild(negativeButton);
			
			if(this.negativeCallback != null)
				negativeButton.onclick = function() { thatDialogue.negativeCallback(this); }
		}
	}
	
	document.body.insertBefore(newDialogue, document.body.childNodes[0]);
	this.setCloseable(true);
	dialogueContainer.setAttribute("style", "max-width: " + dialogueContainer.clientWidth + "px");
}

function makeBordomorFullScreenPositiveResultDialog(title, content, positiveButtonLabel, positiveCallback, neutralButtonLabel, neutralCallback, negativeButtonLabel, negativeCallback)
{
	var bordomorNewFullScreenDialog = document.createElement("DIV");
	bordomorNewFullScreenDialog.setAttribute("id", "bordomor_full_screen_container_" + activeDialogCount);
    bordomorNewFullScreenDialog.setAttribute("class", "bordomor_full_screen_container");
	
	var bordomorNewFullScreenDialogCentererDiv = document.createElement("DIV");
	bordomorNewFullScreenDialogCentererDiv.setAttribute("class", "bordomor_full_screen_dialog_centerer_dummy_div");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogCentererDiv);
	
	bordomorNewFullScreenDialog.onclick = function(e)
	{
		var target;
		var event = e;
		
		if (!event)
			event = window.event;
		
		if (event.target)
			target = event.target;
		else if (evenet.srcElement)
			target = event.srcElement;
			
		if(target == bordomorNewFullScreenDialog)
			destroyBordomorFullScreenDialog(bordomorNewFullScreenDialog)
	};
	
	var bordomorNewFullScreenDialogContainer = document.createElement("DIV");
	bordomorNewFullScreenDialogContainer.setAttribute("class", "bordomor_full_screen_dialog_container");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogContainer);
	
	if(title == null || title.length == 0)
		title = "Başarılı";
		
	var bordomorNewFullScreenTitleContainer = document.createElement("TABLE");
	bordomorNewFullScreenTitleContainer.setAttribute("class", "bordomor_full_screen_dialog_title_container_result_mode");
	bordomorNewFullScreenTitleContainer.setAttribute("cell-spacing", "0");
	bordomorNewFullScreenTitleContainer.setAttribute("cell-padding", "0");
	
	var bordomorNewFullScreenTitleContainerTR = bordomorNewFullScreenTitleContainer.insertRow(0);
	var bordomorNewFullScreenTitleContainerTDIcon = bordomorNewFullScreenTitleContainerTR.insertCell(0);
	var bordomorNewFullScreenTitleContainerTDIconIMG = document.createElement("IMG");
	bordomorNewFullScreenTitleContainerTDIconIMG.setAttribute("src", bordomorFullScreenResourceFolderPath + "/tamam.png");
	bordomorNewFullScreenTitleContainerTDIcon.appendChild(bordomorNewFullScreenTitleContainerTDIconIMG);
	
	var bordomorNewFullScreenTitleContainerTDTitle = bordomorNewFullScreenTitleContainerTR.insertCell(1);
	bordomorNewFullScreenTitleContainerTDTitle.appendChild(document.createTextNode(title));
	
	bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenTitleContainer);
	
	if(content != null)
	{
		var bordomorNewFullScreenContentContainer = document.createElement("DIV");
		bordomorNewFullScreenContentContainer.setAttribute("class", "bordomor_full_screen_dialog_content_container");
		bordomorNewFullScreenContentContainer.innerHTML = content;
		bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenContentContainer);
	}
	
	if(positiveButtonLabel != null || negativeButtonLabel != null || neutralButtonLabel != null)
	{
		var bordomorNewFullScreenButtonContainer = document.createElement("DIV");
		bordomorNewFullScreenButtonContainer.setAttribute("class", "bordomor_full_screen_dialog_button_container");
		bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenButtonContainer);
		
		if(positiveButtonLabel != null)
		{
			bordomorNewFullScreenButtonContainerPositiveButton = document.createElement("BUTTON");
			bordomorNewFullScreenButtonContainerPositiveButton.appendChild(document.createTextNode(positiveButtonLabel));
			bordomorNewFullScreenButtonContainer.appendChild(bordomorNewFullScreenButtonContainerPositiveButton);
			
			if(positiveCallback != null)
				bordomorNewFullScreenButtonContainerPositiveButton.onclick = positiveCallback;
		}
		
		if(neutralButtonLabel != null)
		{
			bordomorNewFullScreenButtonContainerNeutralButton = document.createElement("BUTTON");
			bordomorNewFullScreenButtonContainerNeutralButton.appendChild(document.createTextNode(neutralButtonLabel));
			bordomorNewFullScreenButtonContainer.appendChild(bordomorNewFullScreenButtonContainerNeutralButton);
			
			if(neutralCallback != null)
				bordomorNewFullScreenButtonContainerNeutralButton.onclick = neutralCallback;
		}
		
		if(negativeButtonLabel != null)
		{
			var bordomorNewFullScreenButtonContainerNegativeButton = document.createElement("BUTTON");
			bordomorNewFullScreenButtonContainerNegativeButton.appendChild(document.createTextNode(negativeButtonLabel));
			bordomorNewFullScreenButtonContainer.appendChild(bordomorNewFullScreenButtonContainerNegativeButton);
			
			if(negativeCallback != null)
				bordomorNewFullScreenButtonContainerNegativeButton.onclick = negativeCallback;
		}
	}
	
	document.body.insertBefore(bordomorNewFullScreenDialog, document.body.childNodes[0]);
	activeDialogCount++;
	
	return bordomorNewFullScreenDialog;
}

function makeBordomorFullScreenWarningDialog(title, content, positiveButtonLabel, positiveCallback, negativeButtonLabel, negativeCallback)
{
	var bordomorNewFullScreenDialog = document.createElement("DIV");
	bordomorNewFullScreenDialog.setAttribute("id", "bordomor_full_screen_container_" + activeDialogCount);
    bordomorNewFullScreenDialog.setAttribute("class", "bordomor_full_screen_container");
	
	var bordomorNewFullScreenDialogCentererDiv = document.createElement("DIV");
	bordomorNewFullScreenDialogCentererDiv.setAttribute("class", "bordomor_full_screen_dialog_centerer_dummy_div");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogCentererDiv);
	
	bordomorNewFullScreenDialog.onclick = function(e)
	{
		var target;
		var event = e;
		
		if (!event)
			event = window.event;
		
		if (event.target)
			target = event.target;
		else if (evenet.srcElement)
			target = event.srcElement;
			
		if(target == bordomorNewFullScreenDialog)
			destroyBordomorFullScreenDialog(bordomorNewFullScreenDialog)
	};
	
	var bordomorNewFullScreenDialogContainer = document.createElement("DIV");
	bordomorNewFullScreenDialogContainer.setAttribute("class", "bordomor_full_screen_dialog_container");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogContainer);
	
	if(title == null || title.length == 0)
		title = "Uyarı";
		
	var bordomorNewFullScreenTitleContainer = document.createElement("TABLE");
	bordomorNewFullScreenTitleContainer.setAttribute("class", "bordomor_full_screen_dialog_title_container_result_mode");
	bordomorNewFullScreenTitleContainer.setAttribute("cell-spacing", "0");
	bordomorNewFullScreenTitleContainer.setAttribute("cell-padding", "0");
	
	var bordomorNewFullScreenTitleContainerTR = bordomorNewFullScreenTitleContainer.insertRow(0);
	var bordomorNewFullScreenTitleContainerTDIcon = bordomorNewFullScreenTitleContainerTR.insertCell(0);
	var bordomorNewFullScreenTitleContainerTDIconIMG = document.createElement("IMG");
	bordomorNewFullScreenTitleContainerTDIconIMG.setAttribute("src", bordomorFullScreenResourceFolderPath + "/uyari.png");
	bordomorNewFullScreenTitleContainerTDIcon.appendChild(bordomorNewFullScreenTitleContainerTDIconIMG);
	
	var bordomorNewFullScreenTitleContainerTDTitle = bordomorNewFullScreenTitleContainerTR.insertCell(1);
	bordomorNewFullScreenTitleContainerTDTitle.appendChild(document.createTextNode(title));
	
	bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenTitleContainer);
	
	if(content != null)
	{
		var bordomorNewFullScreenContentContainer = document.createElement("DIV");
		bordomorNewFullScreenContentContainer.setAttribute("class", "bordomor_full_screen_dialog_content_container");
		bordomorNewFullScreenContentContainer.innerHTML = content;
		bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenContentContainer);
	}
	
	if(positiveButtonLabel != null || negativeButtonLabel != null)
	{
		var bordomorNewFullScreenButtonContainer = document.createElement("DIV");
		bordomorNewFullScreenButtonContainer.setAttribute("class", "bordomor_full_screen_dialog_button_container");
		bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenButtonContainer);
		
		if(positiveButtonLabel != null)
		{
			bordomorNewFullScreenButtonContainerPositiveButton = document.createElement("BUTTON");
			bordomorNewFullScreenButtonContainerPositiveButton.appendChild(document.createTextNode(positiveButtonLabel));
			bordomorNewFullScreenButtonContainer.appendChild(bordomorNewFullScreenButtonContainerPositiveButton);
			
			if(positiveCallback != null)
				bordomorNewFullScreenButtonContainerPositiveButton.onclick = positiveCallback;
		}
		
		if(negativeButtonLabel != null)
		{
			var bordomorNewFullScreenButtonContainerNegativeButton = document.createElement("BUTTON");
			bordomorNewFullScreenButtonContainerNegativeButton.appendChild(document.createTextNode(negativeButtonLabel));
			bordomorNewFullScreenButtonContainer.appendChild(bordomorNewFullScreenButtonContainerNegativeButton);
			
			if(negativeCallback != null)
				bordomorNewFullScreenButtonContainerNegativeButton.onclick = negativeCallback;
		}
	}
	
	document.body.insertBefore(bordomorNewFullScreenDialog, document.body.childNodes[0]);
	activeDialogCount++;
	
	return bordomorNewFullScreenDialog;
}

function makeBordomorFullScreenNegativeResultDialog(title, content, positiveButtonLabel, positiveCallback, neutralButtonLabel, neutralCallback, negativeButtonLabel, negativeCallback)
{
	var bordomorNewFullScreenDialog = document.createElement("DIV");
	bordomorNewFullScreenDialog.setAttribute("id", "bordomor_full_screen_container_" + activeDialogCount);
    bordomorNewFullScreenDialog.setAttribute("class", "bordomor_full_screen_container");
	
	var bordomorNewFullScreenDialogCentererDiv = document.createElement("DIV");
	bordomorNewFullScreenDialogCentererDiv.setAttribute("class", "bordomor_full_screen_dialog_centerer_dummy_div");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogCentererDiv);
	
	bordomorNewFullScreenDialog.onclick = function(e)
	{
		var target;
		var event = e;
		
		if (!event)
			event = window.event;
		
		if (event.target)
			target = event.target;
		else if (evenet.srcElement)
			target = event.srcElement;
			
		if(target == bordomorNewFullScreenDialog)
			destroyBordomorFullScreenDialog(bordomorNewFullScreenDialog)
	};
	
	var bordomorNewFullScreenDialogContainer = document.createElement("DIV");
	bordomorNewFullScreenDialogContainer.setAttribute("class", "bordomor_full_screen_dialog_container");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogContainer);
	
	if(title == null || title.length == 0)
		title = "Başarısız";
		
	var bordomorNewFullScreenTitleContainer = document.createElement("TABLE");
	bordomorNewFullScreenTitleContainer.setAttribute("class", "bordomor_full_screen_dialog_title_container_result_mode");
	bordomorNewFullScreenTitleContainer.setAttribute("cell-spacing", "0");
	bordomorNewFullScreenTitleContainer.setAttribute("cell-padding", "0");
	
	var bordomorNewFullScreenTitleContainerTR = bordomorNewFullScreenTitleContainer.insertRow(0);
	var bordomorNewFullScreenTitleContainerTDIcon = bordomorNewFullScreenTitleContainerTR.insertCell(0);
	var bordomorNewFullScreenTitleContainerTDIconIMG = document.createElement("IMG");
	bordomorNewFullScreenTitleContainerTDIconIMG.setAttribute("src", bordomorFullScreenResourceFolderPath + "/hata.png");
	bordomorNewFullScreenTitleContainerTDIcon.appendChild(bordomorNewFullScreenTitleContainerTDIconIMG);
	
	var bordomorNewFullScreenTitleContainerTDTitle = bordomorNewFullScreenTitleContainerTR.insertCell(1);
	bordomorNewFullScreenTitleContainerTDTitle.appendChild(document.createTextNode(title));
	
	bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenTitleContainer);
	
	if(content != null)
	{
		var bordomorNewFullScreenContentContainer = document.createElement("DIV");
		bordomorNewFullScreenContentContainer.setAttribute("class", "bordomor_full_screen_dialog_content_container");
		bordomorNewFullScreenContentContainer.innerHTML = content;
		bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenContentContainer);
	}
	
	if(positiveButtonLabel != null || negativeButtonLabel != null || neutralButtonLabel != null)
	{
		var bordomorNewFullScreenButtonContainer = document.createElement("DIV");
		bordomorNewFullScreenButtonContainer.setAttribute("class", "bordomor_full_screen_dialog_button_container");
		bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenButtonContainer);
		
		if(positiveButtonLabel != null)
		{
			bordomorNewFullScreenButtonContainerPositiveButton = document.createElement("BUTTON");
			bordomorNewFullScreenButtonContainerPositiveButton.appendChild(document.createTextNode(positiveButtonLabel));
			bordomorNewFullScreenButtonContainer.appendChild(bordomorNewFullScreenButtonContainerPositiveButton);
			
			if(positiveCallback != null)
				bordomorNewFullScreenButtonContainerPositiveButton.onclick = positiveCallback;
		}
		
		if(neutralButtonLabel != null)
		{
			bordomorNewFullScreenButtonContainerNeutralButton = document.createElement("BUTTON");
			bordomorNewFullScreenButtonContainerNeutralButton.appendChild(document.createTextNode(neutralButtonLabel));
			bordomorNewFullScreenButtonContainer.appendChild(bordomorNewFullScreenButtonContainerNeutralButton);
			
			if(neutralCallback != null)
				bordomorNewFullScreenButtonContainerNeutralButton.onclick = neutralCallback;
		}
		
		if(negativeButtonLabel != null)
		{
			var bordomorNewFullScreenButtonContainerNegativeButton = document.createElement("BUTTON");
			bordomorNewFullScreenButtonContainerNegativeButton.appendChild(document.createTextNode(negativeButtonLabel));
			bordomorNewFullScreenButtonContainer.appendChild(bordomorNewFullScreenButtonContainerNegativeButton);
			
			if(negativeCallback != null)
				bordomorNewFullScreenButtonContainerNegativeButton.onclick = negativeCallback;
		}
	}
	
	document.body.insertBefore(bordomorNewFullScreenDialog, document.body.childNodes[0]);
	activeDialogCount++;
	
	return bordomorNewFullScreenDialog;
}

function makeBordomorFullScreenProcessingInfoDialog(title, dialogTimeoutMsec)
{
	var bordomorNewFullScreenDialog = document.createElement("DIV");
	bordomorNewFullScreenDialog.setAttribute("id", "bordomor_full_screen_container_" + activeDialogCount);
    bordomorNewFullScreenDialog.setAttribute("class", "bordomor_full_screen_container");
	
	var bordomorNewFullScreenDialogCentererDiv = document.createElement("DIV");
	bordomorNewFullScreenDialogCentererDiv.setAttribute("class", "bordomor_full_screen_dialog_centerer_dummy_div");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogCentererDiv);
	
	var bordomorNewFullScreenDialogContainer = document.createElement("DIV");
	bordomorNewFullScreenDialogContainer.setAttribute("class", "bordomor_full_screen_info_container");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogContainer);
	
	if(title == null || title.length == 0)
		title = "İşlem yapılıyor";
		
	var bordomorNewFullScreenDialogProcessingIMG = document.createElement("IMG");
	bordomorNewFullScreenDialogProcessingIMG.setAttribute("src", bordomorFullScreenResourceFolderPath + "/isleniyor.gif");
	bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenDialogProcessingIMG);
	
	var bordomorNewFullScreenDialogProcessingDIV = document.createElement("DIV");
	bordomorNewFullScreenDialogProcessingDIV.innerHTML = title;
	bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenDialogProcessingDIV);
	
	document.body.insertBefore(bordomorNewFullScreenDialog, document.body.childNodes[0]);
	
	if(dialogTimeoutMsec > 0)
		setTimeout(function(){destroyBordomorFullScreenDialog(bordomorNewFullScreenDialog)}, dialogTimeoutMsec);
	
	activeDialogCount++;
	
	return bordomorNewFullScreenDialog;
}

function makeBordomorFullScreenPositiveInfoDialog(title, dialogTimeoutMsec)
{
	var bordomorNewFullScreenDialog = document.createElement("DIV");
	bordomorNewFullScreenDialog.setAttribute("id", "bordomor_full_screen_container_" + activeDialogCount);
    bordomorNewFullScreenDialog.setAttribute("class", "bordomor_full_screen_container");
	
	var bordomorNewFullScreenDialogCentererDiv = document.createElement("DIV");
	bordomorNewFullScreenDialogCentererDiv.setAttribute("class", "bordomor_full_screen_dialog_centerer_dummy_div");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogCentererDiv);
	
	var bordomorNewFullScreenDialogContainer = document.createElement("DIV");
	bordomorNewFullScreenDialogContainer.setAttribute("class", "bordomor_full_screen_info_container");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogContainer);
	
	if(title == null || title.length == 0)
		title = "İşlem başarılı";
		
	var bordomorNewFullScreenDialogProcessingIMG = document.createElement("IMG");
	bordomorNewFullScreenDialogProcessingIMG.setAttribute("src", bordomorFullScreenResourceFolderPath + "/tamam.png");
	bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenDialogProcessingIMG);
	
	var bordomorNewFullScreenDialogProcessingDIV = document.createElement("DIV");
	bordomorNewFullScreenDialogProcessingDIV.innerHTML = title;
	bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenDialogProcessingDIV);
	
	document.body.insertBefore(bordomorNewFullScreenDialog, document.body.childNodes[0]);
	
	if(dialogTimeoutMsec != null && dialogTimeoutMsec > 0)
		setTimeout(function(){destroyBordomorFullScreenDialog(bordomorNewFullScreenDialog)}, dialogTimeoutMsec);
	
	activeDialogCount++;
	
	return bordomorNewFullScreenDialog;
}

function makeBordomorFullScreenNegativeInfoDialog(title, dialogTimeoutMsec)
{
	var bordomorNewFullScreenDialog = document.createElement("DIV");
	bordomorNewFullScreenDialog.setAttribute("id", "bordomor_full_screen_container");
    bordomorNewFullScreenDialog.setAttribute("class", "bordomor_full_screen_container");
	
	var bordomorNewFullScreenDialogCentererDiv = document.createElement("DIV");
	bordomorNewFullScreenDialogCentererDiv.setAttribute("class", "bordomor_full_screen_dialog_centerer_dummy_div");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogCentererDiv);
	
	var bordomorNewFullScreenDialogContainer = document.createElement("DIV");
	bordomorNewFullScreenDialogContainer.setAttribute("class", "bordomor_full_screen_info_container");
	bordomorNewFullScreenDialog.appendChild(bordomorNewFullScreenDialogContainer);
	
	if(title == null || title.length == 0)
		title = "İşlem başarısız";
		
	var bordomorNewFullScreenDialogProcessingIMG = document.createElement("IMG");
	bordomorNewFullScreenDialogProcessingIMG.setAttribute("src", bordomorFullScreenResourceFolderPath + "/hata.png");
	bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenDialogProcessingIMG);
	
	var bordomorNewFullScreenDialogProcessingDIV = document.createElement("DIV");
	bordomorNewFullScreenDialogProcessingDIV.innerHTML = title;
	bordomorNewFullScreenDialogContainer.appendChild(bordomorNewFullScreenDialogProcessingDIV);
	
	document.body.insertBefore(bordomorNewFullScreenDialog, document.body.childNodes[0]);
	
	if(dialogTimeoutMsec != null && dialogTimeoutMsec > 0)
		setTimeout(function(){destroyBordomorFullScreenDialog(bordomorNewFullScreenDialog)}, dialogTimeoutMsec);
	
	activeDialogCount++;
	
	return bordomorNewFullScreenDialog;
}

function destroyBordomorFullScreenDialog(bordomorFullScreenDialog)
{
	document.body.removeChild(bordomorFullScreenDialog);
	activeDialogCount = activeDialogCount - 1;
	bordomorFullScreenDialog = null;
}

function setBordomorFullScreenDialogPositiveButtonState(enabled)
{
	if(bordomorNewFullScreenButtonContainerPositiveButton != null)
	{
		if(!enabled)
		{
			bordomorNewFullScreenButtonContainerPositiveButton.disabled = true;

			if(!$(bordomorNewFullScreenButtonContainerPositiveButton).hasClass("disabled_state"))
				$(bordomorNewFullScreenButtonContainerPositiveButton).addClass("disabled_state");
		}
		else
		{
			bordomorNewFullScreenButtonContainerPositiveButton.disabled = false;
			$(bordomorNewFullScreenButtonContainerPositiveButton).removeClass("disabled_state");
		}
	}
}

function setBordomorFullScreenDialogNeutralButtonState(enabled)
{
	if(bordomorNewFullScreenButtonContainerNeutralButton != null)
	{
		if(!enabled)
		{
			bordomorNewFullScreenButtonContainerNeutralButton.disabled = true;

			if(!$(bordomorNewFullScreenButtonContainerNeutralButton).hasClass("disabled_state"))
				$(bordomorNewFullScreenButtonContainerNeutralButton).addClass("disabled_state");
		}
		else
		{
			bordomorNewFullScreenButtonContainerNeutralButton.disabled = false;
			$(bordomorNewFullScreenButtonContainerNeutralButton).removeClass("disabled_state");
		}
	}
}

/*Info Dialogue*/
function BordomorInfoDialogue(type, content)
{
	this.id = null;
	this.type = type;
	this.content = content;
	
	this.onLoadCallback = null;
	this.onCloseCallback = null;
	
	thatDialogue = this;
}

BordomorInfoDialogue.prototype.close = function()
{
	let dialogue = document.getElementById(this.id);
	document.body.removeChild(dialogue);
}

BordomorInfoDialogue.prototype.setEventCallbacks = function(onLoadCallback, onCloseCallback)
{
	this.onLoadCallback = onLoadCallback;
	this.onCloseCallback = onCloseCallback;
}

BordomorInfoDialogue.prototype.print = function(timeoutMsec)
{
	this.id = Math.random().toString(36).slice(2, 7) + "_" + Math.random().toString(36).slice(2, 7) + "_" + new Date().getTime();
	
	let newDialogue = document.createElement("div");
	newDialogue.setAttribute("id", this.id);
    newDialogue.setAttribute("class", "bordomor_full_screen_container");
	
	let infoContainer = document.createElement("div");
	infoContainer.setAttribute("class", "bordomor_info_container");
	newDialogue.appendChild(infoContainer);
	
	let conceptImg = document.createElement("img");
	
	if(this.type == "error")
		conceptImg.setAttribute("src", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAEu0lEQVRoge1aX2tTSRT/pdkgSEis0IcUmrZURC3Y6CdIPsDWXfzTtNDcZLXrY9n9AJbdb9BHdbf3JqV2rQq1X6B3X/dBW6EoF0O6PthKStEgRQ2Zsw+5CfemSe6ZSdp1YX8w0CZzZn6/mTnnnjsnwP/4d+HrxiBPLl8OAYgDSACI2X+3gglgA8A6APPqs2elTubuSMCTS5fGAWQAfNfBMKsA9KvPn6+pGCsJeByLjQOYBzCkYt8C2wBmr21sSAmREvB4bCwKIIv2R6RTmAC0a5ubbzid2QIeXbyYQnXVT6nxksJ7ALPXX7zIeXVkCVgZHb0D4JdOWSlg7sbW1q/tOngKWBkd1QGku8VIAcaNra1Mqy/97SxXLly4A+CnrlOSQ+x6Xx89Khb/bPZlyx14eP58ClWH/VqgTbx8ecgnmgp4eO5cFMAmjsdhuXgPYGzi1StXdPqmWU8SIgs58iaqcTwtYWOg+hyJM/ufQvVEJJwf9jT2+uPs2XGJQQHASFpWImlZGQAa00ZLWlYmaVkJVIVwEbf51XFIAIjmQQRmM2ziAICkZeVApHnYaEnLyjlsMiAyJOacd9J1+cDyyMg4gKfM1TAm8/mm4W35zJkUiA4HAJ9Pm3z9uunDaXlkRCZcX5nM59eAxh0gynBXvhV5ALBJuncCaEkeACbzeZmdqM9d34EHw8MhAB8Y6s2pQiHh3Q14MDxcC8XaVKHgmRbYNuvg+WB4qlAo1XeAhIiTEGC0bQ4RALBJD3LJ2zy2mTzigPsIsVYVQHppcDAlIYKVVQKAPW6a2T0BOAUQxSQiQXYpGmWLYJGPRquOz+cQaxQQlzCuihgY6IqIpYEBWfJVvnA8iUWlojJ3drG/H9Nv37LPeCMW+/tTolJRzrmcO6DasouRiNJOLEYiKivvDM1NnsRqyC5GIlFJ8rXX047QjR0AiLTpnR12tAGA6Z2dN/BOO/g7QESqTZve3VXygend3RwRaapzAw4nJiFUOGhasajswACQevcul+3rAxSPk9MHTEnbjsnXB6qOo0mamYDbBzZkzny3yNegFYucVNzZNlwCSIh1Zg5iaHt7bPLG6dPs6KTt7eVICIPJY90lAIDJVD7EJt/bmwLR30ZvL/85QTTEjECmS0B6f79EQqxyskA9HNa9eOjhcIqEyNo2WT0c9hShh8M6MyteTe/vl1wCamMw1yndToRNtjGqtBVhj5dmzl+f+9C1ih4KFcC/dTYypZLrzUwPhbzuk7RMqeTyIT0UkiG/nSmVhmv/HEolSIhZphOBhEgvBIP11VgIBp3HplXLLgSDKYeNTkKkJeacdfJterG1EAyuQ+5qxcTR3wsBgPnDx4+uF6+mAn4/efKrvZm7eXDgyrmaXu6ulcsfrgQCOyD6/lioceDzTdw8OPir8eOWt9Nr5fLmuN9PIEp0mKl2o83d+vTpXlNdXsJ/O3FCJkIcBYxbnz+r1QcAYK1Sefqt30/g31p0E3MzX7783K4Du0Z2PxA47hqZNlMue1YspaqU9wOBqH31HlckxoHp6+nRZsrl7lYpnbjn9x9ZnfjHSuXo6sSNuOvzVSv1ROqVep9vFYB+m+j4KvWNuAso/1biNtDRbyX+8/gHefLe0PWUdiwAAAAASUVORK5CYII=");
	else if(this.type == "success")
		conceptImg.setAttribute("src", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAEg0lEQVRoge2a709bZRTHPxd9jfwDMn3ti5VkahZjbtky5tS1MwwIFFqqMIwLYzA1M2o70LgNsmRkUQfMtRc2JrC50kn2arGNPxadCZXEF74S8R9A/oB7fNHS9Nft/dFSZuK3eQLtPffe7/ee55zn6TmF/7G7UKpxkbE7PfXAMaAJcAHuMuYJIAWsArFQ69xWJfeuSMDYnW4PECRN3iliQCTUeiPu5GRHAkZv+zzAJPCMk/MNsA4MhY/ftCXEloDR212NgEb5KVIpEkAgfHx+w4qxZQHnljo9pMk3OONlC5tA4FzbLVNvWBIQWuwIAaOVsnKA8Fj7wlg5A1MBocWOCNBbLUYOEB1rXwgaHXyi3JmhxfYQMFx1SvbgUtuek+TS78lSBw098PFCmwdY3jFa9uH9pGOpKCZKCvhooa0R+I3aBKxVbAJ7P+1YystOT5ayFNFrlW3soIF0FmzO/bCu0OrDr1s97GyerwTuDL8sigQIMinU/LUpSECQqAXbyVy+eTHwwa03diNwUwpK4LPOb9YyHKykbe/5zrtxKPKABEGo4YgB6jZ5gPOdd4MgUZPzsutC1gNn5731wD8myquJyxe6lg3XmLPz3lXSW3MjPHWha3krm4VEpJItsV0ELvrisyY2qoiUe6DHgNncKdTkkMwm6a2wVdu9Fsgjkh+sJdAEOeuAIOXcVY6QmvlrtvClFBTvRV/cdJv8/s2jfkF6TcxckBfE4rYZgOsg6rjv3tq4794GiAqyaRysoloj/7ofRLNwfzfkeEDXdbNr5yKlKIo60b2S/T477vt27b0br6kikiTHE4qiXJ7oXrG0IXx37lW/ruuaHSJZD9hYdFJAHvltTHSvrAF7SE+rALDHKvkzc0f8gmh2lj/IpNEzc0cagb8s3CcFqJd67ldUSShFnvQ+xxYu9dxX6jL/bFic9+Hqk3/F6pwvMXKnkIiVoY3MHq6vFvmR2cN+EdEs3rtoOBHQICLJYa2lYhHDWktF5IsEkC5nWIELqEjEsNbiaM4XIAH5WShlIwO4BEme1g7ZFnFaO2Q725TJhrlTSF8V0bExXCJ6cih60LKIoehBv4iu2byP0VjNEwDEHDwFlyDJU9EDpiJORQ9U68kj6QwUyxMw2ftgS0RiDoLJJSLJwUizoYjBSHPFAVswYpO9D7YKPQAQsTodCuACSooYjDRXI2ALkeVZVFYZjLj/xHnVOQWoV4KJrcy1doL8+pVg4tntN0VlFV1kCOffi11A8uR1VQV6dfM9vRMM5b4pWdg6eV39jseztJL4/M1kXl3IqLAV4PGszAUKPzSsjb7z1cseQR6b2qiC4v3ire+LaqOG1elH8Y0/9nmeFgpKebuE8Jd9P0yXOmDaH3j72ku73h+42vejs/4AwK/xv5f3Hd01T4Sv9v80Us7Aco9sYGZ/zXtkU/0Pq9Mj28bAzP7GTOnd7ZCYFSQUpS4w1f+wul3KXJyYfnHH+sTTJ37euT5xIfqnXvAAQcF5WVJBiQGRmYFfatepL0Tf1POOfytxbeBRVYsE/zn8C+6UHoptWnktAAAAAElFTkSuQmCC");	
	else if(this.type == "attention")
		conceptImg.setAttribute("src", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH5AUUDg4BtYCMngAAA9hJREFUaN7tmU1sVFUUx3/nvhaqaewdTAiJuoBIM+2CApNWRe2XLPhI1LKgITFh54KQoCIuhMQFKQtiwiRg2JG4Y0OtKNH4NR2jxLS0Q0xbEiaECLGKteUisS04fcfFIFqm9GPmTTsv8b98L++9//+ec8/53/OEAuA+27iMR8uiKJsRNgEbgdWzPOIDCaAfn+9RztvWnpFCOEhexBOxCOK1I+wG6oBH8vqyMgx8iepxLt9I2dev+UUV4L6ORfC8doR9IFFQAsI40ImvcSpu9NvnrmmgAlxXnYddvgWhA6QuQOIPYgLlBDoVty19w4EIcIkNVUj5IYS3AEPRIYAOoeyxzT3JggS47vo1iBwDXmbxcQvYT9XtD+36S5kFC3DJWDV4HwG1LB0U1YOsGDtq112ZmrcAl6xfA+YT0NpAyeRV9cQH/yCMHbVNV3KqlMlNmw1VIMcCJo8qd/N80oC8h67YOtPdaQLcF/UeUn6oGDkvUlDpqkDklEvWR2dNIZdo2I7hY8ArhOzqV9M51652rZ0EKgpchwQ+22xLz2ROBNw3sQiGjkLJF3lLN2PYNXMKeV77PVtQuhAEeMcln7HTBLhELALsIxSQKOjO6REwXjsQDYcABeTNf6JgXDJWDuwmTBCNotqUjYCampLP/Zlb4o6sAJHNefn5pUerS9RHDLCJcGIVnlSbe8fAMKIMJWrmOMOWOp4yhBvrwi6gMuwC+F/AUre0sAsYMGTHfeGEcNOQnVWGNIG4YID+kNK/hZIuA/kO9ECQb77atXbm1h9s+gwxYX4xaOY88POieJcg4etZu+WHu8Y29/0OfBWy9PkTI+f+2weOkx1xhwWdLFs5+G9YR12Kx20n8FoQby/iXIjsQkvcPvupfz8CdsdlH4gDEyFY/TOURS7mWoknJvuBEyVOfgTRw/b5z/0cAfbpHxUhDgyVaNNShCO2sTf9UDNnG3uGEfaQ/blQarbhNLf15Jxu1Db2JIH92bl8gAuoBfWBFDq1127vvZOrawa4ZG0ZUvk2Kh0l4FgHkUybbexPz/s8YJuGMoyPvg/67hJXptRs5B8agfuRSEQN3mNbUU4BKxd1ywqnkam99sW+sdm3xjzguhuiCB+AtIBKkcmPIBwhM37Stg7cmXtvzxMuUVeBt3wXygFEatDAf3aPA2fw9LB9oTc9/+K0QLjumEW8ncAbQE0gxgw6QeO43y7aV35aUPXLOx1cMmbBawLagJeAVQuwzA7hEspZRM8x+uugbbueV9kOJJ/dtw0RoBolCjxJdlxf+cCmHEC5icgFjJ/mj8yw3Zb6q9Bv/w0hujapaU/ESAAAAABJRU5ErkJggg==");
	else if(this.type == "processing")
		conceptImg.setAttribute("src", "data:image/gif;base64,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");
	
	infoContainer.appendChild(conceptImg);
	
	if(this.type == "processing" || this.content != null)
	{
		let contentContainer = document.createElement("div");
		contentContainer.innerHTML = this.type != "processing" ? this.content : (this.content == null ? "Devam Ediyor" : this.content);
		infoContainer.appendChild(contentContainer);
	}
	
	document.body.insertBefore(newDialogue, document.body.childNodes[0]);
	
	if(timeoutMsec != null && timeoutMsec > 0)
		setTimeout(function(){thatDialogue.close()}, timeoutMsec);
}


/*Confirmation Dialogue*/
function BordomorConfirmationDialogue(content, yesButtonLabel, noButtonLabel)
{
	this.id = null;
	this.content = content;
	this.yesButtonLabel = yesButtonLabel != null ? yesButtonLabel : "Evet";
	this.noButtonLabel = noButtonLabel != null ? noButtonLabel : "Hayır";
	
	this.onYesCallback = null;
	this.onNoCallback = null;
	
	this.onLoadCallback = null;
	this.onCloseCallback = null;
	
	thatDialogue = this;
}

BordomorConfirmationDialogue.prototype.close = function()
{
	let dialogue = document.getElementById(this.id);
	document.body.removeChild(dialogue);
}

BordomorConfirmationDialogue.prototype.setChoiceCallbacks = function(onYesCallback, onNoCallback)
{
	this.onYesCallback = onYesCallback;
	this.onNoCallback = onNoCallback;
}

BordomorConfirmationDialogue.prototype.setEventCallbacks = function(onLoadCallback, onCloseCallback)
{
	this.onLoadCallback = onLoadCallback;
	this.onCloseCallback = onCloseCallback;
}

BordomorConfirmationDialogue.prototype.setCloseable = function(isCloseable)
{
	let dialogue = document.getElementById(this.id);
	let closeButton = document.getElementById("closer_" + this.id);
		
	if(!isCloseable)
		dialogue.onclick = function() { return false; }
	else
	{
		dialogue.onclick = function(e)
		{
			let target;
			let event = e;
			
			if(!event)
				event = window.event;
			
			if(event.target)
				target = event.target;
			else if (event.srcElement)
				target = event.srcElement;
				
			if(target == dialogue)
			{
				if(thatDialogue.onCloseCallback != null)
					thatDialogue.onCloseCallback(thatDialogue);
			
				thatDialogue.close();
			}
		};
	}
}

BordomorConfirmationDialogue.prototype.print = function()
{
	this.id = Math.random().toString(36).slice(2, 7) + "_" + Math.random().toString(36).slice(2, 7) + "_" + new Date().getTime();
	
	let newDialogue = document.createElement("div");
	newDialogue.setAttribute("id", this.id);
    newDialogue.setAttribute("class", "bordomor_full_screen_container");
	
	let confContainer = document.createElement("div");
	confContainer.setAttribute("class", "bordomor_info_container");
	newDialogue.appendChild(confContainer);
	
	let qImg = document.createElement("img");
	qImg.setAttribute("src", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH5AUUDg4BtYCMngAAA9hJREFUaN7tmU1sVFUUx3/nvhaqaewdTAiJuoBIM+2CApNWRe2XLPhI1LKgITFh54KQoCIuhMQFKQtiwiRg2JG4Y0OtKNH4NR2jxLS0Q0xbEiaECLGKteUisS04fcfFIFqm9GPmTTsv8b98L++9//+ec8/53/OEAuA+27iMR8uiKJsRNgEbgdWzPOIDCaAfn+9RztvWnpFCOEhexBOxCOK1I+wG6oBH8vqyMgx8iepxLt9I2dev+UUV4L6ORfC8doR9IFFQAsI40ImvcSpu9NvnrmmgAlxXnYddvgWhA6QuQOIPYgLlBDoVty19w4EIcIkNVUj5IYS3AEPRIYAOoeyxzT3JggS47vo1iBwDXmbxcQvYT9XtD+36S5kFC3DJWDV4HwG1LB0U1YOsGDtq112ZmrcAl6xfA+YT0NpAyeRV9cQH/yCMHbVNV3KqlMlNmw1VIMcCJo8qd/N80oC8h67YOtPdaQLcF/UeUn6oGDkvUlDpqkDklEvWR2dNIZdo2I7hY8ArhOzqV9M51652rZ0EKgpchwQ+22xLz2ROBNw3sQiGjkLJF3lLN2PYNXMKeV77PVtQuhAEeMcln7HTBLhELALsIxSQKOjO6REwXjsQDYcABeTNf6JgXDJWDuwmTBCNotqUjYCampLP/Zlb4o6sAJHNefn5pUerS9RHDLCJcGIVnlSbe8fAMKIMJWrmOMOWOp4yhBvrwi6gMuwC+F/AUre0sAsYMGTHfeGEcNOQnVWGNIG4YID+kNK/hZIuA/kO9ECQb77atXbm1h9s+gwxYX4xaOY88POieJcg4etZu+WHu8Y29/0OfBWy9PkTI+f+2weOkx1xhwWdLFs5+G9YR12Kx20n8FoQby/iXIjsQkvcPvupfz8CdsdlH4gDEyFY/TOURS7mWoknJvuBEyVOfgTRw/b5z/0cAfbpHxUhDgyVaNNShCO2sTf9UDNnG3uGEfaQ/blQarbhNLf15Jxu1Db2JIH92bl8gAuoBfWBFDq1127vvZOrawa4ZG0ZUvk2Kh0l4FgHkUybbexPz/s8YJuGMoyPvg/67hJXptRs5B8agfuRSEQN3mNbUU4BKxd1ywqnkam99sW+sdm3xjzguhuiCB+AtIBKkcmPIBwhM37Stg7cmXtvzxMuUVeBt3wXygFEatDAf3aPA2fw9LB9oTc9/+K0QLjumEW8ncAbQE0gxgw6QeO43y7aV35aUPXLOx1cMmbBawLagJeAVQuwzA7hEspZRM8x+uugbbueV9kOJJ/dtw0RoBolCjxJdlxf+cCmHEC5icgFjJ/mj8yw3Zb6q9Bv/w0hujapaU/ESAAAAABJRU5ErkJggg==");
	confContainer.appendChild(qImg);
	
	if(this.content != null)
	{
		let contentContainer = document.createElement("div");
		contentContainer.innerHTML = this.content;
		confContainer.appendChild(contentContainer);
	}
	
	let buttonContainer = document.createElement("div");
	buttonContainer.setAttribute("class", "bordomor_buttons_container");
	confContainer.appendChild(buttonContainer);
	
	let yesButton = document.createElement("button");
	yesButton.appendChild(document.createTextNode(this.yesButtonLabel != null ? this.yesButtonLabel : "Evet"));
	buttonContainer.appendChild(yesButton);
	
	if(this.onYesCallback != null)
		yesButton.onclick = function() { thatDialogue.onYesCallback(); }
	
	let noButton = document.createElement("button");
	noButton.appendChild(document.createTextNode(this.noButtonLabel != null ? this.noButtonLabel : "Hayır"));
	buttonContainer.appendChild(noButton);
	
	if(this.onNoCallback != null)
		noButton.onclick = function() { thatDialogue.onNoCallback(); };
	else
		noButton.onclick = function() { thatDialogue.close(); }
		
	document.body.insertBefore(newDialogue, document.body.childNodes[0]);
	this.setCloseable(true);
}