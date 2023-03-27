package com.xunmo.bs.config.bs;

import cn.zhxu.bs.operator.Contain;
import cn.zhxu.bs.operator.Empty;
import cn.zhxu.bs.operator.IsNull;
import cn.zhxu.bs.operator.NotEmpty;
import cn.zhxu.bs.operator.NotNull;
import com.xunmo.bs.config.bs.op.MyBetween;
import com.xunmo.bs.config.bs.op.MyContain;
import com.xunmo.bs.config.bs.op.MyEndWith;
import com.xunmo.bs.config.bs.op.MyEqual;
import com.xunmo.bs.config.bs.op.MyGreaterEqual;
import com.xunmo.bs.config.bs.op.MyGreaterThan;
import com.xunmo.bs.config.bs.op.MyInList;
import com.xunmo.bs.config.bs.op.MyLessEqual;
import com.xunmo.bs.config.bs.op.MyLessThan;
import com.xunmo.bs.config.bs.op.MyNotBetween;
import com.xunmo.bs.config.bs.op.MyNotEqual;
import com.xunmo.bs.config.bs.op.MyNotIn;
import com.xunmo.bs.config.bs.op.MyNotLike;
import com.xunmo.bs.config.bs.op.MyOrLike;
import com.xunmo.bs.config.bs.op.MyStartWith;

public class Ops {
    public static final Class<MyEqual> Equal = MyEqual.class;
    public static final Class<MyNotEqual> NotEqual = MyNotEqual.class;
    public static final Class<MyGreaterEqual> GreaterEqual = MyGreaterEqual.class;
    public static final Class<MyGreaterThan> GreaterThan = MyGreaterThan.class;
    public static final Class<MyLessEqual> LessEqual = MyLessEqual.class;
    public static final Class<MyLessThan> LessThan = MyLessThan.class;
    public static final Class<IsNull> IsNull = IsNull.class;
    public static final Class<NotNull> NotNull = NotNull.class;
    public static final Class<Empty> Empty = Empty.class;
    public static final Class<NotEmpty> NotEmpty = NotEmpty.class;
    public static final Class<MyContain> Contain = MyContain.class;
    public static final Class<MyStartWith> StartWith = MyStartWith.class;
    public static final Class<MyEndWith> EndWith = MyEndWith.class;
    public static final Class<MyOrLike> OrLike = MyOrLike.class;
    public static final Class<MyNotLike> NotLike = MyNotLike.class;
    public static final Class<MyBetween> Between = MyBetween.class;
    public static final Class<MyNotBetween> NotBetween = MyNotBetween.class;
    public static final Class<MyInList> InList = MyInList.class;
    public static final Class<MyNotIn> NotIn = MyNotIn.class;

    public Ops() {
    }
}
