package com.revitalize.admincontrol.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "condicionantes")
public class CondicionanteProperties {

    private final Scheduler scheduler = new Scheduler();
    private final Queue queue = new Queue();

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Queue getQueue() {
        return queue;
    }

    public static class Scheduler {
        private List<Integer> alertWindows = new ArrayList<>(List.of(90, 60, 30, 15, 7));
        private Duration criticalEscalationDelay = Duration.ofHours(24);
        private String cron = "0 0/30 * * * *";

        public List<Integer> getAlertWindows() {
            return alertWindows;
        }

        public void setAlertWindows(List<Integer> alertWindows) {
            this.alertWindows = alertWindows;
        }

        public Duration getCriticalEscalationDelay() {
            return criticalEscalationDelay;
        }

        public void setCriticalEscalationDelay(Duration criticalEscalationDelay) {
            this.criticalEscalationDelay = criticalEscalationDelay;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }

    public static class Queue {
        private int workerBatchSize = 25;
        private Duration workerInterval = Duration.ofSeconds(15);
        private int maxAttempts = 5;
        private Duration lockTtl = Duration.ofMinutes(5);

        public int getWorkerBatchSize() {
            return workerBatchSize;
        }

        public void setWorkerBatchSize(int workerBatchSize) {
            this.workerBatchSize = workerBatchSize;
        }

        public Duration getWorkerInterval() {
            return workerInterval;
        }

        public void setWorkerInterval(Duration workerInterval) {
            this.workerInterval = workerInterval;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public Duration getLockTtl() {
            return lockTtl;
        }

        public void setLockTtl(Duration lockTtl) {
            this.lockTtl = lockTtl;
        }
    }
}
