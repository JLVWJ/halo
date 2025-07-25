package com.lvwj.halo.dubbo.util;

import com.lvwj.halo.common.enums.IEnum;
import com.lvwj.halo.common.utils.DateTimeUtil;
import com.lvwj.halo.common.utils.JsonUtil;
import com.lvwj.halo.dubbo.serializer.ISerializer;
import com.lvwj.halo.dubbo.serializer.SerializerHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.config.ConfigurationUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.logger.ErrorTypeAwareLogger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.*;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.beans.Transient;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.apache.dubbo.common.constants.LoggerCodeConstants.COMMON_REFLECTIVE_OPERATION_FAILED;
import static org.apache.dubbo.common.utils.ClassUtils.isAssignableFrom;

/**
 * PojoUtils. Travel object deeply, and convert complex type to simple type.
 * <p/>
 * Simple type below will be remained:
 * <ul>
 * <li> Primitive Type, also include <b>String</b>, <b>Number</b>(Integer, Long), <b>Date</b>
 * <li> Array of Primitive Type
 * <li> Collection, eg: List, Map, Set etc.
 * </ul>
 * <p/>
 * Other type will be covert to a map which contains the attributes and value pair of object.
 * <p>
 * TODO: exact PojoUtils to scope bean
 */
@Slf4j
public class MyPojoUtils {

    private static final ErrorTypeAwareLogger logger = LoggerFactory.getErrorTypeAwareLogger(MyPojoUtils.class);
    private static final ConcurrentMap<String, Method> NAME_METHODS_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Class<?>, ConcurrentMap<String, Field>> CLASS_FIELD_CACHE =
            new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, Object> CLASS_NOT_FOUND_CACHE = new ConcurrentHashMap<>();

    private static final Object NOT_FOUND_VALUE = new Object();
    private static final boolean GENERIC_WITH_CLZ = Boolean.parseBoolean(ConfigurationUtils.getProperty(
            ApplicationModel.defaultModel(), CommonConstants.GENERIC_WITH_CLZ_KEY, "true"));

    private static final List<Class<?>> CLASS_CAN_BE_STRING = Arrays.asList(
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class,
            Character.class);

    public static Object[] generalize(Object[] objs) {
        Object[] dests = new Object[objs.length];
        for (int i = 0; i < objs.length; i++) {
            dests[i] = generalize(objs[i]);
        }
        return dests;
    }

    public static Object[] realize(Object[] objs, Class<?>[] types) {
        if (objs.length != types.length) {
            throw new IllegalArgumentException("args.length != types.length");
        }

        Object[] dests = new Object[objs.length];
        for (int i = 0; i < objs.length; i++) {
            dests[i] = realize(objs[i], types[i]);
        }

        return dests;
    }

    public static Object[] realize(Object[] objs, Class<?>[] types, Type[] gtypes) {
        if (objs.length != types.length || objs.length != gtypes.length) {
            throw new IllegalArgumentException("args.length != types.length");
        }
        Object[] dests = new Object[objs.length];
        for (int i = 0; i < objs.length; i++) {
            dests[i] = realize(objs[i], types[i], gtypes[i]);
        }
        return dests;
    }

    public static Object generalize(Object pojo) {
        return generalize(pojo, new IdentityHashMap<>());
    }

    @SuppressWarnings("unchecked")
    private static Object generalize(Object pojo, Map<Object, Object> history) {
        if (pojo == null) {
            return null;
        }

        //插入自定义序列化扩展
        ISerializer<?> serializer = SerializerHolder.find(pojo);
        if (null != serializer) {
            log.debug("[MyPojoUtils.generalize]==> pojoClass:{} pojo:{} serializer:{}", pojo.getClass().getName(), pojo, serializer);
            return serializer.serialize(pojo);
        }

        if (pojo instanceof Enum<?>) {
            return ((Enum<?>) pojo).name();
        }
        if (pojo.getClass().isArray()
                && Enum.class.isAssignableFrom(pojo.getClass().getComponentType())) {
            int len = Array.getLength(pojo);
            String[] values = new String[len];
            for (int i = 0; i < len; i++) {
                values[i] = ((Enum<?>) Array.get(pojo, i)).name();
            }
            return values;
        }

        if (ReflectUtils.isPrimitives(pojo.getClass())) {
            return pojo;
        }

        if (pojo instanceof LocalDate || pojo instanceof LocalDateTime || pojo instanceof LocalTime) {
            return pojo.toString();
        }

        if (pojo instanceof Class) {
            return ((Class) pojo).getName();
        }

        Object o = history.get(pojo);
        if (o != null) {
            return o;
        }
        history.put(pojo, pojo);

        if (pojo.getClass().isArray()) {
            int len = Array.getLength(pojo);
            Object[] dest = new Object[len];
            history.put(pojo, dest);
            for (int i = 0; i < len; i++) {
                Object obj = Array.get(pojo, i);
                dest[i] = generalize(obj, history);
            }
            return dest;
        }
        if (pojo instanceof Collection<?>) {
            Collection<Object> src = (Collection<Object>) pojo;
            int len = src.size();
            Collection<Object> dest = (pojo instanceof List<?>) ? new ArrayList<>(len) : new HashSet<>(len);
            history.put(pojo, dest);
            for (Object obj : src) {
                dest.add(generalize(obj, history));
            }
            return dest;
        }
        if (pojo instanceof Map<?, ?>) {
            Map<Object, Object> src = (Map<Object, Object>) pojo;
            Map<Object, Object> dest = createMap(src);
            history.put(pojo, dest);
            for (Map.Entry<Object, Object> obj : src.entrySet()) {
                dest.put(generalize(obj.getKey(), history), generalize(obj.getValue(), history));
            }
            return dest;
        }
        Map<String, Object> map = new HashMap<>();
        history.put(pojo, map);
        if (GENERIC_WITH_CLZ) {
            map.put("class", pojo.getClass().getName());
        }
        for (Method method : pojo.getClass().getMethods()) {
            //自定义扩展点: 方法上加了@Transient，则不参与序列化
            if (method.isAnnotationPresent(Transient.class)) continue;
            if (ReflectUtils.isBeanPropertyReadMethod(method)) {
                ReflectUtils.makeAccessible(method);
                try {
                    map.put(
                            ReflectUtils.getPropertyNameFromBeanReadMethod(method),
                            generalize(method.invoke(pojo), history));
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        // public field
        for (Field field : pojo.getClass().getFields()) {
            //自定义扩展点: 字段加了transient，则不参与序列化
            if (Modifier.isTransient(field.getModifiers())) continue;
            if (ReflectUtils.isPublicInstanceField(field)) {
                try {
                    Object fieldValue = field.get(pojo);
                    if (history.containsKey(pojo)) {
                        Object pojoGeneralizedValue = history.get(pojo);
                        if (pojoGeneralizedValue instanceof Map
                                && ((Map) pojoGeneralizedValue).containsKey(field.getName())) {
                            continue;
                        }
                    }
                    if (fieldValue != null) {
                        map.put(field.getName(), generalize(fieldValue, history));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return map;
    }

    public static Object realize(Object pojo, Class<?> type) {
        return realize0(pojo, type, null, new IdentityHashMap<>());
    }

    public static Object realize(Object pojo, Class<?> type, Type genericType) {
        return realize0(pojo, type, genericType, new IdentityHashMap<>());
    }

    private static class PojoInvocationHandler implements InvocationHandler {

        private final Map<Object, Object> map;

        public PojoInvocationHandler(Map<Object, Object> map) {
            this.map = map;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(map, args);
            }
            String methodName = method.getName();
            Object value = null;
            if (methodName.length() > 3 && methodName.startsWith("get")) {
                value = map.get(methodName.substring(3, 4).toLowerCase() + methodName.substring(4));
            } else if (methodName.length() > 2 && methodName.startsWith("is")) {
                value = map.get(methodName.substring(2, 3).toLowerCase() + methodName.substring(3));
            } else {
                value = map.get(methodName.substring(0, 1).toLowerCase() + methodName.substring(1));
            }
            if (value instanceof Map<?, ?> && !Map.class.isAssignableFrom(method.getReturnType())) {
                value = realize0(value, method.getReturnType(), null, new IdentityHashMap<>());
            }
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> createCollection(Class<?> type, int len) {
        if (type.isAssignableFrom(ArrayList.class)) {
            return new ArrayList<>(len);
        }
        if (type.isAssignableFrom(HashSet.class)) {
            return new HashSet<>(len);
        }
        if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
            try {
                return (Collection<Object>) type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                // ignore
            }
        }
        return new ArrayList<>();
    }

    private static Map createMap(Map src) {
        Class<? extends Map> cl = src.getClass();
        Map result = null;
        if (HashMap.class == cl) {
            result = new HashMap();
        } else if (Hashtable.class == cl) {
            result = new Hashtable();
        } else if (IdentityHashMap.class == cl) {
            result = new IdentityHashMap();
        } else if (LinkedHashMap.class == cl) {
            result = new LinkedHashMap();
        } else if (Properties.class == cl) {
            result = new Properties();
        } else if (TreeMap.class == cl) {
            result = new TreeMap();
        } else if (WeakHashMap.class == cl) {
            return new WeakHashMap();
        } else if (ConcurrentHashMap.class == cl) {
            result = new ConcurrentHashMap();
        } else if (ConcurrentSkipListMap.class == cl) {
            result = new ConcurrentSkipListMap();
        } else {
            try {
                result = cl.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                /* ignore */
            }

            if (result == null) {
                try {
                    Constructor<?> constructor = cl.getConstructor(Map.class);
                    result = (Map) constructor.newInstance(Collections.EMPTY_MAP);
                } catch (Exception e) {
                    /* ignore */
                }
            }
        }

        if (result == null) {
            result = new HashMap<>();
        }

        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object realize0(Object pojo, Class<?> type, Type genericType, final Map<Object, Object> history) {
        return realize1(pojo, type, genericType, new HashMap<>(8), history);
    }

    private static Object realize1(
            Object pojo,
            Class<?> type,
            Type genericType,
            final Map<String, Type> mapParent,
            final Map<Object, Object> history) {
        if (pojo == null) {
            return null;
        }

        log.debug("[MyPojoUtils.realize1]==>type:" + type + ", genericType:" + genericType + ", pojo:" + pojo+ ", isEnum:" + type.isEnum());

        //入参是枚举时，判断枚举如果实现IEnum，则用IEnum.byCode来获取枚举，否则用Enum.valueOf
        if (type != null && type.isEnum()) {
            Object iEnum = null;
            if (IEnum.class.isAssignableFrom(type)) {
                iEnum = IEnum.byCode((Class<IEnum>) type, pojo);
            }
            if (null == iEnum && pojo.getClass() == String.class) {
                iEnum = Enum.valueOf((Class<Enum>) type, (String) pojo);
            }
            if (null == iEnum) { //枚举需加@JsonCreator
                iEnum = JsonUtil.parse(pojo.toString(), type);
            }
            return iEnum;
        }

        if(type!=null && Temporal.class.isAssignableFrom(type)) {
            String timeStr = ((String) pojo).replace("T"," ");
            if(type.equals(LocalDateTime.class)){
                return DateTimeUtil.parseDateTime(timeStr);
            } else if (type.equals(LocalDate.class)) {
                return DateTimeUtil.parseDate(timeStr);
            } else if (type.equals(LocalTime.class)) {
                return DateTimeUtil.parseTime(timeStr);
            }
        }

        if (ReflectUtils.isPrimitives(pojo.getClass())
                && !(type != null
                && type.isArray()
                && type.getComponentType().isEnum()
                && pojo.getClass() == String[].class)) {
            return CompatibleTypeUtils.compatibleTypeConvert(pojo, type);
        }

        Object o = history.get(pojo);

        if (o != null) {
            return o;
        }

        history.put(pojo, pojo);

        Map<String, Type> mapGeneric = new HashMap<>(8);
        mapGeneric.putAll(mapParent);
        TypeVariable<? extends Class<?>>[] typeParameters = type.getTypeParameters();
        if (genericType instanceof ParameterizedType && typeParameters.length > 0) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < typeParameters.length; i++) {
                if (!(actualTypeArguments[i] instanceof TypeVariable)) {
                    mapGeneric.put(typeParameters[i].getTypeName(), actualTypeArguments[i]);
                }
            }
        }

        if (pojo.getClass().isArray()) {
            if (Collection.class.isAssignableFrom(type)) {
                Class<?> ctype = pojo.getClass().getComponentType();
                int len = Array.getLength(pojo);
                Collection dest = createCollection(type, len);
                history.put(pojo, dest);
                for (int i = 0; i < len; i++) {
                    Object obj = Array.get(pojo, i);
                    Object value = realize1(obj, ctype, null, mapGeneric, history);
                    dest.add(value);
                }
                return dest;
            } else {
                Class<?> ctype = (type != null && type.isArray()
                        ? type.getComponentType()
                        : pojo.getClass().getComponentType());
                int len = Array.getLength(pojo);
                Object dest = Array.newInstance(ctype, len);
                history.put(pojo, dest);
                for (int i = 0; i < len; i++) {
                    Object obj = Array.get(pojo, i);
                    Object value = realize1(obj, ctype, null, mapGeneric, history);
                    Array.set(dest, i, value);
                }
                return dest;
            }
        }

        if (pojo instanceof Collection<?>) {
            if (type.isArray()) {
                Class<?> ctype = type.getComponentType();
                Collection<Object> src = (Collection<Object>) pojo;
                int len = src.size();
                Object dest = Array.newInstance(ctype, len);
                history.put(pojo, dest);
                int i = 0;
                for (Object obj : src) {
                    Object value = realize1(obj, ctype, null, mapGeneric, history);
                    Array.set(dest, i, value);
                    i++;
                }
                return dest;
            } else {
                Collection<Object> src = (Collection<Object>) pojo;
                int len = src.size();
                Collection<Object> dest = createCollection(type, len);
                history.put(pojo, dest);
                for (Object obj : src) {
                    Type keyType = getGenericClassByIndex(genericType, 0);
                    Class<?> keyClazz = obj == null ? null : obj.getClass();
                    if (keyType instanceof Class) {
                        keyClazz = (Class<?>) keyType;
                    }
                    Object value = realize1(obj, keyClazz, keyType, mapGeneric, history);
                    dest.add(value);
                }
                return dest;
            }
        }

        if (pojo instanceof Map<?, ?> && type != null) {
            Object className = ((Map<Object, Object>) pojo).get("class");
            if (className instanceof String) {
                if (!CLASS_NOT_FOUND_CACHE.containsKey(className)) {
                    try {
                        type = DefaultSerializeClassChecker.getInstance()
                                .loadClass(ClassUtils.getClassLoader(), (String) className);
                    } catch (ClassNotFoundException e) {
                        CLASS_NOT_FOUND_CACHE.put((String) className, NOT_FOUND_VALUE);
                    }
                }
            }

            // special logic for enum
            if (type.isEnum()) {
                Object name = ((Map<Object, Object>) pojo).get("name");
                if (name != null) {
                    if (!(name instanceof String)) {
                        throw new IllegalArgumentException("`name` filed should be string!");
                    } else {
                        return Enum.valueOf((Class<Enum>) type, (String) name);
                    }
                }
            }
            Map<Object, Object> map;
            // when return type is not the subclass of return type from the signature and not an interface
            if (!type.isInterface() && !type.isAssignableFrom(pojo.getClass())) {
                try {
                    map = (Map<Object, Object>) type.getDeclaredConstructor().newInstance();
                    Map<Object, Object> mapPojo = (Map<Object, Object>) pojo;
                    map.putAll(mapPojo);
                    if (GENERIC_WITH_CLZ) {
                        map.remove("class");
                    }
                } catch (Exception e) {
                    // ignore error
                    map = (Map<Object, Object>) pojo;
                }
            } else {
                map = (Map<Object, Object>) pojo;
            }

            if (Map.class.isAssignableFrom(type) || type == Object.class) {
                final Map<Object, Object> result;
                // fix issue#5939
                Type mapKeyType = getKeyTypeForMap(map.getClass());
                Type typeKeyType = getGenericClassByIndex(genericType, 0);
                boolean typeMismatch = mapKeyType instanceof Class
                        && typeKeyType instanceof Class
                        && !typeKeyType.getTypeName().equals(mapKeyType.getTypeName());
                if (typeMismatch) {
                    result = createMap(new HashMap(0));
                } else {
                    result = createMap(map);
                }

                history.put(pojo, result);
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    Type keyType = getGenericClassByIndex(genericType, 0);
                    Type valueType = getGenericClassByIndex(genericType, 1);
                    Class<?> keyClazz;
                    if (keyType instanceof Class) {
                        keyClazz = (Class<?>) keyType;
                    } else if (keyType instanceof ParameterizedType) {
                        keyClazz = (Class<?>) ((ParameterizedType) keyType).getRawType();
                    } else {
                        keyClazz =
                                entry.getKey() == null ? null : entry.getKey().getClass();
                    }
                    Class<?> valueClazz;
                    if (valueType instanceof Class) {
                        valueClazz = (Class<?>) valueType;
                    } else if (valueType instanceof ParameterizedType) {
                        valueClazz = (Class<?>) ((ParameterizedType) valueType).getRawType();
                    } else {
                        valueClazz = entry.getValue() == null
                                ? null
                                : entry.getValue().getClass();
                    }

                    Object key = keyClazz == null
                            ? entry.getKey()
                            : realize1(entry.getKey(), keyClazz, keyType, mapGeneric, history);
                    Object value = valueClazz == null
                            ? entry.getValue()
                            : realize1(entry.getValue(), valueClazz, valueType, mapGeneric, history);
                    result.put(key, value);
                }
                return result;
            } else if (type.isInterface()) {
                Object dest = Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(),
                        new Class<?>[] {type},
                        new PojoInvocationHandler(map));
                history.put(pojo, dest);
                return dest;
            } else {
                Object dest;
                if (Throwable.class.isAssignableFrom(type)) {
                    Object message = map.get("message");
                    if (message instanceof String) {
                        dest = newThrowableInstance(type, (String) message);
                    } else {
                        dest = newInstance(type);
                    }
                } else {
                    dest = newInstance(type);
                }

                history.put(pojo, dest);

                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    Object key = entry.getKey();
                    if (key instanceof String) {
                        String name = (String) key;
                        Object value = entry.getValue();
                        if (value != null) {
                            Method method = getSetterMethod(dest.getClass(), name, value.getClass());
                            Field field = getAndCacheField(dest.getClass(), name);
                            if (method != null) {
                                if (!method.isAccessible()) {
                                    method.setAccessible(true);
                                }
                                Type containType = Optional.ofNullable(field)
                                        .map(Field::getGenericType)
                                        .map(Type::getTypeName)
                                        .map(mapGeneric::get)
                                        .orElse(null);
                                if (containType != null) {
                                    // is generic
                                    if (containType instanceof ParameterizedType) {
                                        value = realize1(
                                                value,
                                                (Class<?>) ((ParameterizedType) containType).getRawType(),
                                                containType,
                                                mapGeneric,
                                                history);
                                    } else if (containType instanceof Class) {
                                        value = realize1(
                                                value, (Class<?>) containType, containType, mapGeneric, history);
                                    } else {
                                        Type ptype = method.getGenericParameterTypes()[0];
                                        value = realize1(
                                                value, method.getParameterTypes()[0], ptype, mapGeneric, history);
                                    }
                                } else {
                                    Type ptype = method.getGenericParameterTypes()[0];
                                    value = realize1(value, method.getParameterTypes()[0], ptype, mapGeneric, history);
                                }
                                try {
                                    method.invoke(dest, value);
                                } catch (Exception e) {
                                    String exceptionDescription = "Failed to set pojo "
                                            + dest.getClass().getSimpleName() + " property " + name + " value "
                                            + value.getClass() + ", cause: " + e.getMessage();
                                    logger.error(COMMON_REFLECTIVE_OPERATION_FAILED, "", "", exceptionDescription, e);
                                    throw new RuntimeException(exceptionDescription, e);
                                }
                            } else if (field != null) {
                                value = realize1(value, field.getType(), field.getGenericType(), mapGeneric, history);
                                try {
                                    field.set(dest, value);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(
                                            "Failed to set field " + name + " of pojo "
                                                    + dest.getClass().getName() + " : " + e.getMessage(),
                                            e);
                                }
                            }
                        }
                    }
                }
                return dest;
            }
        }
        return pojo;
    }

    /**
     * Get key type for {@link Map} directly implemented by {@code clazz}.
     * If {@code clazz} does not implement {@link Map} directly, return {@code null}.
     *
     * @param clazz {@link Class}
     * @return Return String.class for {@link com.alibaba.fastjson.JSONObject}
     */
    private static Type getKeyTypeForMap(Class<?> clazz) {
        Type[] interfaces = clazz.getGenericInterfaces();
        if (!ArrayUtils.isEmpty(interfaces)) {
            for (Type type : interfaces) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType t = (ParameterizedType) type;
                    if ("java.util.Map".equals(t.getRawType().getTypeName())) {
                        return t.getActualTypeArguments()[0];
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get parameterized type
     *
     * @param genericType generic type
     * @param index       index of the target parameterized type
     * @return Return Person.class for List<Person>, return Person.class for Map<String, Person> when index=0
     */
    private static Type getGenericClassByIndex(Type genericType, int index) {
        Type clazz = null;
        // find parameterized type
        if (genericType instanceof ParameterizedType) {
            ParameterizedType t = (ParameterizedType) genericType;
            Type[] types = t.getActualTypeArguments();
            clazz = types[index];
        }
        return clazz;
    }

    private static Object newThrowableInstance(Class<?> cls, String message) {
        try {
            Constructor<?> messagedConstructor = cls.getDeclaredConstructor(String.class);
            return messagedConstructor.newInstance(message);
        } catch (Exception t) {
            return newInstance(cls);
        }
    }

    private static Object newInstance(Class<?> cls) {
        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (Exception t) {
            Constructor<?>[] constructors = cls.getDeclaredConstructors();
            /*
             From Javadoc java.lang.Class#getDeclaredConstructors
             This method returns an array of Constructor objects reflecting all the constructors
             declared by the class represented by this Class object.
             This method returns an array of length 0,
             if this Class object represents an interface, a primitive type, an array class, or void.
            */
            if (constructors.length == 0) {
                throw new RuntimeException("Illegal constructor: " + cls.getName());
            }
            Throwable lastError = null;
            Arrays.sort(constructors, Comparator.comparingInt(a -> a.getParameterTypes().length));
            for (Constructor<?> constructor : constructors) {
                try {
                    constructor.setAccessible(true);
                    Object[] parameters = Arrays.stream(constructor.getParameterTypes())
                            .map(MyPojoUtils::getDefaultValue)
                            .toArray();
                    return constructor.newInstance(parameters);
                } catch (Exception e) {
                    lastError = e;
                }
            }
            throw new RuntimeException(lastError.getMessage(), lastError);
        }
    }

    /**
     * return init value
     *
     * @param parameterType
     * @return
     */
    private static Object getDefaultValue(Class<?> parameterType) {
        if ("char".equals(parameterType.getName())) {
            return Character.MIN_VALUE;
        }
        if ("boolean".equals(parameterType.getName())) {
            return false;
        }
        if ("byte".equals(parameterType.getName())) {
            return (byte) 0;
        }
        if ("short".equals(parameterType.getName())) {
            return (short) 0;
        }
        return parameterType.isPrimitive() ? 0 : null;
    }

    private static Method getSetterMethod(Class<?> cls, String property, Class<?> valueCls) {
        String name = "set" + property.substring(0, 1).toUpperCase() + property.substring(1);
        Method method = NAME_METHODS_CACHE.get(cls.getName() + "." + name + "(" + valueCls.getName() + ")");
        if (method == null) {
            try {
                method = cls.getMethod(name, valueCls);
            } catch (NoSuchMethodException e) {
                for (Method m : cls.getMethods()) {
                    if (ReflectUtils.isBeanPropertyWriteMethod(m) && m.getName().equals(name)) {
                        method = m;
                        break;
                    }
                }
            }
            if (method != null) {
                NAME_METHODS_CACHE.put(cls.getName() + "." + name + "(" + valueCls.getName() + ")", method);
            }
        }
        return method;
    }

    private static Field getAndCacheField(Class<?> cls, String fieldName) {
        Field result;
        if (CLASS_FIELD_CACHE.containsKey(cls) && CLASS_FIELD_CACHE.get(cls).containsKey(fieldName)) {
            return CLASS_FIELD_CACHE.get(cls).get(fieldName);
        }

        result = getField(cls, fieldName);

        if (result != null) {
            ConcurrentMap<String, Field> fields =
                    CLASS_FIELD_CACHE.computeIfAbsent(cls, k -> new ConcurrentHashMap<>());
            fields.putIfAbsent(fieldName, result);
        }
        return result;
    }

    private static Field getField(Class<?> cls, String fieldName) {
        Field result = null;
        for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
            try {
                result = acls.getDeclaredField(fieldName);
                if (!Modifier.isPublic(result.getModifiers())) {
                    result.setAccessible(true);
                }
            } catch (NoSuchFieldException e) {
            }
        }
        if (result == null && cls != null) {
            for (Field field : cls.getFields()) {
                if (fieldName.equals(field.getName()) && ReflectUtils.isPublicInstanceField(field)) {
                    result = field;
                    break;
                }
            }
        }
        return result;
    }

    public static boolean isPojo(Class<?> cls) {
        return !ReflectUtils.isPrimitives(cls)
                && !Collection.class.isAssignableFrom(cls)
                && !Map.class.isAssignableFrom(cls);
    }

    /**
     * Update the property if absent
     *
     * @param getterMethod the getter method
     * @param setterMethod the setter method
     * @param newValue     the new value
     * @param <T>          the value type
     * @since 2.7.8
     */
    public static <T> void updatePropertyIfAbsent(Supplier<T> getterMethod, Consumer<T> setterMethod, T newValue) {
        if (newValue != null && getterMethod.get() == null) {
            setterMethod.accept(newValue);
        }
    }

    /**
     * convert map to a specific class instance
     *
     * @param map map wait for convert
     * @param cls the specified class
     * @param <T> the type of {@code cls}
     * @return class instance declare in param {@code cls}
     * @throws ReflectiveOperationException if the instance creation is failed
     * @since 2.7.10
     */
    public static <T> T mapToPojo(Map<String, Object> map, Class<T> cls) throws ReflectiveOperationException {
        T instance = cls.getDeclaredConstructor().newInstance();
        Map<String, Field> beanPropertyFields = ReflectUtils.getBeanPropertyFields(cls);
        for (Map.Entry<String, Field> entry : beanPropertyFields.entrySet()) {
            String name = entry.getKey();
            Field field = entry.getValue();
            Object mapObject = map.get(name);
            if (mapObject == null) {
                continue;
            }

            Type type = field.getGenericType();
            Object fieldObject = getFieldObject(mapObject, type);
            field.set(instance, fieldObject);
        }

        return instance;
    }

    private static Object getFieldObject(Object mapObject, Type fieldType) throws ReflectiveOperationException {
        if (fieldType instanceof Class<?>) {
            return convertClassType(mapObject, (Class<?>) fieldType);
        } else if (fieldType instanceof ParameterizedType) {
            return convertParameterizedType(mapObject, (ParameterizedType) fieldType);
        } else if (fieldType instanceof GenericArrayType
                || fieldType instanceof TypeVariable<?>
                || fieldType instanceof WildcardType) {
            // ignore these type currently
            return null;
        } else {
            throw new IllegalArgumentException("Unrecognized Type: " + fieldType.toString());
        }
    }

    @SuppressWarnings("unchecked")
    private static Object convertClassType(Object mapObject, Class<?> type) throws ReflectiveOperationException {
        if (type.isPrimitive() || isAssignableFrom(type, mapObject.getClass())) {
            return mapObject;
        } else if (Objects.equals(type, String.class) && CLASS_CAN_BE_STRING.contains(mapObject.getClass())) {
            // auto convert specified type to string
            return mapObject.toString();
        } else if (mapObject instanceof Map) {
            return mapToPojo((Map<String, Object>) mapObject, type);
        } else {
            // type didn't match and mapObject is not another Map struct.
            // we just ignore this situation.
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static Object convertParameterizedType(Object mapObject, ParameterizedType type)
            throws ReflectiveOperationException {
        Type rawType = type.getRawType();
        if (!isAssignableFrom((Class<?>) rawType, mapObject.getClass())) {
            return null;
        }

        Type[] actualTypeArguments = type.getActualTypeArguments();
        if (isAssignableFrom(Map.class, (Class<?>) rawType)) {
            Map<Object, Object> map = (Map<Object, Object>)
                    mapObject.getClass().getDeclaredConstructor().newInstance();
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) mapObject).entrySet()) {
                Object key = getFieldObject(entry.getKey(), actualTypeArguments[0]);
                Object value = getFieldObject(entry.getValue(), actualTypeArguments[1]);
                map.put(key, value);
            }

            return map;
        } else if (isAssignableFrom(Collection.class, (Class<?>) rawType)) {
            Collection<Object> collection = (Collection<Object>)
                    mapObject.getClass().getDeclaredConstructor().newInstance();
            for (Object m : (Iterable<?>) mapObject) {
                Object ele = getFieldObject(m, actualTypeArguments[0]);
                collection.add(ele);
            }

            return collection;
        } else {
            // ignore other type currently
            return null;
        }
    }
}
