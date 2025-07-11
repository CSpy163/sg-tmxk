package site.cspy.core.util;


import lombok.NonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 动态泛型实现
 */
public class ParameterizedTypeImpl implements ParameterizedType {
    private final Class<?> rawType;
    private final Type[] typeArguments;

    public ParameterizedTypeImpl(Class<?> rawType, Type[] typeArguments) {
        this.rawType = rawType;
        this.typeArguments = typeArguments;
    }

    @NonNull
    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments;
    }

    @NonNull
    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
