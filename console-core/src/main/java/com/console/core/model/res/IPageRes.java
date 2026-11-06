package com.console.core.model.res;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IPageRes<T> {
    private List<T> records;
    private long total;
    private long size;
    private long current;
    private long page;
}
