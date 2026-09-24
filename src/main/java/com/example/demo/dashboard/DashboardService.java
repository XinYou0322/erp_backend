package com.example.demo.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dashboard.dto.DashboardResponse;
import com.example.demo.dashboard.dto.HourlySalesResponse;
import com.example.demo.dashboard.dto.RevenueDetailRequest;
import com.example.demo.dashboard.dto.RevenueDetailResponse;
import com.example.demo.dashboard.dto.RevenueTrendResponse;
import com.example.demo.dashboard.dto.TopProductResponse;
import com.example.demo.dashboard.dto.TopRevenueResponse;
import com.example.demo.salesOrder.SalesOrderItemRepository;
import com.example.demo.salesOrder.SalesOrderRepository;
import com.example.demo.salesOrder.SalesOrderStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final SalesOrderRepository salesRepo;
        private final SalesOrderItemRepository itemRepo;

        public DashboardResponse getDashboard() {

                LocalDateTime now = LocalDateTime.now();
                LocalDate today = now.toLocalDate();
                LocalDate yesterday = today.minusDays(1);

                DashboardResponse response = new DashboardResponse();

                // 1. 今日至今 (Today to Date): 今日 00:00 ~ 現在
                LocalDateTime todayStart = today.atStartOfDay();
                LocalDateTime todayEnd = now;

                BigDecimal todayRevenue = salesRepo.getRevenueBetween(todayStart, todayEnd);
                response.setTodayRevenue(todayRevenue);

                // 2. 昨日至今 (Yesterday to Date): 昨日 00:00 ~ 昨日現在
                LocalDateTime yesterdayStart = yesterday.atStartOfDay();
                LocalDateTime yesterdayEnd = now.minusDays(1);

                BigDecimal yesterdaySamePeriod = salesRepo.getRevenueBetween(yesterdayStart, yesterdayEnd);

                // 3. 計算環比 (成長率)
                response.setRevenueChangeRate(calcChangeRate(todayRevenue, yesterdaySamePeriod));

                // 4. 今日至今訂單數
                response.setTodayOrders(salesRepo.countOrdersBetween(todayStart, todayEnd));

                // 5. 今日至今平均客單價
                Double avg = salesRepo.getAverageOrderBetween(todayStart, todayEnd);
                response.setAverageOrderAmount(avg != null ? BigDecimal.valueOf(avg) : BigDecimal.ZERO);

                LocalDateTime weekStart = today.minusDays(6).atStartOfDay(); // 包含今天共7天
                LocalDateTime weekEnd = today.atTime(LocalTime.MAX);
                LocalDateTime startDateForRecent = today.minusDays(6).atStartOfDay();

                // 七天營收趨勢
                List<RevenueTrendResponse> rawTrend = salesRepo.getWeeklyRevenue(weekStart, weekEnd).stream()
                                .map(r -> new RevenueTrendResponse(((java.sql.Date) r[0]).toLocalDate(),
                                                (BigDecimal) r[1]))
                                .toList();

                Map<LocalDate, BigDecimal> revenueByDate = rawTrend.stream()
                                .collect(Collectors.toMap(RevenueTrendResponse::getDate,
                                                RevenueTrendResponse::getRevenue));

                List<RevenueTrendResponse> trend = IntStream.range(0, 7)
                                .mapToObj(i -> {
                                        LocalDate date = today.minusDays(6 - i);
                                        BigDecimal revenue = revenueByDate.getOrDefault(date, BigDecimal.ZERO);
                                        return new RevenueTrendResponse(date, revenue);
                                })
                                .toList();

                // 熱門商品 Top5 (全部 & 近七天)
                List<TopProductResponse> top = itemRepo.findTopProducts(PageRequest.of(0, 5)).stream()
                                .map(r -> new TopProductResponse((String) r[0], (BigDecimal) r[1])).toList();

                List<TopProductResponse> recentTop = itemRepo
                                .findTopProductsSince(startDateForRecent, PageRequest.of(0, 5)).stream()
                                .map(r -> new TopProductResponse((String) r[0], (BigDecimal) r[1])).toList();

                // 營收排行 Top5
                List<TopRevenueResponse> topRevenue = itemRepo
                                .findTopProductsByRevenue(SalesOrderStatus.COMPLETED, PageRequest.of(0, 5)).stream()
                                .map(r -> new TopRevenueResponse((String) r[0], (BigDecimal) r[1])).toList();

                List<TopRevenueResponse> recentTopRevenue = itemRepo
                                .findTopProductsByRevenueSince(SalesOrderStatus.COMPLETED, weekStart,
                                                PageRequest.of(0, 5))
                                .stream()
                                .map(r -> new TopRevenueResponse((String) r[0], (BigDecimal) r[1])).toList();

                // 每小時銷售
                List<HourlySalesResponse> hourlySales = itemRepo
                                .findHourlySales(SalesOrderStatus.COMPLETED.name(), today).stream()
                                .map(r -> new HourlySalesResponse(((Number) r[0]).intValue(), (BigDecimal) r[1]))
                                .toList();

                response.setWeeklyRevenue(trend);
                response.setTopProducts(top);
                response.setRecentTopProducts(recentTop);
                response.setTopRevenueProducts(topRevenue);
                response.setRecentTopRevenueProducts(recentTopRevenue);
                response.setHourlySales(hourlySales);

                return response;
        }

        public RevenueDetailResponse getRevenueDetail(RevenueDetailRequest request) {

                LocalDate startDate = request.getStartDate();
                LocalDate endDate = request.getEndDate();
                String groupBy = request.getGroupBy(); // "DAY", "MONTH", "YEAR"

                if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
                        throw new IllegalArgumentException("無效的日期區間：開始日期不能晚於結束日期");
                }

                LocalDateTime start = startDate.atStartOfDay();
                LocalDateTime end = endDate.atTime(LocalTime.MAX);

                // 1. 計算「上期」區間：長度與本期相同，且緊接在本期開始之前
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
                LocalDateTime prevEnd = start.minusNanos(1); // 本期開始的前一瞬間
                LocalDateTime prevStart = prevEnd.minusDays(daysBetween).toLocalDate().atStartOfDay();

                // 2. 取得本期與上期總營收
                BigDecimal totalRevenue = salesRepo.getRevenueBetween(start, end);
                BigDecimal previousRevenue = salesRepo.getRevenueBetween(prevStart, prevEnd);

                // 3. 計算環比百分比
                Double changeRate = calcChangeRate(totalRevenue, previousRevenue);

                // 4. 根據分組維度取得原始資料
                List<Object[]> rawTrend;
                if ("MONTH".equals(groupBy)) {
                        rawTrend = salesRepo.getRevenueGroupedByMonth(start, end);
                } else if ("YEAR".equals(groupBy)) {
                        rawTrend = salesRepo.getRevenueGroupedByYear(start, end);
                } else {
                        rawTrend = salesRepo.getRevenueGroupedByDay(start, end);
                }

                // 5. 將資料轉換為 Map，並將年/月統一轉換為 LocalDate (年初或月初) 以便複用 RevenueTrendResponse
                Map<LocalDate, BigDecimal> revenueMap = rawTrend.stream()
                                .collect(Collectors.toMap(
                                                r -> {
                                                        if ("YEAR".equals(groupBy)) {
                                                                int year = ((Number) r[0]).intValue();
                                                                return LocalDate.of(year, 1, 1);
                                                        } else if ("MONTH".equals(groupBy)) {
                                                                int year = ((Number) r[0]).intValue();
                                                                int month = ((Number) r[1]).intValue();
                                                                return LocalDate.of(year, month, 1);
                                                        } else {
                                                                return ((java.sql.Date) r[0]).toLocalDate();
                                                        }
                                                },
                                                r -> (BigDecimal) r[r.length - 1] // 假設最後一個欄位是 SUM 的金額
                                ));
                // 6. 補齊區間內沒有訂單的日期/月份/年份，確保前端圖表連續
                List<RevenueTrendResponse> trendList = new ArrayList<>();

                if ("YEAR".equals(groupBy)) {
                        for (int y = startDate.getYear(); y <= endDate.getYear(); y++) {
                                LocalDate date = LocalDate.of(y, 1, 1);
                                trendList.add(new RevenueTrendResponse(date,
                                                revenueMap.getOrDefault(date, BigDecimal.ZERO)));
                        }
                } else if ("MONTH".equals(groupBy)) {
                        YearMonth current = YearMonth.from(startDate);
                        YearMonth endYm = YearMonth.from(endDate);
                        while (!current.isAfter(endYm)) {
                                LocalDate date = current.atDay(1);
                                trendList.add(new RevenueTrendResponse(date,
                                                revenueMap.getOrDefault(date, BigDecimal.ZERO)));
                                current = current.plusMonths(1);
                        }
                } else { // DAY
                        long days = ChronoUnit.DAYS.between(startDate, endDate);
                        for (int i = 0; i <= days; i++) {
                                LocalDate currentDate = startDate.plusDays(i);
                                trendList.add(new RevenueTrendResponse(currentDate,
                                                revenueMap.getOrDefault(currentDate, BigDecimal.ZERO)));
                        }
                }

                return new RevenueDetailResponse(groupBy.toLowerCase(), totalRevenue, previousRevenue, changeRate,
                                trendList);

        }

        // 計算環比百分比
        private Double calcChangeRate(BigDecimal current, BigDecimal previous) {
                if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
                        return null; // 上期沒營收，無法計算
                }
                return current.subtract(previous)
                                .multiply(BigDecimal.valueOf(100))
                                .divide(previous, 1, RoundingMode.HALF_UP) // 保留一位小數
                                .doubleValue();
        }
}
