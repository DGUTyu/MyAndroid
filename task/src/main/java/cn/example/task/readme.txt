初始化
class AppApplication : BaseApplication() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()

        TaskDispatcher.init(this)

        val dispatcher = TaskDispatcher.createInstance()

        dispatcher.addTask(InitStethoTask())
                .addTask(InitWeexTask())
                .addTask(InitJPushTask())
                .start()

        dispatcher.await()
    }
}

使用示例：
public class InitJPushTask extends Task {

    @Override
    public List<Class<? extends Task>> dependsOn() {
        List<Class<? extends Task>> task = new ArrayList<>();
        task.add(InitWeexTask.class);
        return task;
    }

    @Override
    public void run() {

    }
}

public class InitWeexTask extends MainTask {

    @Override
    public boolean needWait() {
        return true;
    }

    @Override
    public void run() {

    }
}

public class DelayInitTaskA extends MainTask {

    @Override
    public void run() {

    }
}
