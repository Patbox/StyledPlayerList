package eu.pb4.styledplayerlist;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MicroScheduler {
    private final List<ScheduledTask> singleTasks = new CopyOnWriteArrayList<>();
    private final List<RepeatingTask> repeatingTasks = new CopyOnWriteArrayList<>();
    private static MicroScheduler INSTANCE;
    private final MinecraftServer server;
    private final Thread thread;

    public MicroScheduler(MinecraftServer s) {
        this.server = s;
        this.thread = new Thread(this::run);
        this.thread.start();
    }

    private void run() {
        while (!this.server.isStopped()) {
            try {
                singleTasks.removeIf(this::executeScheduled);
                repeatingTasks.forEach(this::executeScheduled);
                Thread.sleep(10);
            } catch (Throwable e) {

            }
        }
    }

    private void executeScheduled(RepeatingTask repeatingTask) {
        if (System.currentTimeMillis() >= repeatingTask.time.longValue()) {
            this.server.execute(repeatingTask.runnable);
            repeatingTask.time.add(repeatingTask.delay);
        }
    }

    private boolean executeScheduled(ScheduledTask scheduledTask) {
        if (System.currentTimeMillis() >= scheduledTask.time) {
            this.server.execute(scheduledTask.runnable);
            return true;
        }
        return false;
    }

    public void scheduleOnce(long delay, Runnable task) {
        this.singleTasks.add(new ScheduledTask(System.currentTimeMillis() + delay, task));
    }

    public void scheduleRepeating(long delay, Runnable task) {
        this.repeatingTasks.add(new RepeatingTask(delay, task));
    }

    public static MicroScheduler get(MinecraftServer server) {
        if (INSTANCE == null || INSTANCE.server != server) {
            INSTANCE = new MicroScheduler(server);
        }
        return INSTANCE;
    }

    private record ScheduledTask(long time, Runnable runnable) {}
    private record RepeatingTask(long delay, MutableLong time, Runnable runnable) {
        RepeatingTask(long delay, Runnable runnable) {
            this(delay, new MutableLong(System.currentTimeMillis() + delay), runnable);
        }
    }
}
