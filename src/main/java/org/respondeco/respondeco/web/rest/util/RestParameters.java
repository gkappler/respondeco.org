package org.respondeco.respondeco.web.rest.util;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clemens Puehringer on 27/11/14.
 */

public class RestParameters {

    public static Integer DEFAULT_PAGE = 0;
    public static Integer DEFAULT_PAGE_SIZE = 20;

    @Getter
    private Integer page;
    @Getter
    private Integer pageSize;
    @Getter
    private List<String> fields;
    @Getter
    private Sort sort;

    private RestUtil restUtil;

    public RestParameters(Integer page, Integer pageSize) {
        this(page, pageSize, null, null);
    }

    public RestParameters(Integer page, Integer pageSize, String order) {
        this(page, pageSize, order, null);
    }

    public RestParameters(Integer page, Integer pageSize, String order, String fields) {
        this.restUtil = new RestUtil();
        if(page == null) {
            this.page = DEFAULT_PAGE;
        } else {
            this.page = page;
        }
        if(pageSize == null) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        } else {
            this.pageSize = page;
        }
        this.fields = restUtil.splitCommaSeparated(fields);
        buildOrders(order);
    }

    private void buildFields(String fields) {
        this.fields = new ArrayList<>();
        if(fields != null) {
            for(String f : fields.split(",")) {
                this.fields.add(f.trim());
            }
        }
    }

    private void buildOrders(String order) {
        if(order != null) {
            List<String> orders = restUtil.splitCommaSeparated(order);
            List<Sort.Order> sortOrders = new ArrayList<>();
            for (String o : orders) {
                if (o.length() > 0) {
                    if (o.startsWith("-")) {
                        o = o.substring(1);
                        sortOrders.add(new Sort.Order(Sort.Direction.DESC, o));
                    } else if (o.startsWith("+")) {
                        o = o.substring(1);
                        sortOrders.add(new Sort.Order(Sort.Direction.ASC, o));
                    } else {
                        sortOrders.add(new Sort.Order(Sort.Direction.ASC, o));
                    }
                }
            }
            sort = new Sort(sortOrders);
        }
    }

    public PageRequest buildPageRequest() {
        return new PageRequest(page, pageSize, sort);
    }

}