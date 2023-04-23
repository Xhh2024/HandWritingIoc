package xhh.club.bean;

import xhh.club.annotation.XhhBean;
import xhh.club.annotation.XhhDI;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 应用上下文程序
 * @author 谢环环
 * @date 2023/04/23
 */
public class AnnotationXhhApplicationContext implements XhhApplicationContext{

    /**
     * bean存储容器
     */
    private Map<Class, Object> beanFactory = new HashMap<>();

    /**
     * 绝对路径
     */
    private String rootPath;


    /**
     * 获取bean对象
     * @param clazz clazz
     * @return {@link Object}
     */
    @Override
    public Object getBean(Class clazz) {
        return beanFactory.get(clazz);
    }

    /**
     * 扫描文件, 找出被注解标记的文件, 并且加入的存储容器管理
     * @param basePackage 基本包
     */
    public AnnotationXhhApplicationContext(String basePackage){
        try {
            //1. 把.替换成\, xhh\club
            String packagePath = basePackage.replaceAll("\\.","\\\\");
            //2. 获取包的绝对路径
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            //遍历整个URL地址
            while (urls.hasMoreElements()){
                URL url = urls.nextElement();
                //进行编码转义
                String filePath = URLDecoder.decode(url.getFile(), "utf-8");
                //获取文件的绝对路径
                rootPath = filePath.substring(0,filePath.length()-basePackage.length());
                //扫描整个文件,将被标记的bean加入到容器,并且进行管理
                loadBean(new File(filePath));
            }
            //属性注入
            loadDi();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 加载bean
     * 扫描整个路径下的文件夹, 然后将被注解标记的类加入到beanFactory进行管理
     * @param file 文件
     */
    private void loadBean(File file) throws Exception {
        //1.判断是否是文件夹
        if (file.isDirectory()){
            //2. 获取文件夹里面的所有内容
            File[] childrenFiles = file.listFiles();
            //3. 判断文件夹是否为空
            if (childrenFiles.length == 0 || childrenFiles == null){
                return;
            }
            //4.如果文件夹不为空，则遍历文件夹所有内容
            for (File child : childrenFiles) {
                //5.遍历得到每个file对象，如果还是文件夹那么继续遍历
                if (child.isDirectory()){
                    //递归
                    loadBean(child);
                }else{
                    //6.遍历得到的不是文件夹，而是文件,得到了包路径+类名称部分-字符串截取
                    String pathWithClass = child.getAbsolutePath().substring(rootPath.length()-1);
                    //7. 判断文件是否是class文件
                    if (pathWithClass.contains(".class")){
                        //8. 如果是.class类型.把路径\替换, 把.class去掉
                        String allPathName = pathWithClass.replaceAll("\\\\", "\\.")
                                .replaceAll(".class", "");
                        //9. 判断类上面是否有注解@XhhBean, 如果有实例化
                        //实例化对象
                        Class<?> clazz = Class.forName(allPathName);
                        //判断对象是不是接口
                        if (!clazz.isInterface()){
                            //判断类上是否有@XhhBean注解
                            Annotation annotation = clazz.getAnnotation(XhhBean.class);
                            //如果有就实例化
                            if (annotation != null){
                                Object obj = clazz.getConstructor().newInstance();
                                //10.判断这个类是否有接口, 如果有则需要使用接口作为key
                                if (clazz.getInterfaces().length > 0){
                                    //11.把对象实例化之后，放到map集合beanFactory中
                                    beanFactory.put(clazz.getInterfaces()[0], obj);
                                }else{
                                    beanFactory.put(clazz, obj);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 属性注入Di
     */
    private void loadDi() throws IllegalAccessException {
        //遍历bean容器里面的beanFactory
        Set<Map.Entry<Class, Object>> entries = beanFactory.entrySet();
        for (Map.Entry<Class,Object> entry : entries) {
            //获取Class对象
            Object obj = entry.getValue();
            Class<?> clazz = obj.getClass();
            //通过反射操作每个对象的属性
            Field[] fields = clazz.getDeclaredFields();
            //遍历属性
            for (Field field : fields) {
                //判断属性是否被标记XhhDI注解
                Annotation annotation = field.getAnnotation(XhhDI.class);
                if (annotation != null){
                    //如果是私有属性, 设置允许访问
                    field.setAccessible(true);
                    //属性注入, 将IOC容器里面对象注入到该属性中
                    field.set(obj, beanFactory.get(field.getType()));
                }
            }
        }
    }
}
