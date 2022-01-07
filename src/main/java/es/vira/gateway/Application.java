package es.vira.gateway;

import es.vira.gateway.constant.ConsoleConstant;
import es.vira.gateway.environment.ApplicationContext;
import es.vira.gateway.limit.TokenProvider;
import es.vira.gateway.listener.HttpListener;
import es.vira.gateway.listener.HttpsListener;
import es.vira.gateway.mapping.SurvivalChecker;
import es.vira.gateway.thread.DefaultRejectedExecutionHandler;
import es.vira.gateway.thread.DefaultThreadFactory;
import es.vira.gateway.thread.ThreadPoolGroup;
import es.vira.gateway.util.ConsoleUtils;
import eu.medsea.mimeutil.MimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * starter
 *
 * @author Víctor Gómez
 * @since 1.0.0
 */
@Slf4j
public class Application {
    /**
     * main
     *
     * @param args args
     * @throws Exception application exception
     */
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(ConsoleConstant.COMMAND_CONFIG, true, "config file path");
        options.addOption(ConsoleConstant.COMMAND_HELP, false, "help info");
        options.addOption(ConsoleConstant.COMMAND_VERSION, false, "show version info");
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption(ConsoleConstant.COMMAND_HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", options);
            return;
        } else if (cmd.hasOption(ConsoleConstant.COMMAND_VERSION)) {
            log.info("version: " + ConsoleUtils.getVersion());
            return;
        }

        ApplicationContext ac;
        ac = new ApplicationContext(cmd.getOptionValue(ConsoleConstant.COMMAND_CONFIG));
//        if (cmd.hasOption(ConsoleConstant.COMMAND_CONFIG)) {
//            // initialize application context
//            ac = new ApplicationContext(cmd.getOptionValue(ConsoleConstant.COMMAND_CONFIG));
//        } else {
//            throw new FileNotFoundException("config file path arg is not found.");
//        }

        // initialize service thread pool
        ThreadPoolExecutor serviceThreadPool = new ThreadPoolExecutor(10, 10,
                2000, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(10), new DefaultThreadFactory("service"),
                new DefaultRejectedExecutionHandler());
        // load mime resource
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
        ThreadPoolGroup tpe = ac.getContext(ThreadPoolGroup.class);
        // run token provider
        serviceThreadPool.execute(new TokenProvider(ac));
        // run survival checker
        serviceThreadPool.execute(new SurvivalChecker(ac));
        // run http listener
        HttpListener http = new HttpListener(ac);
        serviceThreadPool.execute(http);
        // run https listener
        HttpsListener https = new HttpsListener(ac);
        serviceThreadPool.execute(https);
        log.info("Gateway is running.");

        // add shutdown hook event
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            http.shutdown();
            log.info("Http listener was shutdown!");
            https.shutdown();
            log.info("Https listener was shutdown!");
            tpe.shutdown();
            serviceThreadPool.shutdown();
            log.info("Gateway was shutdown!");
        }));
    }
}
