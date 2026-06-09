package com.app.backend.dto.response;

import com.app.backend.entity.TestItem;

public record TestItemResponse(
        Long id,
        String name,
        String description
) {
    public static TestItemResponse from(TestItem item) {
        return new TestItemResponse(item.getId(), item.getName(), item.getDescription());
    }
}