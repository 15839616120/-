package com.juyoufuli.common.result;

import com.juyoufuli.common.config.BusinessEnum;
import com.juyoufuli.common.lang.StringUtils;

/**
 * <p>
 * Description: [统一返回对象包装类带分页]
 * </p>
 * Created on 2017年9月21日
 * @author <a href="mailto: weiyong@huayingcul1.com">魏勇</a>
 */
public class PageResultBean<T> extends ResultBean<T> {

    private static final long serialVersionUID = -1853099219165525016L;


    private long total;

    public PageResultBean() {
        super();
    }

    public PageResultBean(T data, long total) {
        super(data);
        this.total = total;
    }

    public PageResultBean(int code, String msg) {
        super(code,msg);
    }

    public static <T> PageResultBean<T> error(String message) {
        return new PageResultBean<>(BusinessEnum.INNER_EXCEPTION.getCode(), StringUtils.isBlank(message) ? BusinessEnum.INNER_EXCEPTION.getMsg() : message);
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
