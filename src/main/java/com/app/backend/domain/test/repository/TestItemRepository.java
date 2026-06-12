package com.app.backend.domain.test.repository;

import com.app.backend.domain.test.entity.TestItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestItemRepository extends JpaRepository<TestItem, Long> {
}