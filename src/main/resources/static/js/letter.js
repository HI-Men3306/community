$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	//获取接收方 和 内容框中的值
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	//发送异步请求
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName,"content":content},
		function(data) {
			data = $.parseJSON(data);
			if(data.code == 0) {
				$("#hintBody").text("发送成功!");
			} else {
				$("#hintBody").text(data.msg);
			}

			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();//发送私信之后 重新加载页面
			}, 2000);
		}
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}