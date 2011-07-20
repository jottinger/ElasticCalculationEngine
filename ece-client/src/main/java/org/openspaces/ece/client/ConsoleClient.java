package org.openspaces.ece.client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConsoleClient {
    @Parameter(names = {"-type"}, description = "The type of the client to run (masterworker/executor)")
    public String type = "masterworker";
    @Parameter(names = "-iterations", description = "the number of iterations to run")
    public Integer maxIterations = null;
    @Parameter(names = "-trades", description = "the number of trades to run")
    public Integer maxTrades = null;

    ApplicationContext ctx;

    public static void main(String... args) {
        ConsoleClient client = new ConsoleClient(new ClassPathXmlApplicationContext("/applicationContext.xml"));
        new JCommander(client, args);
        client.run();
    }

    private void run() {
        ECEClient eceClient = ctx.getBean(type, ECEClient.class);
        if (maxIterations != null) {
            eceClient.setMaxIterations(maxIterations);
        }
        if (maxTrades != null) {
            eceClient.setMaxTrades(maxTrades);
        }
        eceClient.issueTrades();
    }

    ConsoleClient(ApplicationContext ctx) {
        this.ctx = ctx;
    }
}
