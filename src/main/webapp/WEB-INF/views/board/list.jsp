<%@ page
        language="java"
        contentType="text/html; charset=utf-8"
        pageEncoding="utf-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@include file="../includes/header.jsp"%>

        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header">Tables</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>
        <!-- /.row -->
        <div class="row">
            <div class="col-lg-12">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        Board List Page
                        <button id="regBtn" type="button" class="btn btn-xs pull-right">Register New Board</button>
                    </div>
                    <!-- /.panel-heading -->
                    <div class="panel-body">
                        <table class="table table-striped table-bordered table-hover">
                            <thead>
                            <tr>
                                <th>#번호</th>
                                <th>제목</th>
                                <th>작성자</th>
                                <th>작성일</th>
                                <th>수정일</th>
                            </tr>
                            </thead>

                            <c:forEach items="${list}" var="board">
                                <tr>
                                    <td><c:out value="${board.bno}"/></td>
                                    <td><a class="move" href="<c:out value='${board.bno}'/>">
                                        <c:out value='${board.title}'/>
                                        <b>[<c:out value="${board.replyCnt}"/>]</b>
                                    </a>
                                    </td>
                                    <td><c:out value="${board.writer}" /></td>
                                    <td><fmt:formatDate pattern="yyyy-MM-dd" value="${board.regdate}"/></td>
                                    <td><fmt:formatDate pattern="yyyy-MM-dd" value="${board.updateDate}"/></td>
                                </tr>
                            </c:forEach>
                        </table>

                        <div class="row">
                            <div class="col-lg-12">

                                <form id="searchForm" action="/board/list" method="get">
                                    <select name="type">
                                        <option value=""
                                            <c:out value="${pageMaker.cri.type == null ? 'selected' : ''}"/>>--</option>
                                        <option value="T"
                                            <c:out value="${pageMaker.cri.type eq 'T' ? 'selected' : ''}" />>제목</option>
                                        <option value="C"
                                            <c:out value="${pageMaker.cri.type eq 'C' ? 'selected' : ''}" />>내용</option>
                                        <option value="W"
                                            <c:out value="${pageMaker.cri.type eq 'W' ? 'selected' : ''}" />>작성자</option>
                                        <option value="TC"
                                            <c:out value="${pageMaker.cri.type eq 'TC' ? 'selected' : ''}" />>제목 or 내용</option>
                                        <option value="TW"
                                            <c:out value="${pageMaker.cri.type eq 'TW' ? 'selected' : ''}" />>제목 or 작성자</option>
                                        <option value="TWC"
                                            <c:out value="${pageMaker.cri.type eq 'TWC' ? 'selected' : ''}" />>제목 or 내용 or 작성자</option>
                                    </select>
                                    <input type= 'text' name='keyword' value='${pageMaker.cri.keyword}'/>
                                    <input type='hidden' name='pageNum' value='${pageMaker.cri.pageNum}'>
                                    <input type='hidden' name='amount' value='${pageMaker.cri.amount}'>
                                    <button class='btn btn-default'>Search</button>
                                </form>
                            </div>
                        </div>

                        <div class='pull-right'>
                            <form id="actionForm" action="/board/list" method="get">

                                <input type="hidden" name="pageNum" value="${pageMaker.cri.pageNum}">
                                <input type="hidden" name="amount" value="${pageMaker.cri.amount}">
                                <input type="hidden" name="type" value="${pageMaker.cri.type}">
                                <input type="hidden" name="keyword" value="${pageMaker.cri.keyword}">

                                <ul class="pagination">

                                <c:if test="${pageMaker.prev}">
                                        <li class="paginate_button previous">
                                            <a href="${pageMaker.startPage-1}">Previous</a>
                                    </li>
                                    </c:if>

                                <c:forEach var="num" begin="${pageMaker.startPage}" end="${pageMaker.endPage}">
                                    <li class="paginate_button ${pageMaker.cri.pageNum == num ? "active":""}">
                                            <a href="${num}">${num}</a></li>
                                </c:forEach>

                                <c:if test="${pageMaker.next}">
                                    <li class="paginate_button next">
                                            <a href="${pageMaker.endPage + 1}">Next</a></li>
                                </c:if>
                            </ul>
                            </form>
                        </div>
                        <!-- end Pagination -->

                        <!-- Modal -->
                        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
                                    </div>
                                    <div class="modal-body">
                                        처리가 완료되었습니다.
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                                        <button type="button" class="btn btn-primary">Save changes</button>
                                    </div>
                                </div>
                                <!-- /.modal-content -->
                            </div>
                            <!-- /.modal-dialog -->
                        </div>
                        <!-- /.modal -->

                    </div>
                    <!-- /.panel-body -->
                </div>
                <!-- /.panel -->
            </div>
            <!-- /.col-lg-6 -->
        </div>
        <!-- /.row -->


<script type="text/javascript">
    $(function(){
        console.log("list jq started...");

        var result = '<c:out value="${result}" />';

        console.log("result : ", result);

        checkModal(result);

        //replaceState(data: any, title: string, url?: string | null): void;
        history.replaceState({},null,null); //result값 history state에 저장 (뒤로가기버튼 클릭시 모달 띄우지 않기 위해)


        //---------------checkModal
        function checkModal(result){
            console.log("checkModal({}) invoked.", result);

            if (result === '' || history.state ){
                return;
            } //if : controller로부터 result값이 넘어오지 않았으면 (성공x) || 이미 처리되어 history.state에 쌓여있으면

            if(parseInt(result) > 0) {
                $(".modal-body").html("게시글 " + parseInt(result) + " 번이 등록되었습니다.");

                console.log("show modal");
            } //if : controller로부터 result값이 넘어왔으면.. (성공)

            $("#myModal").modal("show");
        } //checkModal


        //---------------event
        $("#regBtn").on("click", function(){
            self.location ="/board/register";

        }); //on click for regBtn


        $(".move").on("click", function(e){
            e.preventDefault();

            actionForm.append("<input type='hidden' name='bno' value='"+$(this).attr("href")+"'>");
            actionForm.attr("action", "/board/get");
            actionForm.submit();
        }); //on click for move


        //---------------paging
        var actionForm = $("#actionForm");

        $(".paginate_button a").on("click", function(e){
            e.preventDefault();

            console.log("click for paginate_button");

            actionForm.find("input[name='pageNum']").val($(this).attr("href"));
            actionForm.submit();
        }); //on click for pagenate button


        //--------------searching
        var searchFrom = $("#searchForm");

        $("#searchForm button").on("click", function (e){
            if(!searchFrom.find("option:selected").val()){
                alert("검색 종류를 선택하세요.");

                return false;
            } //if : 검색조건이 선택되지 않았으면

            if(!searchFrom.find("input[name='keyword']").val()){
                alert("키워드를 입력하세요.");

                return false;
            } //if : 키워드가 입력되지 않았으면

            searchFrom.find("input[name='pageNum']").val("1");
            e.preventDefault();

            searchFrom.submit();
        }); //on click for search button

    }) //.jq


</script>

<%@include file="../includes/footer.jsp" %>
