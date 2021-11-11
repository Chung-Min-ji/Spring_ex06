package org.zerock.mapper;

import org.zerock.domain.BoardAttachVO;

import java.util.List;

public interface BoardAttachMapper {

    public void insert(BoardAttachVO vo);
    public void delete(String uuid);                    // 첨부파일 삭제(DB)
    public List<BoardAttachVO> findByBno(Long bno);     // 특정 게시물 번호로 첨부파일 찾기
    public void deleteAll(Long bno);                    // 첨부파일 삭제(DB + 로컬의 원본 파일)
    public List<BoardAttachVO> getOldFiles();           // db에서 첨부파일 목록 가져오기 (로컬 파일과 비교해서, db에 없는 로컬 파일은 삭제해주기 위함.)
                                                        // 이 때, 오늘 업로드 된 파일은 검색하지 않는다. (현재 등록/수정중인 파일이 포함되기 때문)
} //end interface
