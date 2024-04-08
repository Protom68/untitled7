public class Map<T, T1> {
    public boolean containsKey(T className) {
        return false;
    }

    public void put(String className, WeakReference<Class<?>> aClass) {
    }

    public T1 get(T className) {
        return null;
    }

    public void remove(T className) {
    }

    public T1 keySet() {
        return null;
    }

    public Iterable<? extends Entry<String,WeakReference<Class<?>>>> entrySet() {
        return null;
    }

    public boolean get() {
        return false;
    }


    public class Entry<T, T1> {
        public Map<Object, Object> getValue() {
            return null;
        }

        public T getKey() {
            return null;
        }
    }
}
