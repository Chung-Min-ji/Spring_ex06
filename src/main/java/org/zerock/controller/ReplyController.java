package org.zerock.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zerock.domain.Criteria;
import org.zerock.domain.ReplyPageDTO;
import org.zerock.domain.ReplyVO;
import org.zerock.service.ReplyService;

@RequestMapping("/replies/")
@RestController
@Log4j2
@AllArgsConstructor
public class ReplyController {

    @Autowired
//    @Setter(onMethod_=@Autowired)
    private ReplyService service;


    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="/new",
            consumes = "application/json",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> create(@RequestBody ReplyVO vo){
        log.debug("create({}) invoked.", vo);

        int insertCount = service.register(vo);

        log.info("Reply INSERT COUNT : " + insertCount);

        return insertCount == 1
                ? new ResponseEntity<>("success", HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } //create


    @GetMapping(value="/pages/{bno}/{page}",
        produces = {
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE
        })
    // @PathVariable : 맵핑된 URL 경로에 {}로 처리된 부분에 바인딩 해준다.
        // @RequestParam은 localhost:8090/page=1&bno=1 의 형태로 전달된다면,
        // @PathVariable은 localhost:8090/1/1 의 형태로 전달됨.
        // 즉, 파라미터의 값과 이름을 함께 전달해야 할 때는 RequestParam 을 사용하고
        // RESP api에서 값을 호출할때는 주로 PathVariable을 사용한다.
    public ResponseEntity<ReplyPageDTO> getList(@PathVariable("page") int page,
                                                @PathVariable("bno") Long bno){
        log.debug("getList({},{}) invoked.", page, bno);

        Criteria cri = new Criteria(page, 10);

        log.info("\t + cri : " + cri);

        return new ResponseEntity<>(service.getListPage(cri, bno), HttpStatus.OK);
    } //getList


    @GetMapping(value="/{rno}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            })
    public ResponseEntity<ReplyVO> get(@PathVariable("rno") Long rno){
        log.debug("get({}) invoked.", rno);

        return new ResponseEntity<>(service.get(rno), HttpStatus.OK);
    } //get


    // -------- 댓글 삭제
    @PreAuthorize("principal.username == #vo.replyer")
    @DeleteMapping(value="/{rno}",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> remove(@RequestBody ReplyVO vo,
                                         @PathVariable("rno") Long rno){
        log.debug("remove({}, {}) invoked.", vo, rno);

        log.info("\t + replyer : " + vo.getReplyer());

        return service.remove(rno) == 1
                ? new ResponseEntity<>("success", HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);    // 500 Error
    } //remove


    // ------- 댓글 수정
    @PreAuthorize("principal.username == #vo.replyer")
    @RequestMapping(method={RequestMethod.PUT, RequestMethod.PATCH},
        value="/{rno}",
        consumes = "application/json",
        produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> modify(@RequestBody ReplyVO vo,
                                         @PathVariable("rno") Long rno){
        log.debug("modify({},{}) invoked.", vo, rno);

        vo.setRno(rno);

        log.info("\t + rno : {}", rno);
        log.info("\t modify : " + vo);

        return service.modify(vo) == 1
                ? new ResponseEntity<>("success", HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } //modify

} //end class
