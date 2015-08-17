package at.yawk.magellan.tools;

import at.yawk.magellan.tools.cli.Cli;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IStringConverterFactory;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
// we extend CliHolder so that gets called before any logger initializations
@Slf4j
public abstract class Application extends CliHolder
        implements ThrowingRunnable {

    private final ForkJoinPool pool = ForkJoinPool.commonPool();
    @Getter private Cli cli;

    public Application() {}

    final void initCli(Cli cli) {
        this.cli = cli;
    }

    final void loadArgs(List<String> args) {
        JCommander commander = new JCommander();
        commander.addConverterFactory(new IStringConverterFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> Class<? extends IStringConverter<T>> getConverter(Class<T> type) {
                if (type == Path.class) {
                    return (Class) PathConverter.class;
                }
                return null;
            }
        });
        commander.addObject(this);
        try {
            commander.parse(args.toArray(new String[args.size()]));
        } catch (ParameterException e) {
            e.printStackTrace();
            commander.usage();
            System.exit(1);
        }
    }

    public final void execute(@Nonnull ThrowingRunnable command) {
        pool.execute(() -> {
            try {
                command.run();
            } catch (Exception e) {
                log.error("Exception in task", e);
            }
        });
    }

    public final <T> CompletionStage<T> execute(@Nonnull Callable<T> command) {
        CompletableFuture<T> future = new CompletableFuture<>();
        execute(() -> {
            try {
                future.complete(command.call());
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public final <T> CompletionStage<T> executeLogging(@Nonnull Callable<T> command) {
        return execute(command).exceptionally(e -> {
            log.error("Exception in task", e);
            return null;
        });
    }

    final void start() throws Exception {
        log.info("Starting up");
        execute(this);
        pool.awaitQuiescence(30, TimeUnit.DAYS);
    }
}
