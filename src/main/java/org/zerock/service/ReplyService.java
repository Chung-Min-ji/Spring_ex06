package org.zerock.service;

import org.zerock.domain.Criteria;
import org.zerock.domain.ReplyPageDTO;
import org.zerock.domain.ReplyVO;

import java.util.List;

public interface ReplyService {

    public int register(ReplyVO vo);
    public ReplyVO get(Long rno);
    public int modify(ReplyVO vo);
    public int remove(Long rno);
    public List<ReplyVO> getList(Criteria cri, Long bno);

    //댓글 목록 페이징처리
    public ReplyPageDTO getListPage(Criteria cri, Long bno);

} //end interface
