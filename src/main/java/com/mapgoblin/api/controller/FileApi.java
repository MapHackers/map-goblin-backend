package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileApi {

    private final FileService fileService;

    @GetMapping("/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename) throws MalformedURLException {
        Resource file = fileService.loadFile(filename);

        if(file == null){
            return ApiResult.errorMessage("썸네일이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file) throws IOException {

        if(file.isEmpty()){
            return ApiResult.errorMessage("업로드할 파일이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        String fileName = fileService.saveFile(file);

        return ResponseEntity.ok(fileName);
    }

}
