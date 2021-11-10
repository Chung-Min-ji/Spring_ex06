package org.zerock.mapper;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.zerock.domain.MemberVO;

@RunWith(SpringRunner.class)
@ContextConfiguration("file:src/main/webapp/*/spring/*.xml")
@Log4j2
public class MemberMapperTests {

    @Setter(onMethod_=@Autowired)
    private MemberMapper mapper;

    @Test
    public void testRead(){
        MemberVO vo = mapper.read("admin90");

        log.info("\t + vo : " + vo);

        vo.getAuthList().forEach(authVO -> log.info(authVO));

    } //testRead

} //end class
