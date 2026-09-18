package com.example.demo.calendarEvent;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class CalendarEventSpecifications {

    public static Specification<CalendarEvent> filterEvents(CalendarQueryDTO query) {
        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 日期區間過濾 (月檢視/週檢視)
            if (query.getStartDate() != null && query.getEndDate() != null) {
                predicates.add(cb.between(root.get("date"), query.getStartDate(), query.getEndDate()));
            }
            // 狀態過濾
            if (query.getStatusFilter() != null && !"all".equals(query.getStatusFilter())) {
                predicates.add(cb.equal(root.get("status"), query.getStatusFilter()));
            }
            // 優先級過濾
            if (query.getPriorityFilter() != null && !"all".equals(query.getPriorityFilter())) {
                predicates.add(cb.equal(root.get("priority"), query.getPriorityFilter()));
            }
            // 分類過濾
            if (query.getSelectedCategory() != null && !"all".equals(query.getSelectedCategory())) {
                predicates.add(cb.equal(root.get("category"), query.getSelectedCategory()));
            }
            // 模糊搜尋：標題、負責人、地點、單號
            if (query.getSearchQuery() != null && !query.getSearchQuery().trim().isEmpty()) {
                String pattern = "%" + query.getSearchQuery().trim() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(root.get("title"), pattern),
                        cb.like(root.get("organizer"), pattern),
                        cb.like(root.get("location"), pattern),
                        cb.like(root.get("relatedRef"), pattern));
                predicates.add(searchPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
