package com.example.demo.NotificationRecord;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationRecord, Long> {

        @Query("SELECT COUNT(n) FROM NotificationRecord n WHERE n.userId = :userId AND n.read = false " +
                        "AND NOT (n.category = 'inventory' AND (" +
                        "n.title LIKE '%庫存偏低%' OR n.title LIKE '%庫存不足%' OR " +
                        "n.title LIKE '%庫存告急%' OR n.title LIKE '%庫存水位告急%' OR " +
                        "n.title LIKE '%庫存緊急缺料%' OR n.content LIKE '%低於安全庫存%' OR " +
                        "n.content LIKE '%低於安全水位%'))")
        int countVisibleUnread(@Param("userId") Long userId);

        // 1. 使用 CASE WHEN 語法繞過新版 JPQL 的嚴格參數驗證
        @Query("SELECT n FROM NotificationRecord n WHERE n.userId = :userId " +
                        "AND (CASE WHEN :category = 'all' THEN 1 WHEN n.category = :category THEN 1 ELSE 0 END = 1) " +
                        "AND (CASE WHEN :onlyUnread = true THEN (CASE WHEN n.read = false THEN 1 ELSE 0 END) ELSE 1 END = 1) " +
                        "AND NOT (n.category = 'inventory' AND (" +
                        "n.title LIKE '%庫存偏低%' OR n.title LIKE '%庫存不足%' OR " +
                        "n.title LIKE '%庫存告急%' OR n.title LIKE '%庫存水位告急%' OR " +
                        "n.title LIKE '%庫存緊急缺料%' OR n.content LIKE '%低於安全庫存%' OR " +
                        "n.content LIKE '%低於安全水位%'))")
        List<NotificationRecord> findFilteredNotifications(
                        @Param("userId") Long userId,
                        @Param("category") String category,
                        @Param("onlyUnread") boolean onlyUnread,
                        Pageable pageable);

        // 2. 更新方法同步修改
        @Modifying(clearAutomatically = true)
        @Query("UPDATE NotificationRecord n SET n.read = true WHERE n.userId = :userId " +
                        "AND (CASE WHEN :category = 'all' THEN 1 WHEN n.category = :category THEN 1 ELSE 0 END = 1)")
        void markAllAsRead(@Param("userId") Long userId, @Param("category") String category);

        // 3. 刪除方法同步修改
        @Modifying(clearAutomatically = true)
        @Query("DELETE FROM NotificationRecord n WHERE n.userId = :userId " +
                        "AND (CASE WHEN :category = 'all' THEN 1 WHEN n.category = :category THEN 1 ELSE 0 END = 1)")
        void clearNotifications(@Param("userId") Long userId, @Param("category") String category);

}
