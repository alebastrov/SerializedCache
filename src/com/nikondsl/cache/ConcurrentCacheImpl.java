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

public class ConcurrentCacheImpl<K, V> implements Cache<K, V> {
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
                parameters.put("flagsForObject", Properties.class);
                //create a new class
                Class clazz = cachedClasses.get(value.getClass().getCanonicalName() + "DeepCopy");
                if (clazz == null) {
                    clazz = createBeanClass(value.getClass().getCanonicalName() + "DeepCopy", parameters);
                    cachedClasses.putIfAbsent(value.getClass().getCanonicalName() + "DeepCopy", clazz);
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
                        } else {
                            compressedBytes = uncompressedBytes;
                        }
                        invokeMethod(SetGet.SET,
                                field.getName() + "CompressedCopy",
                                obj,
                                new Class[] { byte[].class },
                                new Object[]{ compressedBytes });
                        continue;
                    }
                    //copy original value
                    invokeMethod(SetGet.SET,
                            field.getName(),
                            obj,
                            new Class[] { field.getType() },
                            new Object[]{ field.get(value) });
                }
                //put into cache compressed
                return map.putIfAbsent(key, obj) != null;
            } catch (Exception ex) {
                throw new CompactingException(ex);
            }
        }
        //put into cache usual
        return map.putIfAbsent(key, value) != null;
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

    private void invokeMethod(SetGet setGet, String fieldName, Object target, Class[] paramClasses, Object[] params)
            throws ReflectiveOperationException {
        String nethodName = setGet.getName() + toCapitalize(fieldName);
        Method toBeCalled = target.getClass().getMethod(nethodName, paramClasses);
        toBeCalled.invoke(target, params);
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

    @Override
    public V get(K key) throws CompactingException {

        Object v = map.get(key);
        if (v != null && v.getClass().getName().endsWith("DeepCopy")) {
            // de-compass:
            // create original class
            try {
                Class clazz = Class.forName(v.getClass().getName().replace("DeepCopy", ""));
                Object toBeSetUp = clazz.newInstance();
                // copy all fields
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.getAnnotation(MayBeCompacted.class) != null) {
                        Method toBeInvoked = v.getClass().getMethod("get" + toCapitalize(field.getName()) + "CompressedCopy",
                                new Class[] {});
                        byte[] compressedBytes = (byte[]) toBeInvoked.invoke(v, new Object[] {});
                        byte[] uncompressedBytes;
                        try {
                            uncompressedBytes = ZipUtil.unZip(compressedBytes);
                        } catch (Exception ex) {
                            uncompressedBytes = compressedBytes;
                        }
                        //de-compress and setup
                        if (field.getType() == String.class) {
                             String value = new String(uncompressedBytes, StandardCharsets.UTF_8);
                             field.set(toBeSetUp, value);
                        } else {
                            field.set(toBeSetUp, uncompressedBytes);
                        }
                        continue;
                    }
                    Method toBeInvoked = v.getClass().getMethod("get" + toCapitalize(field.getName()), new Class[] {});
                    //copy usual field
                    field.set(toBeSetUp, toBeInvoked.invoke(v, new Object[] {}));
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
}
