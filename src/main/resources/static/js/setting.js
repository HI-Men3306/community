$(function(){
    $("#uploadForm").submit(upload);
});

//异步上传头像到七牛云
function upload() {
    alert("正在上传！")
    $.ajax({
        url: "http://upload-z1.qiniup.com",//七牛云上传文件的url
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#uploadForm")[0]),//将用户上传的头像 打包进data中
        success: function(data) {
            if(data && data.code == 0) {//上传头像到七牛云成功
                alert("上传成功,正在更新数据库!")
                //再发送一个异步请求用来 更新数据库中的用户的url地址
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},//上传头像的名称
                    function(data) {
                        alert("更新数据库成功！")
                        data = $.parseJSON(data);
                        if(data.code == 0) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            } else {
                alert("上传失败!");
            }
        }
    });
    return false;
}