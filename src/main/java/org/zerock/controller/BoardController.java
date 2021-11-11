package org.zerock.controller;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.PageDTO;
import org.zerock.service.BoardService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RequestMapping("/board/")

@Controller
@Log4j2
public class BoardController {

    @Setter(onMethod_ = @Autowired)
    private BoardService service;

    // 전체 리스트 가져오기
//    @GetMapping("/list")
//    public void list(Model model){
//        log.debug("list(model) invoked");
//
//        model.addAttribute("list", service.getList());
//    } //list


    // 전체 리스트 가져오기 with 페이징
    @GetMapping("/list")
    public void list(Criteria cri, Model model){
        log.debug("list({}, model) invoked.", cri);

        model.addAttribute("list", service.getList(cri));
//        model.addAttribute("pageMaker", new PageDTO(cri, 123));

        int total = service.getTotal(cri);

        log.info("total : " + total);

        model.addAttribute("pageMaker", new PageDTO(cri, total));
    } //list


    // 게시물 등록 화면
    @GetMapping("/register")
    @PreAuthorize("isAuthenticated()")  //로그인 성공한 사용자만 해당 기능 사용 가능 (표현식)
    public void register(){
        log.debug("register() invoked.");

    } //register


    // 새 게시물 등록하기
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")  //로그인 성공한 사용자만 해당 기능 사용 가능 (표현식)
    public String register(BoardVO board, RedirectAttributes rttrs){
        log.debug("register({}, rttrs) invoked.", board);

        log.info("=========== attach ===========");

        if(board.getAttachList() != null){
            board.getAttachList().forEach(
                    attach -> log.info(attach)
            ); //traverse
        } //if

        log.info("=========== / attach ===========");

        service.register(board);

        // 등록작업 끝난 후, 다시 목록 화면으로 이동할 때
        // 새롭게 등록된 게시물의 bno 함께 전달하기 위해 rttrs 사용
        rttrs.addFlashAttribute("result", board.getBno());

        // redirect: 접두어 -> 스프링mvc가 내부적으로 response.sendRedirect()처리
        return "redirect:/board/list";
    } //register


    // 특정 게시물 조회하기
    @GetMapping({"/get","/modify"})
    public void get(@RequestParam("bno") Long bno, @ModelAttribute("cri") Criteria cri, Model model){
        log.debug("get({}, model) invoked. /get or modify.", bno);

        model.addAttribute("board", service.get(bno));
    } //get


    // 게시물 수정
    @PreAuthorize("principla.username == #board.writer")    //파라미터로 board타입 객체 받으므로..
    @PostMapping("/modify")
    public String modify(BoardVO board, @ModelAttribute("cri") Criteria cri, RedirectAttributes rttrs){
        log.debug("modify({}, rttrs) invoked.", board);

        if (service.modify(board)){
            rttrs.addFlashAttribute("result", "success");

        } //if : 성공적으로 modify가 이루어졌으면...

        //-- 1. without getListLink()
//        rttrs.addAttribute("pageNum", cri.getPageNum());
//        rttrs.addAttribute("amount", cri.getAmount());
//        rttrs.addAttribute("type", cri.getType());
//        rttrs.addAttribute("keyword", cri.getKeyword());
//
//        return "redirect:/board/list";

        //-- 2. with getListLink()
        return "redirect:/board/list" + cri.getListLink();

    } //modify


    // 파일 삭제

    private void deleteFiles(List<BoardAttachVO> attachList){
        log.debug("deleteFiles({}) invoked.", attachList);

        if(attachList == null || attachList.size() == 0){
            return;
        } //if

        log.info("\t ++ delete attach files...");
        log.info("attachList :  {} ", attachList);

        attachList.forEach(attach->{
            try{
                Path file = Paths.get(UploadController.UPLOAD_PATH + attach.getUploadPath() + "/" +
                        attach.getUuid() + "_" + attach.getFileName());

                Files.deleteIfExists(file);

                if(Files.probeContentType(file).startsWith("image")){

                    Path thumbNail = Paths.get(UploadController.UPLOAD_PATH + attach.getUploadPath() +
                            "/s_" + attach.getUuid() + "_" + attach.getFileName());

                    Files.delete(thumbNail);
                } //if
            } catch(Exception e){
                log.error("delete file error : " + e.getMessage());
            } //try-catch

        }); //forEach
    } //deleteFiles



    // 게시물 삭제
    @PreAuthorize("principal.username == #writer")  //parameter로 전달받은 writer를 이용해서 체크
    @PostMapping("/remove")
    public String remove(@RequestParam("bno") Long bno,
                         @ModelAttribute("cri") Criteria cri,
                         RedirectAttributes rttrs,
                         String writer){
        log.debug("remove({}, rttrs) invoked.", bno);

        List<BoardAttachVO> attachList = service.getAttachList(bno);

        if (service.remove(bno)){

            //delete Attach Files
            deleteFiles(attachList);

            rttrs.addFlashAttribute("result", "success");
        } //if : 성공적으로 remove가 이루어졌으면..

//        //-- 1. without getListLink()
//        rttrs.addAttribute("pageNum", cri.getPageNum());
//        rttrs.addAttribute("amount", cri.getAmount());
//        rttrs.addAttribute("type", cri.getType());
//        rttrs.addAttribute("keyword", cri.getKeyword());
//
//        return "redirect:/board/list";

        //-- 2. with getListLink()
        return "redirect:/board/list" + cri.getListLink();
    } //remove


    @GetMapping(value="/getAttachList",
        produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<BoardAttachVO>> getAttachList(Long bno){
        log.debug("getAttachList({}) invoked.", bno);

        return new ResponseEntity<>(service.getAttachList(bno), HttpStatus.OK);
    } //getAttachList



} //BoardController
