package com.xiuxiu.callback;

import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by oneki on 2017/5/18.
 */

public abstract class OKHttpCallback<T> {

    private Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            return null;
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public Type getType() {
        return getSuperclassTypeParameter(getClass());
    }

    public abstract void onResponse(T response);

    public abstract void onError(IOException e);

}
