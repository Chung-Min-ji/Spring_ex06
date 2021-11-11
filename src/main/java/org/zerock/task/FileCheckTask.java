package org.zerock.task;//package org.zerock.task;
//
//import lombok.Setter;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.zerock.controller.UploadController;
//import org.zerock.domain.BoardAttachVO;
//import org.zerock.mapper.BoardAttachMapper;
//
//import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Log4j2
//@Component
//public class FileCheckTask {
//
//    @Setter(onMethod_= @Autowired)
//    private BoardAttachMapper attachMapper;
//
//    private String getFolderYesterday(){
//        log.debug("getFolderYesterday() invoked.");
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        Calendar cal = Calendar.getInstance();
//
//        cal.add(Calendar.DATE, -1);
//
//        String str = sdf.format(cal.getTime());
//
//        return str.replace("-", File.separator);
//    } //getFolderYesterday
//
//
//    // cron 속성으로, 주기를 제어함.
//    // 초 분 시 일 월 요일 년(optional)
//    // * : 모든값
//    // 0 * * * * * : 매 분 0초
//    // 0 0 2 * * * : 매일 새벽 2시 0분 0초
//    @Scheduled(cron="0 * * * * *")
//    public void checkFiles() throws Exception{
//
//        log.warn("File Check Task run......");
//        log.warn("\t + TODAY: {}" , new Date());
//
//        //file list in database
//        List<BoardAttachVO> fileList = attachMapper.getOldFiles();
//
//        // ready for check file in directory with database file list
//        // fileListPath : DB에서 어제 날짜로 보관되는 첨부파일 목록
//        List<Path> fileListPaths = fileList.stream()
//                .map(vo ->
//                        Paths.get(UploadController.UPLOAD_PATH,
//                                vo.getUploadPath(), vo.getUuid() + "_" + vo.getFileName()))
//                .collect(Collectors.toList());
//
//        // image file has thumbnail file
//        fileList.stream().filter(vo -> vo.isFileType() == true)
//                .map(vo ->
//                        Paths.get(UploadController.UPLOAD_PATH,
//                                vo.getUploadPath(), "s_" + vo.getUuid() + "_" + vo.getFileName()))
//                .forEach(p -> fileListPaths.add(p));
//
//        log.warn("==============================================");
//
//        fileListPaths.forEach(p -> log.warn(p));
//
//        // files in yesterday directory
//        // removeFiles : 최종적으로 삭제될 파일 목록(서버)
//        File targetDir = Paths.get(UploadController.UPLOAD_PATH, getFolderYesterday()).toFile();
//
//        File[] removeFiles = targetDir.listFiles(
//                file -> fileListPaths.contains(file.toPath()) == false);
//
//        log.warn("------------------------------------------------");
//
//        for(File file : removeFiles){
//            log.warn(file.getAbsolutePath());
//
//            file.delete();
//        } //for
//
//    } //checkFiles
//
//
//} //end class
