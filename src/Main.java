import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

interface Task {
    void execute();
}

interface Processor {
    String process(String input);
    String getInfo();
}

class SumProcessor implements Processor {
    @Override
    public String process(String input) {
        String[] numbers = input.split("\\+");
        int sum = 0;
        for (String num : numbers) {
            sum += Integer.parseInt(num.trim());
        }
        return String.valueOf(sum);
    }

    @Override
    public String getInfo() {
        return "Sumowanie";
    }
}

class UpperCaseProcessor implements Processor {
    @Override
    public String process(String input) {
        return input.toUpperCase();
    }

    @Override
    public String getInfo() {
        return "Zamiana małych liter na duże";
    }
}

class ReverseProcessor implements Processor {
    @Override
    public String process(String input) {
        return new StringBuilder(input).reverse().toString();
    }

    @Override
    public String getInfo() {
        return "Odwracanie ciągu znaków";
    }
}

class Status {
    private final String message;
    private final boolean isCompleted;

    public Status(String message, boolean isCompleted) {
        this.message = message;
        this.isCompleted = isCompleted;
    }

    public String getMessage() {
        return message;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}

interface StatusListener {
    void onStatusUpdate(Status status);
}

class TaskExecutor implements Runnable {
    private final String task;
    private final String className;
    private final List<StatusListener> statusListeners;

    public TaskExecutor(String task, String className, List<StatusListener> statusListeners) {
        this.task = task;
        this.className = className;
        this.statusListeners = statusListeners;
    }

    @Override
    public void run() {
        Status status = new Status("Rozpoczęto przetwarzanie zadania.", false);
        notifyStatusUpdate(status);
        try {
            Thread.sleep(2000); // Symulacja opóźnienia
            Task taskInstance = createTaskInstance(className);
            if (taskInstance instanceof Processor) {
                Processor processor = (Processor) taskInstance;
                String result = processor.process(task);
                status = new Status("Zadanie zakończone. Wynik: " + result, true);
            } else {
                status = new Status("Błąd: Wybrana klasa nie implementuje interfejsu Processor.", true);
            }
        } catch (Exception ex) {
            status = new Status("Błąd podczas wykonywania zadania: (Brak załadowanych klas) " + ex.getMessage(), true);
        }
        notifyStatusUpdate(status);
    }

    private Task createTaskInstance(String className) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, InvocationTargetException {
        Class<?> loadedClass = Class.forName(className);
        if (Task.class.isAssignableFrom(loadedClass)) {
            Constructor<?> constructor = loadedClass.getDeclaredConstructor();
            return (Task) constructor.newInstance();
        } else {
            throw new IllegalArgumentException("Klasa nie implementuje interfejsu Task.");
        }
    }

    private void notifyStatusUpdate(Status status) {
        for (StatusListener listener : statusListeners) {
            listener.onStatusUpdate(status);
        }
    }
}
