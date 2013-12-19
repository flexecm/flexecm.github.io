

var isMobile =/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent);
var isTouchDevice = isMobile;

function attachEvent(src, cb) {
	$(src).unbind();
	if (isTouchDevice) {
		$(src).bind("touchstart", function() {
			$(this).addClass("pressed");
		});
		$(src).bind("touchend",  function() {
			$(this).removeClass("pressed");
			
			$(this).data("moved", false);
			if($(this).data("moved")!=true) {
				cb($(this));
			}
			
		});
		$(src).on('touchmove',function (e){
			$(this).data("moved", true);
			$(this).removeClass("pressed");
		});
	} else {
		$(src).bind("mousedown", function() {
			$(this).addClass("pressed");
		});
		
		$(src).bind("mouseup", function() {
			$(this).removeClass("pressed");
			$(this).siblings().removeClass("current");
			$(this).addClass("current");
			cb($(this));
		});
	}
}

var Utils = {
		arrayIntersection: function(x, y) {
			 var ret = [];
			    for (var i = 0; i < x.length; i++) {
			        for (var z = 0; z < y.length; z++) {
			            if (x[i] == y[z]) {
			                ret.push(x[i]);
			                break;
			            }
			        }
			    }
			    return ret;  	
		},
		
		arrayIntersected: function(x, y ) {
			for (var i = 0; i < x.length; i++) {
		        for (var z = 0; z < y.length; z++) {
		            if (x[i] == y[z]) {
		                return true;
		            }
		        }
		    }
			return false;
		},
		
		//将字符串精简到指定的长度
		shortenString: function(str, len) {
			if (str.length<=len) return str;
			return str.substring(0,len-2) + "..";
		},
		
		formatHour: function(millsec) {
			var hour = Math.floor(millsec/(1000*60*60));
			var t = new Date(millsec);
			return ((hour<10)?("0" + hour):hour) + ":" + ((t.getMinutes()<10)?("0" + t.getMinutes()):t.getMinutes() ) + ":" + ((t.getSeconds()<10)?("0" + t.getSeconds()):t.getSeconds() );
		},
		/**
		 *以更人性化方式展示时间，
		 *小于1分钟 ：  刚刚
		 *1-2分钟： 一分钟前
		 *小于60分钟 ： n分钟前
		 * ....
		 */
		formatTime: function(millsec) {
				var date = new Date(millsec),
					diff = (((new Date()).getTime() - date.getTime()) / 1000),
					day_diff = Math.floor(diff / 86400);
						
				if ( isNaN(day_diff) || day_diff < 0 || day_diff >= 31 )
					return;
				if (day_diff>=31) {
					return date.getFullYear() + "年" + (date.getMonth()+1) + "月" + date.getDate() + "日";
				}
				return day_diff == 0 && (
						diff < 60 && "刚刚" ||
						diff < 120 && "1 分钟前" ||
						diff < 3600 && Math.floor( diff / 60 ) + " 分钟前" ||
						diff < 7200 && "1 小时前" ||
						diff < 86400 && Math.floor( diff / 3600 ) + " 小时前") ||
					day_diff == 1 && ("昨天" + date.getHours() + ":" + (date.getMinutes()<10?("0"+date.getMinutes()):date.getMinutes())) ||
					day_diff < 7 && day_diff + " 天前" ||
					day_diff < 31 && Math.ceil( day_diff / 7 ) + " 周前";
		},
		
		//格式化显示文件大小
		formatFileSize: function(n) {
				if (n > 1073741824) {
					return Math.round(n / 1073741824, 1) + " GB";
				}
				if (n > 1048576) {
					return Math.round(n / 1048576, 1) + " MB";
				}
				if (n > 1024) {
					return Math.round(n / 1024, 1) + " KB";
				}
				return n + " 字节";
		},
		
		getFileExt:function(name) {
			var pos = name.lastIndexOf(".");
			if(pos>-1) {
				return "";
			} else {
				return name.substring(pos+1);
			}
		},
		
		arrayProject: function(ary, field) {
			var result = [];
			
			for ( var i = 0; i < ary.length; i++) {
				result.push(ary[i][field]);
			}
			return result;
		},
		
		isEmail: function(str) {
			var myRegExp = /[a-z0-9-]{1,30}@[a-z0-9-]{1,65}.[a-z]{3}/ ;
			return myRegExp.test(str);
		},
		
		//生成一个10位的随机密码
		genPass: function() {
			var x="123456789poiuytrewqasdfghjklmnbvcxzQWERTYUIPLKJHGFDSAZXCVBNM";
		 	var tmp="";
		 	var ran = Math.random();
		 	for(var i=0;i<10;i++) {
		 		ran *=10;
				tmp += x.charAt(Math.ceil(ran)%x.length);
		 	}
		 	return tmp;
		}
};

function initUi() {
	/*
	attachEvent($("ul.navs li"), function(t) {
		$("ul.navs li").removeClass("current");
		$(t).addClass("current");
		clear();
		$("#" + $(t).attr("target")).show();
	});
	*/
	$("div.oper").hide();
	clear();
}

function clear() {
	hideMessage();
	hideDialog();
	$(".base .center>div").hide();
	$("div.head div.oper").hide();
}

function hideMessage() {
	$("#msg").slideUp();
}

function hideDialog(dialogname) {
	$("div.hide-bg").hide();
	if (dialogname==null) {
		$("div.dialog").hide();
	} else {
		$("#" + dialogname).hide();
	}
}

function showDialog(dialogname) {
	closeDialog();
	$("div.hide-bg").show();
	var left = ($(window).width() - parseInt($("#" + dialogname).css("width")))/2;
	var top = ($(window).height() - parseInt($("#" + dialogname).css("height")))/2;
	$("#" + dialogname).css("left", left);
	$("#" + dialogname).css("top", top);
	$("#" + dialogname).show();
}

function closeDialog() {
	$("div.hide-bg").hide();
	$(".dialog").fadeOut("fast");
}

function showPenddingMsg(text) {
	showDialog("pendding-dialog");
	$("#pendding-dialog div.content").html(text);
}

function showAlert(text, func) {
	showDialog("alert-dialog");
	$("#alert-dialog div.content").html(text);
	
	attachEvent($("#alert-dialog a.button"), function() {
		closeDialog();
		if (func!=null) {
			func();
		}
	});
}

function logout() {
	location.href = "/logout";
}

function showMessage(m, h) {
	if (typeof m === 'string') {
		$("#msg").html(m);
	} else {
		$("#msg").html("");
		$("#msg").append(m);
	}

	$("#msg").show();
	if (h) {
		setTimeout("hideMessage()", h);
	}
}
