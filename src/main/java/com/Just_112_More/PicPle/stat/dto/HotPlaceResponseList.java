package com.Just_112_More.PicPle.stat.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public class HotPlaceResponseList {
    private final List<HotPlaceResponse> hotplaces;

    @Builder
    public HotPlaceResponseList(List<HotPlaceResponse> hotplaces) {
        this.hotplaces = hotplaces;
    }
}
