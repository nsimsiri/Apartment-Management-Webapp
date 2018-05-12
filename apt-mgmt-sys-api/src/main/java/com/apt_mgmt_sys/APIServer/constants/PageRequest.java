package com.apt_mgmt_sys.APIServer.constants;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequest implements Pageable {
    private final int limit;
    private final int offset;
    private Sort sort;
    public PageRequest(){
        this.limit = 200;
        this.offset = 0;
        this.sort = null;
    }
    public PageRequest(Integer offset, Integer limit){
        this.limit = limit == null ? 200 : limit;
        this.offset = offset == null ? 0 : offset;
        this.sort = null;
    }

    public PageRequest(Integer offset, Integer limit, Sort sort){
        System.out.printf("??? %s %s %s\n", offset, limit, sort);
        this.limit = limit == null ? 200 : limit;
        this.offset = offset == null ? 0 : offset;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return this.limit;
    }

    @Override
    public int getOffset() {
        return this.offset;
    }

    @Override

    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    public static PageRequest build(Integer offset, Integer limit){
        return new PageRequest(offset, limit);
    }

    public static PageRequest buildWithOrder(Integer offset, Integer limit, String field, Integer direction){
        if (field == null || direction == null) return build(offset, limit);
        final Sort sort = new Sort(new Sort.Order(
                direction < 0 ? Sort.Direction.ASC : Sort.Direction.DESC,
                field
        ));
        return new PageRequest(offset, limit, sort);

    }
    public static void main(String[] args){
        PageRequest a = new PageRequest(null, null, null);
    }
}
