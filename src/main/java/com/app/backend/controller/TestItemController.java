package com.app.backend.controller;

import com.app.backend.dto.response.TestItemResponse;
import com.app.backend.service.TestItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;






@RestController
@RequestMapping("/api/test")


@RequiredArgsConstructor
public class TestItemController {

    private final TestItemService testItemService;

    @GetMapping("/items")
    public ResponseEntity<List<TestItemResponse>> getItems() {
        return ResponseEntity.ok(testItemService.getAll());
    }
}
