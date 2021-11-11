package org.zerock.service;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.domain.Criteria;
import org.zerock.domain.ReplyPageDTO;
import org.zerock.domain.ReplyVO;
import org.zerock.mapper.BoardMapper;
import org.zerock.mapper.ReplyMapper;

import java.util.List;

@Service
@Log4j2
public class ReplyServiceImpl implements ReplyService{

    @Setter(onMethod_=@Autowired)
    private ReplyMapper mapper;

    // 반정규화로 인해, 댓글의 추가/삭제시 BoardMapper 함께 이용해야 함. (트랜잭션 처리)
    @Setter(onMethod_ = @Autowired)
    private BoardMapper boardMapper;


    // 댓글 등록의 경우, 파마리터로 전달받은 ReplyVO 내에 게시물의 번호가 존재하므로, 이를 이용해 댓글 추가.
    @Transactional
    @Override
    public int register(ReplyVO vo){
        log.debug("register({}) invoked.", vo);

        boardMapper.updateReplyCnt(vo.getBno(), 1);

        return mapper.insert(vo);
    } //register


    @Override
    public ReplyVO get(Long rno){
        log.debug("get({}) invoked.", rno);

        return mapper.read(rno);
    } //get


    @Override
    public int modify(ReplyVO vo){
        log.debug("modify({}) invoked.", vo);

        return mapper.update(vo);
    } //modify


    // 댓글 삭제는, 전달되는 파라미터가 댓글의 번호인 rno만을 받으므로,
    // 해당 댓글의 게시물을 알아내는 과정이 필요함.
    // (파라미터로 게시물 번호 bno를 받을 수 있다면 좋겠지만, 그럴 경우 ReplyController까지 수정해야 함.)
    @Transactional
    @Override
    public int remove(Long rno){
        log.debug("remove({}) invoked.", rno);

        ReplyVO vo = mapper.read(rno);

        boardMapper.updateReplyCnt(vo.getBno(), -1);

        return mapper.delete(rno);
    } //remove


    @Override
    public List<ReplyVO> getList(Criteria cri, Long bno){
        log.debug("getList({},{}) invoked.", cri, bno);

        return mapper.getListWithPaging(cri, bno);
    } //getList


    @Override
    public ReplyPageDTO getListPage(Criteria cri, Long bno){
        log.debug("getListPage({},{}) invoked.", cri, bno);

        ReplyPageDTO dto = new ReplyPageDTO( mapper.getCountByBno(bno),
                mapper.getListWithPaging(cri, bno));

        log.info("\t + dto : {}" , dto);

        return dto;
    } //getListPage


} //end class
