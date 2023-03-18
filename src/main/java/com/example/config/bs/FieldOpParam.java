package com.example.config.bs;

import cn.zhxu.bs.boot.BeanSearcherProperties;
import cn.zhxu.bs.param.FieldParam;

public interface FieldOpParam {

    void setConfig(BeanSearcherProperties config);

    BeanSearcherProperties getConfig();


    void setFieldParam(FieldParam fieldParam);

    FieldParam getFieldParam();

}
