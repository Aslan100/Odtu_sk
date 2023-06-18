var thatRunner = null;
const latencyMsec = 500;

function BordomorAjaxRunner(url, params)
{
	this.url = url;
	this.params = params;
	this.mode = "POST";
	this.isAsync = true;
	this.expectedResponse = "xml";
	this.contentType = "application/x-www-form-urlencoded";
	
	this.runnerSuccessCallback = null;
	this.runnerFailCallback = null;
	
	this.procMsg = null;
	this.failMsg = null;
	this.successMsg = null;
	this.runnerXMLHttp = null;
	
	thatRunner = this;
}

BordomorAjaxRunner.prototype.init = function(mode, isAsync, expectedResponse, contentType)
{
	if(mode !== undefined)
		this.mode = mode;
	
	if(isAsync !== undefined)
		this.isAsync = isAsync;
	
	if(expectedResponse !== undefined)
		this.expectedResponse = expectedResponse;
	
	if(contentType !== undefined)
		this.contentType = contentType;
	
	this.runnerXMLHttp = getConnector();

	function getConnector()
	{
		let activexmodes = ["Msxml2.XMLHTTP", "Microsoft.XMLHTTP"]
		
		if(window.ActiveXObject)
		{
			for (let i = 0; i < activexmodes.length; i++)
			{
				try
				{
					return new ActiveXObject(activexmodes[i])
				}
				catch(e) {}
			}
		}
		else if (window.XMLHttpRequest)
			return new XMLHttpRequest();
		else
			return null;
	}
}

BordomorAjaxRunner.prototype.setCallbacks = function(successCallback, failCallback)
{
	this.runnerSuccessCallback = successCallback;
	this.runnerFailCallback = failCallback;
}

BordomorAjaxRunner.prototype.setProcDialog = function(msg)
{
	this.procMsg = (msg == undefined || msg.length == 0) 
		? "İstek İşleniyor" 
				: msg.trim();
}

BordomorAjaxRunner.prototype.setSuccessDialog = function(msg)
{
	this.successMsg = (msg == undefined || msg.length == 0) 
		? "İstek başarıyla tamamlandı." 
				: msg.trim();
}

BordomorAjaxRunner.prototype.setFailDialog = function(msg)
{
	this.failMsg = (msg == undefined || msg.length == 0) 
		? "Sunucuya ulaşmaya çalışırken bir hata olustu!\nLütfen daha sonra tekrar deneyin." 
				: msg.trim();
}

BordomorAjaxRunner.prototype.run = function(errorRegExp, contentType)
{
	let procDialog = null;
	
	try
	{
		this.runnerXMLHttp.onreadystatechange = function()
		{
			if(this.readyState == 4)
			{
				let thatXMLHttp = this;
				
				setTimeout(function()
				{
					killProcDialog();
						
					if(thatXMLHttp.status == 200)
					{
						let responseText = thatXMLHttp.responseText.trim();
						let isResponseValid = errorRegExp == undefined ? true : !new RegExp(errorRegExp).test(responseText);
						
						if(thatRunner.successMsg != null)
							new BordomorInfoDialogue("success", that.successMsg).print(dialogDisplayDurationMsec);
						
						if(thatRunner.runnerSuccessCallback != null)
						{
							let response = isResponseValid && thatRunner.expectedResponse == "xml" 
								? thatXMLHttp.responseXML 
										: responseText;
							thatRunner.runnerSuccessCallback(isResponseValid, response);
						}
					}
					else
						runFailAction(thatRunner, thatXMLHttp.status);
				
					thatRunner.runnerXMLHttp = null;
				}, latencyMsec);
			}
		}
		
		if(this.procMsg != null)
		{
			procDialog = new BordomorInfoDialogue("processing", this.procMsg);
			procDialog.print(-1);
		}
				
		this.runnerXMLHttp.open(this.mode, this.url, this.isAsync);
		
		if(this.contentType !== null)
			this.runnerXMLHttp.setRequestHeader("Content-Type", this.contentType);
			
		this.runnerXMLHttp.send(this.params);
	}
	catch(err)
	{
		this.runnerXMLHttp = null;
		killProcDialog();
		runFailAction(this, -1, err);
	}
	
	function killProcDialog()
	{
		if(procDialog != null)
		{
			procDialog.close();
			procDialog = null;
		}
	}
	
	function runFailAction(runner, status, err)
	{
		if(runner.failMsg != null)
			new BordomorInfoDialogue("error", runner.failMsg).print(dialogDisplayDurationMsec);
		
		if(runner.runnerFailCallback != null)
			runner.runnerFailCallback(status, err);
	}
}