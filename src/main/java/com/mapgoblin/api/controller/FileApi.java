package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileApi {

    private final FileService fileService;

    @GetMapping("/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename) {


        return ResponseEntity.ok("");
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
