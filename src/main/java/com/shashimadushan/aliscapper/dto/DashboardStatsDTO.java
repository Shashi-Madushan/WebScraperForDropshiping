package com.shashimadushan.aliscapper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardStatsDTO {
    private long totalUsers;
    private long totalProducts;
    private long totalStores;
    private long newUsersThisWeek;
}
