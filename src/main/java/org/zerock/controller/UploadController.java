package org.zerock.controller;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.AttachFileDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

@Controller
@Log4j2
public class UploadController {

    public static final String UPLOAD_PATH = "/Users/jeongminji/Etc/upload/";

    //-- 년/월/일 폴더의 생성
    private String getFolder(){
        log.debug("getFolder() invoked.");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();

        String str = sdf.format(date);

        return str.replace("-", File.separator);
    } //getFolder


    //-- 첨부파일이 이미지 타입인지를 검사
    private boolean checkImageType(File file){
        log.debug("checkImageType({}) invoked.", file);

        try{
            String contentType = Files.probeContentType(file.toPath());

            return contentType.startsWith("image");
        }catch(IOException e){
            e.printStackTrace();;
        } //try-catch

        return false;

    } //checkImageType


    @GetMapping("/uploadForm")
    public void uploadForm(){
        log.debug("uploadForm() invoked.");

    } //uploadForm


    @PostMapping("/uploadFormAction")
    public void uploadFormPost(MultipartFile[]uploadFile, Model model){
        log.debug("uploadFormPost({}, {}) invoked.", uploadFile, model);

        String uploadFolder = UploadController.UPLOAD_PATH;

        for(MultipartFile multipartFile : uploadFile){
            log.info("------------------");
            log.info("Upload File Name : " + multipartFile.getOriginalFilename());
            log.info("Upload File Size : " + multipartFile.getSize());

            File saveFile = new File(uploadFolder, multipartFile.getOriginalFilename());

            try {
                multipartFile.transferTo(saveFile);
            } catch(Exception e){
                log.error(e.getMessage());
            } //try-catch

        } //enhanced-for
    } //uploadForm


    @GetMapping("/uploadAjax")
    public void uploadAjax(){
        log.debug("uploadAjax() invoked.");

    } //uploadAjax


    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="/uploadAjaxAction",
                produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFile){
        log.debug("uploadAjaxPost({}) invoked", uploadFile);

        List<AttachFileDTO> list = new ArrayList<>();
        String uploadFolder = UploadController.UPLOAD_PATH;

        String uploadFolderPath = getFolder();

        //----------- make folder
        File uploadPath = new File(uploadFolder, getFolder());
        log.info("upload path : " + uploadPath);

        if(uploadPath.exists() == false){
            uploadPath.mkdirs();
        } //if : 해당 업로드 경로가 존재하지 않으면, 새로운 디렉토리(yyyy/MM/dd) 생성.

        for(MultipartFile multipartFile : uploadFile){

            AttachFileDTO attachDTO = new AttachFileDTO();

            String uploadFileName = multipartFile.getOriginalFilename();

            // IE has file path
            // IE는 전체 파일 경로가 전송되므로, 마지막 \ 를 기준으로 잘라낸 문자열이 실제 파일이름이 됨.
            uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);

            log.info("only filename : " + uploadFileName);

            attachDTO.setFileName(uploadFileName);

            // 파일명 중복방지 위한 UUID 생성
            UUID uuid = UUID.randomUUID();

            uploadFileName = uuid.toString() + "_" + uploadFileName;

            try {
                File saveFile = new File(uploadPath, uploadFileName);
                multipartFile.transferTo(saveFile);

                attachDTO.setUuid(uuid.toString());
                attachDTO.setUploadPath(uploadFolderPath);

                // check image type file
                if(checkImageType(saveFile)){

                    attachDTO.setImage(true);

                    FileOutputStream thumbnail = new FileOutputStream(
                                                            // 원본 파일은 그대로 저장되고,
                                                            // 파일 이름이 's_'로 시작하는 섬네일 파일이 함께 생성됨.
                                                            new File(uploadPath, "s_" + uploadFileName));

                    Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail,100, 100);

                    thumbnail.close();
                } //if

                // add to List
                list.add(attachDTO);

            } catch(Exception e){
                log.error(e.getMessage());
            } // try-catch

        } //enhanced for

        return new ResponseEntity<>(list, HttpStatus.OK);
    } //uploadAjaxPost


    @GetMapping("/display")
    @ResponseBody
    public ResponseEntity<byte[]> getFile(String fileName){
        log.debug("getFile({}) invoked.", fileName);

        File file = new File(UploadController.UPLOAD_PATH + fileName);

        log.info("file : " + file);

        ResponseEntity<byte[]> result = null;

        try{
            HttpHeaders header = new HttpHeaders();

            header.add("Content-Type", Files.probeContentType(file.toPath()));
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file),
                    header, HttpStatus.OK);
        } catch (IOException e){
            e.printStackTrace();
        } //try-catch

        return result;
    } //getFile


    @GetMapping(value="/download", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(String fileName){
        log.debug("downloadFile({}) invoked.", fileName);

        Resource resource = new FileSystemResource(UploadController.UPLOAD_PATH + fileName);

        if(resource.exists() == false){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } //if : 파일이 첨부되지 않았으면

        String resourceName = resource.getFilename();

        // remove UUID
        String resourceOriginalName = resourceName.substring(resourceName.indexOf("_") + 1);

        HttpHeaders headers = new HttpHeaders();

        try{
            // 브라우저 별 다운로드 처리
            String downloadName = null;

            if(userAgent.contains("Trident")){  //Trident = IE브라우저 엔진 이름. IE11 처리.
                log.info("IE browser");

                downloadName = URLEncoder.encode(resourceOriginalName, "UTF-8").replaceAll("\\+", " ");

            } else if(userAgent.contains("Edge")){
                log.info("Edge browser");

                downloadName = URLEncoder.encode(resourceOriginalName, "UTF-8");

                log.info("Edge name : " + downloadName);
            } else {
                log.info("Chrome browser");

                downloadName =
                        new String(resourceOriginalName.getBytes("UTF-8"), "ISO-8859-1" );
            } //if-else

            log.info("download name : " + downloadName);

            headers.add("Content-Disposition", "attachment; filename=" + downloadName);
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        } //try-catch

        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    } //downloadFile


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/deleteFile")
    @ResponseBody
    public ResponseEntity<String> deleteFile(String fileName, String type){
        log.debug("deleteFile({}, {}) invoked.", fileName, type);

        File file;

        try{
            file = new File(UploadController.UPLOAD_PATH + URLDecoder.decode(fileName, "utf8"));

            file.delete();

            if(type.equals("image")){
                String largeFileName = file.getAbsolutePath().replace("s_", "");

                log.info("largeFileName: " + largeFileName);

                file = new File(largeFileName);

                file.delete();
            } //if
        } catch(UnsupportedEncodingException e){
            e.printStackTrace();

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } //try-catch

        return new ResponseEntity<String>("deleted", HttpStatus.OK);
    } //deleteFile

} //end class
