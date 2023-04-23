package xhh.club.bean;

public interface XhhApplicationContext {

    /**
     * 获取Bean对象
     * @param clazz clazz
     * @return {@link Object}
     */
    Object getBean(Class clazz);
}
