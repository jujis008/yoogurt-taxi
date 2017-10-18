package finance.webhooks;

public interface EventConsumer {

	public abstract boolean consumer(EventTask task);

	public abstract boolean feedback(EventTask task);

}
