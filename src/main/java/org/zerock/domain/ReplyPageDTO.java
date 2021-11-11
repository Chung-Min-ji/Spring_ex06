package org.zerock.domain;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReplyPageDTO {

    private int replyCnt;       // 전체 댓글 수
    private List<ReplyVO> list; // 댓글 전체 목록
} //end class
