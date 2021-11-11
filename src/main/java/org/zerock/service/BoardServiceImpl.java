package org.zerock.service;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.domain.BoardAttachVO;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.mapper.BoardAttachMapper;
import org.zerock.mapper.BoardMapper;

import java.util.List;


@Service

@Log4j2
@AllArgsConstructor
public class BoardServiceImpl implements BoardService{

    // 스프링 4.3 이후부터는, 주입받으려는 필드가 하나일 때 어노테이션 안붙여도 자동주입 됨.
    @Setter(onMethod_ = @Autowired)
    private BoardMapper mapper;

    @Setter(onMethod_ = @Autowired)
    private BoardAttachMapper attachMapper;


    // 게시물 등록
    @Transactional
    @Override
    public void register(BoardVO board){
        log.debug("register({}) invoked.", board);

        mapper.insertSelectKey(board);

        if(board.getAttachList() == null || board.getAttachList().size() <= 0){
            return ;
        } //if

        board.getAttachList().forEach(attach -> {

            log.info("\t ** attach : " + attach);

            attach.setBno(board.getBno());
            attachMapper.insert(attach);
        }); //forEach

    } //register


    // 특정 게시물 조회
    @Override
    public BoardVO get(Long bno) {
        log.debug("get(bno) invoked.");

        log.info("bno : " + bno);

        return mapper.read(bno);
    } //get


    // 게시물 수정
    @Transactional
    @Override
    public boolean modify(BoardVO board) {
        log.debug("modify(board) invoked.");

        // 기존 첨부파일 관련 데이터 모두 삭제한 후, 다시 첨부파일 데이터 추가
        attachMapper.deleteAll(board.getBno());

        boolean modifyResult = mapper.update(board)==1;

        if(modifyResult && board.getAttachList() != null && board.getAttachList().size() >0 ){
            board.getAttachList().forEach(attach ->{

                attach.setBno(board.getBno());
                attachMapper.insert(attach);
            }); //forEach
        } //if

        return modifyResult;
    } //modify


    // 게시물 삭제 (첨부파일 함께 삭제되도록 트랜잭션 처리)
    @Transactional
    @Override
    public boolean remove(Long bno) {
        log.debug("remove({}) invoked.", bno);

        attachMapper.deleteAll(bno);

        return mapper.delete(bno) == 1;
    } //remove


    // 전체 게시물 목록 조회
//    @Override
//    public List<BoardVO> getList() {
//        log.debug("getList() invoked");
//
//        return mapper.getList();
//    } //getList


    // 전체 게시물 조회 with 페이징
    @Override
    public List<BoardVO> getList(Criteria cri){
        log.debug("getList(cri) invoked. cri : " + cri);

        return mapper.getListWithPaging(cri);
    } //getList


    // 전체 게시물 수 조회
    @Override
    public int getTotal(Criteria cri){
        log.debug("getTotal(cri) invoked.");

        return mapper.getTotalCount(cri);
    } //getTotal


    // bno로 첨부파일 목록 가져오기
    @Override
    public List<BoardAttachVO> getAttachList(Long bno){
        log.debug("getAttachList({}) invoked.", bno);

        return attachMapper.findByBno(bno);
    } //getAttachList

} //BoardServiceImpl

