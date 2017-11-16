function SuggestionProvider() {

};

SuggestionProvider.prototype.requestSuggestions = function (oAutoSuggestControl) {
    var sTextboxValue = oAutoSuggestControl.textbox.value;
    var xmlR = new XMLHttpRequest()
    if (sTextboxValue.length > 0){
        var requestURL = "/eBay/suggest?query="+encodeURI(sTextboxValue);

        xmlR.open("GET", requestURL);
        xmlR.onreadystatechange = function(){
            if(xmlR.readyState === 4 && xmlR.status === 200){
                showSuggestions(oAutoSuggestControl,xmlR)
            }
        };
        xmlR.send(null);
    } else {
        var aSuggestions = [];
        this.cur = -1;
        oAutoSuggestControl.autosuggest(aSuggestions);
    }
}

function showSuggestions(oAutoSuggestControl,xmlR) {
    var s = xmlR.responseXML.getElementsByTagName('CompleteSuggestion');

    var aSuggestions = [];
    for (var i=0; i < s.length; i++) {
        aSuggestions.push(s[i].childNodes[0].getAttribute("data"));
    }
    oAutoSuggestControl.autosuggest(aSuggestions);
};