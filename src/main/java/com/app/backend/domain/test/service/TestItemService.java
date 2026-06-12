package com.app.backend.domain.test.service;

import com.app.backend.domain.test.dto.response.TestItemResponse;
import com.app.backend.domain.test.repository.TestItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestItemService {

    private final TestItemRepository testItemRepository;

    public List<TestItemResponse> getAll() {
        return testItemRepository.findAll().stream()
                .map(TestItemResponse::from)
                .toList();
    }
}