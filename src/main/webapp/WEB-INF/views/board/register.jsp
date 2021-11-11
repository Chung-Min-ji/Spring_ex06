<%@ page
        language="java"
        contentType="text/html; charset=utf-8"
        pageEncoding="utf-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ include file ="../includes/header.jsp" %>

<link rel="stylesheet" href="../../../resources/css/board.css" type="text/css">


<div class="row">
    <div class="col-lg-12">
        <h1 class="page-header">Board Register</h1>
    </div>
    <!-- /.col-lg-12 -->
</div>
<!-- /.row -->

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">

            <div class="panel-heading">Board Register</div>
            <!-- /.pannel-heading -->
            <div class="panel-body">

                <form role="form" action="/board/register" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="form-group">
                        <label>Title</label>
                        <input class="form-control" name="title">
                    </div>

                    <div class="form-group">
                        <label>Text area</label>
                        <textarea class="form-control" row="3" name="content"></textarea>
                    </div>

                    <div class="form-group">
                        <label>Writer</label>
                        <input class="form-control" name="writer"
                               <%--스프링 시큐리티에서의 username: 사용자 id--%>
                               value='<sec:authentication property="principal.username"/>' readonly='readonly' >
                    </div>

                    <button type="submit" class="btn btn-default">Submit Button</button>
                    <button type="reset" class="btn btn-default">Reset Button</button>
                </form>
            </div>
            <!-- end panel-body -->

        </div>
        <!-- end panel-heading -->
    </div>
    <!-- end panel -->
</div>
<!-- /.row -->

<%--파일 첨부 영역--%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">

            <div class="panel-heading">File Attach</div>
            <!-- /.panner-heading -->
            <div class="panel-body">
                <div class="form-group uploadDiv">
                    <input type="file" name="uploadFile" multiple>
                </div>

                <div class="uploadResult">
                    <ul>

                    </ul>
                </div>

            </div>
            <!-- end panel-body -->

        </div>
        <!-- end panel-body -->
    </div>
    <!-- end panel -->

</div>
<!-- ./row -->


<script>

$(function(){

    var regex = new RegExp("(.*?)\.(exe|sh|zip|alz)$");
    var maxSize = 5242800; //5MB


    // 함수 정의
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


    function showUploadResult(uploadResultArr){

        if(!uploadResultArr || uploadResultArr.length == 0){
            return ;
        } //if

        var uploadUL = $(".uploadResult ul");
        var str = "";

        $(uploadResultArr).each(function(i, obj){

            if(obj.image){

                var fileCallPath = encodeURIComponent(obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);

                // 게시물 등록 시, 첨부파일의 정보도 함께 포함하기 위해 첨부파일 정보 추가.
                // (data-uuid, data-filename, data-type)
                str += "<li data-path='" + obj.uploadPath + "'";
                str += "data-uuid='" + obj.uuid + "' data-filename='" + obj.fileName + "' data-type='" + obj.image+ "'>";
                str + "<div>";

                str += "<span> " + obj.fileName + "</span>";
                str += "<button type='button'" +
                    "data-file=\'" + fileCallPath + "\'data-type='image'" +
                    "class=btn btn-warning btn-circle'><i class='fa fa-times'></i></button><br>";
                str += "<img src='/display?fileName=" + fileCallPath + "'>";
                str += "</div></li>";
            } else {

                var fileCallPath = encodeURIComponent(obj.uploadPath + "/" + obj.uuid + "_" + obj.fileName);
                var fileLink = fileCallPath.replace(new RegExp(/\\/g), "/");

                str += "<li data-path='" + obj.uploadPath + "' data-uuid='" + obj.uuid +
                    "'data-filename='" + obj.fileName + "'data-type='" + obj.image+ "'><div>";

                str += "<span> " + obj.fileName + "</span>";
                str += "<button type='button' " +
                    "data-file=\'" +fileCallPath+ "\'data-type='file'" +
                    "class='btn btn-warning btn-circle'><i class='fa fa-times'></i></button><br>";
                str += "<img src='../resources/img/attach.png'></a>";
                str += "</div></li>";
            } //if-else

        }); //traverse

        uploadUL.append(str);

    } //showUploadResult



    var formObj = $("form[role='form']");

    $("button[type='submit']").on("click", function(e){
        e. preventDefault();

        console.log("submit clicked.");

        var str="";

        $(".uploadResult ul li").each(function(i, obj){
            var jobj = $(obj);

            console.dir(jobj);

            str += "<input type='hidden' name='attachList[" + i + "].fileName' value='" + jobj.data("filename") +"'>";
            str += "<input type='hidden' name='attachList["+ i + "].uuid' value='" + jobj.data("uuid")+ "'>";
            str += "<input type='hidden' name='attachList["+ i + "].uploadPath' value='" + jobj.data("path")+ "'>";
            str += "<input type='hidden' name='attachList["+ i + "].fileType' value='" + jobj.data("type")+ "'>";

        }); //traverse

        formObj.append(str).submit();

    }); //on click for submit button


    //-------- 파일첨부 이벤트
    var csrfHeaderName = "${_csrf.headerName}";
    var csrfTokenValue = "${_csrf.token}";

    $("input[type='file']").change(function(e){
        var formData = new FormData();
        var inputFile = $("input[name='uploadFile']");
        var files = inputFile[0].files;

        for(var i = 0; i < files.length; i++){

            if(!checkExtension(files[i].name, files[i].size)){
                return false;
            } //if

            formData.append("uploadFile", files[i]);
        } //for

        $.ajax({
            url: '/uploadAjaxAction',
            processData: false,
            contentType: false,
            beforeSend: function(xhr){
                xhr.setRequestHeader(csrfHeaderName, csrfTokenValue);
            },
            data: formData,
            method: 'POST',
            dataType: 'json',
            success: function(result){
                console.log(result);

                showUploadResult(result); //업로드 결과 처리 함수
            } //success
        });// ajax

    }); //change for file input


    //--- x 아이콘 클릭 시, 파일 삭제
    $(".uploadResult").on("click", "button", function(e){
        console.log("delete file");

        var targetFile= $(this).data("file");
        var type = $(this).data("type");

        var targetLi = $(this).closest("li");

        $.ajax({
            url: '/deleteFile',
            data: {fileName: targetFile, type:type},
            beforeSend: function(xhr){
              xhr.setRequestHeader(csrfHeaderName, csrfTokenValue);
            },
            dataType: 'text',
            method: 'post',
            success: function(result){
                alert(result);

                targetLi.remove();
            } //success
        }); //ajax
    }); //on click for x button


}) //.jq
</script>

<%@include file="../includes/footer.jsp" %>
