package com.shashimadushan.aliscapper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashboardDataDTO {
    private ProductCountDTO productCount;
    private List<DailyProductCountDTO> dailyProductCounts;
    private List<ConnectedStoreResponseDto> connectedStores;


}