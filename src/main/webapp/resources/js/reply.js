console.log("Reply js started....");

var replyService = (function(){

    function add(reply, callback, error) {
        console.log("add reply.....");

        $.ajax({
            method: 'post',
            url: '/replies/new',
            data: JSON.stringify(reply),
            contentType: "application/json; charset=urf-8",
            success: function (result, status, xhr) {
                if (callback) {
                    callback(result);
                } //if
            }, //success for ajax

            error: function (xhr, status, er) {
                if (error) {
                    error(er);
                }
            } //error for ajax
        }); //ajax
    } //add


    function getList(param, callback, error){
        var bno = param.bno;
        var page = param.page || 1;

        $.getJSON("/replies/pages/" + bno + "/" + page + ".json",
            function(data){
                if(callback){
                    // 댓글 목록만 가져오는 경우
                    // callback(data);

                    // 댓글 숫자와 목록을 가져오는 경우
                    callback(data.replyCnt, data.list);
                } //if
            }).fail(function(xhr, status, err){
                if(error){
                    error();
                } //if
        }); //function
    } //getList


    function remove(rno, replyer, callback, error){
        $.ajax({
            method: 'delete',
            url: '/replies/' + rno,

            data : JSON.stringify({rno:rno, replyer:replyer}),
            contentType:"application/json; charset=utf-8",

            success: function(deleteResult, status, xhr){
                if(callback){
                    callback(deleteResult);
                } //if
            }, //success
            error: function(xhr, status, er){
                if(error){
                    error(er);
                } //if
            } //error
        }); //ajax
    } //remove


    function update(reply, callback, error){
        console.log("RNO : " + reply.rno);

        $.ajax({
            method: 'put',
            url: '/replies/' + reply.rno,
            data: JSON.stringify(reply),
            contentType: "application/json; charset=utf-8",
            success: function(result, status, xhr){
                if(callback){
                    callback(result);
                } //if
            }, //success
            error: function (xhr, status, er){
                if(error){
                    error(er);
                } //if
            } //error
        }); //ajax
    } //update

    function get(rno, callback, error){
        $.get("/replies/" + rno + ".json", function(result){
            if(callback){
                callback(result);
            } //if
        }).fail(function(xhr, status, err){
            if(error){
                error();
            }//if
        }); //fail
    } //get

    function displayTime(timeValue){
        var today = new Date();

        var gap = today.getTime() - timeValue;

        var dateObj = new Date(timeValue);
        var str= "";

        if (gap < (1000 * 60 * 60 * 24)){

            var hh = dateObj.getHours();
            var mi = dateObj.getMinutes();
            var ss = dateObj.getSeconds();

            return [ ( hh>9 ? '' : '0') + hh , ':',
                (mi>9 ? '' : '0') + mi, ':',
                (ss>9 ? '' : '0') + ss ].join('');

        } else {

            var yy = dateObj.getFullYear();
            var mm = dateObj.getMonth() + 1; //getMonth() is zero-based
            var dd = dateObj.getDate();

            return [ yy, '/', (mm > 9 ? '' : '0') + mm, '/',
                (dd > 9 ? '' : '0') + dd].join('');
        } //if-else
    }; //displayTime

    return {
        add : add,
        getList : getList,
        remove : remove,
        update : update,
        get : get,
        displayTime : displayTime
    }; //return

})(); //IIFE