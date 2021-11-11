<%@ page
        language="java"
        contentType="text/html; charset=utf-8"
        pageEncoding="utf-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>

    <style>

        .uploadResult{
            width: 100%;
            background-color: gray;
        }

        .uploadResult ul{
            display: flex;
            flex-flow: row;
            justify-content: center;
            align-items: center;
        }

        .uploadResult ul li{
            list-style: none;
            padding: 10px;
            align-content: center;
            text-align: center;
        }

        .uploadResult ul li img{
            width: 100px;
        }

        .uploadResult ul li span{
            color:white;
        }

        .bigPictureWrapper{
            position: absolute;
            display: none;
            justify-content: center;
            align-items: center;
            top: 0%;
            width: 100%;
            height: 100%;
            background-color: gray;
            z-index: 100;
            background: rgba(225,225,225,0.5);
        }

        .bigPicture{
            position: relative;
            display: flex;
            justify-content: center;
            align-item: center;
        }

        .bigPicture img{
            width: 600px;
        }
    </style>

</head>
<body>
<h1>Upload With Ajax</h1>


<%--파일선택 영역--%>
<div class="uploadDiv">
    <input type="file" name="uploadFile" multiple>
</div>


<%--첨부파일 목록--%>
<div class="uploadResult">
    <ul>
    </ul>
</div>

<%--업로드 버튼--%>
<button id="uploadBtn">Upload</button>

<%--실제 원본 이미지 보여주는 영역--%>
<div class="bigPictureWrapper">
    <div class="bigPicture">
    </div>
</div>


<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-migrate/3.3.2/jquery-migrate.min.js"></script>

<script>

    // 섬네일 클릭 시, 원본 이미지 보여주기
    // <a>태그에서 직접 호출하기 위해, jq 밖에서 정의
    function showImage(fileCallPath){
        // alert(fileCallPath);

        $(".bigPictureWrapper").css("display", "flex").show();

        $(".bigPicture")
            .html("<img src='/display?fileName=" + encodeURI(fileCallPath) + "'>")
            .animate({widtH:'100%', height:'100%'}, 1000);
    } //showImage


    $(function(){

        // 파일 확장자
        var regex = new RegExp("(.*?)\.(exe|sh|zip|alz)$");

        // 파일 최대 사이즈
        var maxSize = 5242880; //5MB

        // 업로드 부분 초기화에 사용하기 위해,
        // 아무 내용이 없는 <input type='file'> 을 clone.
        var cloneObj = $(".uploadDiv").clone();

        // 첨부파일 목록 div
        var uploadResult = $(".uploadResult");


        //-------- 파일 확장자와 크기 검사
        function checkExtension(fileName, fileSize){

            if(fileSize >= maxSize){
                alert("파일 사이즈 초과");
                return false;
            } //if

            if(regex.test(fileName)){
                alert("해당 종류의 파일은 업로드 할 수 없습니다.");
                return false;
            } //if

            return true;
        } //checkExtension


        //--------- 첨부파일 목록 생성
        function showUploadedFile(uploadResultArr){
            console.log("uploadResultArr : {}", uploadResultArr);

            var str = "";

            $(uploadResultArr).each(function(i, obj){
                if(!obj.image){

                    var fileCallPath =
                        encodeURIComponent(obj.uploadPath + "/" + obj.uuid + "_" + obj.fileName);

                    var fileLinke = fileCallPath.replace(new RegExp(/\\/g), "/");

                    str += "<li><a href='/download?fileName=" + fileCallPath + "'>"
                        + "<img src='/resources/img/attach.png' width='100', height='100' >"+ obj.fileName +"</a>"
                        + "<sapn data-file=\'" + fileCallPath + "\' data-type='file'> x </span>"
                        + "</div></li>";

                } else {
                    var fileCallPath =
                        encodeURIComponent(obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);

                    var originPath = obj.uploadPath + "\\" + obj.uuid + "_" + obj.fileName;

                    originPath = originPath.replace(new RegExp(/\\/g), "/");

                    str += "<li><a href=\"javascript:showImage(\'" + originPath
                        + "\')\"><img src='/display?fileName=" + fileCallPath
                        + "'></a><span data-file=\'" + fileCallPath + "\' data-type='image'> x </span></li>";

                } //if-else

            }); //traverse

            uploadResult.append(str);

        } //showuploadedFile


        //----------- 업로드버튼 클릭 이벤트
        $("#uploadBtn").on("click", function(e){
            var formData = new FormData();
            var inputFile = $("input[name='uploadFile']");
            var files=inputFile[0].files;

            console.log("files : {}", files);

            // add File Data to formData
            // 첨부파일 데이터를 formData 에 추가해서, formData 자체를 전송.
            for(var i = 0; i< files.length; i++){

                if(!checkExtension(files[i].name, files[i].size)){
                    return false;
                } //if

                console.log("checkExtension passed...");
                formData.append("uploadFile", files[i]);
            } //for

            $.ajax({
                url: 'uploadAjaxAction',
                // processData와 contentType은 false로 지정해야 전송됨.

                // processData란?
                // 일반적으로 서버에 전송되는 데이터는 쿼리스트링 형태로 전달되는데,
                // 파일전송의 경우 이를 막기 위해 false 지정.
                processData: false,

                // contentType의 default는 application/x-www-form-urlencoded; charset=UTF-8 인데,
                // 이를 multipart/form-data 로 전송하기 위해 false 지정.
                contentType: false,
                data: formData,
                method: 'post',
                dataType: 'json',
                success: function(result){

                    console.log("result : {}" , result);

                    // 첨부파일 목록 생성
                    showUploadedFile(result);

                    // 첨부파일 부분 초기화
                    $(".uploadDiv").html(cloneObj.html());
                } //success
            }); //ajax
        }); //on click for uploadBtn


        //----------원본 이미지 클릭 이벤트
        $(".bigPictureWrapper").on("click", function(e){
            $(".bigPicture").animate({width:'0%', height:'0%'}, 1000);

            // setTimeout() 에 적용된 arrow function: Chrome에서는 정상 작동하지만, IE11에서는 작동하지 않음.
            setTimeout(() => {
                $(this).hide();
                }, 1000);
        }); //on click for bigPictureWrapper


        //-----------x 표시에 대한 이벤트 처리(첨부파일 삭제)
        // <span>태그는 첨부파일 업로두 후에 생성되므로, 이벤트 위임방식 적용
        $(".uploadResult").on("click", "span", function(e){
            var targetFile = $(this).data("file");
            var type = $(this).data("type");
            console.log(targetFile);

            $.ajax({
                url: '/deleteFile',
                data: {fileName: targetFile, type:type},
                dataType: 'text',
                method: 'POST',
                success: function(result){
                    alert(result);
                } //success
            }); //ajax
        }); //on click for x button
    }) //.jq
</script>

</body>
</html>