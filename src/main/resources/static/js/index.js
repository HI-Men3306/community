$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	// 发送异步请求(POST)
	$.post(
		//这个CONTEXT_PATH为项目的全局前缀 在global.js文件中
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		function(data) {//这里是回调函数  data为返回值
			data = $.parseJSON(data);
			// 在提示框中显示返回消息
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 2秒后,自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 刷新页面
				if(data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);

}