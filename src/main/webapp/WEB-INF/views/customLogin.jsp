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

    <!-- Core CSS - Include with every page -->
    <link href="/resources/sb-admin-v2/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="/resources/sb-admin-v2/font-awesome/css/font-awesome.css" rel="stylesheet" type="text/css">

    <!-- SB Admin CSS - Include with every page -->
    <link href="/resources/sb-admin-v2/css/sb-admin.css" rel="stylesheet">

</head>

<body>

<div class="container">
    <div class="row">
        <div class="col-md-4 col-md-offset-4">
            <div class="login-panel panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Please Sign In</h3>
                </div>
                <div class="panel-body">
                    <form role="form" method="post" action="/login">
                        <fieldset>
                            <div class="form-group">
                                <input class="form-control" placeholder="userid" name="username" type="text" autofocus>
                            </div>
                            <div class="form-group">
                                <input class="form-control" placeholder="Password" name="password" type="password" value="">
                            </div>
                            <div class="checkbox">
                                <label><input name="remember-me" type="checkbox">Remember Me</label>
                            </div>
                            <!-- Changes this to a button or input when using this as a form -->

                            <a href="index.html" class="btn btn-lg btn-success btn-block">Login</a>
                        </fieldset>

                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Core Scripts - Include with every page -->
<script src="/resources/sb-admin-v2/js/jquery-1.10.2.js"></script>
<script src="/resources/sb-admin-v2/js/bootstrap.min.js"></script>
<script src="/resources/sb-admin-v2/js/plugins/metisMenu/jquery.metisMenu.js"></script>

<!-- SB Admin Scripts - Include with every page -->
<script src="/resources/sb-admin-v2/js/sb-admin.js"></script>

<script>
    $(".btn-success").on("click", function(e){

        e.preventDefault();
        $("form").submit();
    }); //on click for btn-success
</script>

</body>

</html>
