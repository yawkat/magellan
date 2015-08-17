package at.yawk.magellan.tools;

import at.yawk.magellan.tools.impl.TileCounter;
import at.yawk.magellan.tools.impl.TileRemover;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;

/**
 * @author yawkat
 */
public class Bootstrap {
    private static final Map<String, Class<? extends Application>> NAMED_APPLICATIONS =
            new HashMap<>();

    static {
        NAMED_APPLICATIONS.put("tileCounter", TileCounter.class);
        NAMED_APPLICATIONS.put("tileRemover", TileRemover.class);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length == 0) {
            System.out.println("Known modules: " + NAMED_APPLICATIONS.keySet());
            System.exit(1);
            return;
        }

        Class<? extends Application> applicationClass = NAMED_APPLICATIONS.get(args[0]);
        if (applicationClass == null) {
            applicationClass = Class.forName(args[0]).asSubclass(Application.class);
        }

        start(applicationClass, args, 1);
    }

    public static void start(Class<? extends Application> applicationClass, String[] args) {
        start(applicationClass, args, 0);
    }

    public static void start(Class<? extends Application> applicationClass, String[] args, int offset) {
        start(applicationClass, Arrays.asList(args).subList(offset, args.length));
    }

    @SneakyThrows
    public static void start(Class<? extends Application> applicationClass, List<String> args) {
        Application application = applicationClass.newInstance();
        application.initCli(CliHolder.CLI);
        application.loadArgs(args);
        application.start();
    }
}
