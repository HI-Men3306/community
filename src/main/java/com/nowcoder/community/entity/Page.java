package com.nowcoder.community.entity;

/**
 * 封装分页相关的信息.
 * 设置分页显示的页码最多5个  即当前页码的前两页   当前页码的后两页
 */
public class Page {

    // 当前页码
    private int current = 1;
    // 每页显示上限
    private int limit = 10;
    // 数据总数(用于计算总页数)
    private int rows;
    // 查询or请求路径(用于复用分页链接)  即当点击翻页时需要一个路径用来向指定的路径发送请求
    //我的一些思考：（不知道对不对） 其实也可以不需要这个查询路径 因为我知道要向那个路径发送请求 完全可以写死   只不过这样就加大了代码的耦合
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     *
     * @return
     */
    public int getOffset() {
        // current * limit - limit
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public int getTotal() {
        // rows / limit [+1]
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码
     *
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结束页码
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }

}
