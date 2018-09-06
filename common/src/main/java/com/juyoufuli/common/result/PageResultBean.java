package com.juyoufuli.common.result;

import com.juyoufuli.common.config.BusinessEnum;
import com.juyoufuli.common.lang.StringUtils;

/**
 * <p>
 * Description: [ͳһ���ض����װ�����ҳ]
 * </p>
 * Created on 2017��9��21��
 * @author <a href="mailto: weiyong@huayingcul1.com">κ��</a>
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
