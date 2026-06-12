package com.app.backend.domain.test.dto.response;

import com.app.backend.domain.test.entity.TestItem;

public record TestItemResponse(
        Long id,
        String name,
        String description
) {
    public static TestItemResponse from(TestItem item) {
        return new TestItemResponse(item.getId(), item.getName(), item.getDescription());
    }
}