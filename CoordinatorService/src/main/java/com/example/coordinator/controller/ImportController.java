package com.example.coordinator.controller;
import com.example.coordinator.dto.ApiDtos.ImportResponse;
import com.example.coordinator.service.CsvImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController @RequestMapping("/api/imports") @RequiredArgsConstructor
@CrossOrigin(origins="*")
public class ImportController {
 private final CsvImportService service;
 @PostMapping("/orders") public ImportResponse upload(@RequestParam("file") MultipartFile file){return service.importFile(file);}
}
