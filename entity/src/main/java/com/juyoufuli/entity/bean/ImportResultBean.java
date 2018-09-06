package com.juyoufuli.entity.bean;

import java.util.List;

public class ImportResultBean {

    /**
     * 导入失败数据
     */
    private List<ImportStaffBean> errorData;

    /**
     * 激活码
     */
    private List<CodeResultBean> codeResults;

    public List<ImportStaffBean> getErrorData() {
        return errorData;
    }

    public void setErrorData(List<ImportStaffBean> errorData) {
        this.errorData = errorData;
    }

    public List<CodeResultBean> getCodeResults() {
        return codeResults;
    }

    public void setCodeResults(List<CodeResultBean> codeResults) {
        this.codeResults = codeResults;
    }
}
