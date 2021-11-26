package com.nikondsl.cache;

import com.sun.beans.decoder.ValueObject;
import net.sf.cglib.beans.BeanGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
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
            Object obj = getCompactedCopy(value);
            //put into cache compacted object
            return map.putIfAbsent(key, obj) != null;
        }
        //put into cache original object
        return map.putIfAbsent(key, value) != null;
    }

    @Override
    public V get(K key) throws CompactingException {
        Object v = map.get(key);
        if (v != null && v.getClass().getName().endsWith(DEEP_COPY)) {
            return getOriginalCopy(v);
        }
        return (V) v;
    }

    @Override
    public void remove(K key) {
        map.remove(key);
    }


    private Object getCompactedCopy(Object value) throws CompactingException {
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
                field.setAccessible(true);
                if (field.getType() != String.class && field.getType() != byte[].class) {
                    if (!(field.get(value) instanceof Serializable)) {
                        throw new IllegalArgumentException("Only strings, byte[] and serializable objects " +
                                "might be compacted, but " + field.getType().getCanonicalName() + " provided");
                    }
                }

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
                    flagsForObject.put(field.getName(), "original");
                    if (field.getType() == String.class) {
                        String uncompressedValue = (String) field.get(value);
                        byte[] uncompressedBytes = uncompressedValue.getBytes(StandardCharsets.UTF_8);
                        if (canBeCompressed(uncompressedBytes, annotation)) {
                            compressedBytes = ZipUtil.zip(uncompressedBytes);
                            flagsForObject.put(field.getName(), "compressed");
                        } else compressedBytes = uncompressedBytes;
                    } else if (field.getType() == byte[].class) {
                        byte[] uncompressedBytes = (byte[]) field.get(value);
                        if (canBeCompressed(uncompressedBytes, annotation)) {
                            compressedBytes = ZipUtil.zip(uncompressedBytes);
                            flagsForObject.put(field.getName(), "compressed");
                        } else compressedBytes = uncompressedBytes;
                    } else {
                        //try to serialize object
                        byte[] uncompressedBytes = serializeObject((Serializable) field.get(value));
                        flagsForObject.put(field.getName(), "serialized");
                        if (canBeCompressed(uncompressedBytes, annotation)) {
                            compressedBytes = ZipUtil.zip(uncompressedBytes);
                            flagsForObject.put(field.getName(), "serialized/compressed");
                        } else compressedBytes = uncompressedBytes;
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
            return obj;
        } catch (Exception ex) {
            throw new CompactingException(ex);
        }
    }

    private boolean canBeCompressed(byte[] uncompressedBytes, MayBeCompacted annotation) {
      return uncompressedBytes.length > annotation.ifMoreThen();
    }

    private byte[] serializeObject(Serializable obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        return bos.toByteArray();
    }

    private Serializable deserializeObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bis);
        return (Serializable) in.readObject();
    }

    private V getOriginalCopy(Object v) throws CompactingException {
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
                    } else if ("original".equals(flagsForObject.get(field.getName()))) {
                        uncompressedBytes = compressedBytes;
                    } else {
                        //serialized/compressed or just serialized
                        byte[] toDeserialize;
                        if ("serialized/compressed".equals(flagsForObject.get(field.getName()))) {
                            toDeserialize = ZipUtil.unZip(compressedBytes);
                        } else {
                            toDeserialize = compressedBytes;
                        }
                        Serializable object = deserializeObject(toDeserialize);
                        field.set(toBeSetUp, object);
                        continue;
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
