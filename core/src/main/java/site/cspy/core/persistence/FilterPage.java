package site.cspy.core.persistence;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import java.util.Map;

@Deprecated
public class FilterPage<T> extends PageDTO<T> {
    private Map<String, Map<String, Object>> filter;

    long getOffset() {
        return offset();
    }

    public FilterPage() {
    }

    public FilterPage(long current, long size) {
        this(current, size, 0);
    }

    public FilterPage(long current, long size, Map<String, Map<String, Object>> filter) {
        this(current, size, 0);
        this.filter = filter;
    }

    public FilterPage(long current, long size, long total) {
        this(current, size, total, true);
    }

    public FilterPage(long current, long size, boolean searchCount) {
        this(current, size, 0, searchCount);
    }

    public FilterPage(long current, long size, long total, boolean searchCount) {
        super(current, size, total, searchCount);
    }

    public Map<String, Map<String, Object>> getFilter() {
        return filter;
    }
}
