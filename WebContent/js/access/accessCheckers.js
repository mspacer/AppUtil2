function AccessChecker(url, params) {	
	var _p = jQuery.extend({
		args: ['objectType', 'objectId'],
		alert: function(message) {
			window.alert(message);
		}
	}, params);

	function argsToMap(args, names) {
		var result = {};
		for(var i = 0; i < names.length && i < args.length-1; i++) {
			var value = args[i+1];
			if(null != value) {
				result[names[i]] =  value;
			}
		}
		return result;
	}

	this.checkAccess = function(callback) {
		jQuery.ajax({
		  type: "POST",
		  url: url,
		  data: argsToMap(arguments, _p.args),
		  success: function(json) {
			if(json.result) {
				callback(json.result);
			} else {
				_p.alert(json.message);
			}
		  },
		  error: function(jqXHR, textStatus, errorThrown) {
			  _p.alert('Произошла ошибка: '+(jqXHR.responseText || textStatus || errorThrown) +'.');
		  },
		  dataType: "json"
		});
	}
}

function SingleTypeAccessChecker(url, params) {
	this.checkAccess = new AccessChecker(url, jQuery.extend({args: ['objectId']}, params)).checkAccess;
}
