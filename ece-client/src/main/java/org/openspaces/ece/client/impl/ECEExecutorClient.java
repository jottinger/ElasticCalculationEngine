package org.openspaces.ece.client.impl;

import com.gigaspaces.async.AsyncFuture;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import org.openspaces.admin.pu.events.ProcessingUnitInstanceLifecycleEventListener;
import org.openspaces.calcengine.common.CalculateNPVUtil;
import org.openspaces.calcengine.masterworker.Request;
import org.openspaces.calcengine.masterworker.Result;
import org.openspaces.core.ExecutorBuilder;
import org.openspaces.core.GigaSpace;
import org.openspaces.ece.client.ECEClient;
import org.openspaces.ece.client.executors.AnalysisTask;
import org.openspaces.ece.client.executors.NPVResultsReducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ECEExecutorClient implements ECEClient, ProcessingUnitInstanceLifecycleEventListener {
    int workersCount = 8;
    ProcessingUnit workerPU = null;
    double rates[] = {2, 3, 4, 5, 6, 7, 8};
    Logger logger = Logger.getLogger(this.getClass().getName());
    DecimalFormat formatter = new DecimalFormat("0.0");
               boolean valid=true;

    public boolean isValid() {
        return valid;
    }

    @Autowired
    PlatformTransactionManager ptm = null;
    @Autowired
    GigaSpace space;

    @Override
    public int getMaxTrades() {
        return maxTrades;
    }

    @Override
    public void setMaxTrades(int maxTrades) {
        this.maxTrades = maxTrades;
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    int maxTrades = 10000;
    int maxIterations = 100;

    public ECEExecutorClient() {
        Admin admin = new AdminFactory().createAdmin();
        workerPU = admin.getProcessingUnits().waitFor("worker", 5, TimeUnit.SECONDS);
        if (workerPU != null) {
            workerPU.addLifecycleListener(this);
            workersCount = workerPU.getNumberOfInstances();
        }
    }

    public ECEExecutorClient(int maxTrades) {
        this();
        setMaxTrades(maxTrades);
    }

    public ECEExecutorClient(int maxTrades, int maxIterations) {
        this(maxTrades);
        setMaxIterations(maxIterations);
    }

    @Override
    public void processingUnitInstanceAdded(ProcessingUnitInstance processingUnitInstance) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        workersCount = workerPU.getNumberOfInstances();
    }

    @Override
    public void processingUnitInstanceRemoved(ProcessingUnitInstance processingUnitInstance) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        workersCount = workerPU.getNumberOfInstances();
    }

    @Override
    public void issueTrades() {
        Integer[] ids = new Integer[getMaxTrades()];
        for (int i = 0; i < getMaxTrades(); i++) {
            ids[i] = i;
        }
        // Mapping IDs to partition
		HashMap<Integer , HashSet<Integer>> partitionIDSDistro = CalculateNPVUtil.splitIDs(ids , workersCount);
		AnalysisTask analysisTasks[] = new AnalysisTask[workersCount];

		for (int c=0;c<getMaxIterations();c++)
		{
			Map<String, Double> positions = null;
			logger.info("Calculating Net present value for "  + getMaxTrades()+ " Trades ...");
			ExecutorBuilder executorBuilder = space.executorBuilder(new NPVResultsReducer());

			// Creating the Tasks. Each partition getting a Task with the exact Trade IDs to calculate
			for (int r=0;r<rates.length; r++)
			{
				long startTime = System.currentTimeMillis();
				for (int i=0;i<workersCount ; i++)
				{
					Integer partIDs[] = new Integer[partitionIDSDistro.get(i).size()];
					partitionIDSDistro.get(i).toArray(partIDs);
					analysisTasks[i] = new AnalysisTask(partIDs,i,rates[r]);
					executorBuilder.add(analysisTasks[i]);
				}

				AsyncFuture<HashMap<String, Double>> future = executorBuilder.execute();

				if (future !=null)
				{
					try
					{
						positions = future.get();
						long endTime = System.currentTimeMillis();
						logger.info("\nTime to calculate Net present value for "
                                +getMaxTrades()+ " Trades using " +rates[r] + " % rate:" + (endTime - startTime) + " ms");
						for(String key: positions.keySet()){
							logger.info("Book = " + key + ", NPV = " + formatter.format(positions.get(key)));
						}
						Thread.sleep(1000);
					}
					catch (Exception e)
					{}
				}
			}
		}
    }
}
