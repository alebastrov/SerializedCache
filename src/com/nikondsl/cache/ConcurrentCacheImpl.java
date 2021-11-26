package com.nikondsl.cache;

import net.sf.cglib.beans.BeanGenerator;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.nikondsl.cache.ConcurrentCacheImpl.SetGet.GET;
import static com.nikondsl.cache.ConcurrentCacheImpl.SetGet.SET;

public class ConcurrentCacheImpl<K, V> implements Cache<K, V> {
    private static final String DEEP_COPY = "DeepCopy";
    private static final String FLAGS = "flagsForObject";
    private ConcurrentMap<K, Object> map = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Class> cachedClasses = new ConcurrentHashMap<>();

    @Override
    public boolean put(K key, V value) throws CompactingException {
        if (value.getClass().getAnnotation(MayBeCompacted.class) != null) {
            // compact:
            // - get all fields supporting compacting
            // - for each field create a shallow byte[] with compacted form
            // - replace field value with null
            try {
                //collect parameters for POJO class
                Map<String, Class<?>> parameters = new HashMap<>();
                Properties flagsForObject = new Properties();

                //put fields to class
                for (Field field : value.getClass().getDeclaredFields()) {
                    if (field.getAnnotation(MayBeCompacted.class) == null) {
                        parameters.put(field.getName(), field.getType());
                        continue;
                    }
                    if (field.getType() != String.class && field.getType() != byte[].class) {
                        throw new CompactingException("Only strings and byte[] are supported");
                    }
                    field.setAccessible(true);
                    //add new synthetic field
                    parameters.put(field.getName() + "CompressedCopy", byte[].class);
                }
                parameters.put(FLAGS, Properties.class);
                //create a new class
                Class clazz = cachedClasses.get(value.getClass().getCanonicalName() + DEEP_COPY);
                if (clazz == null) {
                    clazz = createBeanClass(value.getClass().getCanonicalName() + DEEP_COPY, parameters);
                    cachedClasses.putIfAbsent(value.getClass().getCanonicalName() + DEEP_COPY, clazz);
                }
                Object obj = clazz.newInstance();
                //init a class with values
                for (Field field : value.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    MayBeCompacted annotation = field.getAnnotation(MayBeCompacted.class);
                    if (annotation != null) {
                        //set synthetic field
                        //compact old field value
                        byte[] compressedBytes;
                        byte[] uncompressedBytes;
                        if (field.getType() == String.class) {
                            String uncompressedValue = (String) field.get(value);
                            uncompressedBytes = uncompressedValue.getBytes(StandardCharsets.UTF_8);
                        } else {
                            uncompressedBytes = (byte[]) field.get(value);
                        }
                        if (uncompressedBytes.length > annotation.ifMoreThen()) {
                            compressedBytes = ZipUtil.zip(uncompressedBytes);
                            flagsForObject.put(field.getName(), "compressed");
                        } else {
                            compressedBytes = uncompressedBytes;
                            flagsForObject.put(field.getName(), "as it is");
                        }
                        invokeMethod(SET,
                                field.getName() + "CompressedCopy",
                                obj,
                                new Class[] { byte[].class },
                                new Object[]{ compressedBytes });
                        continue;
                    }
                    //copy original value
                    invokeMethod(SET,
                            field.getName(),
                            obj,
                            new Class[] { field.getType() },
                            new Object[]{ field.get(value) });
                }
                //set flags
                invokeMethod(SET, FLAGS, obj, new Class[] { Properties.class }, new Object[] { flagsForObject });

                //put into cache compressed
                return map.putIfAbsent(key, obj) != null;
            } catch (Exception ex) {
                throw new CompactingException(ex);
            }
        }
        //put into cache usual
        return map.putIfAbsent(key, value) != null;
    }

    @Override
    public V get(K key) throws CompactingException {

        Object v = map.get(key);
        if (v != null && v.getClass().getName().endsWith(DEEP_COPY)) {
            // de-compass:
            try {
                // take the properties
                Properties flagsForObject = (Properties) invokeGet(FLAGS, v);
                // create original class
                String className = v.getClass().getName().replace(DEEP_COPY, "");
                Class clazz = cachedClasses.get(className);
                if (clazz == null) {
                    clazz = Class.forName(className);
                    cachedClasses.putIfAbsent(className, clazz);
                }
                Object toBeSetUp = clazz.newInstance();
                // copy all fields
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.getAnnotation(MayBeCompacted.class) != null) {
                        byte[] compressedBytes = (byte[]) invokeGet(field.getName() + "CompressedCopy", v);
                        byte[] uncompressedBytes;
                        if ("compressed".equals(flagsForObject.get(field.getName()))) {
                            uncompressedBytes = ZipUtil.unZip(compressedBytes);
                        } else {
                            uncompressedBytes = compressedBytes;
                        }
                        // set original value up
                        if (field.getType() == String.class) {
                            String value = new String(uncompressedBytes, StandardCharsets.UTF_8);
                            field.set(toBeSetUp, value);
                        } else {
                            field.set(toBeSetUp, uncompressedBytes);
                        }
                        continue;
                    }
                    Object value = invokeGet(field.getName(), v);
                    //copy usual field
                    field.set(toBeSetUp, value);
                }
                return (V) toBeSetUp;
            } catch (Exception ex) {
                throw new CompactingException(ex);
            }
            // - create a new object to return using default constructor
            // - get all fields supporting compacting
            // - for each field look for shallow byte[] with compacted form
            // - replace field value with real value

        }
        return (V) v;
    }

    @Override
    public void remove(K key) {
        map.remove(key);
    }

    public enum SetGet {
        SET("set"), GET("get");

        private String name;

        SetGet(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private Object invokeGet(String fieldName, Object target)
            throws ReflectiveOperationException {
        return invokeMethod(GET, fieldName, target, new Class[] {}, new Object[] {});
    }

    private Object invokeMethod(SetGet setGet,
                                String fieldName,
                                Object target,
                                Class[] paramClasses,
                                Object[] params)
            throws ReflectiveOperationException {
        String nethodName = setGet.getName() + toCapitalize(fieldName);
        Method toBeCalled = target.getClass().getMethod(nethodName, paramClasses);
        return toBeCalled.invoke(target, params);
    }

    private String toCapitalize(String name) {
        if (name == null || name.length() < 1) {
            throw new IllegalArgumentException("field name is illegal");
        }
        if (name.length() > 1) {
            return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        }
        return name.substring(0, 1).toUpperCase(Locale.ROOT);
    }

    public static Class<?> createBeanClass(
            /* fully qualified class name */
            final String className,
            /* bean properties, name -> type */
            final Map<String, Class<?>> properties) {

        BeanGenerator beanGenerator = new BeanGenerator();
        /* use our own hard coded class name instead of a real naming policy */
        beanGenerator.setNamingPolicy((prefix, source, key, names) -> className);
        BeanGenerator.addProperties(beanGenerator, properties);
        return (Class<?>) beanGenerator.createClass();
    }
}
