import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ClassLoaderUtil {

    private final CustomClassLoader classLoader;
    private Map loadedClasses;

    public ClassLoaderUtil(String classLocation) {
        this.classLoader = new CustomClassLoader(classLocation);
    }

    public void loadClass(String className) throws ClassNotFoundException {
        HashMap<Object, Object> loadedClasses = null;
        if (loadedClasses.containsKey(className)) {
            return; // Class already loaded
        }

        classLoader.loadClass(className);
        Class<?> loadedClass = classLoader.getClass(className);
        loadedClasses.put(className, new WeakReference<>(loadedClass));
    }

    public Task createTaskInstance(String className) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        WeakReference<Class<?>> classRef = (WeakReference<Class<?>>) loadedClasses.get(className);
        if (classRef == null) {
            throw new IllegalArgumentException("Class not loaded.");
        }
        Class<?> taskClass = classRef.get();
        if (taskClass == null) {
            throw new IllegalStateException("Class was unloaded.");
        }
        Constructor<?> constructor = taskClass.getDeclaredConstructor();
        return (Task) constructor.newInstance();
    }

    public void unloadClass(String className) {
        loadedClasses.remove(className);
        // Dodatkowo można wywołać mechanizm odśmiecania, aby zachęcić do wyładowania klas
        System.gc();
    }

    public ArrayList getLoadedClassNames() {
        ArrayList arrayList = new ArrayList<>((Collection) loadedClasses.keySet());
        return arrayList;
    }
}